package top.xfunny.meowcool.core.subject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubjectNode {
    public String uuid;
    public String name;
    public String parentUuid;
    public List<SubjectNode> children = new ArrayList<>(); //创建一个List集合，用来存放子节点
    public int level;
    public boolean isExpanded;

    public String path;

    public SubjectNode(String uuid, String name, String parentUuid, String path) {
        this.uuid = uuid;
        this.name = name;
        this.parentUuid = parentUuid;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public String getUuid() {
        return uuid;
    }

    public String getPath() {
        return path;
    }


    public static List<SubjectNode> buildSubjectTree(SQLiteDatabase db) {
        List<SubjectNode> allNodes = new ArrayList<>();
        Map<String, SubjectNode> nodeMap = new HashMap<>();

        // 查询科目及父节点信息
        String sql = "SELECT s.uuid, s.name, c.ancestor_uuid as parent_uuid " +
                "FROM accounting_subjects s " +
                "LEFT JOIN accounting_subject_closure c " +
                "ON s.uuid = c.descendant_uuid AND c.depth = 1";

        try (Cursor cursor = db.rawQuery(sql, null)) {//扫描并添加树状节点
            while (cursor.moveToNext()) {
                String uuid = cursor.getString(0);
                String name = cursor.getString(1);
                String path = cursor.getString(2);
                String parentUuid = cursor.isNull(3) ? null : cursor.getString(3);

                SubjectNode node = new SubjectNode(uuid, name, parentUuid, path);
                allNodes.add(node);
                nodeMap.put(uuid, node);
            }
        }

        // 构建树结构
        List<SubjectNode> rootNodes = new ArrayList<>();
        for (SubjectNode node : allNodes) {
            if (node.parentUuid == null) {
                rootNodes.add(node);
            } else {
                SubjectNode parent = nodeMap.get(node.parentUuid);
                if (parent != null) {
                    parent.children.add(node);
                }
            }
        }
        return rootNodes;//返回科目结构
    }

    public static List<SubjectNode> getAllSubjects(SQLiteDatabase db) {
        List<SubjectNode> subjects = new ArrayList<>();

        // 添加path字段查询
        String sql = "SELECT uuid, name, parent_uuid, path FROM accounting_subjects";

        try (Cursor cursor = db.rawQuery(sql, null)) {
            while (cursor.moveToNext()) {
                String uuid = cursor.getString(0);
                String name = cursor.getString(1);
                String parentUuid = cursor.isNull(2) ? null : cursor.getString(2);
                String path = cursor.getString(3);
                subjects.add(new SubjectNode(uuid, name, parentUuid, path));
            }
        }
        return subjects;
    }
}
