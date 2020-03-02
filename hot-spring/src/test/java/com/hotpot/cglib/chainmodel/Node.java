package com.hotpot.cglib.chainmodel;

/**
 * @author qinzhu
 * @since 2020/1/10
 */
public interface Node {
    Object process(NodeChain chain);

    void before();

    void after();
}
