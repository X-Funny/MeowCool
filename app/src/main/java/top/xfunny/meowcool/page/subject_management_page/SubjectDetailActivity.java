package top.xfunny.meowcool.page.subject_management_page;

import android.annotation.SuppressLint;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;

import top.xfunny.meowcool.R;
import top.xfunny.meowcool.core.DatabaseManager;
import top.xfunny.meowcool.core.SubjectManager;
import top.xfunny.meowcool.core.data.SubjectNode;
import top.xfunny.meowcool.utils.Dp2px;

public class SubjectDetailActivity extends AppCompatActivity {
    private TextInputEditText editText;
    private TextInputLayout editTextLayout;
    private TextView initialAmountTextView;
    private MaterialButtonToggleGroup toggleGroup;
    private Toolbar toolbar;
    private ConstraintLayout warningLayout;
    private SubjectDetailViewModel detailViewModel;
    private SubjectNode subjectNode;
    private SubjectManager subjectManager;
    private SQLiteDatabase db;
    private List<SubjectNode> usedChildren;
    private boolean isEdible;
    private boolean isUsed;
    private MenuItem saveMenuItem;
    private MenuItem removeMenuItem;

    private boolean saveName = false;
    private String saveSubjectName = "";
    private boolean saveInitialAmount = false;
    private BigDecimal saveSubjectinitialAmount = new BigDecimal("0");
    private boolean saveDirection = false;
    private int saveSubjectDirection = 0;
    private boolean legitimateSubjectName = true;

    private MutableLiveData<Boolean> needSave = new MutableLiveData<>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_subject_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        detailViewModel = new ViewModelProvider(this).get(SubjectDetailViewModel.class);

        db = DatabaseManager.openDatabase(this);
        subjectManager = new SubjectManager(db);

        subjectNode = (SubjectNode) getIntent().getSerializableExtra("node");

        this.isEdible = isEditable();
        this.isUsed = isUsed();

        //加载控件
        toolbar();
        initialAmountTextView();
        toggleGroup();
        editText();

        warningLayout();
    }

    private void editText(){
        editText = findViewById(R.id.subject_name_edit_text);
        editTextLayout = findViewById(R.id.subject_name_input_layout);

        editText.setText(subjectNode.getName());

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                saveName = true;
                String subjectName = s.toString();
                int checkResult = subjectManager.nameCheck(subjectName);

                switch (checkResult){
                    case 0:
                        editTextLayout.setError("");
                        saveSubjectName = subjectName;
                        legitimateSubjectName = true;
                        break;
                    case 1:
                        editTextLayout.setError("科目名不能有空格");
                        legitimateSubjectName = false;
                        break;
                    case 2:
                        editTextLayout.setError("科目名不能为空");
                        legitimateSubjectName = false;
                        break;
                    case 3:
                        editTextLayout.setError("科目名不能含有\\/:*?\"<>|");
                        legitimateSubjectName = false;
                    default:
                        break;
                }
                getNeedSave();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void initialAmountTextView(){
        initialAmountTextView = findViewById(R.id.subject_initial_account_amount);
        initialAmountTextView.setEnabled(!hasChildren());

        if(hasChildren()){
            BigDecimal initialAmount = subjectManager.calculateParentInitialAmount(subjectNode.getUuid());
            initialAmountTextView.setText("¥ "+initialAmount);
        }else{
            initialAmountTextView.setText("¥ "+subjectNode.getInitialAmount().toString());
        }

        initialAmountTextView.setOnClickListener(v -> {
            InitialAmountNumberKeyboard initialAmountNumberKeyboard = new InitialAmountNumberKeyboard();
            initialAmountNumberKeyboard.show(getSupportFragmentManager(), "initialAmountNumberKeyboard");
            detailViewModel.getCurrentAmount().postValue(subjectNode.getInitialAmount().toString());

            detailViewModel.getCurrentAmount().observe(this, newAmount -> {
                if (newAmount != null) {
                    // 使用DecimalFormat格式化显示
                    initialAmountTextView.setText("¥ "+newAmount);
                    saveSubjectinitialAmount = new BigDecimal(newAmount);
                    saveInitialAmount = !saveSubjectinitialAmount.equals(subjectNode.getInitialAmount());
                    getNeedSave();
                }
            });
        });
    }

    private void toggleGroup(){
        toggleGroup = findViewById(R.id.setDetailBalanceDirection);
        toggleGroup.setEnabled(isEdible);

        int direction = subjectNode.getDirection();

        toggleGroup.check(direction == 1 ? R.id.debit_button : R.id.credit_button);
        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if(isChecked){
                if(checkedId == R.id.debit_button){
                    saveDirection = true;
                    saveSubjectDirection = 1;
                }else if (checkedId == R.id.credit_button){
                    saveDirection = true;
                    saveSubjectDirection = -1;
                }
                getNeedSave();
            }
        });
    }

    private void toolbar(){
        toolbar = findViewById(R.id.subject_detail_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            setResult(6);
            onBackPressed();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_subject_detail, menu);
        saveMenuItem = menu.findItem(R.id.action_save);
        removeMenuItem = menu.findItem(R.id.action_remove);

        removeMenuItem.setEnabled(!isUsed);

        getNeedSave().observe(this, aBoolean -> saveMenuItem.setVisible(aBoolean));

        saveMenuItem.setOnMenuItemClickListener(item -> {
            String subjectName;
            BigDecimal initialAmount;
            Integer direction;
            subjectName = saveName? saveSubjectName : null;
            initialAmount = saveInitialAmount? saveSubjectinitialAmount : null;
            direction = saveDirection? saveSubjectDirection : null;

            subjectManager.modifySubject(subjectNode.getUuid(), subjectName, initialAmount, direction);

            Snackbar.make(findViewById(R.id.main), "保存成功", Snackbar.LENGTH_LONG).show();
            return true;
        });

        removeMenuItem.setOnMenuItemClickListener(item -> {
            confirmDeleteDialog();
            return true;
        });

        return true;
    }

    public void warningLayout(){
        String parentUuid = subjectManager.getParentSubjectUuid(subjectNode.getUuid());
        LinearLayout layout = findViewById(R.id.warning_info);
        TextView textView1 = new TextView(this);
        TextView textView2 = new TextView(this);
        TextView textView3 = new TextView(this);

        warningLayout = findViewById(R.id.warning_layout);
        warningLayout.setVisibility(isUsed()||!isEdible ? View.VISIBLE:View.GONE);// 当isUsed为false，则不可见

        textView1.setText("非末级科目，无法编辑初始金额");
        textView2.setText("该科目或其子科目已被使用，该科目无法删除且无法更改借贷方向");
        textView3.setText("非父级科目，无法更改借贷方向");

        if(hasChildren()){
            layout.addView(textView1);
        }

        if(isUsed){
            layout.addView(textView2);
        }

        if(!(parentUuid.equals("ASSET")||parentUuid.equals("LIABILITY")||parentUuid.equals("EQUITY")||parentUuid.equals("COST")||parentUuid.equals("PROFIT_LOSS"))){
            layout.addView(textView3);
        }

        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) child.getLayoutParams();
            if (params == null) {
                params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
            }

            params.setMargins(0, 0, 0, Dp2px.dpToPx(16, this));
            child.setLayoutParams(params);
        }

        if(layout.getChildCount()==0){
            warningLayout.setVisibility(View.GONE);
        }
    }

    public void confirmDeleteDialog(){
        String message = hasChildren()
                ? "该科目下的所有子级科目将被删除"
                : null;

        new MaterialAlertDialogBuilder(this)
                .setTitle("确认删除？该操作无法恢复！")
                .setMessage(message)
                .setIcon(R.drawable.warning_24px)
                .setPositiveButton("确认", (dialog, which) -> {
                    subjectManager.deleteSubjectWithChildren(subjectNode.getUuid());
                    setResult(RESULT_OK);
                    onBackPressed();
                })
                .setNegativeButton("取消", (dialog, which) -> {
                    // 空操作，关闭对话框
                })
                .show();
    }

    private boolean isEditable(){// 返回true，则可编辑
        String parentUuid;
        if(subjectNode!=null){
            parentUuid = subjectManager.getParentSubjectUuid(subjectNode.getUuid());
            if(parentUuid.equals("ASSET")||parentUuid.equals("LIABILITY")||parentUuid.equals("EQUITY")||parentUuid.equals("COST")||parentUuid.equals("PROFIT_LOSS")){//判断是否有父级科目
                return !isUsed();//如果没父级科目，判断是否被使用
            }else{
                return false;
            }
        }else {
            return false;
        }
    }

    public boolean isUsed(){
        Pair<List<SubjectNode>,SubjectNode> result = subjectManager.findUsedDirectChildren(subjectNode.getUuid());

        usedChildren = result.first;
        subjectNode = result.second;

        if (usedChildren.isEmpty()) {
            return (subjectNode.isUsed);//未占用，则返回false
        } else {
            System.out.println("被占用的直接子科目：");
            for (SubjectNode node : usedChildren) {
                System.out.println(" - " + node.name + " (UUID: " + node.uuid + ")");
            }
            return true;
        }
    }

    public boolean hasChildren(){
        List<SubjectNode> children = SubjectNode.buildSubjectTree(db, subjectNode.getUuid());
        return !children.isEmpty();
    }

    public MutableLiveData<Boolean> getNeedSave(){
        if((saveDirection||saveInitialAmount|| saveName) && legitimateSubjectName){
            needSave.setValue(true);
        }else{
            needSave.setValue(false);
        }
        return needSave;
    }


}