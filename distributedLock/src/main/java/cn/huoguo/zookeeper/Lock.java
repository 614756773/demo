package cn.huoguo.zookeeper;

import org.apache.zookeeper.*;

import java.io.IOException;

/**
 * @Date: 2019/7/26
 * @Author: qinzhu
 */
public class Lock {

    private ZooKeeper zk;

    private static final String LOCK_PATH = "/lock";
    public Lock() throws IOException {
        zk = new ZooKeeper("localhost:2181", 100, watchedEvent -> {
            System.out.println(watchedEvent.getType().name());
        });
    }

    public boolean lock() {
        zk.create(LOCK_PATH, "test data".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL, new AsyncCallback.StringCallback() {
            @Override
            public void processResult(int i, String s, Object o, String s1) {
                if (i == KeeperException.Code.OK.intValue()) {
                    // TODO 监听前一个临时节点
                }
            }
        }, null);
        return false;
    }
}
