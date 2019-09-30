package hotpot.demo.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author qinzhu
 * @since 2019/9/30
 *
 * 定时刷新redis健的过期时间
 * `我为长者续1秒`
 */
public class HoldOnScheduler implements Runnable{
    private static final Logger log = LoggerFactory.getLogger(HoldOnScheduler.class);

    private String key;

    private boolean schedulerOpen;

    // 定时任务间隔执行时间，单位秒
    private static final int INTERVAL_TIME = 10;

    // key的新过期时间，单位秒
    private static final int EXTEND_TIME = 20;

    public HoldOnScheduler(String key) {
        this.key = key;
        this.schedulerOpen = true;
    }

    public void stop() {
        this.schedulerOpen = false;
    }

    @Override
    public void run() {
        while (schedulerOpen) {
            if (RedisHelper.expire(key, EXTEND_TIME)) {
                log.info("已重置`{}`TTL为{}秒", key, EXTEND_TIME);
            } else {
                log.info("为`{}`续期失败，该key已被删除，定时任务自动关闭", key);
            }
            try {
                Thread.sleep(INTERVAL_TIME * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        log.info("定时任务关闭");
    }
}
