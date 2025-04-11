package top.xfunny.meowcool.page.initial_page.ui.home.BottomSheetDialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.rd.PageIndicatorView;

import top.xfunny.meowcool.R;
import top.xfunny.meowcool.page.initial_page.ui.home.BottomSheetDialog.OperationSpace.OperationSpacePage1Fragment;
import top.xfunny.meowcool.page.initial_page.ui.home.BottomSheetDialog.OperationSpace.OperationSpacePage2Fragment;
import top.xfunny.meowcool.page.initial_page.ui.home.BottomSheetDialog.TransactionsPage.TransactionsPage1Fragment;
import top.xfunny.meowcool.page.initial_page.ui.home.BottomSheetDialog.TransactionsPage.TransactionsPage2Fragment;

public class AddTransactionsBottomSheetDialogFragment extends BottomSheetDialogFragment {
    private PageIndicatorView pageIndicatorView;
    private ViewPager2 viewPager, operationSpaceViewPager;
    private TransactionsViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {// 加载布局
        return inflater.inflate(R.layout.modal_bottom_sheet_transactions_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(TransactionsViewModel.class);
        viewModel.initDefaultData();
        // 初始化 ViewPager2 和 PageIndicatorView
        viewPager = view.findViewById(R.id.viewPager);
        operationSpaceViewPager = view.findViewById(R.id.operationSpaceViewPager);

        pageIndicatorView = view.findViewById(R.id.pageIndicatorView);

        // 设置 Adapter
        viewPager.setAdapter(new PagerAdapter(this, 2, "upper"));
        operationSpaceViewPager.setAdapter(new PagerAdapter(this, 2, "lower"));

        operationSpaceViewPager.setCurrentItem(0);
        operationSpaceViewPager.setUserInputEnabled(false);

        // 设置 PageIndicatorView 的总页数
        pageIndicatorView.setCount(2);

        // 监听 ViewPager2 的页面变化
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                pageIndicatorView.setSelection(position); // 在页面切换时更新指示器
                viewModel.getUpperPage().setValue(position);

                viewPager.setUserInputEnabled(position != 1);

            }
        });

        viewModel.getLowerPage().observe(getViewLifecycleOwner(), page -> {
            operationSpaceViewPager.setCurrentItem(page);
        });

        viewModel.getUpperPage().observe(getViewLifecycleOwner(), page -> {
            viewPager.setCurrentItem(page);
        });
    }

    // 修改后的 onDestroyView 方法
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // 1. 清理ViewPager适配器
        if (viewPager != null) {
            viewPager.setAdapter(null);
        }
        if (operationSpaceViewPager != null) {
            operationSpaceViewPager.setAdapter(null);
        }

        // 2. 移除ViewModel观察者
        viewModel.getLowerPage().removeObservers(getViewLifecycleOwner());
        viewModel.getUpperPage().removeObservers(getViewLifecycleOwner());

        // 3. 显式销毁子Fragment（关键代码）
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        for (Fragment fragment : getChildFragmentManager().getFragments()) {
            transaction.remove(fragment);
        }
        transaction.commitNowAllowingStateLoss();

        viewModel.clearData();
    }


    // 嵌套 Fragment 需要使用的 Adapter
    private static class PagerAdapter extends FragmentStateAdapter {
        private final int pageCount;
        private final String id;


        public PagerAdapter(@NonNull Fragment fragment, int pageCount, String id) {
            super(fragment);
            this.pageCount = pageCount;
            this.id = id;

        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (id) {
                case "upper":
                    if (position == 0) {
                        return new TransactionsPage1Fragment();
                    } else {
                        return new TransactionsPage2Fragment();
                    }
                case "lower":
                    switch (position) {
                        case 0:
                            return new OperationSpacePage1Fragment();
                        case 1:
                            return new OperationSpacePage2Fragment();
                        default:
                            throw new IllegalArgumentException("Invalid position: " + position);
                    }
                default:
                    throw new IllegalArgumentException("Invalid id: " + id);
            }
        }

        @Override
        public int getItemCount() {
            return pageCount; // 总页数
        }
    }

}
