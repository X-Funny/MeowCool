package top.xfunny.meowcool.core;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import top.xfunny.meowcool.core.data.SubjectNode;

public class SubjectManager {
    private SQLiteDatabase db;

    public SubjectManager(SQLiteDatabase db) {
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

    public String getParentSubjectUuid(String uuid) {
        String sql = "SELECT parent_uuid FROM accounting_subjects WHERE uuid = ?";
        String[] args = {uuid};
        String parentUuid = null;
        try (android.database.Cursor cursor = db.rawQuery(sql, args)) {
            if (cursor.moveToFirst()) {
                parentUuid = cursor.getString(0);
            }
        }
        return parentUuid;
    }

    public List<SubjectNode> findUsedDirectChildren(String targetUuid) {
        // 1. 获取目标科目所有后代UUID（包含自身）
        List<String> descendantUuids = getDescendantUuids(targetUuid);
        if (descendantUuids.isEmpty()) return new ArrayList<>();

        // 2. 批量查询被占用的科目UUID
        Set<String> usedUuids = getUsedSubjectUuids(descendantUuids);

        // 3. 构建科目树
        List<SubjectNode> tree = SubjectNode.buildSubjectTree(db, targetUuid);
        if (tree.isEmpty()) return new ArrayList<>();
        SubjectNode targetNode = tree.get(0);

        // 4. 后序遍历标记被占用状态
        markUsage(targetNode, usedUuids);

        // 5. 收集被占用的直接子科目
        List<SubjectNode> usedChildren = new ArrayList<>();
        for (SubjectNode child : targetNode.children) {
            if (child.isUsed) {
                usedChildren.add(child);
            }
        }
        return usedChildren;
    }

    private List<String> getDescendantUuids(String targetUuid) {
        List<String> uuids = new ArrayList<>();
        String sql = "SELECT descendant_uuid FROM accounting_subject_closure WHERE ancestor_uuid = ?";
        try (Cursor cursor = db.rawQuery(sql, new String[]{targetUuid})) {
            while (cursor.moveToNext()) {
                uuids.add(cursor.getString(0));
            }
        }
        return uuids;
    }

    private Set<String> getUsedSubjectUuids(List<String> uuids) {
        Set<String> used = new HashSet<>();
        if (uuids.isEmpty()) return used;

        // 构建 IN 查询占位符
        String placeholders = String.join(",", Collections.nCopies(uuids.size(), "?"));
        String sql = "SELECT DISTINCT subject_uuid FROM accounting_vouchers WHERE subject_uuid IN (" + placeholders + ")";

        try (Cursor cursor = db.rawQuery(sql, uuids.toArray(new String[0]))) {
            while (cursor.moveToNext()) {
                used.add(cursor.getString(0));
            }
        }
        return used;
    }

    private boolean markUsage(SubjectNode node, Set<String> usedUuids) {
        // 递归处理子节点
        boolean isAnyChildUsed = false;
        for (SubjectNode child : node.children) {
            if (markUsage(child, usedUuids)) {
                isAnyChildUsed = true;
            }
        }

        // 当前节点被占用条件：自身有凭证 或 子节点被占用
        node.isUsed = usedUuids.contains(node.uuid) || isAnyChildUsed;
        return node.isUsed;
    }

    private void insertClosure(String ancestor, String descendant, int depth) {
        ContentValues cv = new ContentValues();
        cv.put("ancestor_uuid", ancestor);
        cv.put("descendant_uuid", descendant);
        cv.put("depth", depth);
        db.insert("accounting_subject_closure", null, cv);
    }
}
