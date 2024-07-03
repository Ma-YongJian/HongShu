package com.hongshu.web.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hongshu.web.domain.HongshuBaseEntity;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 分类
 *
 * @author: hongshu
 */
@Data
@TableName("web_category")
public class WebCategory extends HongshuBaseEntity {

    /**
     * 标题
     */
    private String title;

    /**
     * 父级ID
     */
    private String pid;

    /**
     * 分类描述
     */
    private String description;

    /**
     * 喜欢数量
     */
    private long likeCount;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态
     */
    private String status;

    /**
     * 分类封面
     */
    private String normalCover;

    /**
     * 热门封面
     */
    private String hotCover;

    /**
     * 子导航栏
     */
    @TableField(exist = false)
    private List<WebCategory> children = new ArrayList<WebCategory>();
}
