package top.xfunny.meowcool.page.initial_page.ui.detail;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import top.xfunny.meowcool.R;
import top.xfunny.meowcool.core.TransactionManager;
import top.xfunny.meowcool.utils.FormatMonth;

public class DateFilterAdapter extends RecyclerView.Adapter<DateFilterAdapter.ViewHolder> {
    private final List<Long> dateList;
    private Context context;
    private SQLiteDatabase db;


    public DateFilterAdapter(List<Long> dateList, Context context,SQLiteDatabase db) {
        this.dateList = dateList;
        this.context = context;
        this.db = db;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateMonthTextView;
        TextView dateDayTextView;
        RecyclerView transactionsRecyclerView;
        public ViewHolder(View itemView) {
            super(itemView);
            dateMonthTextView = itemView.findViewById(R.id.date_month);
            dateDayTextView = itemView.findViewById(R.id.date_day);
            transactionsRecyclerView = itemView.findViewById(R.id.transaction_recyclerView);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_day_account_details, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TransactionManager transactionManager = new TransactionManager(db);
        SimpleDateFormat sdfm = new SimpleDateFormat("MM", Locale.getDefault());
        SimpleDateFormat sdfd = new SimpleDateFormat("dd", Locale.getDefault());
        holder.dateMonthTextView.setText(FormatMonth.format(sdfm.format(new Date(dateList.get(position)))));
        holder.dateDayTextView.setText(sdfd.format(new Date(dateList.get(position))));

        holder.transactionsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        TransactionItemAdapter transactionItemAdapter = new TransactionItemAdapter(transactionManager.getTransactionList(dateList.get(position)));
        holder.transactionsRecyclerView.setAdapter(transactionItemAdapter);
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }
}
