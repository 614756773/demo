package com.hotpot.cglib.chainmodel;

/**
 * @author qinzhu
 * @since 2020/1/10
 */
public class NodeB implements Node {

    @Override
    public Object process(NodeChain chain) {
        before();
        Object result = chain.process();
        after();
        return result;
    }

    @Override
    public void before() {
        System.out.println("NodeB代理：before");
    }

    @Override
    public void after() {
        System.out.println("NodeB代理：after");
    }
}
