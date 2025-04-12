package top.xfunny.meowcool.core.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;



import androidx.annotation.NonNull;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubjectNode implements Serializable {
    public String uuid;
    public String name;
    public String parentUuid;
    public List<SubjectNode> children = new ArrayList<>(); //创建一个List集合，用来存放子节点
    public int level;
    public boolean isExpanded;

    public String path;
    public int direction;
    public boolean isUsed;
    public BigDecimal initialAmount = new BigDecimal(0);

    public SubjectNode(String uuid, String name, String parentUuid, String path, int direction) {
        this.uuid = uuid;
        this.name = name;
        this.parentUuid = parentUuid;
        this.path = path;
        this.direction = direction;
        this.isUsed = false;
    }

    public static List<SubjectNode> buildSubjectTree(SQLiteDatabase db, String uid) {
        // 存储所有节点的列表
        List<SubjectNode> allNodes = new ArrayList<>();
        // 通过UUID映射节点，便于快速查找
        Map<String, SubjectNode> nodeMap = new HashMap<>();

        // 查询科目及父节点信息
        String sql = "SELECT s.uuid, s.name, c.ancestor_uuid as parent_uuid, s.path, s.balance_direction "
                + "FROM accounting_subjects s "
                + "LEFT JOIN accounting_subject_closure c "
                + "    ON s.uuid = c.descendant_uuid AND c.depth = 1 "
                + "WHERE s.uuid IN ("
                + "    SELECT descendant_uuid "
                + "    FROM accounting_subject_closure "
                + "    WHERE ancestor_uuid = ? AND depth > 0"
                + ")";


        // 扫描并添加树状节点
        try (Cursor cursor = db.rawQuery(sql, new String[]{uid})) {
            while (cursor.moveToNext()) {
                String uuid = cursor.getString(0);
                String name = cursor.getString(1);
                // 确定是否有父节点
                String parentUuid = cursor.isNull(2) ? null : cursor.getString(2);
                String path = cursor.getString(3);
                int direction = cursor.getInt(4);
                System.out.println("正在扫描，扫描结果：uuid:" + uuid + " name:" + name + " parentUuid:" + parentUuid);
                // 创建节点并添加到节点列表和映射中
                SubjectNode node = new SubjectNode(uuid, name, parentUuid, path, direction);
                allNodes.add(node);
                nodeMap.put(uuid, node);
            }
        }

        // 构建树结构
        List<SubjectNode> rootNodes = new ArrayList<>();
        for (SubjectNode node : allNodes) {
            // 如果节点没有父节点，则将其作为根节点
            if (node.parentUuid == null || node.parentUuid.equals(uid)) {
                rootNodes.add(node);
            } else {
                // 如果节点有父节点，尝试将其添加到父节点的子节点列表中
                SubjectNode parent = nodeMap.get(node.parentUuid);
                if (parent != null) {
                    parent.children.add(node);
                }
            }
        }
        // 返回根节点列表，即完整的科目树结构
        return rootNodes;
    }

    public static List<SubjectNode> buildSubjectTree2(SQLiteDatabase db, String targetUuid) {
        List<SubjectNode> allNodes = new ArrayList<>();
        Map<String, SubjectNode> nodeMap = new HashMap<>();

        // 查询目标科目及其所有后代（包含自身）
        String sql = "SELECT s.uuid, s.name, s.parent_uuid, s.path, s.balance_direction, s.initial_amount  "
                + "FROM accounting_subjects s "
                + "WHERE s.uuid IN ("
                + "    SELECT descendant_uuid "
                + "    FROM accounting_subject_closure "
                + "    WHERE ancestor_uuid = ?"
                + ")";

        try (Cursor cursor = db.rawQuery(sql, new String[]{targetUuid})) {
            while (cursor.moveToNext()) {
                String uuid = cursor.getString(0);
                String name = cursor.getString(1);
                String parentUuid = cursor.isNull(2) ? null : cursor.getString(2);
                String path = cursor.getString(3);
                int direction = cursor.getInt(4);
                BigDecimal initialAmount = new BigDecimal(
                        cursor.isNull(5) ? "0" : cursor.getString(5)
                );
                SubjectNode node = new SubjectNode(uuid, name, parentUuid, path, direction);
                node.setInitialAmount(initialAmount);
                allNodes.add(node);
                nodeMap.put(uuid, node);
            }
        }

        // 构建树结构（明确以 targetUuid 为根）
        List<SubjectNode> rootNodes = new ArrayList<>();
        SubjectNode targetRoot = nodeMap.get(targetUuid);
        if (targetRoot != null) {
            rootNodes.add(targetRoot);
            for (SubjectNode node : allNodes) {
                if (node.parentUuid != null && !node.uuid.equals(targetUuid)) {
                    SubjectNode parent = nodeMap.get(node.parentUuid);
                    if (parent != null) {
                        parent.children.add(node);
                    }
                }
            }
        }
        return rootNodes;
    }

    public static List<SubjectNode> getAllSubjects(SQLiteDatabase db) {
        List<SubjectNode> subjects = new ArrayList<>();

        // 添加path字段查询
        String sql = "SELECT uuid, name, parent_uuid, path , balance_direction FROM accounting_subjects";

        try (Cursor cursor = db.rawQuery(sql, null)) {
            while (cursor.moveToNext()) {
                String uuid = cursor.getString(0);
                String name = cursor.getString(1);
                String parentUuid = cursor.isNull(2) ? null : cursor.getString(2);
                String path = cursor.getString(3);
                int direction = cursor.getInt(4);
                subjects.add(new SubjectNode(uuid, name, parentUuid, path, direction));
            }
        }
        return subjects;
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

    public int getDirection() {return direction;}

    public BigDecimal getInitialAmount() {
        return initialAmount;
    }

    public void setInitialAmount(BigDecimal initialAmount) {
        this.initialAmount = initialAmount;
    }

    @NonNull
    public String toString() {
        return name;
    }
}
