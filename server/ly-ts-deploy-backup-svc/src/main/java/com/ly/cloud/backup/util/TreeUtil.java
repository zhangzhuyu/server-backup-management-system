package com.ly.cloud.backup.util;

import com.ly.cloud.backup.vo.TreeSelectVo;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 构建树结构工具类
 *
 * @author admin
 */
@Slf4j
public class TreeUtil {

    public static final String PARENT_COLUMN_DEFAULT_NAME = "value";
    public static final String PID_COLUMN_DEFAULT_NAME = "parentValue";
    public static final String ROOT_DEFAULT_VALUE = "-1";
    public static final String CHILDREN_DEFAULT_NAME = "children";

    /**
     * 先根据List数据集合获取每一组树的根集合，然后将根集合和数据List集合进行递归得到树
     *
     * @param nodes : 全部数据节点
     * @return List<T> : 构建后的数据集合
     */
    public static <T> List<T> getTree(List<T> nodes) throws Exception {
        return getTree(nodes, PARENT_COLUMN_DEFAULT_NAME, PID_COLUMN_DEFAULT_NAME, ROOT_DEFAULT_VALUE, CHILDREN_DEFAULT_NAME);
    }

    /**
     * 先根据List数据集合获取每一组树的根集合，然后将根集合和数据List集合进行递归得到树
     *
     * @param nodes            : 全部数据节点
     * @param parentColumnName : 对象根ID的属性名
     * @param pidColumnName    : 对象pid的属性名
     * @param rootValue        : 根的属性值为多少
     * @param childrenName     : 对象装子节点的属性名
     * @return List<T> : 构建后的数据集合
     */
    public static <T> List<T> getTree(List<T> nodes, String parentColumnName, String pidColumnName, String rootValue, String childrenName) throws Exception {
        long start = System.currentTimeMillis();
        if (UsualUtil.collIsEmpty(nodes) || UsualUtil.strIsEmpty(parentColumnName, pidColumnName, rootValue, childrenName)) {
            return new ArrayList<>();
        }
        List<T> root = buildTreeNode(nodes, pidColumnName, rootValue);
        List<T> res = buildTree(nodes, root, parentColumnName, pidColumnName, childrenName);
        long end = System.currentTimeMillis();
        log.info("构建树完成耗时:{}", end - start);
        return res;
    }

    /**
     * 循环根的集合， 将根的id和数据集合的父id进行遍历对比整合为子集合，然后继续将子集合作为根进行递归，最后将子集合存入根对象中
     *
     * @param nodes            : 全部数据节点
     * @param root             : 根节点的集合
     * @param parentColumnName : 对象根ID的属性名
     * @param pidColumnName    : 对象pid的属性名
     * @param childrenName     : 对象装子节点的属性名
     * @return List<T> : 构建后的数据集合
     */
    private static <T> List<T> buildTree(List<T> nodes, List<T> root, String parentColumnName, String pidColumnName, String childrenName) throws Exception {
        for (T rootT : root) {
            Class<?> tClass = rootT.getClass();
            Field rootField = tClass.getDeclaredField(parentColumnName);
            Field childrenNameField = tClass.getDeclaredField(childrenName);
            rootField.setAccessible(true);
            childrenNameField.setAccessible(true);
            Object id = rootField.get(rootT);
            List<T> children = buildTreeNode(nodes, pidColumnName, id);
            buildTree(nodes, children, parentColumnName, pidColumnName, childrenName);
            childrenNameField.set(rootT, children);
        }
        return root;
    }

    /**
     * 递归
     *
     * @param nodes      : 数据节点集合
     * @param columnName : 属性名
     * @param id         : id值，主键值
     * @return List<T> : 数据集合
     */
    private static <T> List<T> buildTreeNode(List<T> nodes, String columnName, Object id) throws Exception {
        List<T> res = new ArrayList<>();
        Iterator<T> iterator = nodes.iterator();
        while (iterator.hasNext()) {
            T t = iterator.next();
            Field field = t.getClass().getDeclaredField(columnName);
            field.setAccessible(true);
            Object pid = field.get(t);
            if (UsualUtil.objEquals(pid, id)) {
                res.add(t);
                iterator.remove();
            }
        }
        return res;
    }

    /**
     * 构建List<TreeSelectVo>树数据
     *
     * @param treeSelectVos : 全部数据节点
     * @return List<TreeSelectVo> : 构建后的数据集合
     */
    public static List<TreeSelectVo> buildTree(List<TreeSelectVo> treeSelectVos) {
        // 先选出非顶级的节点
        List<TreeSelectVo> list = treeSelectVos.stream().filter(node -> UsualUtil.objUnequals(node.getParentValue(), ROOT_DEFAULT_VALUE)).collect(Collectors.toList());
        // 将这些非顶级节点的数据按parentValue进行分组
        Map<String, List<TreeSelectVo>> children = list.stream().collect(Collectors.groupingBy(TreeSelectVo::getParentValue));
        // 循环设置对应的子节点（根据 value = parentValue）
        treeSelectVos.forEach(node -> node.setChildren(children.get(node.getValue())));
        // 过滤掉父节点数据
        return treeSelectVos.stream().filter(node -> UsualUtil.objEquals(node.getParentValue(), ROOT_DEFAULT_VALUE)).collect(Collectors.toList());
    }

}

