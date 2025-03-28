package top.xfunny.meowcool.page.subject_management_page;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.material.dialog.MaterialDialogs;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import top.xfunny.meowcool.R;
import top.xfunny.meowcool.core.DatabaseManager;
import top.xfunny.meowcool.core.SubjectManager;
import top.xfunny.meowcool.core.subject.SubjectNode;

public class SubjectManagementPageActivity extends AppCompatActivity {
    private SubjectManager subjectManager;
    private SQLiteDatabase db;
    private SubjectAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_subject_management_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        toolbar();


        db = DatabaseManager.openDatabase(this);
        subjectManager = new SubjectManager(db);

        RecyclerView recyclerView = findViewById(R.id.rvSubjects);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SubjectAdapter(this);
        recyclerView.setAdapter(adapter);

        loadData();
        testBtn();
    }

    private void loadData() {
        List<SubjectNode> rootNodes = SubjectNode.buildSubjectTree(db);//使用返回的科目结构
        adapter.setData(rootNodes);//将其应用与adapter中
    }

    private void toolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.rounded_arrow_back_24);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void testBtn(){//科目添加测试按钮，即将删除
        Button btnTest = findViewById(R.id.fabAdd);
        btnTest.setOnClickListener(v -> showAddSubjectDialog());
    }

    private void insertTestData() {//科目添加测试，即将删除
        // 定义测试科目UUID（确保唯一性）
        String rootUuid = "test_root_" + System.currentTimeMillis();
        String level1Uuid = "test_level1_" + System.currentTimeMillis();
        String level2Uuid = "test_level2_" + System.currentTimeMillis();

        // 使用事务确保原子性
        db.beginTransaction();
        try {
            // 插入一级科目（资产）
            subjectManager.insertSubject(
                    rootUuid,
                    "测试-资产",
                    1, // balanceDirection
                    null, // parentUuid
                    "/" + rootUuid + "/"
            );

            // 插入二级科目（流动资产）
            subjectManager.insertSubject(
                    level1Uuid,
                    "测试-流动资产",
                    1,
                    rootUuid,
                    "/" + rootUuid + "/" + level1Uuid + "/"
            );

            // 插入三级科目（现金）
            subjectManager.insertSubject(
                    level2Uuid,
                    "测试-现金",
                    1,
                    level1Uuid,
                    "/" + rootUuid + "/" + level1Uuid + "/" + level2Uuid + "/"
            );

            db.setTransactionSuccessful();
            Toast.makeText(this, "测试数据插入成功", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("TestData", "插入失败", e);
            Toast.makeText(this, "插入失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            db.endTransaction();
            loadData(); // 刷新数据
        }
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_subject_management, menu);
        MenuItem add = menu.findItem(R.id.action_add);
        MenuItem search = menu.findItem(R.id.action_location_search);
        MenuItem filter = menu.findItem(R.id.action_filter);
        return false;
    }

    private void showAddSubjectDialog() {
        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setTitle("添加科目")
                .setView(R.layout.dialog_add_subject)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            // 初始化视图
            AutoCompleteTextView etParent = dialog.findViewById(R.id.etParentSubject);
            EditText etName = dialog.findViewById(R.id.etSubjectName);
            MaterialButtonToggleGroup toggleGroup = dialog.findViewById(R.id.tgBalanceDirection);
            Button btnConfirm = dialog.findViewById(R.id.btnConfirm);
            Button btnCancel = dialog.findViewById(R.id.btnCancel);

            // 加载父科目列表
            loadParentSubjects(etParent);

            // 设置默认选择"借"
            toggleGroup.check(R.id.btnDebit);

            // 确认按钮点击
            btnConfirm.setOnClickListener(v -> {
                String subjectName = etName.getText().toString().trim();
                SubjectNode selectedParent = (SubjectNode) etParent.getTag();
                int direction = toggleGroup.getCheckedButtonId() == R.id.btnDebit ? 1 : -1;

                if (TextUtils.isEmpty(subjectName)) {
                    etName.setError("科目名称不能为空");
                    return;
                }

                insertNewSubject(subjectName, direction, selectedParent);
                dialog.dismiss();
            });

            // 取消按钮
            btnCancel.setOnClickListener(v -> dialog.dismiss());
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

    private void insertNewSubject(String name, int direction, SubjectNode parent) {
        String uuid = UUID.randomUUID().toString();
        String parentUuid = parent != null ? parent.getUuid() : null;
        String path = parent != null ? parent.getPath() + uuid + "/" : "/" + uuid + "/";

        db.beginTransaction();
        try {
            subjectManager.insertSubject(uuid, name, direction, parentUuid, path);
            db.setTransactionSuccessful();
            Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
            loadData();
        } catch (Exception e) {
            Log.e("AddSubject", "插入失败", e);
            Toast.makeText(this, "添加失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            db.endTransaction();
        }
    }

    // 更新菜单项点击处理
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            showAddSubjectDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}