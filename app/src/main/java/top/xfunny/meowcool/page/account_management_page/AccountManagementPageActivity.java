package top.xfunny.meowcool.page.account_management_page;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import top.xfunny.meowcool.R;
import top.xfunny.meowcool.core.DatabaseManager;

public class AccountManagementPageActivity extends AppCompatActivity {
    public LinearLayout linearLayout;
    private BroadcastReceiver refreshReceiver;
    private boolean isMultiSelectMode = false;
    private AccountCardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account_set_management_page);
        linearLayout = findViewById(R.id.card_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        refreshReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("top.xfunny.ACTION_REFRESH_ACCOUNT_LIST")) {
                    System.out.println("重新加载accountcard");
                    adapter();
                    accountCard();
                }
            }
        };

        registerReceiver(refreshReceiver, new IntentFilter("top.xfunny.ACTION_REFRESH_ACCOUNT_LIST"), Context.RECEIVER_EXPORTED);


        adapter();// 初始化适配器
        toolBar();
        fab();
        accountCard();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 取消注册广播接收器
        unregisterReceiver(refreshReceiver);
    }

    private void adapter() {
        adapter = new AccountCardAdapter(
                this,
                DatabaseManager.getDatabaseList(this),
                DatabaseManager.getDatabaseCreateTime(),
                new AccountCardAdapter.OnCardClickListener() {
                    @Override
                    public void onCardClick(String accountName) {
                        if (!isMultiSelectMode) {
                            jumpToAccountDetailActivity(accountName);
                        }
                    }

                    @Override
                    public void onMultiSelectModeEnter() {
                        enterMultiSelectMode();
                    }

                    @Override
                    public void onMultiSelectModeExit() {
                        exitMultiSelectMode();
                    }
                });
    }

    private void toolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.rounded_arrow_back_24);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        if (isMultiSelectMode) {
            toolbar.setNavigationIcon(R.drawable.rounded_close_24);
            toolbar.setNavigationOnClickListener(v -> exitMultiSelectMode());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_account_management, menu);
        MenuItem selectAll = menu.findItem(R.id.action_select_all);
        MenuItem deSelectAll = menu.findItem(R.id.action_deselect_all);

        selectAll.setVisible(false);
        deSelectAll.setVisible(false);

        selectAll.setVisible(isMultiSelectMode);
        deSelectAll.setVisible(isMultiSelectMode);
        return false;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_select_all) {
            adapter.selectAll(linearLayout);
            return true;
        } else if (itemId == R.id.action_deselect_all) {
            adapter.deselectAll(linearLayout);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void accountCard() {
        linearLayout.removeAllViews();
        // 遍历适配器中的数据项并添加到 LinearLayout
        for (int i = 0; i < adapter.getCount(); i++) {
            // 获取每个卡片的视图
            View itemView = adapter.getView(i, null, linearLayout);

            // 设置卡片的 LayoutParams
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, 10, 0, 15);  // 设置每个卡片之间的间距
            itemView.setLayoutParams(layoutParams);

            // 将卡片添加到 LinearLayout
            linearLayout.addView(itemView);
        }
    }

    private void enterMultiSelectMode() {
        if (adapter == null) {
            return; // 防止空指针异常
        }

        isMultiSelectMode = true;
        adapter.setMultiSelectMode(true, linearLayout);

        //toolbar重载
        toolBar();
        invalidateOptionsMenu();

        //fab重载
        fab();
        deleteFab();
    }

    private void exitMultiSelectMode() {
        if (adapter == null) {
            return; // 防止空指针异常
        }

        isMultiSelectMode = false;
        adapter.setMultiSelectMode(false, linearLayout);
        //toolbar重载
        toolBar();
        invalidateOptionsMenu();
        //fab重载
        fab();
        deleteFab();
        //card重载
        accountCard();
    }

    private void fab() {
        ExtendedFloatingActionButton fab = findViewById(R.id.account_create_extended_fab);
        if (isMultiSelectMode) {
            fab.setVisibility(View.GONE);
        } else {
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(view -> jumpToAccountDetailActivity(null));
        }

    }

    private void deleteFab() {
        ExtendedFloatingActionButton fab = findViewById(R.id.account_delete_extended_fab);
        if (isMultiSelectMode) {
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(view -> confirmDeleteDialog());
        } else {
            fab.setVisibility(View.GONE);
        }

    }

    private void confirmDeleteDialog() {
        File[] filesToDelete = getSelectedFiles();
        boolean isCurrentDatabaseSelected = false;
        String currentDatabaseName = DatabaseManager.getSelectedDatabaseName(this);

        for (File file : filesToDelete) {
            if (file.getName().equals(currentDatabaseName)) {
                isCurrentDatabaseSelected = true;
                break;
            }
        }

        String message = isCurrentDatabaseSelected
                ? String.format("其中账套 %s 正在使用中", currentDatabaseName.substring(0, currentDatabaseName.length() - 3))
                : null;

        new MaterialAlertDialogBuilder(this)
                .setTitle("确认删除？该操作无法恢复！")
                .setMessage(message)
                .setIcon(R.drawable.warning_24px)
                .setPositiveButton("确认", (dialog, which) -> {
                    if (filesToDelete.length > 0) {
                        // 调用 DatabaseManager 的删除方法
                        DatabaseManager.delete(this, filesToDelete);
                        exitMultiSelectMode();
                    } else {
                        Toast.makeText(this, "没有选中任何文件！", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", (dialog, which) -> {
                    // 空操作，关闭对话框
                })
                .show();
    }


    // 跳转账套详情activity
    private void jumpToAccountDetailActivity(String accountName) {
        Intent intent = new Intent();
        intent.setClass(this, AccountDetailActivity.class);
        intent.putExtra("accountName", accountName);
        startActivity(intent);
    }

    private File[] getSelectedFiles() {
        List<File> selectedFiles = new ArrayList<>();

        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.isSelected(i)) { // 检查是否选中
                String fileName = adapter.getItem(i);
                File file = new File(getFilesDir(), fileName);
                selectedFiles.add(file);
            }
        }
        return selectedFiles.toArray(new File[0]); // 转换为数组返回
    }

}
