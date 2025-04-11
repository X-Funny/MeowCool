package top.xfunny.meowcool.core.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import kotlin.Triple;

public class TransactionItem {
    long time;
    String summary;
    int number;
    List<Triple<String, Integer, BigDecimal>> transactionList ;

    public TransactionItem() {
        this.transactionList = new ArrayList<>();
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getSummary() {
        return summary;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public void addTransaction(String subjectUuid, int isCredit, BigDecimal amount) {
        transactionList.add(new Triple<>(subjectUuid, isCredit, amount));
    }

    public List<Triple<String, Integer, BigDecimal>> getTransactionList() {
        return transactionList;
    }

    public BigDecimal getTotalAmount() {
        BigDecimal totalAmount = new BigDecimal("0");
        for (Triple<String, Integer, BigDecimal> transaction : transactionList) {
            if(transaction.getSecond() == 1){
                totalAmount = totalAmount.add(transaction.getThird());
            }
        }
        return totalAmount;
    }
}
