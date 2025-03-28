package top.xfunny.meowcool.core;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class SubjectManager {
    private SQLiteDatabase db;

    public SubjectManager(SQLiteDatabase db){
        this.db = db;
    }

    // 插入科目并更新闭包表
    public long insertSubject(String uuid, String name, int balanceDirection, String parentUuid, String path) {
        ContentValues values = new ContentValues();
        values.put("uuid", uuid);
        values.put("name", name);
        values.put("balance_direction", balanceDirection);//借贷方向
        values.put("parent_uuid", parentUuid);//父级科目uuid
        values.put("path", path);//科目路径

        // 开启事务
        db.beginTransaction();
        try {
            // 1. 插入科目
            long result = db.insert("accounting_subjects", null, values);
            if (result == -1) return -1; // 插入失败

            // 2. 更新闭包表
            if (parentUuid == null) {
                // 根节点：仅插入自身关系
                insertClosure(uuid, uuid, 0);
            } else {
                // 继承父节点的所有祖先关系
                String insertClosureSQL =
                        "INSERT INTO accounting_subject_closure (ancestor_uuid, descendant_uuid, depth) " +
                                "SELECT ancestor_uuid, ?, depth + 1 " + // 新节点是父节点后代的后代
                                "FROM accounting_subject_closure " +
                                "WHERE descendant_uuid = ? " +
                                "UNION ALL SELECT ?, ?, 0"; // 插入自身关系
                db.execSQL(insertClosureSQL, new String[]{uuid, parentUuid, uuid, uuid});
            }

            db.setTransactionSuccessful();
            return result;
        } finally {
            db.endTransaction();
        }
    }

    private void insertClosure(String ancestor, String descendant, int depth) {
        ContentValues cv = new ContentValues();
        cv.put("ancestor_uuid", ancestor);
        cv.put("descendant_uuid", descendant);
        cv.put("depth", depth);
        db.insert("accounting_subject_closure", null, cv);
    }
}
