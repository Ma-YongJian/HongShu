package com.hongshu.web.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hongshu.web.domain.HongshuBaseEntity;
import lombok.Data;

/**
 * 评论
 *
 * @Author hongshu
 */
@Data
@TableName("web_comment")
public class WebComment extends HongshuBaseEntity {

    /**
     * 笔记ID
     */
    private String nid;

    /**
     * 评论关联笔记的用户ID
     */
    private String noteUid;

    /**
     * 发布评论用户
     */
    private String uid;

    /**
     *根评论ID
     */
    private String pid;

    /**
     * 回复的评论ID
     */
    private String replyId;

    /**
     * 回复评论的用户ID
     */
    private String replyUid;

    /**
     * 评论等级
     */
    private Integer level;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评论点赞数量
     */
    private Long likeCount;

    /**
     * 二级评论数量
     */
    private Long twoCommentCount;
}
