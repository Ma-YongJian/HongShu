package com.hongshu.web.websocket.im;

import cn.hutool.json.JSONUtil;
import com.hongshu.common.constant.ImConstant;
import com.hongshu.common.utils.RedisUtils;
import com.hongshu.web.service.IWebChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author hongshu
 */
@Component
public class ChatUtils {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    IWebChatService IWebChatService;

    /**
     * @param userId
     * @param type   0 点赞 1 评论 2 关注
     */
    public void sendMessage(String userId, Integer type) {
        String messageCountKey = ImConstant.MESSAGE_COUNT_KEY + userId;
        CountMessage countMessage = null;
        if (Boolean.TRUE.equals(redisUtils.hasKey(messageCountKey))) {
            String json = redisUtils.get(messageCountKey);
            countMessage = JSONUtil.toBean(json, CountMessage.class);
        } else {
            countMessage = new CountMessage();
            countMessage.setLikeOrCollectionCount(0L);
            countMessage.setFollowCount(0L);
            countMessage.setCommentCount(0L);
            redisUtils.set(messageCountKey, JSONUtil.toJsonStr(countMessage));
        }

        switch (type) {
            case 0:
                countMessage.setLikeOrCollectionCount(countMessage.getLikeOrCollectionCount() + 1);
                break;
            case 1:
                countMessage.setCommentCount(countMessage.getCommentCount() + 1);
                break;
            default:
                countMessage.setFollowCount(countMessage.getFollowCount() + 1);
                break;
        }
        Message message = new Message();
        message.setContent(countMessage);
        message.setMsgType(0);
        message.setAcceptUid(userId);
        IWebChatService.sendMsg(message);
    }
}
