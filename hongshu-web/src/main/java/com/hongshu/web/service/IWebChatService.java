package com.hongshu.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hongshu.web.domain.entity.WebChat;
import com.hongshu.web.domain.vo.ChatUserRelationVO;
import com.hongshu.web.websocket.im.CountMessage;
import com.hongshu.web.websocket.im.Message;

import java.util.List;

/**
 * 聊天
 *
 * @Author hongshu
 */
public interface IWebChatService extends IService<WebChat> {

    /**
     * 发送消息
     *
     * @param message 消息
     */
    void sendMsg(Message message);

    /**
     * 获取所有聊天记录
     *
     * @param currentPage 分页
     * @param pageSize    分页数
     * @param acceptUid   接收方用户ID
     */
    Page<WebChat> getAllChatRecord(long currentPage, long pageSize, String acceptUid);

    /**
     * 获取当前用户下所有聊天的用户信息
     */
    List<ChatUserRelationVO> getChatUserList();

    /**
     * 获取所有聊天记录数量
     */
    CountMessage getCountMessage();

    /**
     * 清除聊天数量
     *
     * @param sendUid 发送方用户ID
     * @param type    类型
     */
    void clearMessageCount(String sendUid, Integer type);

    /**
     * 关闭聊天
     *
     * @param sendUid 发送方用户ID
     */
    boolean closeChat(String sendUid);
}
