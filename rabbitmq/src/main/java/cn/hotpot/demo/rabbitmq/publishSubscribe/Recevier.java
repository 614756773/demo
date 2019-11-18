package cn.hotpot.demo.rabbitmq.publishSubscribe;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * @author qinzhu
 * @since 2019/11/18
 */
public class Recevier {

    private final static String EXCHANGE_NAME = "sub";

    public static void main(String[] args) throws Exception {
        new Recevier().recevie("AA");
        new Recevier().recevie("BB");
    }

    private void recevie(String workName) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
        // 因为是订阅消息，即每个消息接受者都会有一个自己的匿名队列去接受数据，消息生产者发布的消息会被每个接受者都消费一次
        String queueName = channel.queueDeclare().getQueue();
        // 将这个匿名队列绑定到交换器上
        channel.queueBind(queueName, EXCHANGE_NAME, "");
        channel.basicConsume(queueName, true, (consumerTag, message) -> {
            String s = new String(message.getBody(), StandardCharsets.UTF_8);
            System.out.println(workName + "消费了一条消息：" + s);
        }, consumerTag -> {});
    }
}
