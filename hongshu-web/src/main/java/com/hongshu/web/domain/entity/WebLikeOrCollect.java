package com.hongshu.web.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hongshu.web.domain.HongshuBaseEntity;
import lombok.Data;

/**
 * 收藏
 *
 * @Author hongshu
 */
@TableName("web_like_or_collect")
@Data
public class WebLikeOrCollect extends HongshuBaseEntity {

    /**
     * 点赞用户ID
     */
    private String uid;

    /**
     * 点赞和收藏的ID（可能是图片/评论）
     */
    private String likeOrCollectionId;

    /**
     * 点赞和收藏通知的用户ID
     */
    private String publishUid;

    /**
     * 点赞和收藏类型（1：点赞图片 2：点赞评论 3：收藏图片 4：收藏专辑）
     */
    private Integer type;

    /**
     * 时间戳
     */
    private long timestamp;
}
