package com.hongshu.server.controller.web;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hongshu.common.enums.Result;
import com.hongshu.common.validator.myVaildator.noLogin.NoLoginIntercept;
import com.hongshu.web.domain.entity.WebChat;
import com.hongshu.web.domain.vo.ChatUserRelationVO;
import com.hongshu.web.websocket.im.CountMessage;
import com.hongshu.web.websocket.im.Message;
import com.hongshu.web.service.IWebChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 聊天
 *
 * @Author hongshu
 */
@RequestMapping("/web/im/chat")
@RestController
public class WebChatController {

    @Autowired
    private IWebChatService chatService;


    /**
     * 发送消息
     *
     * @param message 消息实体
     */
    @PostMapping("sendMsg")
    @NoLoginIntercept
    public Result<?> sendMsg(@RequestBody Message message) {
        chatService.sendMsg(message);
        return Result.ok();
    }

    /**
     * 获取所有的聊天记录
     *
     * @param currentPage 分页
     * @param pageSize    分页数
     * @param acceptUid   接收方用户ID
     */
    @GetMapping("getAllChatRecord/{currentPage}/{pageSize}")
    public Result<?> getAllChatRecord(@PathVariable long currentPage, @PathVariable long pageSize, String acceptUid) {
        Page<WebChat> page = chatService.getAllChatRecord(currentPage, pageSize, acceptUid);
        return Result.ok(page);
    }

    /**
     * 获取当前用户下所有聊天的用户信息
     */
    @GetMapping("getChatUserList")
    public Result<?> getChatUserList() {
        List<ChatUserRelationVO> list = chatService.getChatUserList();
        return Result.ok(list);
    }

    /**
     * 获取所有聊天记录数量
     */
    @GetMapping("getCountMessage")
    public Result<?> getCountMessage() {
        CountMessage countMessage = chatService.getCountMessage();
        return Result.ok(countMessage);
    }

    /**
     * 删除聊天
     */
    @GetMapping("deleteMsg")
    public String deleteMsg() {
        return null;
    }

    /**
     * 删除所有聊天记录
     */
    @GetMapping("deleteAllChatRecord")
    public String deleteAllChatRecord() {
        return null;
    }

    /**
     * 删除所有聊天用户
     */
    @GetMapping("deleteChatUser")
    public String deleteChatUser() {
        return null;
    }

    /**
     * 清除聊天数量
     *
     * @param sendUid 发送方用户ID
     * @param type    类型
     */
    @GetMapping("clearMessageCount")
    public Result<?> clearMessageCount(String sendUid, Integer type) {
        chatService.clearMessageCount(sendUid, type);
        return Result.ok();
    }

    /**
     * 关闭聊天
     *
     * @param sendUid 发送方用户ID
     */
    @RequestMapping("closeChat/{sendUid}")
    public boolean closeChat(@PathVariable("sendUid") String sendUid) {
        return chatService.closeChat(sendUid);
    }
}
