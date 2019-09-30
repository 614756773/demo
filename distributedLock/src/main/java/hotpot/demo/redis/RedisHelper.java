package hotpot.demo.redis;

import redis.clients.jedis.Jedis;

/**
 * @author qinzhu
 * @since 2019/9/30
 */
public class RedisHelper {
    private static final String HOST = "localhost";

    private static final Integer PORT = 6379;

    private static Jedis jedis = null;

    private static Jedis getJedis() {
        if (jedis == null) {
            synchronized (RedisHelper.class) {
                if (jedis == null) {
                    jedis = new Jedis(HOST, PORT);
                }
            }
        }
        return jedis;
    }

    /**
     *
     * @return true-设置成功,false-设置失败
     */
    public static boolean setnx(String key, String value) {
        Long result = getJedis().setnx(key, value);
        return result == 1;
    }

    /**
     * 设置过期时间
     */
    public static boolean expire(String key, int extendTime) {
        Long expire = getJedis().expire(key, extendTime);
        return expire == 1;
    }

    /**
     * 使用lua脚本删除key
     * lua脚本参考https://www.runoob.com/redis/redis-scripting.html
     */
    public static boolean luaDel(String key, String value) {
        String luaScript = "if redis.call('get',KEYS[1]) == ARGV[1] " +
                "then return redis.call('del', KEYS[1]) " +
                "else return 0 end";
        Long result = (Long) getJedis().eval(luaScript, 1, key, value);
        return result == 1;
    }
}
