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
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import top.xfunny.meowcool.Application;
import top.xfunny.meowcool.R;
import top.xfunny.meowcool.core.DatabaseManager;
import top.xfunny.meowcool.core.SubjectManager;
import top.xfunny.meowcool.core.data.SubjectNode;

public class SubjectManagementPageActivity extends AppCompatActivity {
    private SubjectManager subjectManager;
    private SQLiteDatabase db;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    private SubjectManagementViewModel viewModel;

    private SubjectDetailViewModel detailViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SubjectManagementViewModel.class);
        detailViewModel = new ViewModelProvider((ViewModelStoreOwner) getApplication()).get(SubjectDetailViewModel.class);
        EdgeToEdge.enable(this); // 启用边缘到边缘的布局支持，以适应不同设备的屏幕边缘
        setContentView(R.layout.activity_subject_management_page); // 设置活动的布局资源文件

        // 设置主视图的窗口嵌入监听器，以处理系统栏（如状态栏、导航栏）的显示
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars()); // 获取系统栏的 insets
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom); // 根据系统栏的大小调整视图的内边距
            return insets;
        });

        toolbar(); // 初始化工具栏
        fab();// 初始化FAB

        db = DatabaseManager.openDatabase(this); // 打开数据库
        subjectManager = new SubjectManager(db); // 创建 SubjectManager 实例，用于管理主题

        // 设置ViewPager
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        CategoryPagerAdapter pagerAdapter = new CategoryPagerAdapter(this, SubjectAdapterMode.MODE_EDIT);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            String[] titles = {"资产", "负债", "净资产", "损益"};
            tab.setText(titles[position]);
        }).attach();
    }

    private void toolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.rounded_arrow_back_24);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_subject_management, menu);
        MenuItem add = menu.findItem(R.id.action_add);
        MenuItem search = menu.findItem(R.id.action_location_search);
        MenuItem filter = menu.findItem(R.id.action_filter);
        return false;
    }

    private void fab() {
        Button fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(v -> {
            showAddSubjectDialog();
        });

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
            viewModel.refreshAll();
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
            //todo:需要新的刷新方式
        } catch (Exception e) {
            Log.e("AddSubject", "插入失败", e);
            Toast.makeText(this, "添加失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            db.endTransaction();
        }
    }
}