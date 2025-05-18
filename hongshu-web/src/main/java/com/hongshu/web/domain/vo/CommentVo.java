package com.hongshu.web.domain.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 评论
 *
 * @Author hongshu
 */
@Data
@Accessors(chain = true)
public class CommentVO implements Serializable {

    private String id;

    private String pid;

    private String nid;

    private String title;

    private String noteCover;

    private String uid;

    private String username;

    private String avatar;

    private String noteUid;

    private String pushUsername;

    private String replyId;

    private String replyUid;

    private String replyUsername;

    private String replyAvatar;

    private String content;

    private String replyContent;

    private Integer level;

    private Long time;

    private Date createTime;

    private Long likeCount;

    private Boolean isLike;

    private Long twoCommentCount;

    private List<CommentVO> children;

}
