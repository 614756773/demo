package hotpot.demo;

import hotpot.demo.common.Lock;
import hotpot.demo.mysql.MysqlLock;
import hotpot.demo.redis.RedisHelper;
import hotpot.demo.redis.RedisLock;
import hotpot.demo.zookeeper.ZookeeperLock;

import java.io.IOException;
import java.util.Random;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws IOException, InterruptedException {
        for (int i = 0; i < 2; i++) {
            new Thread(() -> {
                Lock lock = new RedisLock();
                lock.lock();
            }).start();
        }
    }

    private static void mysqlLockTest() {
        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                Lock lock = new MysqlLock(new Random().nextInt(5000) + "号");
                lock.lock();
                lock.lock();
                lock.unlock();
                lock.unlock();
            }).start();
        }
    }

    private static void zookeeperLockTest() {
        for (int i = 0; i < 200; i++) {
            new Thread(() -> {
                Lock lock = new ZookeeperLock(new Random().nextInt(5000) + "号");
                lock.lock();
                lock.unlock();
            }).start();
        }
    }
}
