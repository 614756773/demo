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

    @Pointcut(classRegex = "com\\.hotpot\\.test\\..+", methodRegex = "get\\(.*\\)")
    public void pointcut() {

    }

    @Around("pointcut()")
    public void around(ProceedingJoinPoint joinPoint) {
        System.out.println("around1--前，获取到参数[" + getArgsString(joinPoint) + "]");
        Object result = joinPoint.process();
        System.out.println("around1--后，返回结果：[" + result + "]");
    }

    @Around("pointcut()")
    public void around2(ProceedingJoinPoint joinPoint) {
        System.out.println("around2--前，获取到参数为[" + getArgsString(joinPoint) + "]");
        Object result = joinPoint.process();
        System.out.println("around2--后，返回结果：[" + result + "]");
    }

    @Before("pointcut()")
    public void beforeA(JoinPoint joinPoint) {
        System.out.println("beforeA，获取到参数为[" + getArgsString(joinPoint) + "]");
    }

    @Before("pointcut()")
    public void beforeB(JoinPoint joinPoint) {
        System.out.println("beforeB，获取到参数为[" + getArgsString(joinPoint) + "]");
    }

    @After("pointcut()")
    public void after(JoinPoint joinPoint) {
        System.out.println("afterA，获取到参数为：[" + getArgsString(joinPoint) + "]");
    }

    @After("pointcut()")
    public void afterB(JoinPoint joinPoint) {
        System.out.println("afterB，获取到参数为：[" + getArgsString(joinPoint) + "]");
    }

    private StringBuilder getArgsString(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        StringBuilder sb = new StringBuilder();
        for (Object arg : args) {
            sb.append(arg).append(",");
        }
        return sb.deleteCharAt(sb.length() - 1);
    }
}
