package cn.huoguo;

import cn.huoguo.common.Lock;
import cn.huoguo.mysql.MysqlLock;
import cn.huoguo.zookeeper.ZookeeperLock;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws IOException, InterruptedException {
        mysqlLockTest();
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
