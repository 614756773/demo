package com.hotpot.aop.model.joinpoint;

import com.hotpot.aop.model.MethodFilter;
import net.sf.cglib.proxy.MethodProxy;

import java.util.List;

/**
 * @author qinzhu
 * @since 2020/3/3
 * 连接点，声明{@link com.hotpot.aop.annotation.Around}类型的切面时必须使用
 */
public class ProceedingJoinPoint implements JoinPoint {
    private Object target;

    private Object[] args;

    private MethodProxy methodProxy;

    private List<MethodFilter> methodFilters;

    private int index;

    private Object result;

    public ProceedingJoinPoint(Object target, Object[] args, MethodProxy methodProxy, List<MethodFilter> methodFilters) {
        this.target = target;
        this.args = args;
        this.methodProxy = methodProxy;
        this.methodFilters = methodFilters;
    }

    public Object process() {
        if (index >= methodFilters.size()) {
            try {
                this.result = methodProxy.invokeSuper(target, args);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                throw new RuntimeException(throwable);
            }
        } else {
            methodFilters.get(index++).doFilter(this);
        }
        return this.result;
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
