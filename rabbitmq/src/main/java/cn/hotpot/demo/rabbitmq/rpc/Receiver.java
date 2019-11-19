package cn.hotpot.demo.rabbitmq.rpc;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * @author qinzhu
 * @since 2019/11/19
 */
public class Receiver {

    private static String requestQueueName = "rpc_queue";

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(requestQueueName, false, false, true, null);
        // 一次只处理一个请求
        channel.basicQos(1);
        channel.basicConsume(requestQueueName, false, (consumerTag, message) -> {
            // 处理请求
            Integer number = Integer.valueOf(new String(message.getBody()));
            int result = factorial(number);
            String returnMessage = result > 0 ? String.valueOf(result) : "参数不能小于0";
            channel.basicAck(message.getEnvelope().getDeliveryTag(), false);

            // 响应结果
            AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();
            AMQP.BasicProperties properties = builder.correlationId(message.getProperties().getCorrelationId())
                    .build();
            channel.basicPublish("", message.getProperties().getReplyTo(), properties, returnMessage.getBytes("UTF-8"));
        }, consumerTag -> {
        });
    }

    private static int factorial(Integer number) {
        System.out.println("RPC服务端，开始进行阶乘计算");
        if (number == 0) {
            return 0;
        }
        if (number < 0) {
            return -1;
        }
        int result = 1;
        for (int i = number; i > 0; i--) {
            result *= i;
        }
        System.out.println("参数：" + number + "结果：" + result);
        return result;
    }
}
