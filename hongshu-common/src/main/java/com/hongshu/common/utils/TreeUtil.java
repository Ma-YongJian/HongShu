package com.hongshu.common.utils;

import com.hongshu.common.core.domain.Tree;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 树形结构递归处理类
 *
 * @Author hongshu
 */
public class TreeUtil {

    private static final String PID = "pid";
    private static final String ID = "id";
    private static final String CHILDREN = "children";

    /**
     * 递归方法
     */
    public static List<Tree> tree(List<?> source) {
        List<Tree> trees = DozerUtil.convertor(source, Tree.class);
        //获取父节点
        List<Tree> collect = trees.stream().filter(m -> m.getParentId() == 0).map(
                (m) -> {
                    m.setChildren(getChildrens(m, trees));
                    return m;
                }
        ).collect(Collectors.toList());
        return collect;
    }


    /**
     * 获取当前节点的所有子节点
     */
    private static List<Tree> getChildrens(Tree root, List<Tree> all) {
        List<Tree> children = all.stream().filter(m -> {
            return Objects.equals(m.getParentId(), root.getId());
        }).map(
                (m) -> {
                    m.setChildren(getChildrens(m, all));
                    return m;
                }
        ).collect(Collectors.toList());
        return children;
    }

    /**
     * 递归方法
     * 必须有参数parentId和children
     */
    public static <T, S> List<T> tree(List<S> source, Class<T> cs) {
        List<T> trees = DozerUtil.convertor(source, cs);
        //获取父节点
        List<T> collect = trees.stream().filter(m -> {
            try {
                Field field = m.getClass().getDeclaredField(PID);
                field.setAccessible(true);
                return Integer.valueOf(field.get(m).toString()).equals(0);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }).map(
                (m) -> {
                    try {
                        Field field = m.getClass().getDeclaredField(CHILDREN);
                        field.setAccessible(true);
                        field.set(m, getChildrens(m, trees));
                        return m;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return m;
                }
        ).collect(Collectors.toList());
        return collect;
    }

    /**
     * 获取当前节点的所有子节点
     */
    private static <T> List<T> getChildrens(T t, List<T> all) {
        List<T> children = all.stream().filter(m -> {
            try {
                Field field = m.getClass().getDeclaredField(PID);
                field.setAccessible(true);
                Field fieldt = t.getClass().getDeclaredField(ID);
                fieldt.setAccessible(true);
                return Objects.equals(field.get(m), fieldt.get(t));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }).map(
                (m) -> {
                    try {
                        Field field = m.getClass().getDeclaredField(CHILDREN);
                        field.setAccessible(true);
                        field.set(m, getChildrens(m, all));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return m;
                }
        ).collect(Collectors.toList());
        return children;
    }

}
