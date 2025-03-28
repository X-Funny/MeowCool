package top.xfunny.meowcool.page.subject_management_page;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import top.xfunny.meowcool.R;
import top.xfunny.meowcool.core.subject.SubjectNode;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder>{
    private List<SubjectNode> visibleNodes;
    private final Context context;

    public SubjectAdapter(Context context) {
        this.context = context;
        this.visibleNodes = new ArrayList<>();
    }

    public void setData(List<SubjectNode> rootNodes) {
        visibleNodes.clear();
        flattenTree(rootNodes, 0);
        notifyDataSetChanged();
    }

    private void flattenTree(List<SubjectNode> nodes, int level) {
        for (SubjectNode node : nodes) {
            node.level = level;
            visibleNodes.add(node);
            if (node.isExpanded) {
                flattenTree(node.children, level + 1);
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_subject, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SubjectNode node = visibleNodes.get(position);

        // 设置缩进
        int indentPx = (int) (node.level * 16 * context.getResources().getDisplayMetrics().density);
        holder.spaceIndent.getLayoutParams().width = indentPx;

        // 科目名称
        holder.tvSubjectName.setText(node.name);

        // 展开/折叠图标处理
        if (!node.children.isEmpty()) {
            holder.ivExpand.setVisibility(View.VISIBLE);
            // 根据展开状态设置不同图标
            if (node.isExpanded) {
                holder.ivExpand.setImageResource(R.drawable.keyboard_arrow_down_24px);
            } else {
                holder.ivExpand.setImageResource(R.drawable.keyboard_arrow_right_24px);
            }

            holder.ivExpand.setOnClickListener(v -> {
                node.isExpanded = !node.isExpanded;
                int currentPosition = holder.getAdapterPosition();

                if (node.isExpanded) {
                    int insertPosition = currentPosition + 1;
                    int insertCount = flattenSubNodes(node.children, node.level + 1, insertPosition);
                    notifyItemRangeInserted(insertPosition, insertCount);
                } else {
                    int removeCount = countCollapsedNodes(node);
                    visibleNodes.subList(currentPosition + 1, currentPosition + 1 + removeCount).clear();
                    notifyItemRangeRemoved(currentPosition + 1, removeCount);
                }
                // 更新当前项的箭头图标
                notifyItemChanged(currentPosition);
            });
        } else {
            holder.ivExpand.setVisibility(View.GONE);
        }
    }

    private int flattenSubNodes(List<SubjectNode> nodes, int level, int insertPosition) {
        int count = 0;
        for (SubjectNode n : nodes) {
            n.level = level;
            visibleNodes.add(insertPosition + count, n);
            count++;
            if (n.isExpanded) {
                count += flattenSubNodes(n.children, level + 1, insertPosition + count);
            }
        }
        return count;
    }

    private int countCollapsedNodes(SubjectNode node) {
        int count = 0;
        for (SubjectNode child : node.children) {
            count++;
            if (child.isExpanded) {
                count += countCollapsedNodes(child);
            }
        }
        return count;
    }

    @Override
    public int getItemCount() {
        return visibleNodes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        Space spaceIndent;
        ImageView ivExpand;
        TextView tvSubjectName;

        public ViewHolder(View itemView) {
            super(itemView);
            spaceIndent = itemView.findViewById(R.id.spaceIndent);
            ivExpand = itemView.findViewById(R.id.ivExpand);
            tvSubjectName = itemView.findViewById(R.id.tvSubjectName);
        }
    }
}