package com.hotpot.aop.model.joinpoint;

/**
 * @author qinzhu
 * @since 2020/3/3
 */
public class SimpleJoinPoint implements JoinPoint {
    private Object target;

    private Object[] args;

    public SimpleJoinPoint(Object target, Object[] args) {
        this.target = target;
        this.args = args;
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
