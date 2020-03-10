package com.hotpot.aop.model.joinpoint;

/**
 * @author qinzhu
 * @since 2020/3/3
 */
public interface JoinPoint {

    Object[] getArgs();

    Object getTarget();
}
