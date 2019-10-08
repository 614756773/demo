package hotpot.demo.redis;

import redis.clients.jedis.Jedis;

/**
 * @author qinzhu
 * @since 2019/9/30
 */
public class RedisHelper {
    private final String HOST = "localhost";

    private final Integer PORT = 6379;

    private Jedis jedis;

    public RedisHelper() {
        jedis = new Jedis(HOST, PORT);
    }

    /**
     *
     * @return true-设置成功,false-设置失败
     */
    public boolean setnx(String key, String value) {
        Long result = jedis.setnx(key, value);
        return result == 1;
    }

    /**
     * 设置过期时间
     */
    public boolean expire(String key, int extendTime) {
        Long expire = jedis.expire(key, extendTime);
        return expire == 1;
    }

    /**
     * 使用lua脚本删除key
     * lua脚本参考https://www.runoob.com/redis/redis-scripting.html
     */
    public boolean luaDel(String key, String value) {
        String luaScript = "if redis.call('get',KEYS[1]) == ARGV[1] " +
                "then return redis.call('del', KEYS[1]) " +
                "else return 0 end";
        Long result = (Long) jedis.eval(luaScript, 1, key, value);
        return result == 1;
    }
}
