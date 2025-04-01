package top.xfunny.meowcool.page.initial_page.ui.home.BottomSheetDialog;

public class EntryItem {
    private int direction;  // 保存借/贷按钮的选中状态，借1，贷-1
    private String subject; // 科目
    private String amount;  // 金额

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
