package com.hongshu.web.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 聊天用户
 *
 * @Author hongshu
 */
@Data
@TableName("web_chat_user_relation")
public class WebChatUserRelation implements Serializable {

    /**
     * 主键ID
     */
    private String id;

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
     * 用户未读数量
     */
    private Integer count;

    /**
     * 聊天类型（0：私聊 1：群聊）
     */
    private Integer chatType;

    /**
     * 消息类型（0：文本 1：图片 2：语音 3：视频）
     */
    private Integer msgType;

    /**
     * 时间戳
     */
    private long timestamp;

    /**
     * 创建者
     */
    @TableField(fill = FieldFill.INSERT)
    private String creator;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}
