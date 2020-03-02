package com.hotpot.cglib.service;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * 只代理名称为run的方法
 */
public class ServiceProxy implements MethodInterceptor {

    @Override
    public Object intercept(Object target, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        if (method.getName().equals("run")) {
            System.out.println("Before Method Invoke");
            Object result = proxy.invokeSuper(target, args);
            System.out.println("After Method Invoke");
            return result;
        }
        return proxy.invokeSuper(target, args);
    }
    
}