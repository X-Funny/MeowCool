package top.xfunny.meowcool.account_management_page;

import static androidx.appcompat.widget.TintTypedArray.obtainStyledAttributes;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.card.MaterialCardView;

import java.util.List;
import java.util.Objects;

import top.xfunny.meowcool.R;

public class AccountCardAdapter extends ArrayAdapter<String> {

    private final LayoutInflater inflater;
    private final List<String> createTimes; // 保存创建时间的列表
    private final OnCardClickListener onCardClickListener; // 接口回调
    private final SparseBooleanArray selectedItems = new SparseBooleanArray(); // 用于保存选中状态
    private boolean isMultiSelectMode = false; // 是否处于多选模式


    // 构造方法中接收 OnCardClickListener 接口
    public AccountCardAdapter(@NonNull Context context, @NonNull List<String> titles, @NonNull List<String> createTimes, OnCardClickListener onCardClickListener) {
        super(context, 0, titles);
        this.inflater = LayoutInflater.from(context);
        this.createTimes = createTimes;
        this.onCardClickListener = onCardClickListener; // 初始化回调接口
    }

    // 定义接口
    public interface OnCardClickListener {
        void onCardClick(String accountName);

        void onMultiSelectModeEnter();

        void onMultiSelectModeExit();
    }

    //多选模式
    public void setMultiSelectMode(boolean enabled, LinearLayout linearLayout) {
        isMultiSelectMode = enabled;

        if (!enabled) {
            // 清空选中状态记录
            selectedItems.clear();

            // 遍历 LinearLayout 的所有子视图
            for (int i = 0; i < linearLayout.getChildCount(); i++) {
                View childView = linearLayout.getChildAt(i);
                if (childView != null) {
                    MaterialCardView cardView = childView.findViewById(R.id.account_card);
                    if (cardView != null) {
                        cardView.setChecked(false); // 取消选中状态
                    }
                }
            }
        }

        notifyDataSetChanged();
    }

    /**
     * 切换给定位置的条目是否选中
     * 如果条目已经选中，则取消选中；如果未选中，则选中
     *
     * @param position 条目的位置
     * @return 返回选中状态是否为真
     */
    public boolean toggleSelection(int position, MaterialCardView cardView) {
        // 检查条目是否已选中
        if (selectedItems.get(position, false)) {
            // 如果已选中，则删除（取消选中）
            selectedItems.delete(position);
            cardView.setChecked(false);
        } else {
            // 如果未选中，则添加（选中）
            selectedItems.put(position, true);
            cardView.setChecked(true);
        }
        // 通知数据集已更改
        notifyDataSetChanged();
        // 返回选中状态是否为真
        return selectedItems.size() > 0;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Reuse the view if possible
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.account_card, parent, false);
        }

        // Get the data items
        String title = Objects.requireNonNull(getItem(position)).substring(0, Objects.requireNonNull(getItem(position)).length() - 3); // 获取标题
        String createTime = createTimes.get(position); // 获取对应的创建时间

        // views
        TextView titleView = convertView.findViewById(R.id.account_card_title);
        TextView subtitleView = convertView.findViewById(R.id.account_create_time_sub_title);
        MaterialCardView cardView = convertView.findViewById(R.id.account_card);

        // Set text content
        titleView.setText(title);
        subtitleView.setText("创建时间: " + createTime); // 显示创建时间作为副标题

        //点击事件
        cardView.setOnClickListener(v -> {
            if (isMultiSelectMode) {
                boolean hasSelections = toggleSelection(position, cardView);

                if (!hasSelections) {
                    onCardClickListener.onMultiSelectModeExit();
                }
            } else {
                onCardClickListener.onCardClick(getItem(position));
            }
        });

        //长按事件
        cardView.setOnLongClickListener(v -> {
            isMultiSelectMode = true;
            toggleSelection(position, cardView);

            onCardClickListener.onMultiSelectModeEnter();
            return true;
        });

        return convertView;
    }

    // 全选方法
    public void selectAll(LinearLayout linearLayout) {
        selectedItems.clear();
        for (int i = 0; i < getCount(); i++) {
            selectedItems.put(i, true); // 将所有条目设为选中
        }

        // 更新界面
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            View childView = linearLayout.getChildAt(i);
            if (childView != null) {
                MaterialCardView cardView = childView.findViewById(R.id.account_card);
                if (cardView != null) {
                    cardView.setChecked(true); // 设置卡片选中状态
                }
            }
        }

        notifyDataSetChanged();
    }

    // 全不选方法
    public void deselectAll(LinearLayout linearLayout) {
        selectedItems.clear();

        // 更新界面
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            View childView = linearLayout.getChildAt(i);
            if (childView != null) {
                MaterialCardView cardView = childView.findViewById(R.id.account_card);
                if (cardView != null) {
                    cardView.setChecked(false); // 取消选中状态
                }
            }
        }

        notifyDataSetChanged();
    }

    public boolean isSelected(int position) {
    return selectedItems.get(position, false);
}

}

