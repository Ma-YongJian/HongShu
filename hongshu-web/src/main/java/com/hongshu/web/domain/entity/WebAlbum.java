package com.hongshu.web.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hongshu.web.domain.HongshuBaseEntity;
import lombok.Data;

/**
 * 专辑
 *
 * @Author hongshu
 */
@Data
@TableName("web_album")
public class WebAlbum extends HongshuBaseEntity {

    /**
     * 标题
     */
    private String title;

    /**
     * 用户ID
     */
    private String uid;

    /**
     * 专辑封面图
     */
    private String albumCover;

    /**
     * 专辑类型（0：默认）
     */
    private Integer type;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 专辑中笔记数量
     */
    private Long imgCount;

    /**
     * 收藏数量
     */
    private Long collectionCount;

}
