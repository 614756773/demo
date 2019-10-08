package hotpot.demo.redis;

import hotpot.demo.common.Lock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * @author qinzhu
 * @since 2019/9/30
 */
public class RedisLock implements Lock{

    private static final Logger log = LoggerFactory.getLogger(RedisLock.class);

    private static final String KEY = "FIRST_LOCK";

    /**
     * value是对象私有的，用于保证在释放锁时是释放的自己进程的锁，不然一不小心把其他进程的锁释放就尴尬了
     */
    private String value = Thread.currentThread().getName() + "-" +UUID.randomUUID().toString();

    private HoldOnScheduler scheduler;

    private RedisHelper redisHelper;

    public RedisLock() {
        this.redisHelper = new RedisHelper();
    }

    @Override
    public void lock() {
        while (true) {
            if (redisHelper.setnx(KEY, value)) {
                log.info("已获得锁");
                this.scheduler = new HoldOnScheduler(KEY, redisHelper);
                new Thread(scheduler).start();
                break;
            }
        }
    }

    @Override
    public void unlock() {
        if (redisHelper.luaDel(KEY, value)) {
            log.info("已释放锁");
            scheduler.stop();
        } else {
            log.info("手动释放失败，可能是已过期或者key不对");
        }
    }
}
