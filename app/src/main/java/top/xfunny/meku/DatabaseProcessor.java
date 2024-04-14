package top.xfunny.meku;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
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

public class DatabaseProcessor {

    private static SharedPreferences sharedPref;
    private static String selectedDatabase;
    private static List<String> databaseFiles;

    public static void createDatabase(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("添加账簿");
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String databaseName = String.format("%s.db", input.getText().toString());

                DatabaseHelper dbHelper = new DatabaseHelper(context, databaseName);
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                String message = String.format("账簿%s已创建", databaseName);

                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("取消", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    static void closeSelectedDatabase(Context context) {

        if (sharedPref != null) {

            String selectedDatabase = sharedPref.getString("selectedDatabase", "");
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.remove("selectedDatabase").apply();
            System.out.println("已清空");


            TestActivity.textView1.setText("请选择账簿");
        } else {

            sharedPref = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);


            String selectedDatabase = sharedPref.getString("selectedDatabase", "");
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.remove("selectedDatabase").apply();
            System.out.println("已清空");


            TestActivity.textView1.setText("请选择账簿");
        }
    }



    public static void showDatabaseSelectDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("请选择账簿");

        sharedPref = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        selectedDatabase = sharedPref.getString("selectedDatabase", "");

        getDatabaseFiles(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.databaseprocessor, null);
        ListView listView = dialogView.findViewById(R.id.database_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_single_choice, databaseFiles);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setItemChecked(getCheckeddatabase(), true);
        builder.setView(dialogView);

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedDatabase = getSelectedDatabase(listView);
                Toast.makeText(context, "已选择 " + selectedDatabase, Toast.LENGTH_SHORT).show();
                saveSelectedDatabase(context, selectedDatabase);
                setTextView1(context);
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();

        if (databaseFiles.isEmpty()) {
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
            });
        }

        dialog.show();
    }

    public static void showDatabaseDeleteDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("请选择需要删除的账簿");

        sharedPref = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        selectedDatabase = sharedPref.getString("selectedDatabase", "");

        getDatabaseFiles(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.databaseprocessor, null);
        ListView listView = dialogView.findViewById(R.id.database_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_multiple_choice, databaseFiles);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        builder.setView(dialogView);

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteSelectedDatabases(context, listView);
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private static void getDatabaseFiles(Context context) {
        databaseFiles = new ArrayList<>();
        File databaseFolder = context.getDatabasePath("databases").getParentFile();
        File[] files = databaseFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".db")) {
                    databaseFiles.add(file.getName());
                    System.out.println("文件数组"+databaseFiles);
                }
            }
        }
        if (databaseFiles.isEmpty()) {
            Toast.makeText(context, "没有账簿", Toast.LENGTH_SHORT).show();
        }
    }

    private static String getSelectedDatabase(ListView listView) {
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

    private static void saveSelectedDatabase(Context context, String selectedDatabase) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("selectedDatabase", selectedDatabase);
        editor.apply();
    }

    public static void setTextView1(Context context) {
        sharedPref = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        selectedDatabase = sharedPref.getString("selectedDatabase", "");

        TextView textView1 = ((AppCompatActivity) context).findViewById(R.id.textView1);

        String selectedDatabase1 = String.format("当前正在使用的账簿: %s", selectedDatabase);
        textView1.setText(selectedDatabase1);
    }

    private static int getCheckeddatabase() {
        int position = findStringInArray(databaseFiles.toArray(new String[0]), selectedDatabase);
        if (position != -1) {
            System.out.println("目标字符串 " + selectedDatabase + " 在数组中的位置是: " + position);
        } else {
            System.out.println("目标字符串 " + selectedDatabase + " 不在数组中。");
        }
        System.out.println("内position值" + position);
        return position;
    }

    private static void deleteSelectedDatabases(Context context, ListView listView) {
        File[] selectedFiles = getSelectedFiles(context, listView);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("确认删除？该操作不可恢复！");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteDatabases(context, selectedFiles); // 这里传入了Context
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private static File[] getSelectedFiles(Context context, ListView listView) {
        List<File> selectedFiles = new ArrayList<>();
        for (int i = 0; i < listView.getCount(); i++) {
            if (listView.isItemChecked(i)) {
                File file = new File(context.getDatabasePath("databases"), databaseFiles.get(i));
                selectedFiles.add(file);
            }
        }
        return selectedFiles.toArray(new File[0]);
    }

    private static void deleteDatabases(Context context, File[] files) {
        File databasesFolder = context.getDatabasePath("databases").getParentFile();
        String selectedDatabase = sharedPref.getString("selectedDatabase", "");

        // 检查是否要关闭数据库
        if (check(files, selectedDatabase)) {
            closeDatabasePrompt(context, selectedDatabase);
        } else {
            for (File file : files) {
                File databaseFile = new File(databasesFolder, file.getName());
                if (databaseFile.exists()) {
                    if (databaseFile.delete()) {
                        databaseFiles.remove(file.getName());
                        Toast.makeText(context, "已删除选中的账簿", Toast.LENGTH_SHORT).show();
                        System.out.println("已删除" + databaseFile.getAbsolutePath());
                    } else {
                        System.out.println("无法删除文件: " + databaseFile.getAbsolutePath());
                    }
                } else {
                    System.out.println("文件不存在: " + databaseFile.getAbsolutePath());
                }
            }
        }
    }


    private static boolean check(File[] files, String selectedDatabase) {
        for (File file : files) {
            if (file.getName().equals(selectedDatabase)) {
                return true;
            }
        }
        return false;
    }




    private static void closeDatabasePrompt(Context context, String databaseName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(databaseName + "未关闭，无法删除");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
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
}
