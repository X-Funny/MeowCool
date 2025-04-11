package top.xfunny.meowcool.page.initial_page.ui.detail;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import top.xfunny.meowcool.R;
import top.xfunny.meowcool.core.DatabaseManager;
import top.xfunny.meowcool.core.TransactionManager;
import top.xfunny.meowcool.databinding.FragmentDetailBinding;
import top.xfunny.meowcool.page.initial_page.ui.home.HomeViewModel;

public class DetailFragment extends Fragment {
    private FragmentDetailBinding binding;
    private TransactionManager transactionManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDetailBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SQLiteDatabase db = DatabaseManager.openDatabase(requireContext());
        transactionManager = new TransactionManager(db);

        List<Long> dateList = transactionManager.getDateList();//todo:添加日期筛选功能

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(R.string.title_detail);

        // 日期筛选adapter
        RecyclerView dateFilterRecyclerView = view.findViewById(R.id.detail_recyclerView);
        dateFilterRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        DateFilterAdapter dateFilterAdapter = new DateFilterAdapter(dateList, requireContext(),db);
        dateFilterRecyclerView.setAdapter(dateFilterAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
