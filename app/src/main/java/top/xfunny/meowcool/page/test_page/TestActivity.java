package top.xfunny.meowcool.page.test_page;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import top.xfunny.meowcool.R;
import top.xfunny.meowcool.core.DatabaseManager;

public class TestActivity extends AppCompatActivity {
    static TextView textView1 = null;
    private Button button2 = null;
    private Button button3 = null;
    private Button button4 = null;
    private Button button5 = null;
    private Button button6 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        this.button2 = findViewById(R.id.button2);
        this.button3 = findViewById(R.id.button3);
        this.button4 = findViewById(R.id.button4);
        this.button5 = findViewById(R.id.button5);
        this.textView1 = findViewById(R.id.textView1);
        this.button6 = findViewById(R.id.button6);
        DatabaseProcessor.setContext(this);


        button2.setOnClickListener(v -> DatabaseProcessor.createDatabase(TestActivity.this));

        button3.setOnClickListener(v -> DatabaseProcessor.showDatabaseSelectDialog(TestActivity.this));

        button4.setOnClickListener(v -> DatabaseProcessor.showDatabaseDeleteDialog(TestActivity.this));

        button5.setOnClickListener(v -> DatabaseManager.close(TestActivity.this));


        if (DatabaseProcessor.getusedDatabase()) {
            textView1.setText(R.string.textView1);
        } else {
            DatabaseProcessor.setTextView1(this);
        }

        button6.setOnClickListener(v -> subjecteditDialog());


    }

    private void subjecteditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("科目编辑");
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
