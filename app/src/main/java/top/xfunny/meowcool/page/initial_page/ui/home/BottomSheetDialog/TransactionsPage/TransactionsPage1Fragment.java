package top.xfunny.meowcool.page.initial_page.ui.home.BottomSheetDialog.TransactionsPage;

import android.annotation.SuppressLint;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Collections;
import java.util.List;

import top.xfunny.meowcool.R;
import top.xfunny.meowcool.core.DatabaseManager;
import top.xfunny.meowcool.core.TransactionManager;
import top.xfunny.meowcool.core.data.EntryItem;
import top.xfunny.meowcool.page.initial_page.ui.home.BottomSheetDialog.TransactionsViewModel;

public class TransactionsPage1Fragment extends Fragment {
    private TransactionsItemCardAdapter adapter;
    private TransactionsViewModel viewModel;
    private RecyclerView recyclerView;
    private TextInputEditText editText;
    private TextView transactionNumber;

    private ConstraintLayout rootLayout;

    private ItemTouchHelper itemTouchHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transactions_page1, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SQLiteDatabase db = DatabaseManager.openDatabase(requireContext());
        TransactionManager transactionManager = new TransactionManager(db);

        viewModel = new ViewModelProvider(requireParentFragment()).get(TransactionsViewModel.class);
        recyclerView = view.findViewById(R.id.recycleView);

        editText = view.findViewById(R.id.text_input_summary);
        transactionNumber = view.findViewById(R.id.transaction_number);

        transactionNumber.setText("记-"+(transactionManager.getLastRowColumnValue()+1));

        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setRemoveDuration(250); // 默认是120ms，可根据需求设置
        animator.setMoveDuration(250);   // 卡片补齐是移动动画
        recyclerView.setItemAnimator(animator);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new TransactionsItemCardAdapter(this, viewModel);
        recyclerView.setAdapter(adapter);

        // 获取 ConstraintLayout 用于过渡动画
        rootLayout = view.findViewById(R.id.rootConstraintLayout);

        view.findViewById(R.id.addTransactionItem).setOnClickListener(v -> {
            viewModel.addNewTransactionItem();
            // 启动过渡动画
            if (rootLayout != null) {
                TransitionManager.beginDelayedTransition(rootLayout, new AutoTransition());
            }
        });

        viewModel.getItemList().observe(getViewLifecycleOwner(), newItems -> {
            adapter.notifyItemChanged(0, newItems.size());
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.getSummary().postValue(s.toString());
            }
        });

        setupItemTouchHelper();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (recyclerView != null) {
            recyclerView.setAdapter(null); // 清除RecyclerView引用
        }
        if (itemTouchHelper != null) {
            itemTouchHelper.attachToRecyclerView(null); // 解绑触摸帮助类
        }
    }


    private void setupItemTouchHelper() {
        ItemTouchHelper.Callback callback = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                int swipeFlags = ItemTouchHelper.RIGHT;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder source, @NonNull RecyclerView.ViewHolder target) {
                int fromPos = source.getBindingAdapterPosition();
                int toPos = target.getBindingAdapterPosition();

                List<EntryItem> items = viewModel.getItemList().getValue();
                if (items != null && fromPos != toPos) {
                    Collections.swap(items, fromPos, toPos);
                    adapter.notifyItemMoved(fromPos, toPos);
                }
                return true;
            }

            @Override
            public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
                super.onSelectedChanged(viewHolder, actionState);
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                viewHolder.itemView.setAlpha(1f);
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    viewModel.removeTransactionItem(position);
                    adapter.notifyItemRemoved(position);

                    rootLayout.requestLayout();
                    TransitionManager.beginDelayedTransition(
                            rootLayout,
                            new AutoTransition()
                                    .setDuration(260)
                                    .excludeTarget(recyclerView, true)
                    );
                }
            }

        };

        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
}
