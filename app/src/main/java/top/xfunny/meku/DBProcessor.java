package top.xfunny.meku;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.AndroidException;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class DBProcessor extends AppCompatActivity {
    private Button button2 = null;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dbprocessor);
        this.button2 = super.findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DBProcessor.this);
                builder.setTitle("设置账簿名称");
                final EditText input = new EditText(DBProcessor.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String databaseName = input.getText().toString(); // 获取输入的文本
                        DatabaseHelper dbHelper = new DatabaseHelper(DBProcessor.this, databaseName);
                        String message1 = String.format("账簿%s已创建", databaseName);
                        Toast toast1 = Toast.makeText(DBProcessor.this,message1,Toast.LENGTH_SHORT);
                        toast1.show();
                        SQLiteDatabase db = dbHelper.getWritableDatabase();

                    }
                });

                builder.setNegativeButton("取消", null);

                // 创建并显示弹窗
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
}






