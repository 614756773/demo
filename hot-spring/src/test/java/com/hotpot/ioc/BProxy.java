package com.hotpot.ioc;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author qinzhu
 * @since 2020/1/10
 */
public class BProxy implements MethodInterceptor {
    @Override
    public Object intercept(Object object, Method method, Object[] objects, MethodProxy proxy) throws Throwable {
        System.out.println("B 之前");
        proxy.invokeSuper(object, objects);
        System.out.println("B 之后");
        return object;
    }
}
