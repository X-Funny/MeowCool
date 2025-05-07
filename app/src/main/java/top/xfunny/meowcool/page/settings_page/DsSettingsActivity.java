package top.xfunny.meowcool.page.settings_page;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import top.xfunny.meowcool.R;
import top.xfunny.meowcool.core.data.SettingsManager;

public class DsSettingsActivity extends AppCompatActivity {
    private TextInputEditText editText;
    private TextInputLayout editTextLayout;
    private SettingsManager settingsManager;
    private Toolbar toolbar;

    String apiKey;
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ds_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        settingsManager = new SettingsManager(this);

        toolbar();
        editText();
        saveButton();
    }

    private void editText() {
        editText = findViewById(R.id.api_edit_text);
        editTextLayout = findViewById(R.id.api_input_layout);

        editText.setText(settingsManager.getApiKey());
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String key = s.toString();
                apiKey = key;
            }
        });
    }

    private void saveButton() {
        saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(v -> {
            settingsManager.setApiKey(apiKey);
            Snackbar.make(v, "保存成功", Snackbar.LENGTH_SHORT).show();
        });
    }

    private void toolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            onBackPressed();
        });
    }
}