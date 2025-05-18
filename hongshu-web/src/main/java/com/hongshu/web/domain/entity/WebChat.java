package com.hongshu.web.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hongshu.web.domain.HongshuBaseEntity;
import lombok.Data;

/**
 * 聊天
 *
 * @Author hongshu
 */
@Data
@TableName("web_chat")
public class WebChat extends HongshuBaseEntity {

    /**
     * 发送方用户ID
     */
    private String sendUid;

    /**
     * 接收方用户ID
     */
    private String acceptUid;

    /**
     * 聊天内容
     */
    private String content;

    /**
     * 聊天类型（0：私聊 1：群聊）
     */
    private Integer chatType;

    /**
     * 信息类型（0：通知 1：文本 2：图片 3：语音 4：视频 5：自定义消息）
     */
    private Integer msgType;

    /**
     * 时间戳
     */
    private long timestamp;
}
