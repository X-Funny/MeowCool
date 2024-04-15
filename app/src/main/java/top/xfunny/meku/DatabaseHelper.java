package top.xfunny.meku;

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
                "id INTEGER PRIMARY KEY," +
                "code TEXT NOT NULL," +
                "level INTEGER NOT NULL," +
                "name TEXT NOT NULL" +
                ")";
        db.execSQL(createAccounting_subjectsTable);
        String createAccountingVouchersTable = "CREATE TABLE IF NOT EXISTS accounting_vouchers (" +
                "id INTEGER PRIMARY KEY," +
                "date TEXT NOT NULL," +
                "number TEXT NOT NULL," +
                "summary TEXT NOT NULL," +
                "subject_code TEXT NOT NULL," +
                "subject_name TEXT NOT NULL," +
                "dc_flag TEXT NOT NULL," +
                "amount REAL NOT NULL" +
                ")";
        db.execSQL(createAccountingVouchersTable);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 在这里处理数据库升级逻辑
    }
}
