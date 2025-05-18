package com.hongshu.web.domain.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 分类
 *
 * @Author hongshu
 */
@Data
public class WebNavbarVO {

    /**
     * 标题
     */
    private Long id;

    /**
     * 父级ID
     */
    private Long pid;

    /**
     * 标题
     */
    private String title;

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
    private Integer status;

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
    private List<WebNavbarVO> children = new ArrayList<WebNavbarVO>();
}
