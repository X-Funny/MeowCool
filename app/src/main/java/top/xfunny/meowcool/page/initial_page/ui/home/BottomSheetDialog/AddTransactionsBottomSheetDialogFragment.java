package top.xfunny.meowcool.page.initial_page.ui.home.BottomSheetDialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.rd.PageIndicatorView;

import top.xfunny.meowcool.R;

public class AddTransactionsBottomSheetDialogFragment extends BottomSheetDialogFragment {
    private PageIndicatorView pageIndicatorView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.modal_bottom_sheet_transactions_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 初始化 ViewPager2 和 PageIndicatorView
        ViewPager2 viewPager = view.findViewById(R.id.viewPager);
        ViewPager2 operationSpaceViewPager = view.findViewById(R.id.operationSpaceViewPager);

        pageIndicatorView = view.findViewById(R.id.pageIndicatorView);

        // 设置 Adapter
        viewPager.setAdapter(new PagerAdapter(this, 2, "upper"));
        operationSpaceViewPager.setAdapter(new PagerAdapter(this, 3, "lower"));

        operationSpaceViewPager.setCurrentItem(1);
        operationSpaceViewPager.setUserInputEnabled(false);

        // 设置 PageIndicatorView 的总页数
        pageIndicatorView.setCount(2);

        // 监听 ViewPager2 的页面变化
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                pageIndicatorView.setSelection(position); // 在页面切换时更新指示器
            }
        });

    }

    // 嵌套 Fragment 需要使用的 Adapter
    private static class PagerAdapter extends FragmentStateAdapter {
        private final int pageCount;
        private final String id;

        public PagerAdapter(@NonNull Fragment fragment, int pageCount, String id) {
            super(fragment); // 使用 Fragment 的 childFragmentManager
            this.pageCount = pageCount;
            this.id = id;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (id) {
                case "upper":
                    return position == 0 ? new TransactionsPage1Fragment() : new TransactionsPage2Fragment();
                case "lower":
                    switch (position) {
                        case 0:
                            return new OperationSpacePage1Fragment();
                        case 1:
                            return new OperationSpacePage2Fragment();
                        case 2:
                            return new OperationSpacePage3Fragment();
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
