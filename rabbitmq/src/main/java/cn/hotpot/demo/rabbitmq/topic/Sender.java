package cn.hotpot.demo.rabbitmq.topic;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * @author qinzhu
 * @since 2019/11/19
 */
public class Sender {
    private static final String EXCHANGE_NAME = "topic_animal";

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
            channel.basicPublish(EXCHANGE_NAME, "fast.orange.dog", null, "跑得快的橘色的狗".getBytes("UTF-8"));
            channel.basicPublish(EXCHANGE_NAME, "fast.black.rabbit", null, "跑得快的黑色的兔子".getBytes("UTF-8"));
            channel.basicPublish(EXCHANGE_NAME, "lazy.dark", null, "懒惰的鸭子".getBytes("UTF-8"));
        }
    }
}
