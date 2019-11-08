package cn.hotpot.redis;

import cn.hotpot.redis.model.Student;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

/**
 * @author qinzhu
 * @since 2019/11/8
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class RedisPushTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    public void test() throws JsonProcessingException {
        final String channel = "subChannel";
        Student student = new Student("小明", "22");
        String message = new ObjectMapper().writeValueAsString(student);

        redisTemplate.convertAndSend(channel, message);
        System.out.println("已发送" + LocalDateTime.now());
    }
}
