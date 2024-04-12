package top.xfunny.meku;

import static top.xfunny.meku.DatabaseSelectDialog.saveSelectedDatabase;
import static top.xfunny.meku.DatabaseSelectDialog.selectedDatabase;
import static top.xfunny.meku.DatabaseSelectDialog.sharedPref;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DBProcessor extends AppCompatActivity {
    private Button button2 = null;
    private Button button3 = null;
    private Button button4 = null;
    private Button button5 = null;
    private static TextView textView1 = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dbprocessor);

        this.button2 = findViewById(R.id.button2);
        this.button3 = findViewById(R.id.button3);
        this.button4 = findViewById(R.id.button4);
        this.button5 = findViewById(R.id.button5);
        this.textView1 = findViewById(R.id.textView1);
        SharedPreferences sharedPref = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String selectedDatabase = sharedPref.getString("selectedDatabase", "");

        if (!selectedDatabase.isEmpty()) {
            // 如果之前有保存的数据库名称，则在这里进行处理
            // 例如，设置到textView1中显示
            setTextView1(this);
        }





        // Create new database
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DBProcessor.this);
                builder.setTitle("添加账簿");
                final EditText input = new EditText(DBProcessor.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String databaseName = String.format("%s.db", input.getText().toString());

                        DatabaseHelper dbHelper = new DatabaseHelper(DBProcessor.this, databaseName);
                        SQLiteDatabase db = dbHelper.getWritableDatabase();

                        String message = String.format("账簿%s已创建", databaseName);

                        Toast.makeText(DBProcessor.this, message, Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setNegativeButton("取消", null);

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });



        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatabaseSelectDialog();
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatabaseDeleteDialog();
            }
        });
        button5.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                closeSlectedDatabase(DBProcessor.this);
            }
        });


    }
    public static void setTextView1(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String selectedDatabase = sharedPref.getString("selectedDatabase", "");

        String selectedDatabase1 = String.format("当前正在使用的账簿:%s",selectedDatabase);
        textView1.setText(selectedDatabase1);
    }






    private void showDatabaseSelectDialog() {
        DatabaseSelectDialog dialog = new DatabaseSelectDialog(this);
        dialog.show();
    }

    private void showDatabaseDeleteDialog() {
        DatabaseDeleteDialog dialog = new DatabaseDeleteDialog(this);
        dialog.show();
    }

    private void closeSlectedDatabase(Context context){

        if (sharedPref != null) {
            // 调用saveSelectedDatabase方法
            DatabaseSelectDialog.saveSelectedDatabase(null);
            textView1.setText("请选择账簿");
        } else {
            // 处理sharedPref为空的情况，这里可以添加适当的处理代码
            sharedPref = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
            DatabaseSelectDialog.saveSelectedDatabase(null);
            textView1.setText("请选择账簿");
        }


    }




}

