package com.hotpot.ioc.model;

/**
 * @author qinzhu
 * @since 2020/1/9
 */
public class BeanMetadata {
    /**
     * bean的类名称
     */
    private String className;

    /**
     * bean的Class对象
     */
    private Class classInstance;

    /**
     * bean实例
     */
    private Object beanInstance;

    private BeanMetadata() {
    }

    private BeanMetadata(String className, Class classInstance, Object beanInstance) {
        this.className = className;
        this.classInstance = classInstance;
        this.beanInstance = beanInstance;
    }

    public static BeanMetadata create(String className, Class classInstance, Object beanInstance) {
        return new BeanMetadata(className, classInstance, beanInstance);
    }

    public String getClassName() {
        return className;
    }

    public Class getClassInstance() {
        return classInstance;
    }

    public Object getBeanInstance() {
        return beanInstance;
    }
}
