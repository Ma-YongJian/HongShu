package com.hongshu.web.websocket.factory;

import com.hongshu.web.websocket.im.Message;

/**
 * @Author hongshu
 */
public interface MessageFactory {

    void sendMessage(Message message);
}
