package cn.hotpot.demo.rabbitmq.route;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author qinzhu
 * @since 2019/11/19
 */
public class Sender {

    private static final String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
            send("info", channel);
            send("debug", channel);
            send("error", channel);
        }
    }

    private static void send(String routingKey, Channel channel) throws IOException {
        channel.basicPublish(EXCHANGE_NAME, routingKey, null, ("这是" + routingKey + "消息").getBytes(StandardCharsets.UTF_8));
    }
}
