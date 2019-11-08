package cn.hotpot.redis.config;

import cn.hotpot.redis.service.RedisSub;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author qinzhu
 * @since 2019/11/8
 */
@Configuration
public class RedisSubConfig {
    @Bean
    public RedisMessageListenerContainer produceContainer(RedisConnectionFactory connectionFactory,
                                                          MessageListener messageListener) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(messageListener, new ChannelTopic("subChannel"));
        return container;
    }

    @Bean
    public MessageListener produceMessageListener() {
        MessageListenerAdapter handleMessage = new MessageListenerAdapter(new RedisSub(), "handleMessage");
        handleMessage.setSerializer(new StringRedisSerializer());
        return handleMessage;
    }
}
