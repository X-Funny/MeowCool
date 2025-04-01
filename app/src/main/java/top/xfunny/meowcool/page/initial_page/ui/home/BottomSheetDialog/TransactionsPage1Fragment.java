package top.xfunny.meowcool.page.initial_page.ui.home.BottomSheetDialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import top.xfunny.meowcool.R;

public class TransactionsPage1Fragment extends Fragment {

    private TransactionsItemCardAdapter adapter;
    private final List<EntryItem> itemList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                           @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        // 加载布局
        return inflater.inflate(R.layout.fragment_transactions_page1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 初始化RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext())); // 使用 requireContext() 获取 Context

        // 添加2条初始条目
        for (int i = 0; i < 2; i++) {
            itemList.add(new EntryItem());
        }

        adapter = new TransactionsItemCardAdapter(itemList,requireContext());
        recyclerView.setAdapter(adapter);

        view.findViewById(R.id.addTransactionItem).setOnClickListener(v -> {
            itemList.add(new EntryItem());
            adapter.notifyItemInserted(itemList.size() - 1);
        });
    }
}
