package top.xfunny.meku;

import static android.content.Context.MODE_PRIVATE;


import static androidx.core.content.res.TypedArrayUtils.getText;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
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
    private static Context context; // 添加静态的 Context 变量

    // 设置静态的 Context
    public static void setContext(Context context) {
        DatabaseProcessor.context = context;
    }

    public static void createDatabase(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.title_dialog_setaccount));
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton(context.getString(R.string.button_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String databaseName = String.format("%s.db", input.getText().toString());

                if (DatabaseNameCheck(context, databaseName)) {

                } else {
                    DatabaseHelper dbHelper = new DatabaseHelper(context, databaseName);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    Boolean getDatabaseFilesToast=false;
                    getDatabaseFiles(context,getDatabaseFilesToast);
                    Toast.makeText(context, "数据库" + databaseName + "创建成功", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton(context.getString(R.string.button_cancel), null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private static boolean DatabaseNameCheck(Context context, String databaseName) {
        Boolean getDatabaseFilesToast=false;
        getDatabaseFiles(context,getDatabaseFilesToast);
        for (String name : databaseFiles) {
            if (name.equals(databaseName)) {
                Toast.makeText(context, "数据库名称已存在，请重新输入", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        if(databaseName.equals(".db")){
            Toast.makeText(context, "非法的数据库名称", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }




    static void closeSelectedDatabase(Context context) {

        if (sharedPref != null) {

            String selectedDatabase = sharedPref.getString("selectedDatabase", "");
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.remove("selectedDatabase").apply();
            System.out.println("已清空");


            TestActivity.textView1.setText(R.string.textView1);
        } else {

            sharedPref = context.getSharedPreferences("MyPreferences", MODE_PRIVATE);


            String selectedDatabase = sharedPref.getString("selectedDatabase", "");
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.remove("selectedDatabase").apply();
            System.out.println("已清空");


            TestActivity.textView1.setText(context.getString(R.string.textView1));
        }
    }



    public static void showDatabaseSelectDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.title_dailog_selectdatabase));

        sharedPref = context.getSharedPreferences("MyPreferences", MODE_PRIVATE);
        selectedDatabase = sharedPref.getString("selectedDatabase", "");
        Boolean getDatabaseFilesToast=true;
        getDatabaseFiles(context,getDatabaseFilesToast);
        getDatabaseFiles(context,getDatabaseFilesToast);

        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.databaseprocessor, null);
        ListView listView = dialogView.findViewById(R.id.database_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_single_choice, databaseFiles);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setItemChecked(getCheckeddatabase(), true);
        builder.setView(dialogView);

        builder.setPositiveButton(context.getString(R.string.button_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedDatabase = getSelectedDatabase(listView);
                Toast.makeText(context, context.getString(R.string.selected) + selectedDatabase, Toast.LENGTH_SHORT).show();
                saveSelectedDatabase(context, selectedDatabase);
                setTextView1(context);
            }
        });

        builder.setNegativeButton(context.getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
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
        builder.setTitle(context.getString(R.string.title_dialog_deletedatabase));

        sharedPref = context.getSharedPreferences("MyPreferences", MODE_PRIVATE);
        selectedDatabase = sharedPref.getString("selectedDatabase", "");
        Boolean getDatabaseFilesToast=true;
        getDatabaseFiles(context,getDatabaseFilesToast);

        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.databaseprocessor, null);
        ListView listView = dialogView.findViewById(R.id.database_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_multiple_choice, databaseFiles);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        builder.setView(dialogView);

        builder.setPositiveButton(context.getString(R.string.button_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteSelectedDatabases(context, listView);
            }
        });

        builder.setNegativeButton(context.getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
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

    private static void getDatabaseFiles(Context context, Boolean getDatabaseFilesToast) {
        databaseFiles = new ArrayList<>();
        File databaseFolder = context.getDatabasePath("databases").getParentFile();
        File[] files = databaseFolder.listFiles();
        System.out.println("没有账簿1");
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".db")) {
                    databaseFiles.add(file.getName());
                    System.out.println("文件数组"+databaseFiles);
                }
            }
        }
        if (databaseFiles.isEmpty() && getDatabaseFilesToast) {
            Toast.makeText(context, context.getString(R.string.noaccountdatabases), Toast.LENGTH_SHORT).show();
            System.out.println("没有账簿");
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

    @SuppressLint("SetTextI18n")
    public static void setTextView1(Context context) {
        sharedPref = context.getSharedPreferences("MyPreferences", MODE_PRIVATE);
        selectedDatabase = sharedPref.getString("selectedDatabase", "");

        TextView textView1 = ((AppCompatActivity) context).findViewById(R.id.textView1);

        textView1.setText(context.getString(R.string.useddatabase)+selectedDatabase);
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
        builder.setTitle(context.getString(R.string.database_confirmdelete));
        builder.setPositiveButton(context.getString(R.string.button_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteDatabases(context, selectedFiles); // 这里传入了Context
            }
        });
        builder.setNegativeButton(context.getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
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
                    // 删除数据库文件
                    if (databaseFile.delete()) {
                        databaseFiles.remove(file.getName());

                        System.out.println("已删除" + databaseFile.getAbsolutePath());



                    } else {
                        System.out.println("无法删除文件: " + databaseFile.getAbsolutePath());
                    }

                    // 删除journal文件
                    String journalFileName = file.getName().replace(".db", ".db-journal");
                    File journalFile = new File(databasesFolder, journalFileName);
                    if (journalFile.exists()) {
                        if (journalFile.delete()) {
                            System.out.println("已删除journal文件: " + journalFile.getAbsolutePath());
                        } else {
                            System.out.println("无法删除journal文件: " + journalFile.getAbsolutePath());
                        }
                    }else {System.out.println("journal文件不存在: " + journalFile.getAbsolutePath());}
                } else {
                    System.out.println("文件不存在: " + databaseFile.getAbsolutePath());
                }
            }
            Toast.makeText(context, context.getString(R.string.deleteddatabase), Toast.LENGTH_SHORT).show();
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
        builder.setTitle(databaseName + context.getString(R.string.closedatabaseprompt));
        builder.setPositiveButton(context.getString(R.string.button_ok), new DialogInterface.OnClickListener() {
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


    public static boolean getusedDatabase() {
        if (context == null) {
            throw new IllegalStateException("Context has not been set. Call setContext(Context) before using this method.");
        }

        SharedPreferences sharedPref = context.getSharedPreferences("MyPreferences", MODE_PRIVATE);
        String selectedDatabase = sharedPref.getString("selectedDatabase", "");
        return selectedDatabase.isEmpty();
    }

}
