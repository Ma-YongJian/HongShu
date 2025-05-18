package com.hongshu.web.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 聊天-用户
 *
 * @Author hongshu
 */
@Data
public class ChatUserRelationVO implements Serializable {

    private String id;

    private String uid;

    private String username;

    private String avatar;

    private String content;

    // 用户未读消息数量
    private Integer count;

    // 0是文本消息，1是图片消息，2是语音消息，3是视频消息
    private Integer msgType;

    private long timestamp;
}
