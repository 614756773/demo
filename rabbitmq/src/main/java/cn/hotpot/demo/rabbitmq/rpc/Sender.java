package cn.hotpot.demo.rabbitmq.rpc;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Semaphore;

/**
 * @author qinzhu
 * @since 2019/11/19
 */
public class Sender {

    private static String requestQueueName = "rpc_queue";

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        String correlationId = UUID.randomUUID().toString();
        String queueName = channel.queueDeclare().getQueue();
        rpcCall(channel, correlationId, queueName);

        // 使用信号量来阻塞主线程，不然还没接收到回调主线程就被关闭了
        Semaphore semaphore = new Semaphore(1);
        semaphore.acquire();
        rpcResult(channel, queueName, correlationId, semaphore);
        semaphore.acquire();
        System.out.println("over");
        channel.close();
        connection.close();
    }

    /**
     * 使用消息，调用远程服务
     */
    private static void rpcCall(Channel channel, String correlationId, String queueName) throws IOException {
        AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();
        AMQP.BasicProperties properties = builder.correlationId(correlationId)
                .replyTo(queueName)
                .build();
        System.out.println("开始调用远程服务，用于计算10的阶乘");
        channel.basicPublish("", requestQueueName, properties, "10".getBytes("utf-8"));
    }

    /**
     * rpc响应的结果
     */
    private static void rpcResult(Channel channel, String queueName, String correlationId, Semaphore semaphore) throws IOException {
        channel.basicConsume(queueName, (consumerTag, message) -> {
            String id = message.getProperties().getCorrelationId();
            if (!id.equals(correlationId)) {
                semaphore.release();
                System.out.println("不是属于自己的回调");
                return;
            }
            System.out.println("rpc调用的结果：" + new String(message.getBody(), "utf-8"));
            semaphore.release();
        }, consumerTag -> {
            semaphore.release();
        });
    }
}
