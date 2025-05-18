package com.hongshu.web.websocket.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.hongshu.web.websocket.im.Message;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 * websocket配置
 *
 * @Author hongshu
 */
public class ServerEncoder implements Encoder.Text<Message> {

    @Override
    public void destroy() {
        // TODO Auto-generated method stub
        // 这里不重要
    }

    @Override
    public void init(EndpointConfig arg0) {
        // TODO Auto-generated method stub
        // 这里也不重要

    }

    /*
     *  encode()方法里的参数和Text<T>里的T一致，如果你是Student，这里就是encode（Student student）
     */
    @Override
    public String encode(Message message) throws EncodeException {
        try {
            /*
             * 这里是重点，只需要返回Object序列化后的json字符串就行
             * 你也可以使用gosn，fastJson来序列化。
             */
            JsonMapper jsonMapper = new JsonMapper();
            return jsonMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
