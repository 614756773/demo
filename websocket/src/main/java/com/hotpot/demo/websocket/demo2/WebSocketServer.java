package com.hotpot.demo.websocket.demo2;

import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.Map;

/**
 * @author qinzhu
 * @since 2019/10/31
 */
@ServerEndpoint("/websocket/{id}")
@Component
public class WebSocketServer {
    private static Map<String, Session> map = new HashMap<>();

    public static Map<String, Session> getMap() {
        return map;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("id") String id) {
        System.out.println("连接已建立");
        map.put(id, session);
    }

    @OnMessage
    public String onMessage(String message, Session session) {
        System.out.println(String.format("服务端收到来自客户端的消息：【%s】", message));
        return "我收到你的消息了";
    }

    @OnClose
    public void onClose(Session session, @PathParam("id") String id) {
        map.remove(session);
        System.out.println(String.format("已关闭【%s】的连接", id));
    }
}
