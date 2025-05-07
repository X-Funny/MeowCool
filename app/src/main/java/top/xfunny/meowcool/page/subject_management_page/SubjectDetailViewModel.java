package top.xfunny.meowcool.page.subject_management_page;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;


public class SubjectDetailViewModel extends AndroidViewModel {
    private final MutableLiveData<String> currentAmount = new MutableLiveData<>("0.00");

    public SubjectDetailViewModel(@NonNull Application application) {
        super(application);
    }

    public void onNumberClick(String number) {
        String current = currentAmount.getValue() != null ? currentAmount.getValue() : "";

        // 预处理初始状态
        if (current.equals("0.00")) {
            current = current.isEmpty() ? "" : "0"; // 统一处理为数字模式
        }

        if (number.equals(".")) {
            // 处理小数点逻辑
            if (current.isEmpty()) {
                current = "0.";
            } else if (!current.contains(".")) {
                // 在数字后添加小数点
                current += ".";
            }
        } else if (number.equals("del")) {
            // 处理删除逻辑
            if (!current.isEmpty()) {
                current = current.substring(0, current.length() - 1);
            }
            // 删除后处理默认值
            if (current.isEmpty() || current.equals("0")) {
                current = "0.00";
            }
        } else {
            // 处理数字输入
            if (current.equals("0.00") || current.equals("0")) {
                current = number; // 替换初始零值
            } else {
                int dotIndex = current.indexOf(".");
                if (dotIndex == -1) {
                    // 整数部分最多10位
                    if (current.length() >= 10) return;
                } else {
                    // 小数部分最多两位
                    if (current.substring(dotIndex + 1).length() >= 2) return;
                }
                current += number;
            }
        }

        // 后处理特殊情况
        if (current.endsWith(".")) {
            // 显示优化：末尾小数点保留
        } else if (!current.contains(".") && !current.equals("0.00")) {
            // 添加默认小数位（可选）
        }

        // 空值保护
        if (current.isEmpty()) {
            current = "0.00";
        }

        getCurrentAmount().setValue(current);
    }

    public MutableLiveData<String> getCurrentAmount() {
        return currentAmount;
    }
}


