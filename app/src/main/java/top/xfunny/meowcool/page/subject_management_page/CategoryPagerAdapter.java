package top.xfunny.meowcool.page.subject_management_page;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;


public class CategoryPagerAdapter extends FragmentStateAdapter {
    private final String[] categories = {"ASSET", "LIABILITY", "EQUITY", "COST", "PROFIT_LOSS"};
    private final SubjectAdapterMode mode;
    private final Fragment parentFragment;


    public CategoryPagerAdapter(@NonNull  Fragment fragment, SubjectAdapterMode mode) {
        super(fragment);
        this.mode = mode;
        this.parentFragment = fragment;
    }

    public CategoryPagerAdapter(@NonNull  FragmentActivity fa, SubjectAdapterMode mode) {
        super(fa);
        this.mode = mode;
        this.parentFragment = null;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        CategoryFragment fragment = CategoryFragment.newInstance(categories[position], mode);
        // 设置父 Fragment（关键）
        if (parentFragment != null) {
            fragment.setParentFragment(parentFragment);
        }
        return fragment;
    }

    @Override
    public int getItemCount() {
        return categories.length;
    }

}
