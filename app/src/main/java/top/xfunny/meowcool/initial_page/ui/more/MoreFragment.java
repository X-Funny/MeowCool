package top.xfunny.meowcool.initial_page.ui.more;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;


import java.util.concurrent.atomic.AtomicInteger;

import top.xfunny.meowcool.account_management_page.AccountSetManagementPageActivity;
import top.xfunny.meowcool.R;
import top.xfunny.meowcool.core.DatabaseManager;
import top.xfunny.meowcool.test_page.TestActivity;
import top.xfunny.meowcool.databinding.FragmentMoreBinding;
import top.xfunny.meowcool.initial_page.ui.home.HomeViewModel;

public class MoreFragment extends Fragment {
    private FragmentMoreBinding binding;

    @Override
    public void onResume() {
        super.onResume();
        refreshCurrentAccountTextview();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentMoreBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(R.string.fragment_more_title);

        //加载控件
        selectAccountButton(view);
        accountSetManagementCard(view);
        testPageCard(view);
        refreshCurrentAccountTextview();
    }

    private void selectAccountButton(View view) {
        Button button = view.findViewById(R.id.account_select);
        button.setOnClickListener(v -> {
            showDatabaseSelectDialog(requireContext());
        });
    }

    private void accountSetManagementCard(View view) {
        MaterialCardView button = view.findViewById(R.id.account_set_management);
        button.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(requireActivity(), AccountSetManagementPageActivity.class);
            startActivity(intent);
        });
    }

    private void testPageCard(View view) {
        LinearLayout testPageButton = view.findViewById(R.id.test_activity);
        testPageButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(requireActivity(), TestActivity.class);
            startActivity(intent);
        });
    }

    //选择账套对话框


public void showDatabaseSelectDialog(Context context) {
    // 获取数据库列表并去掉 .db 后缀
    String[] databaseList = DatabaseManager.getDatabaseList(context)
            .stream()
            .map(name -> name.replace(".db", "")) // 去掉 .db 后缀
            .toArray(String[]::new);

    String selectedDatabase = DatabaseManager.getSelectedDatabase(context).replace(".db", ""); // 处理选中项

    // 查找当前选中的数据库索引
    int initialSelectedIndex = -1;
    for (int i = 0; i < databaseList.length; i++) {
        if (databaseList[i].equals(selectedDatabase)) {
            initialSelectedIndex = i;
            break;
        }
    }

    // 如果数据库列表为空，提示用户并返回
    if (databaseList.length == 0) {
        Toast.makeText(context, "没有账套", Toast.LENGTH_SHORT).show();
        return;
    }

    // 使用 AtomicInteger 代替普通变量
    AtomicInteger selectedIndex = new AtomicInteger(initialSelectedIndex);

    // 构建单选对话框
    new MaterialAlertDialogBuilder(context)
            .setTitle(R.string.title_dailog_selectdatabase)
            .setSingleChoiceItems(databaseList, initialSelectedIndex, (dialog, which) -> {
                // 更新选中的索引
                selectedIndex.set(which);
            })
            .setPositiveButton(R.string.button_ok, (dialog, which) -> {
                if (selectedIndex.get() != -1) {
                    String selectedName = databaseList[selectedIndex.get()];
                    DatabaseManager.open(context, selectedName + ".db"); // 重新加上后缀
                    refreshCurrentAccountTextview();
                    Toast.makeText(context, context.getString(R.string.selected) + selectedName, Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton(R.string.button_cancel, (dialog, which) -> dialog.dismiss())
            .show();
}


    public void refreshCurrentAccountTextview() {
        //todo:获取最后记账时间
        TextView currentAccount = binding.currentAccount;
        String databaseName = DatabaseManager.getSelectedDatabase(requireContext());
        if (databaseName == null) {
            currentAccount.setText("请选择账套");
        } else {
            currentAccount.setText(databaseName.replace(".db", ""));
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
