package top.xfunny.meowcool.page.initial_page.ui.home.BottomSheetDialog;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import top.xfunny.meowcool.core.data.SubjectNode;
import top.xfunny.meowcool.core.data.EntryItem;

public class TransactionsViewModel extends ViewModel {
    private final MutableLiveData<List<EntryItem>> itemList = new MutableLiveData<>();
    private final MutableLiveData<SelectedPosition> selectedPosition = new MutableLiveData<>(new SelectedPosition(0, "amount"));
    private final MutableLiveData<Integer> lowerPage = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> upperPage = new MutableLiveData<>(0);
    private final MutableLiveData<String> summary = new MutableLiveData<>(null);

    public TransactionsViewModel() {
        List<EntryItem> items = new ArrayList<>();
        for (int i = 0; i < 2; i++) { // 初始创建两个卡片
            items.add(new EntryItem());
        }
        itemList.postValue(items);
    }

    public void initDefaultData() {
        if (itemList.getValue() == null || itemList.getValue().isEmpty()) {
            List<EntryItem> items = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                items.add(new EntryItem());
            }
            itemList.postValue(items);
        }
        upperPage.postValue(0);
        lowerPage.postValue(0);
        summary.postValue("");
    }

    public void addNewTransactionItem() { // 添加新条目
        List<EntryItem> items = itemList.getValue();
        if (items != null) {
            items.add(new EntryItem());
            itemList.postValue(new ArrayList<>(items));
        }
    }

    public MutableLiveData<List<EntryItem>> getItemList() { // 获取条目列表
        return itemList;
    }

    public void setSelectedPosition(int position, String viewType) {
        selectedPosition.setValue(new SelectedPosition(position, viewType));
    }

    public LiveData<SelectedPosition> getSelectedPosition() {
        return selectedPosition;
    }

    public void onNumberClick(String number) {// 数字点击事件
        EntryItem currentItem = Objects.requireNonNull(itemList.getValue()).get(Objects.requireNonNull(selectedPosition.getValue()).position);
        String current = currentItem.getAmount() != null ? currentItem.getAmount() : "";

        // 如果当前值是 "0.00"，则清空
        if (current.equals("0.00")) {
            current = "";
        }

        // 添加输入验证逻辑
        if (number.equals(".")) {
            if (!current.contains(".")) {
                current += ".";
            }
        } else if (number.equals("del")) {
            // 删除最后一个字符
            if (!current.isEmpty()) {
                current = current.substring(0, current.length() - 1);
            }
        } else {
            // 检查是否已经包含小数点
            int dotIndex = current.indexOf(".");
            if (dotIndex != -1) {
                // 如果小数点后已经有两位数字，则不允许再输入数字
                if (current.length() - dotIndex - 1 >= 2) {
                    return; // 忽略输入
                }
            }
            current += number;
        }

        // 强制触发LiveData更新
        currentItem.getAmountLiveData().setValue(current);

        // 通知RecyclerView局部刷新
        int pos = itemList.getValue().indexOf(currentItem);
        if (pos != -1) {
            MutableLiveData<List<EntryItem>> liveList = itemList;
            liveList.postValue(new ArrayList<>(liveList.getValue())); // 触发列表更新
        }
    }

    public LiveData<Integer> getLowerPage() { // 获取切换页面
        return lowerPage;
    }

    public void setLowerPage(int page) { // 切换页面
        lowerPage.postValue(page);
    }

    public void selectSubject(SubjectNode node) {
        EntryItem currentItem = Objects.requireNonNull(itemList.getValue()).get(Objects.requireNonNull(selectedPosition.getValue()).position);
        currentItem.getSubjectLiveData().setValue(node);
    }

    public void setSubjectDirection(int direction) {
        EntryItem currentItem = Objects.requireNonNull(itemList.getValue()).get(Objects.requireNonNull(selectedPosition.getValue()).position);
        currentItem.getDirectionLiveData().setValue(direction);
    }

    public void removeTransactionItem(int position) {// 删除条目
        List<EntryItem> items = itemList.getValue();
        if (items != null && position >= 0 && position < items.size()) {
            items.remove(position);
            itemList.postValue(new ArrayList<>(items));
        }
    }

    public MutableLiveData<Integer> getUpperPage() {
        return upperPage;
    }

    public MutableLiveData<String> getSummary() {
        return summary;
    }

    public void clearData() {
        List<EntryItem> items = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            items.add(new EntryItem());
        }
        itemList.postValue(items);
        summary.postValue(null);
        upperPage.postValue(0);
        lowerPage.postValue(0);
    }


    public static class SelectedPosition {// 选中条目位置之数据类
        public final int position;
        public final String viewType; // amount 或 subject

        public SelectedPosition(int position, String viewType) {
            this.position = position;
            this.viewType = viewType;
        }
    }
}
