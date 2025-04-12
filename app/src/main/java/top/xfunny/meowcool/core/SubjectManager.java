package top.xfunny.meowcool.core;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.util.Pair;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import top.xfunny.meowcool.core.data.SubjectNode;

public class SubjectManager {
    private SQLiteDatabase db;

    public SubjectManager(SQLiteDatabase db) {
        this.db = db;
    }

    // 插入科目并更新闭包表
    public long insertSubject(String uuid, String name, int balanceDirection, String parentUuid, String path) {
        ContentValues values = new ContentValues();
        values.put("uuid", uuid);
        values.put("name", name);
        values.put("balance_direction", balanceDirection);//借贷方向
        values.put("parent_uuid", parentUuid);//父级科目uuid
        values.put("path", path);//科目路径

        // 开启事务
        db.beginTransaction();
        try {
            // 1. 插入科目
            long result = db.insert("accounting_subjects", null, values);
            if (result == -1) return -1; // 插入失败

            // 2. 更新闭包表
            if (parentUuid == null) {
                // 根节点：仅插入自身关系
                insertClosure(uuid, uuid, 0);
            } else {
                // 继承父节点的所有祖先关系
                String insertClosureSQL =
                        "INSERT INTO accounting_subject_closure (ancestor_uuid, descendant_uuid, depth) " +
                                "SELECT ancestor_uuid, ?, depth + 1 " + // 新节点是父节点后代的后代
                                "FROM accounting_subject_closure " +
                                "WHERE descendant_uuid = ? " +
                                "UNION ALL SELECT ?, ?, 0"; // 插入自身关系
                db.execSQL(insertClosureSQL, new String[]{uuid, parentUuid, uuid, uuid});
            }

            db.setTransactionSuccessful();
            return result;
        } finally {
            db.endTransaction();
        }
    }

    public String getParentSubjectUuid(String uuid) {
        String sql = "SELECT parent_uuid FROM accounting_subjects WHERE uuid = ?";
        String[] args = {uuid};
        String parentUuid = null;
        try (android.database.Cursor cursor = db.rawQuery(sql, args)) {
            if (cursor.moveToFirst()) {
                parentUuid = cursor.getString(0);
            }
        }
        return parentUuid;
    }


    public Pair<List<SubjectNode>,SubjectNode> findUsedDirectChildren(String targetUuid) {
        // 1. 获取目标科目及其所有后代UUID
        List<String> descendantUuids = getDescendantUuids(targetUuid);
        descendantUuids.add(targetUuid); // 包含自身

        // 2. 批量查询被占用的科目UUID
        Set<String> usedUuids = getUsedSubjectUuids(descendantUuids);

        // 3. 构建科目树
        List<SubjectNode> tree = SubjectNode.buildSubjectTree2(db, targetUuid);
        if (tree.isEmpty()) return new Pair<>(new ArrayList<>() , null);
        List<SubjectNode> usedChildren = new ArrayList<>();
        SubjectNode targetNode = tree.get(0);
        // 4. 后序遍历标记占用状态
        postOrderMarkUsage(targetNode, usedUuids);

        // 5. 收集被占用的直接子科目

        for (SubjectNode child : targetNode.children) {
            if (child.isUsed) {
                usedChildren.add(child);
            }
        }
        return new Pair<>(usedChildren,targetNode);
    }

    // 后序遍历核心逻辑
    private void postOrderMarkUsage(SubjectNode node, Set<String> usedUuids) {
        if (node == null) return;

        // 递归处理子节点
        for (SubjectNode child : node.children) {
            postOrderMarkUsage(child, usedUuids);
        }

        // 当前节点是否被占用：自身被引用 或 子节点被占用
        boolean isSelfUsed = usedUuids.contains(node.uuid);
        boolean isAnyChildUsed = node.children.stream().anyMatch(child -> child.isUsed);
        node.isUsed = isSelfUsed || isAnyChildUsed;
    }

    // 获取所有后代UUID（包括自身）
    private List<String> getDescendantUuids(String targetUuid) {
        List<String> uuids = new ArrayList<>();
        String sql = "SELECT descendant_uuid FROM accounting_subject_closure WHERE ancestor_uuid = ?";
        try (Cursor cursor = db.rawQuery(sql, new String[]{targetUuid})) {
            while (cursor.moveToNext()) {
                uuids.add(cursor.getString(0));
            }
        }
        return uuids;
    }

    // 批量查询被占用的UUID
    private Set<String> getUsedSubjectUuids(List<String> uuids) {
        Set<String> used = new HashSet<>();
        if (uuids.isEmpty()) return used;

        String placeholders = String.join(",", Collections.nCopies(uuids.size(), "?"));
        String sql = "SELECT DISTINCT subject_uuid FROM accounting_vouchers WHERE subject_uuid IN (" + placeholders + ")";
        try (Cursor cursor = db.rawQuery(sql, uuids.toArray(new String[0]))) {
            while (cursor.moveToNext()) {
                used.add(cursor.getString(0));
            }
        }
        return used;
    }

    public void modifySubject(String uuid, String name, BigDecimal initialAmount, Integer balanceDirection) {
        ContentValues values = new ContentValues();
        if(name!=null){
            values.put("name", name);
        }

        if(initialAmount!=null){
            values.put("initial_amount", String.valueOf(initialAmount));
        }

        if(balanceDirection!=null){
            values.put("balance_direction", balanceDirection);
        }

        db.update("accounting_subjects", values, "uuid = ?", new String[]{uuid});
    }

    public BigDecimal calculateParentInitialAmount(String targetUuid) {
        // 1. 构建科目树（包含初始金额）
        List<SubjectNode> tree = SubjectNode.buildSubjectTree2(db, targetUuid);
        if (tree.isEmpty()) return BigDecimal.ZERO;

        // 2. 获取根节点
        SubjectNode root = tree.get(0);

        // 3. 后序遍历累加金额
        postOrderSumInitialAmount(root);

        // 4. 返回根节点（目标科目）的汇总金额
        return root.getInitialAmount();
    }

    public int nameCheck(String name) {
        // 预定义的非法字符集合（使用Set优化查找性能）
        final Set<Character> ILLEGAL_CHARS = Set.of(
                '\\', '/', ':', '*', '?', '"', '<', '>', '|'
        );

        // 1. 先检查空值或空白字符串（最高优先级）
        if (name == null || name.trim().isEmpty()) {
            return 2;
        }

        // 2. 检查空格（次优先级）
        if (name.contains(" ")) {
            return 1;
        }

        // 3. 最后检查非法字符
        for (char c : name.toCharArray()) {
            if (ILLEGAL_CHARS.contains(c)) {
                return 3;
            }
        }

        // 所有检查通过
        return 0;
    }

    // 后序遍历累加子节点金额
    private void postOrderSumInitialAmount(SubjectNode node) {
        if (node == null) return;

        // 先处理子节点
        for (SubjectNode child : node.children) {
            postOrderSumInitialAmount(child);
        }

        // 累加所有子节点的金额到当前节点
        BigDecimal childrenSum = node.children.stream()
                .map(SubjectNode::getInitialAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        node.setInitialAmount(
                node.getInitialAmount().add(childrenSum)
        );
    }

    public int deleteSubjectWithChildren(String targetUuid) {
        // 1. 构建目标科目的树结构（包含自身及所有子节点）
        List<SubjectNode> tree = SubjectNode.buildSubjectTree2(db, targetUuid);
        if (tree.isEmpty()) return 0;

        // 2. 收集所有需要删除的UUID（包含自身和子节点）
        List<String> uuidsToDelete = new ArrayList<>();
        collectAllUuids(tree.get(0), uuidsToDelete);

        // 3. 开启事务执行删除
        db.beginTransaction();
        try {
            // 3.1 删除科目表记录
            int deletedCount = deleteSubjects(uuidsToDelete);

            // 3.2 删除闭包表记录
            deleteClosureRelations(uuidsToDelete);

            db.setTransactionSuccessful();
            return deletedCount;
        } finally {
            db.endTransaction();
        }
    }

    private void collectAllUuids(SubjectNode node, List<String> uuids) {
        if (node == null) return;

        // 先处理子节点
        for (SubjectNode child : node.children) {
            collectAllUuids(child, uuids);
        }

        // 最后添加当前节点
        uuids.add(node.uuid);
    }


    private int deleteSubjects(List<String> uuids) {
        if (uuids.isEmpty()) return 0;

        String placeholders = String.join(",", Collections.nCopies(uuids.size(), "?"));
        String sql = "DELETE FROM accounting_subjects WHERE uuid IN (" + placeholders + ")";
        db.execSQL(sql, uuids.toArray(new String[0]));

        // 返回受影响的行数（需要特殊处理）
        return getDeletedCount();
    }


    private void deleteClosureRelations(List<String> uuids) {
        if (uuids.isEmpty()) return;

        String placeholders = String.join(",", Collections.nCopies(uuids.size(), "?"));
        // 删除所有涉及这些UUID的关系（无论是祖先还是后代）
        String sql = "DELETE FROM accounting_subject_closure " +
                "WHERE ancestor_uuid IN (" + placeholders + ") " +
                "   OR descendant_uuid IN (" + placeholders + ")";
        List<String> args = new ArrayList<>(uuids);
        args.addAll(uuids); // 参数需要重复两次
        db.execSQL(sql, args.toArray(new String[0]));
    }


    private int getDeletedCount() {
        try (Cursor cursor = db.rawQuery("SELECT changes()", null)) {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        }
        return 0;
    }

    private void insertClosure(String ancestor, String descendant, int depth) {
        ContentValues cv = new ContentValues();
        cv.put("ancestor_uuid", ancestor);
        cv.put("descendant_uuid", descendant);
        cv.put("depth", depth);
        db.insert("accounting_subject_closure", null, cv);
    }
}
