package top.xfunny.meowcool.page.initial_page.ui.home.BottomSheetDialog.TransactionsPage;

import top.xfunny.meowcool.page.initial_page.ui.home.BottomSheetDialog.CheckingStatus;

public class CheckResult {
    private final int checkingPosition;
    private final CheckingStatus status;

    public CheckResult(int checkingPosition, CheckingStatus status) {
        this.checkingPosition = checkingPosition;
        this.status = status;
    }

    public int getCheckingPosition() {
        return checkingPosition;
    }

    public CheckingStatus getStatus() {
        return status;
    }
}