package top.xfunny.meowcool.core;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import top.xfunny.meowcool.R;

public class SubjectProcessor extends AppCompatActivity {
    private static Context context;
    private String DatabaseFile = DatabaseManager.getSelectedDatabase(context);

    protected void onCreate() {
        setContentView(R.layout.subject_processor);

    }

    public void readSubject() {
        String countQuery = "SELECT COUNT(*) FROM accounting_subjects";

    }

}
