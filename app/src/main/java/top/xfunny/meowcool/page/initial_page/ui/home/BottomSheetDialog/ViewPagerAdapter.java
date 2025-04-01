package top.xfunny.meowcool.page.initial_page.ui.home.BottomSheetDialog;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private final int[] colors = {Color.RED, Color.BLUE, Color.GREEN};

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new AddTransactionsBottomSheetDialogFragment(); // 第一页显示计算器
        } //todo
        return null;
    }

    @Override
    public int getItemCount() {
        return colors.length + 1; // 总页数+1
    }

}
