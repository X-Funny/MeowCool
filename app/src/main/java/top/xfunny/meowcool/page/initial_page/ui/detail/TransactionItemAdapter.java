package top.xfunny.meowcool.page.initial_page.ui.detail;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import top.xfunny.meowcool.R;
import top.xfunny.meowcool.core.data.TransactionItem;

public class TransactionItemAdapter extends RecyclerView.Adapter<TransactionItemAdapter.ViewHolder> {
    private List<TransactionItem> transactionItemList;

    public TransactionItemAdapter(List<TransactionItem> transactionItemList) {
        this.transactionItemList = transactionItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.singe_transaction_card, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        TransactionItem transactionItem = transactionItemList.get(position);
        holder.summaryTextView.setText(transactionItem.getSummary());
        holder.amountTextView.setText("¥ " + transactionItem.getTotalAmount().toString());
        holder.dateTextView.setText(sdf.format(transactionItem.getTime()));
        holder.numberTextView.setText("记-" + transactionItem.getNumber());
    }

    @Override
    public int getItemCount() {
        return transactionItemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView summaryTextView;
        TextView amountTextView;
        TextView dateTextView;
        TextView numberTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            summaryTextView = itemView.findViewById(R.id.card_summary);
            amountTextView = itemView.findViewById(R.id.card_amount);
            dateTextView = itemView.findViewById(R.id.card_date);
            numberTextView = itemView.findViewById(R.id.card_transaction_number);
        }
    }
}
