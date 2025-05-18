package com.hongshu.web.websocket.factory;

import cn.hutool.json.JSONUtil;
import com.hongshu.common.constant.ImConstant;
import com.hongshu.common.utils.RedisUtils;
import com.hongshu.web.websocket.im.CountMessage;
import com.hongshu.web.websocket.im.Message;

/**
 * @Author hongshu
 */
public class ChatCountMessage implements MessageFactory {

    RedisUtils redisUtils;

    public ChatCountMessage(RedisUtils redisUtils) {
        this.redisUtils = redisUtils;
    }

    @Override
    public void sendMessage(Message message) {
        String messageCountKey = ImConstant.MESSAGE_COUNT_KEY + message.getAcceptUid();
        CountMessage countMessage = JSONUtil.toBean(JSONUtil.toJsonStr(message.getContent()), CountMessage.class);
        countMessage.setUid(message.getAcceptUid());
        redisUtils.set(messageCountKey, JSONUtil.toJsonStr(countMessage));
    }
}
