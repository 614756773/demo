package cn.hotpot.redis.service;

import cn.hotpot.redis.model.Student;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * @author qinzhu
 * @since 2019/11/8
 */
public class RedisSub {

    public void handleMessage(String message){
        System.out.println("订阅到消息" + LocalDateTime.now());
        Student student = getStudent(message);
        System.out.println(student.toString());
    }

    private Student getStudent(String message) {
        Student student = null;
        try {
            student = new ObjectMapper().readValue(message, Student.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return student;
    }
}
