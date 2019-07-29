package cn.huoguo;

import cn.huoguo.common.Lock;
import cn.huoguo.zookeeper.ZookeeperLock;

import java.io.IOException;
import java.util.Random;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws IOException, InterruptedException {
        for (int i = 0; i < 200; i++) {
            new Thread(() -> {
                Lock lock = new ZookeeperLock(new Random().nextInt(5000) + "号");
                lock.lock();
                lock.unlock();
            }).start();
        }
    }
}
