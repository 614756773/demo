package cn.hotpot.demo.rabbitmq.topic;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.nio.charset.StandardCharsets;

/**
 * @author qinzhu
 * @since 2019/11/19
 */
public class Receiver {
    private static final String EXCHANGE_NAME = "topic_animal";

    public static void main(String[] args) throws Exception {
        Receiver receiverA = new Receiver();
        receiverA.receive("lazy.*", "worker-A");

        Receiver receiverB = new Receiver();
        receiverB.receive("#.rabbit", "worker-B");

        Receiver receiverC = new Receiver();
        receiverC.receive("*.orange.*", "worker-C");
    }

    private void receive(String topic, String workerName) throws Exception {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, topic);
        channel.basicConsume(queueName, (consumerTag, message) -> {
            String result = String.format("%s监听：%s，收到消息：%s，该消息的原routingKey：%s", workerName,
                    topic, new String(message.getBody(), StandardCharsets.UTF_8),
                    message.getEnvelope().getRoutingKey());
            System.out.println(result);
        }, consumerTag -> {
        });
    }
}
