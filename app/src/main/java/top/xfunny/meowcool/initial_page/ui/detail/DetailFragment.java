package top.xfunny.meowcool.initial_page.ui.detail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import top.xfunny.meowcool.databinding.FragmentDetailBinding;
import top.xfunny.meowcool.initial_page.ui.home.HomeViewModel;

public class DetailFragment extends Fragment {
    private FragmentDetailBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentDetailBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textDetail;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
