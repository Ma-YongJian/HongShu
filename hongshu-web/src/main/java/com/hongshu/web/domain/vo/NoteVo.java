package com.hongshu.web.domain.vo;

import com.hongshu.web.domain.entity.WebTag;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 笔记
 *
 * @Author hongshu
 */
@Data
@Accessors(chain = true)
public class NoteVO implements Serializable {

    private String id;

    private String title;

    private String content;

    private String noteCover;

    private String uid;

    private String username;

    private String avatar;

    private String urls;

    private String cid;

    private String cpid;

    //图片数量
    private Integer count;

    //类型（图片或视频）
    private Integer type;

    private Long likeCount;

    private Long collectionCount;

    private Long commentCount;

    private List<WebTag> tagList;

    private Long time;

    private String pinned;

    //点赞关注收藏
    private Boolean isFollow;

    private Boolean isLike;

    private Boolean isCollection;
}
