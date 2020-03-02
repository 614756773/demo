package com.hotpot.test;

import com.hotpot.ioc.annotation.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * @author qinzhu
 * @since 2020/1/6
 */
@Component
public class StudentServiceImpl implements StudentService {
    private Set<String> set = new HashSet<>();

    @Override
    public void save(String name) {
        System.out.println("已保存学生：" + name);
        set.add(name);
    }

    @Override
    public String get(String name) {
        if (!set.contains(name)) {
            System.out.println("不存在学生：" + name);
            return null;
        }
        System.out.println("已获取学生：" + name);
        return name;
    }
}
