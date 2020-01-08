package com.hotpot.test;

/**
 * @author qinzhu
 * @since 2020/1/6
 *
 * 模拟在Web开发中的Service
 */
public interface StudentService {
    /**
     * 保存学生
     */
    void save(String name);

    /**
     * 获取学生
     */
    String get(String name);
}
