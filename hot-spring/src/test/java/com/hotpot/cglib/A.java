package com.hotpot.cglib;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author qinzhu
 * @since 2020/3/3
 */
public class A {

    public void sum(int a, int b) {
        System.out.println("sum = " + (a + b));
    }

    public static void main(String[] args) throws NoSuchMethodException {
        A a = new A();
        Method method = a.getClass().getMethod("sum", int.class, int.class);
        JP jp = new JP(a, method, new Object[]{1, 4});

        a.aroundProxy(jp);
    }

    public void aroundProxy(JP jp) {
        System.out.println("执行前");
        jp.process();
        System.out.println("执行后");
    }

    static class JP{
        private Object bean;

        private Method beanMethod;

        private Object[] args;

        public JP(Object o, Method method, Object[] args) {
            this.bean = o;
            this.beanMethod = method;
            this.args = args;
        }

        public void process() {
            try {
                beanMethod.invoke(bean, args);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
