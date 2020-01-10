package com.hotpot.aop.model;

import com.hotpot.aop.annotation.Pointcut;

/**
 * @author qinzhu
 * @since 2020/1/10
 */
public class PointcutMetadata {
    /**
     * 类名的正表达式
     */
    private String classRegex;

    /**
     * 方法的正则表达式
     */
    private String methodRegex;

    private PointcutMetadata(){}

    private PointcutMetadata(String classRegex, String methodRegex) {
        this.classRegex = classRegex;
        this.methodRegex = methodRegex;
    }

    public String getClassRegex() {
        return classRegex;
    }

    public void setClassRegex(String classRegex) {
        this.classRegex = classRegex;
    }

    public String getMethodRegex() {
        return methodRegex;
    }

    public void setMethodRegex(String methodRegex) {
        this.methodRegex = methodRegex;
    }

    public static PointcutMetadata of(Pointcut pointcut) {
        return new PointcutMetadata(pointcut.classRegex(), pointcut.methodRegex());
    }
}
