package top.xfunny.meowcool.page.initial_page.ui.home;

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
import com.google.android.material.tabs.TabLayoutMediator;
import com.rd.PageIndicatorView;

import top.xfunny.meowcool.R;

public class AddTransactionsBottomSheetDialogFragment extends BottomSheetDialogFragment {
    private ViewPager2 viewPager;
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
        viewPager = view.findViewById(R.id.viewPager);
        pageIndicatorView = view.findViewById(R.id.pageIndicatorView);

        // 设置 Adapter
        viewPager.setAdapter(new PagerAdapter(this));

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
    private class PagerAdapter extends FragmentStateAdapter {
        public PagerAdapter(@NonNull Fragment fragment) {
            super(fragment); // 使用 Fragment 的 childFragmentManager
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return position == 0 ?
                    new TransactionsPage1Fragment() : // 第一页
                    new TransactionsPage2Fragment();  // 第二页
        }

        @Override
        public int getItemCount() {
            return 2; // 总页数
        }
    }
}
