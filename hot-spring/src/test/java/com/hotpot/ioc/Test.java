package com.hotpot.ioc;

import com.hotpot.ioc.utils.ClassScanner;
import net.sf.cglib.proxy.Enhancer;

/**
 * @author qinzhu
 * @since 2020/1/7
 */
public class Test {
    public static void main(String[] args) {
        cglibTest();
//        ClassScanner.listClass("net.sf.cglib");
    }

    private static void cglibTest() {
        ServiceProxy proxy = new ServiceProxy();
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Service.class);
        enhancer.setCallback(proxy);

        Service service = (Service)enhancer.create();
        service.run();

        BProxy bproxy = new BProxy();
        Enhancer enhancer1 = new Enhancer();
        enhancer1.setSuperclass(Service.class);
        enhancer1.setCallback(bproxy);

        Service service1 = (Service)enhancer1.create();
        service1.run();
    }
}
