package com.hongshu.common.core.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 树形结构
 *
 * @Author hongshu
 */
@Data
public class Tree implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * 菜单名称
     */
    private String title;

    /**
     * 节点名称
     */
    private String name;

    /**
     * 节点路径
     */
    private String path;

    /**
     * 父级id
     */
    private Long parentId;

    /**
     * 等级
     */
    private Integer level;

    /**
     * 类目图片
     */
    private String icon;

    /**
     * 排序
     */
    private Integer rank;


    /**
     * 子类
     */
    private List<Tree> children;

}
