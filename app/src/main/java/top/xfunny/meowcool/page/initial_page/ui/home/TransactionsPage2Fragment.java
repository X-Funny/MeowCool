package top.xfunny.meowcool.page.initial_page.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import top.xfunny.meowcool.R;

public class TransactionsPage2Fragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // 加载你的表单布局
        return inflater.inflate(R.layout.fragment_transactions_page2, container, false);
    }
}
