package top.xfunny.meowcool.page.subject_management_page;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import top.xfunny.meowcool.R;
import top.xfunny.meowcool.core.DatabaseManager;
import top.xfunny.meowcool.core.SubjectManager;
import top.xfunny.meowcool.core.data.SettingsManager;
import top.xfunny.meowcool.core.data.SubjectNode;

public class PRsubjectAdapter extends RecyclerView.Adapter<PRsubjectAdapter.ViewHolder>{
    private List<String> subjectUuidList;
    private SubjectManager subjectManager;
    private SettingsManager settingsManager;

    public PRsubjectAdapter(List<String> subjectUuidList,Context context){
        this.subjectUuidList = subjectUuidList;
        subjectManager = new SubjectManager(DatabaseManager.openDatabase(context));
        settingsManager = new SettingsManager(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pr_subject, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(!subjectUuidList.isEmpty()){
            holder.textView.setText(subjectManager.findSubjectByUuid(subjectUuidList.get(position)).getName());
            holder.cardView.setOnLongClickListener(v -> {
                showDeleteMenu(v, position);
                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        return subjectUuidList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        MaterialCardView cardView;
        public ViewHolder(View itemView) {
            super(itemView);
            textView =  itemView.findViewById(R.id.subject_name);
            cardView = itemView.findViewById(R.id.card_view);
        }
    }

    private void showDeleteMenu(View view, int position) {
        // 创建 PopupMenu
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_delete_pr_subject, popupMenu.getMenu());

        // 设置菜单项点击事件
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_delete) {
                // 删除操作
                subjectUuidList.remove(position);
                settingsManager.updatePRSubject(subjectUuidList);
                notifyDataSetChanged(); // 刷新 RecyclerView
                return true;
            }
            return false;
        });

        // 显示菜单
        popupMenu.show();
    }
}
