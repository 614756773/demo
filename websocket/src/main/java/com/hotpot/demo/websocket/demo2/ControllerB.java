package com.hotpot.demo.websocket.demo2;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.Session;
import java.io.IOException;

/**
 * @author qinzhu
 * @since 2019/10/31
 */
@RestController
public class ControllerB {

    @GetMapping("/demo2/push/{id}")
    public String push(@PathVariable String id, String msg) throws IOException {
        Session session = WebSocketServer.getMap().get(id);
        session.getBasicRemote().sendText(msg);
        return String.format("已发送消息【%s】给【%s】", id, msg);
    }
}
