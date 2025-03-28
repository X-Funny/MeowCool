package top.xfunny.meowcool.core;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context, String name) {
        super(context, name, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //启用外键约束
        db.execSQL("PRAGMA foreign_keys = ON;");

        //科目表
        String createAccountingSubjectsTable =
                "CREATE TABLE IF NOT EXISTS accounting_subjects (" +
                        "uuid TEXT PRIMARY KEY, " +           //存储 UUID
                        "name TEXT NOT NULL, " +
                        "balance_direction INTEGER NOT NULL, " + // 余额方向，0为借，1为贷
                        "parent_uuid TEXT, " +                // 父节点 UUID
                        "path TEXT, " +                       // 路径
                        "FOREIGN KEY (parent_uuid) REFERENCES accounting_subjects(uuid) ON DELETE CASCADE" +
                        ")";
        db.execSQL(createAccountingSubjectsTable);

        //闭包表
        String createClosureTable =
                "CREATE TABLE IF NOT EXISTS accounting_subject_closure (" +
                        "ancestor_uuid TEXT NOT NULL, " +
                        "descendant_uuid TEXT NOT NULL, " +
                        "depth INTEGER NOT NULL, " +
                        "PRIMARY KEY (ancestor_uuid, descendant_uuid), " +
                        "FOREIGN KEY (ancestor_uuid) REFERENCES accounting_subjects(uuid) ON DELETE CASCADE, " +
                        "FOREIGN KEY (descendant_uuid) REFERENCES accounting_subjects(uuid) ON DELETE CASCADE" +
                        ")";
        db.execSQL(createClosureTable);

        //为闭包表添加索引
        db.execSQL("CREATE INDEX idx_ancestor ON accounting_subject_closure(ancestor_uuid);");
        db.execSQL("CREATE INDEX idx_descendant ON accounting_subject_closure(descendant_uuid);");

        //凭证表
        String createVouchersTable =
                "CREATE TABLE IF NOT EXISTS accounting_vouchers (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " + // 添加自增主键
                        "date TEXT NOT NULL, " +
                        "number TEXT NOT NULL, " +
                        "summary TEXT NOT NULL, " +
                        "is_credit INTEGER NOT NULL, " +       // 改用 INTEGER 表示布尔
                        "subject_uuid TEXT NOT NULL, " +       // 关联科目 UUID
                        "amount REAL NOT NULL, " +
                        "FOREIGN KEY (subject_uuid) REFERENCES accounting_subjects(uuid)" +
                        ")";
        db.execSQL(createVouchersTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 在这里处理数据库升级逻辑
    }
}
