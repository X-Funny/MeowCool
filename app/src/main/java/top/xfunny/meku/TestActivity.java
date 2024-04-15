package top.xfunny.meku;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TestActivity extends AppCompatActivity {
    private Button button2 = null;
    private Button button3 = null;
    private Button button4 = null;
    private Button button5 = null;
    static TextView textView1 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        this.button2 = findViewById(R.id.button2);
        this.button3 = findViewById(R.id.button3);
        this.button4 = findViewById(R.id.button4);
        this.button5 = findViewById(R.id.button5);
        this.textView1 = findViewById(R.id.textView1);
        DatabaseProcessor.setContext(this);


        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseProcessor.createDatabase(TestActivity.this);
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseProcessor.showDatabaseSelectDialog(TestActivity.this);
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseProcessor.showDatabaseDeleteDialog(TestActivity.this);
            }
        });

        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseProcessor.closeSelectedDatabase(TestActivity.this);
            }
        });


        if (DatabaseProcessor.getusedDatabase() == true){
            textView1.setText(R.string.textView1);
        }else {
        DatabaseProcessor.setTextView1(this);}


    }
}
