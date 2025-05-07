package top.xfunny.meowcool.page.initial_page.ui.home.BottomSheetDialog.TransactionsPage;

import android.annotation.SuppressLint;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

import java.math.BigDecimal;
import java.util.List;

import top.xfunny.meowcool.R;
import top.xfunny.meowcool.core.DatabaseManager;
import top.xfunny.meowcool.core.TransactionManager;
import top.xfunny.meowcool.core.data.EntryItem;
import top.xfunny.meowcool.core.data.SubjectNode;
import top.xfunny.meowcool.page.initial_page.ui.home.BottomSheetDialog.CheckingStatus;
import top.xfunny.meowcool.page.initial_page.ui.home.BottomSheetDialog.TransactionsViewModel;

public class TransactionsPage2Fragment extends Fragment {
    TransactionsViewModel viewModel;
    SQLiteDatabase db;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // 加载布局
        return inflater.inflate(R.layout.fragment_transactions_page2, container, false);
    }

    @SuppressLint("SetTextI18n")
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireParentFragment()).get(TransactionsViewModel.class);
        viewModel.getUpperPage().observe(getViewLifecycleOwner(), page -> {
            if (page == 1) {
                trySaveTransactionItem(view, viewModel);
            }
        });

        db = DatabaseManager.openDatabase(requireContext());// 打开数据库
    }

    @SuppressLint("SetTextI18n")
    private void trySaveTransactionItem(View view, TransactionsViewModel viewModel) {
        ImageView ivResult = view.findViewById(R.id.result_imageView);
        TextView tvResult = view.findViewById(R.id.result_textView);
        MaterialButton btnOperation = view.findViewById(R.id.operation_button);


        List<EntryItem> itemList = viewModel.getItemList().getValue();


        // 检查交易条目
        CheckResult result = checkTransactionItem(itemList);
        if (!result.getStatus().equals(CheckingStatus.DONE)) {
            ivResult.setImageResource(R.drawable.rounded_close_24);
            String errorMessage;
            switch (result.getStatus()) {
                case NULL_AMOUNT:
                    errorMessage = "金额不能为空";
                    tvResult.setText("第" + result.getCheckingPosition() + 1 + "行遇到错误，原因是:" + errorMessage);
                    break;
                case NULL_SUBJECT:
                    errorMessage = "科目不能为空";
                    tvResult.setText("第" + result.getCheckingPosition() + 1 + "行遇到错误，原因是:" + errorMessage);
                    break;
                case NULL_DIRECTION:
                    errorMessage = "借贷方向不能为空，必须选择「借」或「贷」";
                    tvResult.setText("第" + result.getCheckingPosition() + 1 + "行遇到错误，原因是:" + errorMessage);
                    break;
                case NULL_TRANSACTION:
                    errorMessage = "交易条目不能为空";
                    tvResult.setText(errorMessage);
                    break;
                case NULL_SUMMARY:
                    errorMessage = "摘要不能为空";
                    tvResult.setText(errorMessage);
                    break;
                case UNBALANCED_ACCOUNTS:
                    errorMessage = "借贷不平";
                    tvResult.setText("遇到错误，原因是:" + errorMessage);
                    break;
            }
            btnOperation.setText("返回修改");

            btnOperation.setOnClickListener(v -> {
                viewModel.getUpperPage().setValue(0);
            });
        } else {
            addNewTransactionItem(itemList);
            ivResult.setImageResource(R.drawable.rounded_check_24);
            tvResult.setText("记账成功");

            btnOperation.setText("关闭");

            btnOperation.setOnClickListener(v -> {
                if (getParentFragment() instanceof BottomSheetDialogFragment) {
                    ((BottomSheetDialogFragment) getParentFragment()).dismiss();
                }
            });
        }
    }


    private CheckResult checkTransactionItem(List<EntryItem> itemList) {
        int checkingPosition = 0;
        BigDecimal creditGross = new BigDecimal(0);
        BigDecimal debitGross = new BigDecimal(0);

        if (itemList == null || itemList.isEmpty()) {
            return new CheckResult(checkingPosition, CheckingStatus.NULL_TRANSACTION);
        }

        if (viewModel.getSummary().getValue() == null) {
            return new CheckResult(checkingPosition, CheckingStatus.NULL_SUMMARY);
        }

        for (EntryItem item : itemList) {
            SubjectNode subject = item.getSubjectLiveData().getValue();
            BigDecimal amount = new BigDecimal(item.getAmount());
            Integer direction = item.getDirectionLiveData().getValue();

            if (subject != null || !amount.equals(BigDecimal.ZERO) || (direction != 0)) {
                if (subject == null) {
                    return new CheckResult(checkingPosition, CheckingStatus.NULL_SUBJECT);
                } else if (amount.equals(BigDecimal.ZERO)) {
                    return new CheckResult(checkingPosition, CheckingStatus.NULL_AMOUNT);
                } else if (direction == 0) {
                    return new CheckResult(checkingPosition, CheckingStatus.NULL_DIRECTION);
                }
            }

            if (direction == 1) {
                creditGross = creditGross.add(amount);
            } else if (direction == -1) {
                debitGross = debitGross.add(amount);
            }

            if (creditGross.equals(BigDecimal.ZERO) && debitGross.equals(BigDecimal.ZERO)) {
                return new CheckResult(checkingPosition, CheckingStatus.NULL_TRANSACTION);
            }
        }

        if (creditGross.equals(debitGross)) {
            return new CheckResult(checkingPosition, CheckingStatus.DONE);
        } else {
            return new CheckResult(checkingPosition, CheckingStatus.UNBALANCED_ACCOUNTS);
        }
    }

    public void addNewTransactionItem(List<EntryItem> itemList) {
        TransactionManager transactionManager = new TransactionManager(db);
        int maxRowCount = transactionManager.getLastRowColumnValue();
        for (EntryItem item : itemList) {
            String summary = viewModel.getSummary().getValue();
            Integer direction = item.getDirectionLiveData().getValue();
            BigDecimal amount = new BigDecimal(item.getAmount());
            SubjectNode subject = item.getSubjectLiveData().getValue();

            long currentTime = System.currentTimeMillis();

            assert subject != null;
            transactionManager.addNewTransaction(
                    currentTime,
                    maxRowCount + 1,
                    summary,
                    direction,
                    subject.getUuid(),
                    amount
            );
        }
    }

}
