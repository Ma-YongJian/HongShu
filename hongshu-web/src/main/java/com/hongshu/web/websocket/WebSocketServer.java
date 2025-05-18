package com.hongshu.web.websocket;

import com.hongshu.web.websocket.config.ServerEncoder;
import com.hongshu.web.websocket.im.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author hongshu
 */
@ServerEndpoint(value = "/web/ws/{uid}", encoders = {ServerEncoder.class})
@Component
@Slf4j
public class WebSocketServer {
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static final AtomicInteger ONLINE_NUM = new AtomicInteger();

    //concurrent包的线程安全Set，用来存放每个客户端对应的WebSocketServer对象。
    private static final ConcurrentHashMap<String, Session> SESSION_POOLS = new ConcurrentHashMap<>();

    private final Object lockObj = new Object();

    //发送消息
    public void sendMessage(Session session, Message message) {
        if (session != null) {
            synchronized (lockObj) {
                log.info("发送数据={}", message);
                try {
                    session.getBasicRemote().sendObject(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //给指定用户发送信息
    public void sendInfo(Message message) {
        Session session = SESSION_POOLS.get(message.getAcceptUid());
        try {
            sendMessage(session, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 群发消息
    public void broadcast(Message message) {
        for (Session session : SESSION_POOLS.values()) {
            try {
                sendMessage(session, message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //建立连接成功调用
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "uid") String uid) {
        SESSION_POOLS.put(uid, session);
        addOnlineCount();
        log.info("{}加入webSocket！当前人数为={}", uid, ONLINE_NUM);
    }

    //关闭连接时调用
    @OnClose
    public void onClose(@PathParam(value = "uid") String uid) {
        SESSION_POOLS.remove(uid);
        subOnlineCount();
        log.info("{}断开webSocket连接！当前人数为={}", uid, ONLINE_NUM);
    }

    //收到客户端信息后，根据接收人的username把消息推下去或者群发
    // to=-1群发消息
    @OnMessage
    public void onMessage(String message) {
        log.info("收到客户端消息{}", message);
//        broadcast(message);
    }

    //错误时调用
    @OnError
    public void onError(Session session, Throwable throwable) {
        log.error("发生错误", throwable);
        throwable.printStackTrace();
    }

    public static void addOnlineCount() {
        ONLINE_NUM.incrementAndGet();
    }

    public static void subOnlineCount() {
        ONLINE_NUM.decrementAndGet();
    }

    public static AtomicInteger getOnlineNumber() {
        return ONLINE_NUM;
    }

    public static ConcurrentMap<String, Session> getSessionPools() {
        return SESSION_POOLS;
    }
}
