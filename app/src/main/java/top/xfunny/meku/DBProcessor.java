package top.xfunny.meku;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DBProcessor extends AppCompatActivity {
    private Button button2 = null;
    private Button button3 = null;
    private Button button4 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dbprocessor);

        this.button2 = findViewById(R.id.button2);
        this.button3 = findViewById(R.id.button3);
        this.button4 = findViewById(R.id.button4);

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

        // Select existing database
        Button button3 = findViewById(R.id.button3);
        Button button4 = findViewById(R.id.button4);

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
    }

    private void showDatabaseSelectDialog() {
        DatabaseSelectDialog dialog = new DatabaseSelectDialog(this);
        dialog.show();
    }

    private void showDatabaseDeleteDialog() {
        DatabaseDeleteDialog dialog = new DatabaseDeleteDialog(this);
        dialog.show();
    }


}

