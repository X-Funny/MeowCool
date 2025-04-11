package top.xfunny.meowcool.page.subject_management_page;

import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import top.xfunny.meowcool.R;

public class InitialAmountNumberKeyboard extends BottomSheetDialogFragment {
    private Vibrator vibrator;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.modal_bottom_sheet_initial_amount_keyboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setButtonListeners(view);
    }

    private void setButtonListeners(View view) {
        setButtonListener(view, R.id.btn_0, "0");
        setButtonListener(view, R.id.btn_1, "1");
        setButtonListener(view, R.id.btn_2, "2");
        setButtonListener(view, R.id.btn_3, "3");
        setButtonListener(view, R.id.btn_4, "4");
        setButtonListener(view, R.id.btn_5, "5");
        setButtonListener(view, R.id.btn_6, "6");
        setButtonListener(view, R.id.btn_7, "7");
        setButtonListener(view, R.id.btn_8, "8");
        setButtonListener(view, R.id.btn_9, "9");
        setButtonListener(view, R.id.btn_dot, ".");
        setButtonListener(view, R.id.btn_backspace, "del");
    }

    private void setButtonListener(View view, int buttonId, String number) {
        view.findViewById(buttonId).setOnClickListener(v -> {
            //viewModel.onNumberClick(number);
            vibrate();
        });
    }

    private void vibrate() {
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK));
        }
    }
}
