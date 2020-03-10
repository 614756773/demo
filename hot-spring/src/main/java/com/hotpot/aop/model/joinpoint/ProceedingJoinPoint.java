package com.hotpot.aop.model.joinpoint;

import net.sf.cglib.proxy.MethodProxy;

/**
 * @author qinzhu
 * @since 2020/3/3
 * 连接点，声明{@link com.hotpot.aop.annotation.Around}类型的切面时必须使用
 */
// TODO 该类就相当于过滤器模式里的FilterChan，还需要定义Filer以及index
public class ProceedingJoinPoint implements JoinPoint {
    private Object target;

    private Object[] args;

    private MethodProxy methodProxy;

    private int index;

    public ProceedingJoinPoint(Object target, Object[] args, MethodProxy methodProxy) {
        this.target = target;
        this.args = args;
        this.methodProxy = methodProxy;
    }

    public Object process() {
        Object result;
        try {
            result = methodProxy.invokeSuper(target, args);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw new RuntimeException(throwable);
        }
        return result;
    }

    @Override
    public Object[] getArgs() {
        if (args == null) {
            return new Object[0];
        }
        return args;
    }

    @Override
    public Object getTarget() {
        return target;
    }
}
