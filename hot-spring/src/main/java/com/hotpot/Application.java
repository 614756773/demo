package com.hotpot;

import com.hotpot.ioc.context.ContextFactory;
import com.hotpot.test.StudentController;

/**
 * @author qinzhu
 * @since 2020/1/6
 */
public class Application {
    public static void main(String[] args) {
        StudentController controller = ContextFactory.getBean(StudentController.class);
        controller.saveStudent("小米");
        System.out.println();
    }
}
