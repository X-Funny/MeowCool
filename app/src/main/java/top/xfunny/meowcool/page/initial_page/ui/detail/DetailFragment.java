package top.xfunny.meowcool.page.initial_page.ui.detail;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import top.xfunny.meowcool.R;
import top.xfunny.meowcool.core.DatabaseManager;
import top.xfunny.meowcool.core.TransactionManager;
import top.xfunny.meowcool.databinding.FragmentDetailBinding;

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

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(R.string.title_detail);


        if(db != null){
            transactionManager = new TransactionManager(db);

            List<Long> dateList = transactionManager.getDateList();//todo:添加日期筛选功能

            // 日期筛选adapter
            RecyclerView dateFilterRecyclerView = view.findViewById(R.id.detail_recyclerView);
            dateFilterRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            DateFilterAdapter dateFilterAdapter = new DateFilterAdapter(dateList, requireContext(), db);
            dateFilterRecyclerView.setAdapter(dateFilterAdapter);
        }else{
            Toast.makeText(requireContext(), "请先打开账套", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
