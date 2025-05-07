package top.xfunny.meowcool.core;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import top.xfunny.meowcool.core.data.SettingsManager;
import top.xfunny.meowcool.core.data.TransactionItem;

public class TransactionManager {
    private SQLiteDatabase db;

    public TransactionManager(android.database.sqlite.SQLiteDatabase db) {
        this.db = db;
    }

    public static boolean isSameDate(long timestamp1, long timestamp2) {
        LocalDate date1 = Instant.ofEpochMilli(timestamp1)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate date2 = Instant.ofEpochMilli(timestamp2)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        return date1.equals(date2);
    }

    public void addNewTransaction(long date, int number, String summary, int isDebit, String subjectUuid, BigDecimal amount) {
        System.out.println("摘要" + summary);
        db.beginTransaction();
        ContentValues values = new ContentValues();
        values.put("date", date);
        values.put("number", number);
        values.put("summary", summary);
        values.put("is_debit", isDebit);
        values.put("subject_uuid", subjectUuid);
        values.put("amount", String.valueOf(amount));

        try {
            long result = db.insert("accounting_vouchers", null, values);
            if (result == -1) return; // 插入失败
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public int getLastRowColumnValue() {
        int columnValue = 0;
        String query = "SELECT number FROM accounting_vouchers ORDER BY ROWID DESC LIMIT 1";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                columnValue = cursor.getInt(0);
            }
            cursor.close();
        }
        return columnValue;
    }

    public List<Long> getDateList() {
        List<Long> dateList = new ArrayList<>();
        String query = "SELECT DISTINCT date FROM accounting_vouchers";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                long date = cursor.getLong(0);
                if (dateList.isEmpty()) {
                    dateList.add(date);
                } else if (!isSameDate(date, dateList.get(dateList.size() - 1))) {
                    dateList.add(date);
                }
            }
            cursor.close();
        }
        return dateList;
    }

    public List<TransactionItem> getTransactionList(long date) {
        List<TransactionItem> transactionList = new ArrayList<>();
        String query = "SELECT * FROM accounting_vouchers";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                long transactionDate = cursor.getLong(1);
                int number = cursor.getInt(2);
                String summary = cursor.getString(3);
                int isCredit = cursor.getInt(4);
                String subjectUuid = cursor.getString(5);
                BigDecimal amount = new BigDecimal(cursor.getString(6));

                if (isSameDate(transactionDate, date)) {
                    // 检查 transactionList 是否为空
                    if (transactionList.isEmpty()) {
                        TransactionItem transactionItem = new TransactionItem();
                        transactionItem.setTime(transactionDate);
                        transactionItem.setSummary(summary);
                        transactionItem.setNumber(number);
                        transactionItem.addTransaction(subjectUuid, isCredit, amount);
                        transactionList.add(transactionItem);
                    } else {
                        // 获取最后一个 TransactionItem
                        TransactionItem lastItem = transactionList.get(transactionList.size() - 1);
                        if (number != lastItem.getNumber()) {
                            // 如果 number 不同，创建新的 TransactionItem
                            TransactionItem transactionItem = new TransactionItem();
                            transactionItem.setTime(transactionDate);
                            transactionItem.setSummary(summary);
                            transactionItem.setNumber(number);
                            transactionItem.addTransaction(subjectUuid, isCredit, amount);
                            transactionList.add(transactionItem);
                        } else {
                            // 如果 number 相同，添加到最后一个 TransactionItem
                            lastItem.addTransaction(subjectUuid, isCredit, amount);
                        }
                    }
                }

            }
            cursor.close();
        }
        return transactionList;
    }

    public BigDecimal getTransactionCredit(String subjectUuid) {
        BigDecimal credit = new BigDecimal(0);
        String query = "SELECT amount FROM accounting_vouchers WHERE subject_uuid = ? AND is_debit = -1";
        Cursor cursor = db.rawQuery(query, new String[]{subjectUuid});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                BigDecimal amount = new BigDecimal(cursor.getString(0));
                credit = credit.add(amount);
            }
            cursor.close();
        }
        return credit;
    }

    public BigDecimal getTransactionDebit(String subjectUuid) {
        BigDecimal debit = new BigDecimal(0);
        String query = "SELECT amount FROM accounting_vouchers WHERE subject_uuid = ? AND is_debit = 1";
        Cursor cursor = db.rawQuery(query, new String[]{subjectUuid});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                BigDecimal amount = new BigDecimal(cursor.getString(0));
                debit = debit.add(amount);
            }
        }
        return debit;
    }

    public BigDecimal getAccumulatedPaymentOrRevenue(Context context, int type) {
        SettingsManager settingsManager = new SettingsManager(context);
        BigDecimal accumulatedPayment = new BigDecimal(0);
        List<String> prSubjectUuidList = settingsManager.getPRSubject();
        for(String subjectUuid : prSubjectUuidList){
            String query = "SELECT amount FROM accounting_vouchers WHERE subject_uuid = ? AND is_debit = ?";
            Cursor cursor = db.rawQuery(query, new String[]{subjectUuid, String.valueOf(type)});
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    BigDecimal amount = new BigDecimal(cursor.getString(0));
                    accumulatedPayment = accumulatedPayment.add(amount);
                }
                cursor.close();
            }
        }
        return accumulatedPayment;
    }

    public String getAllTransactionPrompt() {
        SubjectManager subjectManager = new SubjectManager(db);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());

        String startPrompt = "你是一名专业的个人高级财务顾问，以下是用户的交易记录，编号相同的记录为同一分录，请你根据这些交易记录进行详细的分析，并给出建议，同时为用户作出未来预算。注意，1.不要使用markdown语法，因为用户的界面不支持markdown 2.用中文回答。3.用户没有输入框，无法进一步向您发问。4.用户目前使用电子复式记账软件";
        StringBuilder endPrompt = new StringBuilder();
        List<String> prTransactionList = new ArrayList<>();

        String number;
        String summary;
        String date;
        String direction;
        String SubjectName;
        String amount;

        String query = "SELECT * FROM accounting_vouchers";
        Cursor cursor = db.rawQuery(query, null);
        try {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    date = sdf.format(new Date(cursor.getLong(1)));
                    number = String.valueOf(cursor.getInt(2));
                    summary = cursor.getString(3);
                    direction = cursor.getInt(4) == 1 ? "借" : "贷";
                    SubjectName = subjectManager.findSubjectByUuid(cursor.getString(5)).getName();
                    amount = cursor.getString(6) + "元";

                    String signalprompt = "记-" + number + "，" + date + "," + summary + "," + direction + " :" + SubjectName + "-" + amount + "。";
                    prTransactionList.add(signalprompt);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close(); // 确保 Cursor 被关闭
            }
        }

        for (String prompt : prTransactionList) {
            endPrompt.append(prompt);
        }
        return startPrompt + endPrompt;
    }
}
