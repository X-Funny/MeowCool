package top.xfunny.meowcool.page.subject_management_page;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import top.xfunny.meowcool.R;

public class InitialAmountNumberKeyboard extends BottomSheetDialogFragment {
    private Vibrator vibrator;
    private SubjectDetailViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 初始化Vibrator
        vibrator = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_initial_amount_keyboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(SubjectDetailViewModel.class);

        setButtonListeners(view);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
            View bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);

            if (bottomSheet != null) {
                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setSkipCollapsed(true);

                Window window = bottomSheetDialog.getWindow();
                if (window != null) {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                }
            }
        });

        return dialog;
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
            vibrate();
            viewModel.onNumberClick(number);
        });
    }

    private void vibrate() {
        try {
            if (vibrator != null && vibrator.hasVibrator()) {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}