package top.xfunny.meku;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseDeleteDialog {
    private Context context;
    private List<String> databaseFiles;
    public String deleteDatabase;


    public DatabaseDeleteDialog(Context context) {
        this.context = context;
        databaseFiles = new ArrayList<>();

    }

    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("请选择需要删除的账簿");
        getDatabaseFiles();

        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_delete_database, null);
        ListView listView = dialogView.findViewById(R.id.database_delete);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_multiple_choice, databaseFiles);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        builder.setView(dialogView);
        //加入确定按钮
        builder.setPositiveButton("确定", null);
        //加入取消按钮
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog=builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                final AlertDialog alertDialog = (AlertDialog) dialogInterface;
                final Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                positiveButton.setEnabled(false); // 初始时设置为不可点击

                // 设置列表项选中状态变化的监听器
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // 如果没有选中的项目，则禁用确定按钮
                        if (listView.getCheckedItemCount() == 0) {
                            positiveButton.setEnabled(false);
                        } else {
                            positiveButton.setEnabled(true);
                        }
                    }
                });

                // 设置确定按钮的点击监听器
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteDatabase = getdeleteDatabase(listView);
                        String message = String.format("已选择%s", deleteDatabase);
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        deleteDialog(getSelectedFiles(listView));
                        alertDialog.dismiss();
                    }
                });
            }
        });

        dialog.show();
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

    private String getdeleteDatabase(ListView listView) {
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

    private void checkSelectedDatabase(){

    }

    private File[] getSelectedFiles(ListView listView) {
        List<File> selectedFiles = new ArrayList<>();
        for (int i = 0; i < listView.getCount(); i++) {
            if (listView.isItemChecked(i)) {
                File file = new File("/data/data/top.xfunny.meku/databases", databaseFiles.get(i));
                selectedFiles.add(file);
            }
        }
        return selectedFiles.toArray(new File[0]);
    }

    private void deleteDialog(final File[] selectedFiles) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("确认删除？该操作不可恢复！");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences sharedPref;
                String selectedDatabase;
                sharedPref = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                selectedDatabase = sharedPref.getString("selectedDatabase", "");
                System.out.println("check值为 " + check(selectedFiles, selectedDatabase));
                if (check(selectedFiles, selectedDatabase).equals("true")) {
                    closeDatabaseprompt(selectedDatabase);
                } else {
                    delete(selectedFiles);
                    System.out.println("执行了 false");
                }

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
    private void closeDatabaseprompt(String string){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(string+"未关闭，无法删除");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
    private void delete(File[] files) {
        for (File file : files) {
            if (file.delete()) {
                databaseFiles.remove(file.getName());
                System.out.println("已删除" + file.getAbsolutePath());
            }
        }
        String message = String.format("已删除%s", deleteDatabase);
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    private static String check(File[] files, String string){
        StringBuilder stringBuilder = new StringBuilder();
        for (File file : files) {
            if (file.getName().equals(string)) {
                return "true"; // 找到匹配的文件名
            }
        }
        return "false"; // 没有找到匹配的文件名
    }
}

