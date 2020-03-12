package com.hotpot;

import com.hotpot.ioc.context.ContextFactory;
import com.hotpot.mvc.HttpServer;
import com.hotpot.test.StudentController;

/**
 * @author qinzhu
 * @since 2020/1/6
 */
public class Application {
    public static void main(String[] args) {
        StudentController controller = ContextFactory.getBean(StudentController.class);
        String wtf = controller.getStudent("wtf");
        System.out.println(wtf);

        HttpServer server = ContextFactory.getBean(HttpServer.class);
        server.run(8080);
    }
}
