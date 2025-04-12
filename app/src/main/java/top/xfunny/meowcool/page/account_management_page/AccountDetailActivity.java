package top.xfunny.meowcool.page.account_management_page;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import top.xfunny.meowcool.R;
import top.xfunny.meowcool.core.DatabaseManager;
import top.xfunny.meowcool.utils.Dp2px;

public class AccountDetailActivity extends AppCompatActivity {
    MaterialButton applyButton;
    private Boolean isBrandNew = true;
    private Boolean legitimateName = false;
    private String accountName;
    private String tempAccountName;
    private EditText accountNameEditText;
    private Context context;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        accountName = intent.getStringExtra("accountName");
        isBrandNew = accountName == null;


        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account_detail);
        LinearLayout layout = findViewById(R.id.account_detail_linear_layout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        context = this;

        toolBar();
        textEdit();
        if (isBrandNew) {
            createAccountButton(layout);
        } else if (accountName.equals(DatabaseManager.getSelectedDatabaseName(context))) {
            Snackbar.make(layout, "注意：当前账套已启用！", Snackbar.LENGTH_LONG).show();
        }
    }

    private void toolBar() {
        Toolbar toolbar = findViewById(R.id.account_detail_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        if (isBrandNew) {
            toolbar.setTitle("新建账套");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_account_detail, menu);
        MenuItem saveMenuItem = menu.findItem(R.id.action_save);
        Drawable icon = saveMenuItem.getIcon();
        if (icon != null) {
            icon = DrawableCompat.wrap(icon);
            saveMenuItem.setIcon(icon);
        }
        saveMenuItem.setVisible(false);
        if (!isBrandNew) {
            saveMenuItem.setVisible(allowSaving());
        }

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        LinearLayout layout = findViewById(R.id.account_detail_linear_layout);
        if (itemId == R.id.action_save) {

            String updatedAccountName = accountNameEditText.getText().toString();

            // 保存重命名
            if (!updatedAccountName.equals(tempAccountName)) {
                if (accountName.equals(DatabaseManager.getSelectedDatabaseName(context))) {
                    DatabaseManager.close(context);
                    DatabaseManager.saveAccountName(accountName, String.format("%s%s", updatedAccountName, ".db"), this);
                    DatabaseManager.selectDatabase(context, String.format("%s%s", updatedAccountName, ".db"));
                } else {
                    DatabaseManager.saveAccountName(accountName, String.format("%s%s", updatedAccountName, ".db"), this);
                    DatabaseManager.saveAccountName(String.format("%s%s", tempAccountName, ".db-journal"), String.format("%s%s", updatedAccountName, ".db-journal"), this);
                }
            }

            needRefresh();
            Snackbar.make(layout, "保存成功", Snackbar.LENGTH_LONG).show();


            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void textEdit() {
        TextInputLayout accountNameInputLayout = findViewById(R.id.account_name_input_layout);
        accountNameEditText = findViewById(R.id.account_name_edit_text);

        if (accountName != null && accountName.endsWith(".db")) {
            tempAccountName = accountName.substring(0, accountName.length() - 3); // 去掉.db
            if (accountNameEditText != null) {
                accountNameEditText.setText(tempAccountName);
            }
        }

        accountNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // 这个方法在文本变化之前调用
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // 这个方法在文本变化时调用

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // 这个方法在文本变化之后调用
                String inputText = editable.toString();

                // 检查文本是否包含空格或".db"
                if (inputText.contains(" ")) {
                    // 设置错误提示信息
                    accountNameInputLayout.setError("账户名不能包含空格");
                    legitimateName = false;
                } else if (inputText.contains(".db")) {
                    accountNameInputLayout.setError("账户名不能包含.db");
                    legitimateName = false;
                } else if (DatabaseManager.nameCheck(String.format("%s%s", inputText, ".db"), context) == 1) {
                    if (!inputText.equals(tempAccountName)) {
                        accountNameInputLayout.setError("存在相同账套");
                        legitimateName = false;
                    } else {
                        accountNameInputLayout.setError(null);
                    }
                } else if (DatabaseManager.nameCheck(inputText, context) == 3) {
                    accountNameInputLayout.setError("账套名不能含有\\/\\:*?\"<>|");
                    legitimateName = false;
                } else if (inputText.isEmpty()) {
                    accountNameInputLayout.setError("账套名不能为空");
                    legitimateName = false;
                } else {
                    // 如果没有问题，清除错误提示
                    accountNameInputLayout.setError(null);
                    legitimateName = true;
                }


                //更新按钮状态
                if (applyButton != null) {
                    applyButton.setEnabled(allowSaving());
                }
                //更新toolbar
                invalidateOptionsMenu();
            }
        });
    }

    private void createAccountButton(LinearLayout layout) {
        //添加一个button
        applyButton = new MaterialButton(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, Dp2px.dpToPx(16, context), 0, 0);
        applyButton.setLayoutParams(params); // 应用布局参数
        applyButton.setText("新建账套");
        applyButton.setEnabled(false);
        applyButton.setOnClickListener(v -> {
            if (DatabaseManager.creat(String.format("%s%s", accountNameEditText.getText().toString(), ".db"), context)) {

                Toast.makeText(context, "数据库" + accountNameEditText.getText().toString() + "创建成功", Toast.LENGTH_SHORT).show();
                System.out.println("开始刷新");
                needRefresh();
                finish();

                //todo:添加一个加载动画
            }

        });
        layout.addView(applyButton);
    }


    private Boolean allowSaving() {//todo:预留的检查方法
        if (legitimateName) {
            return true;
        } else {
            return false;
        }
    }

    private void needRefresh() {
        Intent broadcastIntent = new Intent("top.xfunny.ACTION_REFRESH_ACCOUNT_LIST");
        sendBroadcast(broadcastIntent);
    }
}