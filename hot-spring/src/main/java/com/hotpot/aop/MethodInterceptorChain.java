package com.hotpot.aop;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author qinzhu
 * @since 2020/1/10
 * 参考<a href="https://www.bbsmax.com/A/WpdKwmVMdV/"></a>
 * 设计思路 ![image.png](https://img.hacpai.com/file/2020/01/image-0cd3c3a3.png)
 * TODO 用过滤器链 + 递归 实现多重代理
 */
public class MethodInterceptorChain implements MethodInterceptor {

    private List<Method> before;

    private List<Method> around;

    private List<Method> after;

    public MethodInterceptorChain(List<Method> before, List<Method> around, List<Method> after) {
        this.before = before;
        this.around = around;
        this.after = after;
    }

    @Override
    public Object intercept(Object target, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        if (before != null && !before.isEmpty()) {
            for (Method e : before) {
                e.invoke(target, args);
            }
        }
        // TODO around怎么弄还待思考
        Object result = methodProxy.invokeSuper(target, args);
        if (after != null && !after.isEmpty()) {
            for (Method e : after) {
                e.invoke(target, args);
            }
        }
        return result;
    }
}
