package top.xfunny.meowcool.page.subject_management_page;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

import top.xfunny.meowcool.R;
import top.xfunny.meowcool.core.DatabaseManager;
import top.xfunny.meowcool.core.data.SubjectNode;
import top.xfunny.meowcool.page.initial_page.ui.home.BottomSheetDialog.TransactionsViewModel;


public class CategoryFragment extends Fragment {
    private static final String ARG_CATEGORY = "category";
    public TransactionsViewModel transactionsViewModel;
    private SubjectAdapter adapter;
    private SubjectManagementViewModel viewModel;
    private Fragment parentFragment;

    public static CategoryFragment newInstance(String category, SubjectAdapterMode mode) {//创建fragment实例
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY, category);
        args.putSerializable("mode", mode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(SubjectManagementViewModel.class);


        transactionsViewModel = Objects.equals(requireArguments().getSerializable("mode"), SubjectAdapterMode.MODE_SELECT)?
                new ViewModelProvider(parentFragment).get(TransactionsViewModel.class) :
                null;

        RecyclerView recyclerView = view.findViewById(R.id.subject_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = Objects.equals(requireArguments().getSerializable("mode"), SubjectAdapterMode.MODE_SELECT) ?
                new SubjectAdapter(requireContext(), (SubjectAdapterMode) requireArguments().getSerializable("mode"), transactionsViewModel) :
                new SubjectAdapter(requireContext(),  (SubjectAdapterMode)requireArguments().getSerializable("mode"));

        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                rv.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        loadData();
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String categoryId = requireArguments().getString(ARG_CATEGORY);
        switch (Objects.requireNonNull(categoryId)) {
            case "ASSET":
                viewModel.getNeedAssetRefresh().observe(getViewLifecycleOwner(), refresh -> {
                    if (refresh) {
                        viewModel.setNeedAssetRefresh(false);
                        loadData();
                    }
                });
                break;
            case "LIABILITY":
                viewModel.getNeedLiabilityRefresh().observe(getViewLifecycleOwner(), refresh -> {
                    if (refresh) {
                        viewModel.setNeedLiabilityRefresh(false);
                        loadData();
                    }
                });
                break;
            case "EQUITY":
                viewModel.getNeedEquityRefresh().observe(getViewLifecycleOwner(), refresh -> {
                    if (refresh) {
                        viewModel.setNeedEquityRefresh(false);
                        loadData();
                    }
                });
                break;
            case "COST":
                viewModel.getNeedCostRefresh().observe(getViewLifecycleOwner(), refresh -> {
                    if (refresh) {
                        viewModel.setNeedCostRefresh(false);
                        loadData();
                    }
                });
                break;
            case "PROFIT_LOSS":
                viewModel.getNeedProfitLossRefresh().observe(getViewLifecycleOwner(), refresh -> {
                    if (refresh) {
                        viewModel.setNeedProfitLossRefresh(false);
                        loadData();
                    }
                });
                break;
        }
    }

    private void loadData() {
        String categoryId = requireArguments().getString(ARG_CATEGORY);
        System.out.println("加载数据" + categoryId);
        SQLiteDatabase db = DatabaseManager.openDatabase(getContext());
        List<SubjectNode> nodes = SubjectNode.buildSubjectTree(db, categoryId);
        adapter.setData(nodes);
    }

    public void setParentFragment(Fragment parentFragment) {
        this.parentFragment = parentFragment;
    }

}



