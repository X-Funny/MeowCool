package top.xfunny.meowcool.page.subject_management_page;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

import top.xfunny.meowcool.R;
import top.xfunny.meowcool.core.DatabaseManager;
import top.xfunny.meowcool.core.SubjectManager;
import top.xfunny.meowcool.core.data.SubjectNode;

public class SubjectDetailActivity extends AppCompatActivity {
    private TextInputEditText editText;
    private TextInputLayout editTextLayout;
    private TextView initialAmountTextView;
    private MaterialButtonToggleGroup toggleGroup;
    private SubjectDetailViewModel detailViewModel;
    private SubjectNode subjectNode;
    private SubjectManager subjectManager;
    private SQLiteDatabase db;
    private List<SubjectNode> usedChildren;

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

        db = DatabaseManager.openDatabase(this);
        subjectManager = new SubjectManager(db);

        subjectNode = (SubjectNode) getIntent().getSerializableExtra("node");

        checkUsed();
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
                String subjectName = s.toString();

            }
        });
    }

    private void initialAmountTextView(){
        initialAmountTextView = findViewById(R.id.subject_initial_account_amount);
    }

    private void toggleGroup(){
        toggleGroup = findViewById(R.id.setBalanceDirection);
    }

    private boolean isEditable(){
        String parentUuid;
        if(subjectNode!=null){
           parentUuid = subjectManager.getParentSubjectUuid(subjectNode.getUuid());
           if(parentUuid.equals("ASSET")||parentUuid.equals("LIABILITY")||parentUuid.equals("EQUITY")||parentUuid.equals("PROFIT_LOSS")){
               return true;// 如果是一级科目，可直接编辑
           }else{
               return false;
           }
        }else {
            return false;
        }
    }

    public void checkUsed(){
        usedChildren = subjectManager.findUsedDirectChildren(subjectNode.getUuid());
        if (usedChildren.isEmpty()) {
            System.out.println("该科目及其子科目未被占用");
        } else {
            System.out.println("被占用的直接子科目：");
            for (SubjectNode node : usedChildren) {
                System.out.println(" - " + node.name + " (UUID: " + node.uuid + ")");
            }
        }
    }
}