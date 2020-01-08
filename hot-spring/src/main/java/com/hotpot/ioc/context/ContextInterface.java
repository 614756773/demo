package com.hotpot.ioc.context;

/**
 * @author qinzhu
 * @since 2020/1/6
 */
public interface ContextInterface {
    /**
     * 注册bean
     */
    void registerBean() throws ClassNotFoundException, IllegalAccessException, InstantiationException;

    /**
     * 装配bean
     */
    void assembleBean();
}
