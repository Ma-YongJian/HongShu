package com.hongshu.web.websocket.factory;

import com.hongshu.web.websocket.im.Message;

/**
 * @author: hongshu
 */
public interface MessageFactory {
    void sendMessage(Message message);
}
