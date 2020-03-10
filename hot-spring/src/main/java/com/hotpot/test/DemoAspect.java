package com.hotpot.test;

import com.hotpot.aop.annotation.*;
import com.hotpot.aop.model.joinpoint.JoinPoint;
import com.hotpot.aop.model.joinpoint.ProceedingJoinPoint;
import com.hotpot.aop.model.joinpoint.SimpleJoinPoint;
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

    @Around("pointcut()")
    public void around(ProceedingJoinPoint joinPoint) {
        System.out.println("around-----前");
        Object result = joinPoint.process();
        System.out.println("around-----后，返回结果：" + result);
    }

    @Around("pointcut()")
    public void around2(ProceedingJoinPoint joinPoint) {
        System.out.println("around2-----前");
        Object result = joinPoint.process();
        System.out.println("around2-----后，返回结果：" + result);
    }

    @Before("pointcut()")
    public void before(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        StringBuilder sb = new StringBuilder();
        for (Object arg : args) {
            sb.append(arg).append(",");
        }
        System.out.println("方法执行前，获取到参数为：" + sb.deleteCharAt(sb.length() - 1));
    }

    @Before("pointcut()")
    public void before2(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        StringBuilder sb = new StringBuilder();
        for (Object arg : args) {
            sb.append(arg).append(",");
        }
        System.out.println("方法执行前2，获取到参数为：" + sb.deleteCharAt(sb.length() - 1));
    }
//
//    @After("pointcut()")
//    public void after(JoinPoint joinPoint) {
//        Object[] args = joinPoint.getArgs();
//        StringBuilder sb = new StringBuilder();
//        for (Object arg : args) {
//            sb.append(arg).append(",");
//        }
//        System.out.println("方法执行后，获取到参数为：" + sb.deleteCharAt(sb.length() - 1));
//    }
}
