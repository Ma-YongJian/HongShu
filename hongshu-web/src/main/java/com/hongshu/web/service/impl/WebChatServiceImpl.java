package com.hongshu.web.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hongshu.common.constant.ImConstant;
import com.hongshu.common.constant.UserConstant;
import com.hongshu.common.utils.ConvertUtils;
import com.hongshu.common.utils.RedisUtils;
import com.hongshu.common.utils.WebUtils;
import com.hongshu.web.auth.AuthContextHolder;
import com.hongshu.web.domain.entity.WebChat;
import com.hongshu.web.domain.entity.WebChatUserRelation;
import com.hongshu.web.domain.entity.WebUser;
import com.hongshu.web.domain.vo.ChatUserRelationVO;
import com.hongshu.web.mapper.WebChatMapper;
import com.hongshu.web.mapper.WebChatUserRelationMapper;
import com.hongshu.web.mapper.WebUserMapper;
import com.hongshu.web.service.IWebChatService;
import com.hongshu.web.websocket.WebSocketServer;
import com.hongshu.web.websocket.factory.ChatCountMessage;
import com.hongshu.web.websocket.factory.ChatUserMessage;
import com.hongshu.web.websocket.factory.MessageFactory;
import com.hongshu.web.websocket.im.CountMessage;
import com.hongshu.web.websocket.im.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * 聊天
 *
 * @Author hongshu
 */
@Service
public class WebChatServiceImpl extends ServiceImpl<WebChatMapper, WebChat> implements IWebChatService {

    @Autowired
    WebUserMapper userMapper;
    @Autowired
    WebSocketServer webSocketServer;
    @Autowired
    WebChatUserRelationMapper chatUserRelationMapper;
    @Autowired
    RedisUtils redisUtils;


    /**
     * 发送消息
     *
     * @param message 消息
     */
    @Override
    public void sendMsg(Message message) {
        webSocketServer.sendInfo(message);
        MessageFactory messageFactory = null;

        // 过滤发送的请求类型
        switch (message.getMsgType()) {
            case 0:
                messageFactory = new ChatCountMessage(redisUtils);
                break;
            case 1:
                messageFactory = new ChatUserMessage(webSocketServer, this, userMapper, chatUserRelationMapper);
                break;
            default:
                break;
        }
        if (messageFactory != null) {
            messageFactory.sendMessage(message);
        }
    }

    /**
     * 获取所有聊天记录
     *
     * @param currentPage 分页
     * @param pageSize    分页数
     * @param acceptUid   接收方用户ID
     */
    @Override
    public Page<WebChat> getAllChatRecord(long currentPage, long pageSize, String acceptUid) {
        String currentUid = AuthContextHolder.getUserId();
        QueryWrapper<WebChat> queryWrapper = new QueryWrapper<WebChat>().and(e -> e.eq("send_uid", currentUid).eq("accept_uid", acceptUid)).or(e -> e.eq("send_uid", acceptUid).eq("accept_uid", currentUid)).orderByDesc("timestamp");
        return this.page(new Page<>((int) currentPage, (int) pageSize), queryWrapper);
    }

    /**
     * 获取当前用户下所有聊天的用户信息
     */
    @Override
    public List<ChatUserRelationVO> getChatUserList() {
        String currentUid = AuthContextHolder.getUserId();
        List<ChatUserRelationVO> result = new ArrayList<>();
        List<WebChatUserRelation> chatUserRelationList = chatUserRelationMapper.selectList(new QueryWrapper<WebChatUserRelation>().eq("accept_uid", currentUid).orderByDesc("timestamp"));
        if (chatUserRelationList.isEmpty()) {
            return result;
        }
        Set<String> uids = chatUserRelationList.stream().map(WebChatUserRelation::getSendUid).collect(Collectors.toSet());
        Map<String, WebUser> userMap = userMapper.selectBatchIds(uids).stream().collect(Collectors.toMap(WebUser::getId, user -> user));

        chatUserRelationList.forEach(item -> {
            ChatUserRelationVO chatUserRelationVo = ConvertUtils.sourceToTarget(item, ChatUserRelationVO.class);
            WebUser user = userMap.get(item.getSendUid());
            chatUserRelationVo.setUid(String.valueOf(user.getId()));
            chatUserRelationVo.setUsername(user.getUsername());
            chatUserRelationVo.setAvatar(user.getAvatar());
            result.add(chatUserRelationVo);
        });
        return result;
    }

    /**
     * 获取所有聊天记录数量
     */
    @Override
    public CountMessage getCountMessage() {
        String currentUid = AuthContextHolder.getUserId();
        String messageCountKey = ImConstant.MESSAGE_COUNT_KEY + currentUid;
        if (Boolean.FALSE.equals(redisUtils.hasKey(messageCountKey))) {
            CountMessage countMessage = new CountMessage();
            countMessage.setFollowCount(0L);
            countMessage.setCommentCount(0L);
            countMessage.setLikeOrCollectionCount(0L);
            return countMessage;
        }
        String json = redisUtils.get(messageCountKey);
        return JSONUtil.toBean(json, CountMessage.class);
    }

    /**
     * 清除聊天数量
     *
     * @param sendUid 发送方用户ID
     * @param type    类型
     */
    @Override
    public void clearMessageCount(String sendUid, Integer type) {
        if (type == 3) {
            String currentUid = AuthContextHolder.getUserId();
            WebChatUserRelation chatUserRelation = chatUserRelationMapper
                    .selectOne(new QueryWrapper<WebChatUserRelation>().eq("send_uid", sendUid).eq("accept_uid", currentUid));
            if (chatUserRelation != null) {
                chatUserRelation.setCount(0);
                chatUserRelationMapper.updateById(chatUserRelation);
            }
        } else {
            String messageCountKey = ImConstant.MESSAGE_COUNT_KEY + sendUid;
            String json = redisUtils.get(messageCountKey);
            CountMessage countMessage = JSONUtil.toBean(json, CountMessage.class);
            switch (type) {
                case 0:
                    countMessage.setLikeOrCollectionCount(0L);
                    break;
                case 1:
                    countMessage.setCommentCount(0L);
                    break;
                default:
                    countMessage.setFollowCount(0L);
                    break;
            }
            redisUtils.set(messageCountKey, JSONUtil.toJsonStr(countMessage));
        }
    }

    /**
     * 关闭聊天
     *
     * @param sendUid 发送方用户ID
     */
    @Override
    public boolean closeChat(String sendUid) {
        try {
            webSocketServer.onClose(sendUid);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
