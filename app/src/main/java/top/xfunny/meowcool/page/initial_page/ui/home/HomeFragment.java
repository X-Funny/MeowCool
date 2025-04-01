package top.xfunny.meowcool.page.initial_page.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import top.xfunny.meowcool.databinding.FragmentHomeBinding;
import top.xfunny.meowcool.page.initial_page.ui.home.BottomSheetDialog.AddTransactionsBottomSheetDialogFragment;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        fab();


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void fab() {
        ExtendedFloatingActionButton fab = binding.fabAdd;
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(
                v -> {
                    AddTransactionsBottomSheetDialogFragment bottomSheet = new AddTransactionsBottomSheetDialogFragment();
                    bottomSheet.show(getChildFragmentManager(), "AddTransactionsBottomSheetDialogFragment");
                }
        );
    }
}