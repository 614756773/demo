package com.hotpot;

import com.hotpot.chainmodel.Node;
import com.hotpot.chainmodel.NodeA;
import com.hotpot.chainmodel.NodeB;
import com.hotpot.chainmodel.NodeChain;
import com.hotpot.ioc.Service;
import com.hotpot.ioc.ServiceProxy;
import com.hotpot.ioc.utils.ClassScanner;
import net.sf.cglib.proxy.Enhancer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qinzhu
 * @since 2020/1/7
 */
public class Test {
    public static void main(String[] args) {
        cglibChainTest();
//        cglibTest();
//        ClassScanner.listClass("net.sf.cglib");
    }

    private static void cglibChainTest() {
        List<Node> nodeList = new ArrayList<>();
        nodeList.add(new NodeA());
        nodeList.add(new NodeB());
        NodeChain chain = new NodeChain(nodeList);

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Service.class);
        enhancer.setCallback(chain);

        Service service = (Service) enhancer.create();
        String result = service.run();
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.err.println(result);
    }

    private static void cglibTest() {
        ServiceProxy proxy = new ServiceProxy();
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Service.class);
        enhancer.setCallback(proxy);

        Service service = (Service)enhancer.create();
        String result = service.run();
        System.out.println(result);
    }
}
