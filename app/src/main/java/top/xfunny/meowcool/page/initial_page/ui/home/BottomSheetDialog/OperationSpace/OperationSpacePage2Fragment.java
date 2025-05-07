package top.xfunny.meowcool.page.initial_page.ui.home.BottomSheetDialog.OperationSpace;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import top.xfunny.meowcool.R;
import top.xfunny.meowcool.page.subject_management_page.CategoryPagerAdapter;
import top.xfunny.meowcool.page.subject_management_page.SubjectAdapterMode;


public class OperationSpacePage2Fragment extends Fragment {
    private ViewPager2 viewPager;
    private TabLayout tabLayout;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_operation_space_page2, container, false);

        viewPager = view.findViewById(R.id.viewPager1);
        tabLayout = view.findViewById(R.id.tabLayout1);

        CategoryPagerAdapter pagerAdapter = new CategoryPagerAdapter(this.requireParentFragment(), SubjectAdapterMode.MODE_SELECT);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            String[] titles = {"资产", "负债", "净资产", "成本", "损益"};
            tab.setText(titles[position]);
        }).attach();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (viewPager != null) {
            viewPager.setAdapter(null);
            tabLayout.removeAllTabs();
        }
    }

}