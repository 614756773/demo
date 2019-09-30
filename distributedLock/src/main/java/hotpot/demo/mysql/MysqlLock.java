package hotpot.demo.mysql;

import hotpot.demo.common.Lock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 当我们要锁住某个方法或资源时，我们就在该表中增加一条记录，想要释放锁的时候就删除这条记录。
 * @Date: 2019/7/30
 * @Author: qinzhu
 */
public class MysqlLock implements Lock {

    private static final Logger log = LoggerFactory.getLogger(MysqlLock.class);

    private static final String LOCK_TABLE = "method_lock";

    /**
     * tag用于标志多个锁,一张表只做1个锁感觉太浪费了
     */
    private static final String DEFAULT_TAG = "default";

    /**
     * 用于标志可重入锁当中重入的次数
     */
    private int count = 0;

    private String name;

    public MysqlLock() {
    }

    public MysqlLock(String name) {
        this.name = name;
    }

    /**
     * 由于没有通知机制,数据库实现阻塞锁只能通过轮询的方式达到阻塞,导致效率很差
     * 而且如果在获得锁后发生异常,会导致无法释放锁,可以用一个定时任务去清理数据表解决
     * 建表时需要给tag加上唯一索引
     */
    @Override
    public void lock() {
        String sql = String.format("insert into %s (tag, thread_name) values ('%s', '%s')", LOCK_TABLE, DEFAULT_TAG, Thread.currentThread());
        while (true) {
            // 尝试获取锁
            if (MysqlHelper.execute(sql)) {
                count++;
                log.info(name + "获得锁");
                return;
            } else {
                // 尝试获取可重入锁
                List<Object> objects = MysqlHelper.selectOne(String.format("select thread_name from %s where tag = '%s'", LOCK_TABLE, DEFAULT_TAG));
                if (objects.size() == 0) {
                    continue;
                }
                String o = (String) objects.get(0);
                if (Thread.currentThread().toString().equals(o)) {
                    count++;
                    log.info(name + "获得可重入锁");
                    return;
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void unlock() {
        if (count < 1) {
            throw new IllegalArgumentException("没有加锁,你释放啥");
        }
        if (count == 1) {
            String sql = String.format("delete from %s where tag = '%s'", LOCK_TABLE, DEFAULT_TAG);
            MysqlHelper.execute(sql);
            log.info(name + "释放锁");
        } else {
            count--;
            log.info(name + "释放可重入锁,剩余次数["+ count +"]");
        }
    }
}
