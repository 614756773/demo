package com.hotpot.demo.websocket.demo1;

import com.hotpot.demo.websocket.demo1.model.Greeting;
import com.hotpot.demo.websocket.demo1.model.HelloMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author qinzhu
 * @since 2019/10/31
 */
@Controller
public class ControllerA {

    @Autowired
    private SimpMessagingTemplate template;

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting getAndSendMessage(HelloMessage message) {
        System.out.println(String.format("服务端收到消息【%s】", message.getName()));
        return new Greeting("我收到你的消息了");
    }

    @GetMapping("demo1/push")
    @ResponseBody
    public String pushMessageToOne(String str) {
        String destination = "/topic/greetings";
        Greeting message = new Greeting(str);
        template.convertAndSend(destination, message);
        return String.format("推送给订阅了【%s】的客户端，内容为【%s】", destination, str);
    }
}
