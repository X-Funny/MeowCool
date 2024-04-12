package top.xfunny.meku;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DatabaseSelectDialog {

    private static Context context;
    private List<String> databaseFiles;
    private static SharedPreferences sharedPref;
    static String selectedDatabase;

    public DatabaseSelectDialog(Context context) {
        this.context = context;
        databaseFiles = new ArrayList<>();
        sharedPref = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        selectedDatabase = sharedPref.getString("selectedDatabase", "");
    }

    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("请选择账簿");

        getDatabaseFiles();

        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_select_database, null);
        ListView listView = dialogView.findViewById(R.id.database_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_single_choice, databaseFiles);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setItemChecked(getCheckeddatabase(), true);
        builder.setView(dialogView);

        // 加入确定按钮
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedDatabase = getSelectedDatabase(listView);
                System.out.println("已选择" + selectedDatabase);
                String message = String.format("已选择%s", selectedDatabase);
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                saveSelectedDatabase(selectedDatabase);
                DBProcessor.setTextView1(context); // 传递当前的 context
            }
        });

        // 加入取消按钮
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();

        // 检查数据库文件列表是否为空
        if (databaseFiles.isEmpty()) {
            // 如果数据库文件列表为空，则设置确定按钮不可点击
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
            });

        }

        dialog.show();
    }


    public int getCheckeddatabase() {
        int position = findStringInArray(databaseFiles.toArray(new String[0]), selectedDatabase);
        if (position != -1) {
            System.out.println("目标字符串 " + selectedDatabase + " 在数组中的位置是: " + position);
        } else {
            System.out.println("目标字符串 " + selectedDatabase + " 不在数组中。");
        }
        System.out.println("内position值" + position);
        return position;
    }

    private void getDatabaseFiles() {
        File databaseFolder = new File("/data/data/top.xfunny.meku/databases");
        File[] files = databaseFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".db")) {
                    databaseFiles.add(file.getName());
                }
            }
        }
        if (databaseFiles.isEmpty()) {
            Toast.makeText(context, "没有账簿", Toast.LENGTH_SHORT).show();
        }
    }

    private String getSelectedDatabase(ListView listView) {
        StringBuilder selectedItems = new StringBuilder();
        for (int i = 0; i < listView.getCount(); i++) {
            if (listView.isItemChecked(i)) {
                selectedItems.append(databaseFiles.get(i)).append(", ");
            }
        }
        if (selectedItems.length() > 0) {
            selectedItems.delete(selectedItems.length() - 2, selectedItems.length());
        }
        return selectedItems.toString();
    }

    public static int findStringInArray(String[] arr, String selectedDatabase) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(selectedDatabase)) {
                return i;
            }
        }
        return -1;
    }

    public static void saveSelectedDatabase(String selectedDatabase) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("selectedDatabase", selectedDatabase);
        editor.apply();
    }
}
