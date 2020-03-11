package com.hotpot.aop.model;

import com.hotpot.aop.model.joinpoint.ProceedingJoinPoint;

import java.lang.reflect.Method;

/**
 * @author qinzhu
 * @since 2020/3/11
 */
public class MethodFilter {
    private Method method;

    public MethodFilter(Method method) {
        this.method = method;
    }

    public Object doFilter(ProceedingJoinPoint chain) {
        try {
            return method.invoke(method.getDeclaringClass().newInstance(), chain);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
