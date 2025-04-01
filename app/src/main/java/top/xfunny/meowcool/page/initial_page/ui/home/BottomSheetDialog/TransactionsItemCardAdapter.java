package top.xfunny.meowcool.page.initial_page.ui.home.BottomSheetDialog;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import top.xfunny.meowcool.R;

public class TransactionsItemCardAdapter extends RecyclerView.Adapter<TransactionsItemCardAdapter.EntryViewHolder>{
    private List<EntryItem> items;
    private Context context;

    public TransactionsItemCardAdapter(List<EntryItem> items, Context context) {
        this.items = items;
        this.context = context;
    }

    public static class EntryViewHolder extends RecyclerView.ViewHolder {
        MaterialButtonToggleGroup toggleGroup;
        TextInputEditText subjectEditText;
        TextInputEditText amountEditText;

        public EntryViewHolder(View itemView) {
            //绑定部件
            super(itemView);
            toggleGroup = itemView.findViewById(R.id.setBalanceDirection);
            subjectEditText = itemView.findViewById(R.id.setSubject);
            amountEditText = itemView.findViewById(R.id.setAmount);
        }
    }

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_entry_card, parent, false);
        return new EntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
        int adapterPosition = holder.getBindingAdapterPosition();
        if (adapterPosition == RecyclerView.NO_POSITION) {
            return; // 如果位置无效，直接返回
        }

        EntryItem item = items.get(adapterPosition);

        // 绑定数据到组件
        int checkedButtonId = item.getDirection() == 1 ? R.id.credit_button : R.id.debit_button;
        holder.toggleGroup.check(checkedButtonId); // 使用正确的按钮 ID
        holder.subjectEditText.setText(item.getSubject());
        holder.amountEditText.setText(item.getAmount());

        // 监听按钮组变化
        holder.toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                items.get(adapterPosition).setDirection(checkedId);
            }
        });

        // 监听科目输入
        holder.subjectEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                items.get(adapterPosition).setSubject(s.toString());
            }
        });

        // 监听金额输入
        holder.amountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                items.get(adapterPosition).setAmount(s.toString());
            }
        });
    }


    @Override
    public int getItemCount() {
        return items.size();
    }
}
