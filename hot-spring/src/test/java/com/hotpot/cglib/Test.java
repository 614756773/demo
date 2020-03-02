package com.hotpot.cglib;

import com.hotpot.cglib.chainmodel.Node;
import com.hotpot.cglib.chainmodel.NodeA;
import com.hotpot.cglib.chainmodel.NodeB;
import com.hotpot.cglib.chainmodel.NodeChain;
import com.hotpot.cglib.service.Service;
import com.hotpot.cglib.service.ServiceProxy;
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
        String result = service.stop();
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
