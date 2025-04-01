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

    /**
     * 主题管理页面的创建方法
     * 该方法在活动初始化时调用，用于设置活动的布局、初始化数据库和视图组件
     *
     * @param savedInstanceState 保存的实例状态，如果活动被重新创建（如屏幕旋转），则可用于恢复之前的状态
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // 启用边缘到边缘的布局支持，以适应不同设备的屏幕边缘
        setContentView(R.layout.activity_subject_management_page); // 设置活动的布局资源文件

        // 设置主视图的窗口嵌入监听器，以处理系统栏（如状态栏、导航栏）的显示
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars()); // 获取系统栏的 insets
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom); // 根据系统栏的大小调整视图的内边距
            return insets;
        });

        toolbar(); // 初始化工具栏

        db = DatabaseManager.openDatabase(this); // 打开数据库
        subjectManager = new SubjectManager(db); // 创建 SubjectManager 实例，用于管理主题

        RecyclerView recyclerView = findViewById(R.id.rvSubjects); // 获取 RecyclerView 实例
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // 设置 RecyclerView 的布局管理器为LinearLayoutManager
        adapter = new SubjectAdapter(this); // 创建 SubjectAdapter 实例
        recyclerView.setAdapter(adapter); // 设置 RecyclerView 的适配器

        loadData(); // 加载数据到 RecyclerView 中
        testBtn(); // 初始化测试按钮
    }

    private void loadData() {
        List<SubjectNode> rootNodes = SubjectNode.buildSubjectTree(db);
        adapter.setData(rootNodes);
    }

    private void toolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.rounded_arrow_back_24);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void testBtn() {//科目添加测试按钮，即将删除
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
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("添加科目");
        builder.setView(R.layout.dialog_add_subject);

        // 使用setPositiveButton和setNegativeButton来添加确认和取消按钮
        builder.setPositiveButton("确认", (dialog, which) -> {
            // 初始化视图
            AlertDialog alertDialog = (AlertDialog) dialog;
            AutoCompleteTextView etParent = alertDialog.findViewById(R.id.etParentSubject);
            EditText etName = alertDialog.findViewById(R.id.etSubjectName);
            MaterialButtonToggleGroup toggleGroup = alertDialog.findViewById(R.id.tgBalanceDirection);

            // 获取输入值
            String subjectName = etName.getText().toString().trim();
            SubjectNode selectedParent = (SubjectNode) etParent.getTag();
            int direction = toggleGroup.getCheckedButtonId() == R.id.btnDebit ? 1 : -1;

            // 验证并插入新科目
            if (TextUtils.isEmpty(subjectName)) {
                etName.setError("科目名称不能为空");
                return;
            }

            insertNewSubject(subjectName, direction, selectedParent);
            dialog.dismiss();
        });

        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());

        // 创建并显示对话框
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            // 加载父科目列表
            AlertDialog alertDialog = (AlertDialog) dialogInterface;
            AutoCompleteTextView etParent = alertDialog.findViewById(R.id.etParentSubject);
            loadParentSubjects(etParent);

            // 设置默认选择"借"
            MaterialButtonToggleGroup toggleGroup = alertDialog.findViewById(R.id.tgBalanceDirection);
            toggleGroup.check(R.id.btnDebit);
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
            //showAddSubjectDialog();
            insertTestData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}