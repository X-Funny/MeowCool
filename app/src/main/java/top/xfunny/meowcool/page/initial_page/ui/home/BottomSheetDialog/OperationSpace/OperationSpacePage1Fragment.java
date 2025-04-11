package top.xfunny.meowcool.page.initial_page.ui.home.BottomSheetDialog.OperationSpace;

import android.content.Context;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import top.xfunny.meowcool.R;
import top.xfunny.meowcool.page.initial_page.ui.home.BottomSheetDialog.TransactionsViewModel;

public class OperationSpacePage1Fragment extends Fragment {
    private TransactionsViewModel viewModel;
    private Vibrator vibrator;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 获取 Vibrator 服务
        vibrator = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_operation_space_page1, container, false);

        viewModel = new ViewModelProvider(requireParentFragment()).get(TransactionsViewModel.class);

        // 设置按钮点击事件
        setButtonListeners(view);

        return view;
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
            viewModel.onNumberClick(number);
            vibrate();
        });
    }

    private void vibrate() {
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK));
        }
    }
}
