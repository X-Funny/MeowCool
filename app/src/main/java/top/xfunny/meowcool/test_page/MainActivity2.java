package top.xfunny.meowcool.test_page;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import top.xfunny.meowcool.R;

public class MainActivity2 extends AppCompatActivity {
    private Button button1 = null;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        this.button1= super.findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity2.this, TestActivity.class);
                startActivity(intent);

            }
        });
    }
}





