package com.hotpot.demo.websocket.demo1;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

/**
 * @author qinzhu
 * @since 2019/10/31
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSokectConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 启用simpleBroker，使得订阅了/topic的客户端能收到消息
        registry.enableSimpleBroker("/topic");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 配置建立连接的地址
        registry.addEndpoint("/gs-guide-com.hotpot.demo.websocket")
                .addInterceptors(new HttpSessionHandshakeInterceptor())
                .withSockJS();
    }
}
