package cn.hotpot.demo.rabbitmq.route;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;

import java.nio.charset.StandardCharsets;

/**
 * @author qinzhu
 * @since 2019/11/19
 */
public class Recevier {
    private static final String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] args) throws Exception {
        Recevier recevierA = new Recevier();
        String[] routingKeysA = {"info", "error"};
        recevierA.recevie(routingKeysA, "worker-A");

        Recevier recevierB = new Recevier();
        String[] routingKeysB = {"debug", "error"};
        recevierB.recevie(routingKeysB, "worker-B");
    }

    private void recevie(String[] routingKeys, String workerName) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Channel channel = factory.newConnection().createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        String queueName = channel.queueDeclare().getQueue();
        for (String routingKey : routingKeys) {
            channel.queueBind(queueName, EXCHANGE_NAME, routingKey);
        }
        channel.basicConsume(queueName, false, (consumerTag, message) -> {
            System.out.println(workerName + "路由为：" + message.getEnvelope().getRoutingKey());
            System.out.println(workerName + "消息为：" + new String(message.getBody(), StandardCharsets.UTF_8));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println(workerName + "消息确认\n---------------------------");
                channel.basicAck(message.getEnvelope().getDeliveryTag(), true);
            }
        }, consumerTag -> {
        });

    }
}
