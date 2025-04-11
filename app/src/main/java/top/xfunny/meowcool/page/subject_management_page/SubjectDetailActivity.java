package top.xfunny.meowcool.page.subject_management_page;

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

import top.xfunny.meowcool.R;
import top.xfunny.meowcool.core.data.SubjectNode;

public class SubjectDetailActivity extends AppCompatActivity {
    private TextInputEditText editText;
    private TextInputLayout editTextLayout;
    private TextView initialAmountTextView = findViewById(R.id.subject_initial_account_amount);
    private MaterialButtonToggleGroup toggleGroup;
    private SubjectDetailViewModel detailViewModel;

    private SubjectNode subjectNode;

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

        subjectNode = detailViewModel.getSubjectNode();


        //detailViewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(SubjectDetailViewModel.class);

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

}