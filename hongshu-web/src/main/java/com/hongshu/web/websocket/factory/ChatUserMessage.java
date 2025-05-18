package com.hongshu.web.websocket.factory;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hongshu.common.utils.ConvertUtils;
import com.hongshu.web.domain.entity.WebChat;
import com.hongshu.web.domain.entity.WebChatUserRelation;
import com.hongshu.web.domain.entity.WebUser;
import com.hongshu.web.domain.vo.ChatUserRelationVO;
import com.hongshu.web.websocket.im.Message;
import com.hongshu.web.mapper.WebChatUserRelationMapper;
import com.hongshu.web.mapper.WebUserMapper;
import com.hongshu.web.service.IWebChatService;
import com.hongshu.web.websocket.WebSocketServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * @Author hongshu
 */
public class ChatUserMessage implements MessageFactory {

    private final WebSocketServer webSocketServer;

    private final IWebChatService IWebChatService;

    private final WebUserMapper userMapper;

    private final WebChatUserRelationMapper chatUserRelationMapper;

    public ChatUserMessage(WebSocketServer webSocketServer, IWebChatService IWebChatService, WebUserMapper userMapper, WebChatUserRelationMapper chatUserRelationMapper) {
        this.webSocketServer = webSocketServer;
        this.IWebChatService = IWebChatService;
        this.userMapper = userMapper;
        this.chatUserRelationMapper = chatUserRelationMapper;
    }

    @Override
    public void sendMessage(Message message) {
        String content = String.valueOf(message.getContent());
        // 当前用户插入
        saveMessage(message, 0);
        saveMessage(message, 1);

        getUserChatList(message, 0);
        getUserChatList(message, 1);

        WebChat chat = ConvertUtils.sourceToTarget(message, WebChat.class);
        chat.setTimestamp(System.currentTimeMillis());
        chat.setContent(content);
        IWebChatService.save(chat);
    }

    private void saveMessage(Message message, Integer type) {
        String content = String.valueOf(message.getContent());
        String sendUid = type == 0 ? message.getSendUid() : message.getAcceptUid();
        String acceptUid = type == 0 ? message.getAcceptUid() : message.getSendUid();
        WebChatUserRelation chatUserRelation = chatUserRelationMapper.selectOne(new QueryWrapper<WebChatUserRelation>().eq("send_uid", sendUid).eq("accept_uid", acceptUid));

        if (chatUserRelation != null) {
            chatUserRelation.setContent(content);
            chatUserRelation.setTimestamp(System.currentTimeMillis());
            chatUserRelation.setCount(type == 0 ? chatUserRelation.getCount() + 1 : 0);
            chatUserRelationMapper.updateById(chatUserRelation);
        } else {
            chatUserRelation = new WebChatUserRelation();
            chatUserRelation.setSendUid(sendUid);
            chatUserRelation.setAcceptUid(acceptUid);
            chatUserRelation.setCount(type == 0 ? 1 : 0);
            chatUserRelation.setContent(content);
            chatUserRelation.setTimestamp(System.currentTimeMillis());
            chatUserRelationMapper.insert(chatUserRelation);
        }
    }

    public void getUserChatList(Message message, Integer type) {
        String acceptUid = type == 0 ? message.getSendUid() : message.getAcceptUid();
        List<WebChatUserRelation> chatUserRelationList = chatUserRelationMapper.selectList(new QueryWrapper<WebChatUserRelation>().eq("accept_uid", acceptUid).orderByDesc("create_time"));
        Set<String> uids = chatUserRelationList.stream().map(WebChatUserRelation::getSendUid).collect(Collectors.toSet());
        Map<String, WebUser> userMap = userMapper.selectBatchIds(uids).stream().collect(Collectors.toMap(WebUser::getId, user -> user));
        List<ChatUserRelationVO> chatUserRelationVOList = new ArrayList<>();
        chatUserRelationList.forEach(item -> {
            ChatUserRelationVO chatUserRelationVo = ConvertUtils.sourceToTarget(item, ChatUserRelationVO.class);
            WebUser user = userMap.get(item.getSendUid());
            chatUserRelationVo.setUid(String.valueOf(user.getId()));
            chatUserRelationVo.setUsername(user.getUsername());
            chatUserRelationVo.setAvatar(user.getAvatar());
            chatUserRelationVOList.add(chatUserRelationVo);
        });

        Message currentUserMessage = new Message();
        currentUserMessage.setAcceptUid(acceptUid);
        currentUserMessage.setContent(chatUserRelationVOList);
        currentUserMessage.setMsgType(5);
        webSocketServer.sendInfo(currentUserMessage);
    }
}
