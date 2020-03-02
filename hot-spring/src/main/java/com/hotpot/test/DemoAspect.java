package com.hotpot.test;

import com.hotpot.aop.annotation.After;
import com.hotpot.aop.annotation.Aspect;
import com.hotpot.aop.annotation.Before;
import com.hotpot.aop.annotation.Pointcut;
import com.hotpot.ioc.annotation.Component;

/**
 * @author qinzhu
 * @since 2020/1/8
 */
@Component
@Aspect
public class DemoAspect {

    @Pointcut(classRegex = "com\\.hotpot\\.test\\..+", methodRegex = "save\\(.*\\)")
    public void pointcut() {

    }

    @Before("pointcut()")
    public void before() {
        System.out.println("方法执行前----");
    }

    @After("pointcut()")
    public void after() {
        System.out.println("方法执行后----");
    }
}
