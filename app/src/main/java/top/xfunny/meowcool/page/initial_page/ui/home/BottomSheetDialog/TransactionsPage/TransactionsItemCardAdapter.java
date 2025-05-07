package top.xfunny.meowcool.page.initial_page.ui.home.BottomSheetDialog.TransactionsPage;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButtonToggleGroup;

import top.xfunny.meowcool.R;
import top.xfunny.meowcool.core.data.EntryItem;
import top.xfunny.meowcool.page.initial_page.ui.home.BottomSheetDialog.TransactionsViewModel;

public class TransactionsItemCardAdapter extends RecyclerView.Adapter<TransactionsItemCardAdapter.EntryViewHolder> {
    private final Fragment fragment;
    private final TransactionsViewModel viewModel;


    public TransactionsItemCardAdapter(Fragment fragment, TransactionsViewModel viewModel) {// 构造方法
        this.fragment = fragment;
        this.viewModel = viewModel;
    }


    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(fragment.getContext())
                .inflate(R.layout.item_entry_card, parent, false);// 加载布局
        return new EntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
        subjectDirection(holder, position);
        amountTextView(holder, position);
        subjectTextView(holder, position);

        // 边框状态管理
        viewModel.getSelectedPosition().observe(fragment.getViewLifecycleOwner(), selectedPos -> {
            if (selectedPos != null && selectedPos.position == position) {
                // 根据控件类型执行不同操作
                switch (selectedPos.viewType) {
                    case "amount":
                        holder.amountTextView.setSelected(true);
                        holder.subjectTextView.setSelected(false);
                        break;
                    case "subject":
                        holder.subjectTextView.setSelected(true);
                        holder.amountTextView.setSelected(false);
                        break;
                    default:
                        holder.subjectTextView.setSelected(false);
                        holder.amountTextView.setSelected(false);
                        break;
                }
            } else {
                holder.amountTextView.setSelected(false);
                holder.subjectTextView.setSelected(false);
            }
        });


    }

    public void subjectDirection(EntryViewHolder holder, int position) {
        final int adapterPosition = position;
        final EntryItem item = viewModel.getItemList().getValue().get(adapterPosition);
        if (adapterPosition == RecyclerView.NO_POSITION) return;
        // 初始化方向选择状态
        switch (item.getDirectionLiveData().getValue()) {
            case -1:
                holder.toggleGroup.check(R.id.credit_button);
                break;
            case 1:
                holder.toggleGroup.check(R.id.debit_button);
                break;
            default:
                break;
        }

        holder.toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                int direction = checkedId == R.id.credit_button ? -1 : 1;
                viewModel.setSelectedPosition(adapterPosition, "subject_direction");
                viewModel.setSubjectDirection(direction);
            }
        });

    }

    public void subjectTextView(EntryViewHolder holder, int position) {
        final int adapterPosition = position;
        if (adapterPosition == RecyclerView.NO_POSITION) return;

        final EntryItem item = viewModel.getItemList().getValue().get(adapterPosition);
        // 清除旧Tag关联的观察者
        if (holder.subjectTextView.getTag() != null) {
            LiveData<String> oldLiveData = (LiveData<String>) holder.subjectTextView.getTag();
            oldLiveData.removeObservers(fragment.getViewLifecycleOwner());
        }

        holder.subjectTextView.setOnClickListener(v -> {
            viewModel.setSelectedPosition(adapterPosition, "subject");
            viewModel.setLowerPage(1);
        });

        // 设置当前LiveData到Tag
        holder.subjectTextView.setTag(item.getSubjectLiveData());

        if (item.getSubjectLiveData().getValue() != null) {
            holder.subjectTextView.setText(item.getSubjectLiveData().getValue().toString());
        }


        // 绑定观察者到ViewHolder自身生命周期
        item.getSubjectLiveData().observe(
                fragment.getViewLifecycleOwner(),
                subject -> {
                    if (item.getUuid().equals(holder.subjectTextView.getTag(R.id.item_uuid)) && subject != null) {
                        holder.subjectTextView.setText(subject.toString());
                    }
                }
        );


        // 设置UUID标记防止错乱
        holder.subjectTextView.setTag(R.id.item_uuid, item.getUuid());
    }

    @SuppressLint("SetTextI18n")
    public void amountTextView(EntryViewHolder holder, int position) {
        final int adapterPosition = position;
        if (adapterPosition == RecyclerView.NO_POSITION) return;

        final EntryItem item = viewModel.getItemList().getValue().get(adapterPosition);

        // 清除旧Tag关联的观察者
        if (holder.amountTextView.getTag() != null) {
            LiveData<String> oldLiveData = (LiveData<String>) holder.amountTextView.getTag();
            oldLiveData.removeObservers(fragment.getViewLifecycleOwner());
        }

        holder.amountTextView.setOnClickListener(v -> {
            viewModel.setSelectedPosition(adapterPosition, "amount");
            viewModel.setLowerPage(0);
        });

        // 设置当前LiveData到Tag
        holder.amountTextView.setTag(item.getAmountLiveData());

        // 初始化显示
        holder.amountTextView.setText("¥" + item.getAmount());

        // 绑定观察者到ViewHolder自身生命周期
        item.getAmountLiveData().observe(
                fragment.getViewLifecycleOwner(),
                amount -> {
                    if (item.getUuid().equals(holder.amountTextView.getTag(R.id.item_uuid))) {
                        holder.amountTextView.setText("¥" + amount);
                    }
                }
        );

        // 设置UUID标记防止错乱
        holder.amountTextView.setTag(R.id.item_uuid, item.getUuid());
    }

    @Override
    public int getItemCount() {// 获取条目数量,用于创建相应数量的卡片
        if (viewModel.getItemList().getValue() != null) {
            return viewModel.getItemList().getValue().size();
        }
        return 0;
    }

    public void updateItems() {
        notifyDataSetChanged();// 更新数据
    }

    public static class EntryViewHolder extends RecyclerView.ViewHolder {// ViewHolder
        MaterialButtonToggleGroup toggleGroup;
        TextView subjectTextView;
        TextView amountTextView;


        public EntryViewHolder(View itemView) {// 构造方法
            super(itemView);
            toggleGroup = itemView.findViewById(R.id.setBalanceDirection);
            subjectTextView = itemView.findViewById(R.id.setSubject);
            amountTextView = itemView.findViewById(R.id.setAmount);
        }
    }
}
