package top.xfunny.meowcool.page.subject_management_page;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import top.xfunny.meowcool.R;
import top.xfunny.meowcool.core.DatabaseManager;
import top.xfunny.meowcool.core.TransactionManager;
import top.xfunny.meowcool.core.data.SubjectNode;
import top.xfunny.meowcool.page.initial_page.ui.home.BottomSheetDialog.TransactionsViewModel;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder> {
    private final Context context;
    private final List<SubjectNode> visibleNodes;
    private final SubjectAdapterMode mode;
    private TransactionsViewModel transactionsViewModel;



    public SubjectAdapter(Context context, SubjectAdapterMode mode) {// 编辑模式
        this.context = context;
        this.visibleNodes = new ArrayList<>();
        this.mode = mode;


    }

    public SubjectAdapter(Context context, SubjectAdapterMode mode, TransactionsViewModel transactionsViewModel) {
        this.context = context;
        this.visibleNodes = new ArrayList<>();
        this.mode = mode;
        this.transactionsViewModel = transactionsViewModel;
    }

    public void setData(List<SubjectNode> rootNodes) {
        visibleNodes.clear();
        flattenTree(rootNodes, 0);
        notifyDataSetChanged();
    }

    /**
     * 将树形结构的节点展平为列表
     * 此方法用于遍历树形结构的节点，并将它们展平为一个列表，同时记录每个节点所在的层级
     * 主要用于在用户界面中以列表形式展示树形结构的数据，便于用户浏览
     *
     * @param nodes 树形结构的节点列表，代表待展平的节点
     * @param level 当前节点所在的层级，根节点层级为0，每向下一层递增1
     */
    private void flattenTree(List<SubjectNode> nodes, int level) {
        for (SubjectNode node : nodes) {
            // 设置当前节点的层级
            node.level = level;
            // 将当前节点添加到可见节点列表中
            visibleNodes.add(node);
            // 如果当前节点被展开，则递归展平其子节点
            if (node.isExpanded) {
                flattenTree(node.children, level + 1);
            }
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        Space spaceIndent;
        ImageView ivExpand;
        TextView tvSubjectName;
        TextView tvSubject_amount;

        public ViewHolder(View itemView) {
            super(itemView);
            spaceIndent = itemView.findViewById(R.id.spaceIndent);
            ivExpand = itemView.findViewById(R.id.ivExpand);
            tvSubjectName = itemView.findViewById(R.id.tvSubjectName);
            tvSubject_amount = itemView.findViewById(R.id.tvSubject_amount);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_subject, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        TransactionManager transactionManager = new TransactionManager(DatabaseManager.openDatabase(context));
        // 获取当前位置的科目节点
        SubjectNode node = visibleNodes.get(position);

        // 根据节点层级计算缩进宽度
        int indentPx = (int) (node.level * 16 * context.getResources().getDisplayMetrics().density);
        holder.spaceIndent.getLayoutParams().width = indentPx;

        // 设置科目名称
        holder.tvSubjectName.setText(node.name);

        // 处理展开/折叠图标
        if (!node.children.isEmpty()) {// 如果该科目有子节点
            holder.ivExpand.setVisibility(View.VISIBLE);
            // 根据展开状态设置不同图标
            if (node.isExpanded) {
                holder.ivExpand.setImageResource(R.drawable.keyboard_arrow_down_24px);
            } else {
                holder.ivExpand.setImageResource(R.drawable.keyboard_arrow_right_24px);
            }

            // 设置图标点击事件，用于展开或折叠科目
            holder.ivExpand.setOnClickListener(v -> {
                node.isExpanded = !node.isExpanded;
                int currentPosition = holder.getAdapterPosition();

                // 展开子节点
                if (node.isExpanded) {
                    int insertPosition = currentPosition + 1;
                    int insertCount = flattenSubNodes(node.children, node.level + 1, insertPosition);
                    notifyItemRangeInserted(insertPosition, insertCount);
                } else {
                    // 折叠子节点
                    int removeCount = countCollapsedNodes(node);
                    visibleNodes.subList(currentPosition + 1, currentPosition + 1 + removeCount).clear();
                    notifyItemRangeRemoved(currentPosition + 1, removeCount);
                }
                // 更新当前项的箭头图标
                notifyItemChanged(currentPosition);
            });

            // 当有子节点时：禁用条目点击，启用展开图标点击
            if (mode == SubjectAdapterMode.MODE_SELECT) {
                holder.itemView.setClickable(false);
                holder.itemView.setBackgroundResource(0); // 移除点击背景
            } else {
                holder.itemView.setClickable(true);
                holder.itemView.setOnClickListener(v -> {
                    SubjectNode currentNode = visibleNodes.get(position);
                    Intent intent = new Intent(context, SubjectDetailActivity.class);
                    intent.putExtra("node", currentNode);
                    ((AppCompatActivity) context).startActivityForResult(intent, 1001);
                });
            }

            holder.ivExpand.setClickable(true);
        } else {
            BigDecimal balance = new BigDecimal(0);
            if(node.getDirection()==1){
                balance = balance.add(transactionManager.getTransactionDebit(node.getUuid())).subtract(transactionManager.getTransactionCredit(node.getUuid()));
            }else if(node.getDirection()==-1){
                balance = balance.subtract(transactionManager.getTransactionDebit(node.getUuid())).add(transactionManager.getTransactionCredit(node.getUuid()));
            }

            holder.tvSubject_amount.setText("¥ "+balance.toString());

            // 如果没有子节点，隐藏展开/折叠图标
            holder.ivExpand.setVisibility(View.GONE);

            holder.itemView.setClickable(true);

            // 设置条目点击监听
            holder.itemView.setOnClickListener(v -> {
                // 这里添加条目点击后的业务逻辑（科目管理界面、科目选择界面）
                SubjectNode currentNode = visibleNodes.get(position);
                Log.d("SubjectAdapter", "Item clicked: " + currentNode.name);
                if (mode == SubjectAdapterMode.MODE_SELECT) {
                    // 传入所选中的subject node
                    transactionsViewModel.selectSubject(node);
                }else{
                    Intent intent = new Intent(context, SubjectDetailActivity.class);
                    intent.putExtra("node", currentNode);
                    context.startActivity(intent);
                }
            });
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
}