package com.hotpot.ioc;

import java.time.LocalDate;

/**
 * @author qinzhu
 * @since 2020/1/7
 */
public class Service {

    public String run() {
        System.err.println("调用service的run方法");
        return "调用结果：" + LocalDate.now().toString();
    }

    public String stop() {
        System.err.println("调用service的stop方法");
        return "调用结果：" + LocalDate.now().toString();
    }
}
