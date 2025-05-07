package top.xfunny.meowcool.page.subject_management_page;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import top.xfunny.meowcool.R;
import top.xfunny.meowcool.core.DatabaseManager;
import top.xfunny.meowcool.core.data.SettingsManager;
import top.xfunny.meowcool.core.data.SubjectNode;

public class PRConfiguationActivity extends AppCompatActivity {
    private List<String> subjectUuidList;
    private SettingsManager settingsManager;
    private RecyclerView recyclerView;
    Button button;
    PRsubjectAdapter adapter;

    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pr_configuation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        settingsManager = new SettingsManager(this);
        db = DatabaseManager.openDatabase(this);
        subjectUuidList =  new ArrayList<>(settingsManager.getPRSubject());

        if(!subjectUuidList.isEmpty()){
            if(subjectUuidList.get(0).isEmpty()){
                subjectUuidList.clear();
            }
        }

        toolbar();
        button();
        recyclerVIew();
    }

    private void button(){
        button = findViewById(R.id.fabAdd);
        button.setOnClickListener(v -> {showAddSubjectDialog();
        });
    }

    private void toolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.rounded_arrow_back_24);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void recyclerVIew(){
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PRsubjectAdapter(subjectUuidList, this);
        recyclerView.setAdapter(adapter);
    }

    private void showAddSubjectDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("添加收付科目");
        builder.setView(R.layout.dialog_add_pr_subject);

        // 使用setPositiveButton和setNegativeButton来添加确认和取消按钮
        builder.setPositiveButton("确认", (dialog, which) -> {
            // 初始化视图
            AlertDialog alertDialog = (AlertDialog) dialog;
            AutoCompleteTextView etPR = alertDialog.findViewById(R.id.etPRSubject);
            SubjectNode selectedParent = (SubjectNode) etPR.getTag();// 获取选中的科目
            String uuid = selectedParent.getUuid();

            subjectUuidList.add(uuid);
            settingsManager.setPRSubject(uuid);

            adapter.notifyDataSetChanged();

            dialog.dismiss();
        });

        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());

        // 创建并显示对话框
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            // 加载父科目列表
            AlertDialog alertDialog = (AlertDialog) dialogInterface;
            AutoCompleteTextView etPR = alertDialog.findViewById(R.id.etPRSubject);
            loadParentSubjects(etPR);
        });

        dialog.show();
    }

    private void loadParentSubjects(AutoCompleteTextView etParent) {
        List<SubjectNode> allSubjects = SubjectNode.getAllSubjects(db);
        ArrayAdapter<SubjectNode> adapter = new ArrayAdapter<SubjectNode>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                allSubjects
        ) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setText(getItem(position).getName());
                return view;
            }
        };

        etParent.setAdapter(adapter);
        etParent.setOnItemClickListener((parent, view, position, id) -> {
            SubjectNode selected = adapter.getItem(position);
            etParent.setTag(selected);
        });
    }
}