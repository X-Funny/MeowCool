package top.xfunny.meowcool.core;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import top.xfunny.meowcool.R;

public class DatabaseManager {
    private static SharedPreferences sharedPref;
    private static List<String> databaseFiles;
    private static List<String> creatTime;


    public static Boolean creat(String databaseName, Context context) {//创建数据库
        if (nameCheck(databaseName, context) == 0) {
            System.out.println("创建数据库" + databaseName);
            DatabaseHelper databaseHelper = new DatabaseHelper(context, databaseName);
            databaseHelper.getWritableDatabase();
            return true;
        }
        return false;
    }

    public static void delete(Context context, File[] files) {
        sharedPref = context.getSharedPreferences("MyPreferences", MODE_PRIVATE);
        File databasesFolder = context.getDatabasePath("databases").getParentFile();
        String selectedDatabase = sharedPref.getString("selectedDatabase", "");

        boolean shouldCloseDatabase = false;

        // 检查是否要关闭数据库
        for (File file : files) {
            if (file.getName().equals(selectedDatabase)) {
                shouldCloseDatabase = true;
                break;
            }
        }

        if (shouldCloseDatabase) {
            close(context);
        }
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
                } else {
                    System.out.println("journal文件不存在: " + journalFile.getAbsolutePath());
                }
            } else {
                System.out.println("文件不存在: " + databaseFile.getAbsolutePath());
            }
        }
        Toast.makeText(context, context.getString(R.string.deleteddatabase), Toast.LENGTH_SHORT).show();

    }

    public static void selectDatabase(Context context, ListView listView) {//设置需要操作的账套,即将弃用
        StringBuilder selectedItems = new StringBuilder();
        for (int i = 0; i < listView.getCount(); i++) {
            if (listView.isItemChecked(i)) {
                selectedItems.append(databaseFiles.get(i)).append(", ");
            }
        }
        if (selectedItems.length() > 0) {
            selectedItems.delete(selectedItems.length() - 2, selectedItems.length());
        }
        sharedPref = context.getSharedPreferences("MyPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("selectedDatabase", selectedItems.toString());
        editor.apply();
    }

    public static void selectDatabase(Context context, String selectedDatabase) {//设置需要操作的账套
        sharedPref = context.getSharedPreferences("MyPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("selectedDatabase", selectedDatabase);
        editor.apply();
    }

    public static SQLiteDatabase openDatabase(Context context) {//打开账套,用于编辑
        String DatabaseName = getSelectedDatabaseName(context);
        if(!DatabaseName.isEmpty()){
            return SQLiteDatabase.openDatabase(
                    context.getDatabasePath(DatabaseName).getPath(),
                    null,
                    SQLiteDatabase.OPEN_READWRITE
            );
        }else{
            return null;
        }
    }

    public static void close(Context context) {//与账套断开连接
        sharedPref = context.getSharedPreferences("MyPreferences", MODE_PRIVATE);
        sharedPref.edit().putString("selectedDatabase", null).apply();
        sharedPref.edit().putString("PRSubject", null).apply();
    }

    public static String getSelectedDatabaseName(Context context) {//读取目前正在操作的账套名称
        sharedPref = context.getSharedPreferences("MyPreferences", MODE_PRIVATE);
        return sharedPref.getString("selectedDatabase", "");
    }

    public static int nameCheck(String databaseName, Context context) {//账套文件名检查
        isDatabaseExist(context);
        String ILLEGAL_CHARACTERS = "\\/:*?\"<>|";
        for (String name : databaseFiles) {
            if (name.equals(databaseName)) {// 存在相同文件
                return 1;
            }
        }

        if (databaseName.equals(".db")) {// 空文件名
            return 2;
        }

        for (char c : ILLEGAL_CHARACTERS.toCharArray()) {// 含有非法字符
            if (databaseName.indexOf(c) != -1) {
                return 3;
            }
        }
        return 0;
    }

    public static Boolean isDatabaseExist(Context context) {//返回账套是否存在
        databaseFiles = new ArrayList<>();
        creatTime = new ArrayList<>();
        File databaseFolder = context.getDatabasePath("databases").getParentFile();

        File[] files = databaseFolder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".db")) {

                    databaseFiles.add(file.getName());


                    try {
                        // 使用 NIO 获取文件属性
                        BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);

                        // 转换为本地时间并格式化
                        LocalDateTime creationTime = attrs.creationTime()
                                .toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime();

                        // 使用 DateTimeFormatter 格式化
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        String formattedTime = creationTime.format(formatter);

                        // 添加创建时间到数组
                        creatTime.add(formattedTime); // 默认格式：yyyy-MM-ddTHH:mm:ss
                        System.out.println("文件名: " + file.getName() + " | 创建时间: " + formattedTime);

                    } catch (IOException e) {
                        System.err.println("获取文件创建时间失败: " + e.getMessage());
                        creatTime.add("未知"); // 如果获取失败，添加占位符
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }


    public static List<String> getDatabaseList(Context context) {
        isDatabaseExist(context);
        return databaseFiles;
    }

    public static List<String> getDatabaseCreateTime() {
        return creatTime;
    }

    public static void saveAccountName(String oldName, String newName, Context context) {
        File databasesFolder = context.getDatabasePath("databases").getParentFile();

        File oldFile = new File(databasesFolder, oldName);
        File newFile = new File(databasesFolder, newName);

        if (oldFile.exists()) {
            if (newFile.exists()) {
                // 新文件已存在，无法重命名
                System.out.println("新文件已存在，无法重命名");
            } else {
                // 重命名文件
                if (oldFile.renameTo(newFile)) {
                    System.out.println("文件重命名成功");
                } else {
                    System.out.println("文件重命名失败");
                }
            }
        }
    }


}





