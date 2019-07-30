package cn.huoguo.zookeeper;

import cn.huoguo.common.Lock;
import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @Date: 2019/7/26
 * @Author: qinzhu
 */
public class ZookeeperLock implements Lock {

    private static ZooKeeper zk;

    private static final String LOCK_PATH = "/lock/";

    private static final String LOCK_NAME = "/lock";

    private String name;

    private String currentNode;

    private static CountDownLatch parentNodeExist = new CountDownLatch(1);

    private static final Logger log = LoggerFactory.getLogger(ZookeeperLock.class);

    static {
        // 创建单例zk对象
        if (zk == null) {
            synchronized (ZookeeperLock.class) {
                if (zk == null) {
                    try {
                        zk = new ZooKeeper("127.0.0.1:2181", 100, e -> {
                            log.info("zookeeper连接已建立");
                            createParentNode();
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 保证父节点存在
     */
    private static void createParentNode() {
        try {
            // 如果没有父节点则创建
            List<String> children = zk.getChildren("/", false);
            if (!children.contains(LOCK_PATH.replaceAll("\\/", ""))) {
                log.info(String.format("创建父目录:[%s]", LOCK_PATH.replaceAll("\\/", "")));
                new Thread(() -> {
                    zk.create(LOCK_NAME, "test data".getBytes(),
                            ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT,
                            (rc, path, ctx, name) -> {
                                if (rc == KeeperException.Code.OK.intValue()) {
                                    parentNodeExist.countDown();
                                }
                            }, null);
                }).start();
            } else {
                // 有父节点则直接通知其余操作可执行
                parentNodeExist.countDown();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public ZookeeperLock() {
    }

    public ZookeeperLock(String name) {
        this.name = name;
    }

    /**
     * 获取锁
     */
    @Override
    public void lock() {
        CountDownLatch lockLatch = new CountDownLatch(1);
        ensureParentNodeExist();

        zk.create(LOCK_PATH, "test data".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL, (result, path, o, currentNodeFullName) -> {
            if (result == KeeperException.Code.OK.intValue()) {
                currentNode = currentNodeFullName;
                try {
                    List<String> children = zk.getChildren(LOCK_NAME, false);
                    tryLock(currentNodeFullName, children, lockLatch);
                } catch (KeeperException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, null);

        wait(lockLatch);
    }

    /**
     * 释放锁
     */
    @Override
    public void unlock() {
        try {
            zk.delete(currentNode, 0);
            log.info(name + "释放锁");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 等待父节点创建完毕
     */
    private void ensureParentNodeExist() {
        try {
            parentNodeExist.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断当前节点是否最小.如果是则说明获得锁
     *
     * @param currentNodeFullName 当前节点的全路径名称,如: /lock/0000002
     * @param children            兄弟节点的名称,如:0000000,0000001,0000003
     */
    private void tryLock(String currentNodeFullName, List<String> children, CountDownLatch latch) throws KeeperException, InterruptedException {
        if (children.size() == 0 || isMinNode(currentNodeFullName, children)) {
            latch.countDown();
            log.info(name + "获得锁");
        } else {
            List<String> list = zk.getChildren(LOCK_NAME, createWatcher(currentNodeFullName, latch), null);
            log.info(name + "已注册监听器");
            // 下面这的重复判断尤其重要,是为了防止如下情况：
            // 如果前一个节点0001刚好获取锁完毕,然后释放了节点,这时当前节点0002才注册监听器
            // 就会导致当前节点0002一直等待前一个节点0001删除,然而前一个节点早就删除了,所以永远不会触发监听事件
            if (children.size() == 0 || isMinNode(currentNodeFullName, list)) {
                latch.countDown();
                log.info(name + "获得锁");
            }
        }
    }

    /**
     * 创建监听器
     * 因为zk中的监听器只会触发一次,所以当上次触发后但是又发现当前节点还不是最小的,所以需要继续监听
     */
    private Watcher createWatcher(String currentNodeFullName, CountDownLatch latch) {
        return event -> {
            try {
                log.info(String.format("%s发现状态发生变化:[%s]", name, event.getType()));
                if (event.getType() == Watcher.Event.EventType.NodeChildrenChanged) {
                    List<String> children = zk.getChildren(LOCK_NAME, false);
                    tryLock(currentNodeFullName, children, latch);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    private boolean isMinNode(String currentNodeFullName, List<String> children) {
        Integer min = children.stream()
                .map(Integer::valueOf)
                .min(Integer::compareTo).get();
        return Integer.valueOf(currentNodeFullName.replace(LOCK_PATH, "")).equals(min);
    }

    private void wait(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
