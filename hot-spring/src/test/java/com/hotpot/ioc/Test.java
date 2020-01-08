package com.hotpot.ioc;

import net.sf.cglib.proxy.Enhancer;

/**
 * @author qinzhu
 * @since 2020/1/7
 */
public class Test {
    public static void main(String[] args) {
        ServiceProxy proxy = new ServiceProxy();

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Service.class);
        enhancer.setCallback(proxy);

        Service service = (Service)enhancer.create();
        service.run();
    }
}
