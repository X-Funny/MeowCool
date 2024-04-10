package top.xfunny.meku;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    EditText et1;
    Button bt1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et1 = findViewById(R.id.et1);
        bt1 = findViewById(R.id.bt1);

        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dbname = et1.getText().toString();
                database = new DBProcessor(MainActivity.this,dbname);






                Toast.makeText(MainActivity.this, "输入的文字已赋值给db变量", Toast.LENGTH_SHORT).show();

            }

            private String bridge(String db) {
                return db;
            }

            ;


        });


    }
}





