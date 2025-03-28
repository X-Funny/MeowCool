package top.xfunny.meowcool.page.test_page;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import top.xfunny.meowcool.R;
import top.xfunny.meowcool.core.DatabaseManager;

public class DatabaseProcessor {


    private static Context context;

    // 设置静态的 Context
    public static void setContext(Context context) {
        DatabaseProcessor.context = context;
    }

    public static void createDatabase(Context context) {//创建账套窗口
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.title_dialog_setaccount));
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton(context.getString(R.string.button_ok), (dialog, which) -> {
            String databaseName = String.format("%s.db", input.getText().toString());
            if (DatabaseManager.creat(databaseName, context)) {
                Toast.makeText(context, "数据库" + databaseName + "创建成功", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(context.getString(R.string.button_cancel), null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void showDatabaseSelectDialog(Context context) {//选择账套窗口
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.title_dailog_selectdatabase));
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.databaseprocessor, null);

        ListView listView = dialogView.findViewById(R.id.database_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_single_choice, DatabaseManager.getDatabaseList(context));
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setItemChecked(getCheckeddatabase(), true);
        builder.setView(dialogView);

        builder.setPositiveButton(context.getString(R.string.button_ok), (dialog, which) -> {
            DatabaseManager.selectDatabase(context, listView);
            Toast.makeText(context, context.getString(R.string.selected) + DatabaseManager.getSelectedDatabaseName(context), Toast.LENGTH_SHORT).show();
            setTextView1(context);
        });

        builder.setNegativeButton(context.getString(R.string.button_cancel), (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();

        if (DatabaseManager.getDatabaseList(context).isEmpty()) {
            dialog.setOnShowListener(dialogInterface -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false));
        }

        dialog.show();
    }

    public static void showDatabaseDeleteDialog(Context context) {//删除账套窗口
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.title_dialog_deletedatabase));

        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.databaseprocessor, null);
        ListView listView = dialogView.findViewById(R.id.database_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_multiple_choice, DatabaseManager.getDatabaseList(context));
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        builder.setView(dialogView);

        builder.setPositiveButton(context.getString(R.string.button_ok), (dialog, which) -> deleteSelectedDatabases(context, listView));

        builder.setNegativeButton(context.getString(R.string.button_cancel), (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();

        if (DatabaseManager.getDatabaseList(context).isEmpty()) {
            dialog.setOnShowListener(dialogInterface -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false));
        }
        dialog.show();
    }

    private static void deleteSelectedDatabases(Context context, ListView listView) {//确认删除窗口
        File[] selectedFiles = getSelectedFiles(context, listView);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.database_confirmdelete));
        builder.setPositiveButton(context.getString(R.string.button_ok), (dialog, which) -> {
            DatabaseManager.delete(context, selectedFiles);
        });
        builder.setNegativeButton(context.getString(R.string.button_cancel), (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }


    @SuppressLint("SetTextI18n")
    public static void setTextView1(Context context) {
        String selectedDatabase = DatabaseManager.getSelectedDatabaseName(context);

        TextView textView1 = ((AppCompatActivity) context).findViewById(R.id.textView1);
        textView1.setText(context.getString(R.string.useddatabase) + selectedDatabase);


    }

    private static int getCheckeddatabase() {
        String selectedDatabase = DatabaseManager.getSelectedDatabaseName(context);
        int position = findStringInArray(DatabaseManager.getDatabaseList(context).toArray(new String[0]), selectedDatabase);
        if (position != -1) {
            System.out.println("目标字符串 " + selectedDatabase + " 在数组中的位置是: " + position);
        } else {
            System.out.println("目标字符串 " + selectedDatabase + " 不在数组中。");
        }
        System.out.println("内position值" + position);
        return position;
    }


    private static File[] getSelectedFiles(Context context, ListView listView) {
        List<File> selectedFiles = new ArrayList<>();
        for (int i = 0; i < listView.getCount(); i++) {
            if (listView.isItemChecked(i)) {
                File file = new File(context.getDatabasePath("databases"), DatabaseManager.getDatabaseList(context).get(i));
                selectedFiles.add(file);
            }
        }
        return selectedFiles.toArray(new File[0]);
    }


    public static void closeDatabasePrompt(Context context, String databaseName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(databaseName + context.getString(R.string.closedatabaseprompt));
        builder.setPositiveButton(context.getString(R.string.button_ok), (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }


    private static int findStringInArray(String[] arr, String selectedDatabase) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(selectedDatabase)) {
                return i;
            }
        }
        return -1;
    }


    public static boolean getusedDatabase() {
        if (context == null) {
            throw new IllegalStateException("Context has not been set. Call setContext(Context) before using this method.");
        }

        SharedPreferences sharedPref = context.getSharedPreferences("MyPreferences", MODE_PRIVATE);
        String selectedDatabase = sharedPref.getString("selectedDatabase", "");
        return selectedDatabase.isEmpty();
    }

}
