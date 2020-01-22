package com.hotpot.chainmodel;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author qinzhu
 * @since 2020/1/13
 */
public class NodeChain implements MethodInterceptor{
    private List<Node> proxyNodes;

    private int index = -1;

    private Object target;

    private Object[] args;

    private MethodProxy methodProxy;

    public NodeChain(List<Node> proxyNode) {
        this.proxyNodes = proxyNode;
    }

    public Object process() {
        index++;
//        if (index > proxyNodes.size()) {
//            return target;
//        }

        if (index == proxyNodes.size()) {
            try {
                return methodProxy.invokeSuper(target, args);
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        } else {
            Node node = proxyNodes.get(index);
            return node.process(this);
        }
    }

    @Override
    public Object intercept(Object target, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        this.target = target;
        this.args = args;
        this.methodProxy = methodProxy;
        return process();
    }
}
