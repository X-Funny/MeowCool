package top.xfunny.meku;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.os.Looper;
import android.util.AndroidException;
import android.widget.Toast;

import androidx.annotation.Nullable;

public abstract class DBProcessor extends SQLiteOpenHelper {
    public DBProcessor(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
public abstract void AccountCreator();


        public void onCreate (SQLiteDatabase db){
            AccountCreator();

    }

}



