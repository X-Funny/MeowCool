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

        String createAccounting_subjectsTable = "CREATE TABLE IF NOT EXISTS accounting_subjects (" +
                "id INTEGER PRIMARY KEY," +//序号
                "code TEXT NOT NULL," +//科目代码
                "name TEXT NOT NULL" +//科目名称
                ")";
        db.execSQL(createAccounting_subjectsTable);
        String createAccountingVouchersTable = "CREATE TABLE IF NOT EXISTS accounting_vouchers (" +
                "id INTEGER PRIMARY KEY," +//序号
                "date TEXT NOT NULL," +//日期
                "number TEXT NOT NULL," +//凭证编号
                "summary TEXT NOT NULL," +//摘要
                "debit_subject_code TEXT NOT NULL," +//借方科目代码
                "debit_amount REAL NOT NULL," +//借方科目金额
                "credit_subject_code TEXT NOT NULL," +//贷方科目代码
                "credit_amount REAL NOT NULL" +//贷方科目金额
                ")";
        db.execSQL(createAccountingVouchersTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 在这里处理数据库升级逻辑
    }
}
