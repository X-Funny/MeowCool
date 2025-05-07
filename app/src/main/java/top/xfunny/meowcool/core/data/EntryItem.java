package top.xfunny.meowcool.core.data;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import java.util.UUID;

public class EntryItem {
    private final MutableLiveData<String> amountLiveData = new MutableLiveData<>("0.00");
    private final MutableLiveData<SubjectNode> subjectLiveData = new MutableLiveData<>(null);
    private final String uuid = UUID.randomUUID().toString();
    private MutableLiveData<Integer> direction = new MutableLiveData<>(0);  // 保存借/贷按钮的选中状态，借1，贷-1, 不选0
    private MutableLiveData<String> summary = new MutableLiveData<>("");

    public MutableLiveData<Integer> getDirectionLiveData() {
        return direction;
    }

    public void setDirectionLiveData(MutableLiveData<Integer> direction) {
        this.direction = direction;
    }


    public MutableLiveData<String> getAmountLiveData() {
        return amountLiveData;
    }

    public String getAmount() {
        return amountLiveData.getValue();
    }

    public MutableLiveData<SubjectNode> getSubjectLiveData() {
        return subjectLiveData;
    }

    public MutableLiveData<String> getSummaryLiveData() {
        return summary;
    }

    public void setSummaryLIveData(MutableLiveData<String> summary) {
        this.summary = summary;
    }

    public String getUuid() {
        return uuid;
    }


    @NonNull
    @Override
    public String toString() {
        return "EntryItem{" +
                "direction=" + direction +
                ", subject='" + subjectLiveData + '\'' +
                ", amount='" + amountLiveData + '\'' +
                '}';
    }
}
