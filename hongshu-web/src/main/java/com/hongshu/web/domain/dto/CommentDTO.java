package com.hongshu.web.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 评论
 *
 * @Author hongshu
 */
@Data
@ApiModel(value = "评论DTO")
public class CommentDTO implements Serializable {

    @ApiModelProperty("笔记id")
    private String nid;

    @ApiModelProperty("笔记发布的用户id")
    private String noteUid;

    @ApiModelProperty("评论的父id")
    private String pid;

    @ApiModelProperty("当前评论回复的评论id")
    private String replyId;

    @ApiModelProperty("回复的评论的用户id")
    private String replyUid;

    @ApiModelProperty("评论等级")
    private Integer level;

    @ApiModelProperty("评论内容")
    private String content;
}
