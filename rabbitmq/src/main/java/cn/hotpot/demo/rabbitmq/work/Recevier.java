package cn.hotpot.demo.rabbitmq.work;

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

    private final static String QUEUE_NAME = "work-queue";

    public static void main(String[] args) throws Exception {
        new Recevier().recevie("AA");
        new Recevier().recevie("BB");
    }

    public void recevie(String workName) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        // 设置每次只消费一条信息
        channel.basicQos(1);
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        channel.basicConsume(QUEUE_NAME, true, (consumerTag, message) -> {
            String s = new String(message.getBody(), StandardCharsets.UTF_8);
            // 模拟延时操作，每有一个`.`就休眠1秒钟
            String[] split = s.split("/.");
            for (String str : split) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(workName + "消费了一条消息：" + s);
        }, consumerTag -> {});
    }
}
