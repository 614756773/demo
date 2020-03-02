package com.hotpot.ioc.model;

import com.hotpot.aop.annotation.After;
import com.hotpot.aop.annotation.Around;
import com.hotpot.aop.annotation.Before;
import com.sun.istack.internal.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qinzhu
 * @since 2020/1/13
 * 一个bean可能会被前、环绕、后三种模式代理
 * 而一个方法被前置代理时也有可能会被前置增强多次，同理环绕、后代理也是一样的
 */
public class MethodGroup {
    /**
     * value -> 前置织入的方法
     * key -> 原方法的标志，由方法名 + 参数名组合而成，如下：
     * run:java.lang.String,java.lang,Integer 或者 run:
     */
    private Map<String, List<Method>> beforeMethods;

    /**
     * value -> 环绕织入的方法
     * key -> 被代理方法的标志，由方法名 + 参数名组合而成，如下：
     * run:java.lang.String,java.lang,Integer 或者 run:
     */
    private Map<String, List<Method>> aroundMethods;

    /**
     * value -> 后置织入的方法
     * key -> 被代理方法的标志，由方法名 + 参数名组合而成，如下：
     * run:java.lang.String,java.lang,Integer 或者 run:
     */
    private Map<String, List<Method>> afterMethods;

    public MethodGroup() {
        beforeMethods = new HashMap<>(8);
        aroundMethods = new HashMap<>(8);
        afterMethods = new HashMap<>(8);
    }

    public Map<String, List<Method>> getBeforeMethods() {
        return beforeMethods;
    }

    /**
     * @param targetMethodName 被代理方法的名称
     * @param parameterTypes   被代理方法的参数类型
     * @param proxyMethod      需要织入的method
     */
    public void addMethod(Class<? extends Annotation> annotationClass, String targetMethodName, Class<?>[] parameterTypes, Method proxyMethod) {
        if (annotationClass == Before.class) {
            addBeforeMethod(targetMethodName, parameterTypes, proxyMethod);
        } else if (annotationClass == Around.class) {
            addAroundMethods(targetMethodName, parameterTypes, proxyMethod);
        } else if (annotationClass == After.class) {
            addAfterMethods(targetMethodName, parameterTypes, proxyMethod);
        }
    }

    public Map<String, List<Method>> getAroundMethods() {
        return aroundMethods;
    }

    public Map<String, List<Method>> getAfterMethods() {
        return afterMethods;
    }

    private void addBeforeMethod(String targetMethodName, Class<?>[] parameterTypes, Method proxyMethod) {
        List<Method> list = beforeMethods.computeIfAbsent(generateKey(targetMethodName, parameterTypes), key -> new ArrayList<>());
        list.add(proxyMethod);
    }

    private void addAroundMethods(String targetMethodName, Class<?>[] parameterTypes, Method proxyMethod) {
        List<Method> list = aroundMethods.computeIfAbsent(generateKey(targetMethodName, parameterTypes), key -> new ArrayList<>());
        list.add(proxyMethod);
    }

    private void addAfterMethods(String targetMethodName, Class<?>[] parameterTypes, Method proxyMethod) {
        List<Method> list = afterMethods.computeIfAbsent(generateKey(targetMethodName, parameterTypes), key -> new ArrayList<>());
        list.add(proxyMethod);
    }

    /**
     * @return 返回值如下：
     * run:java.lang.String,java.lang,Integer 或者 run:
     */
    private String generateKey(String targetMethodName, @Nullable Class<?>[] parameterTypes) {
        StringBuilder sb = new StringBuilder(targetMethodName).append(":");
        if (parameterTypes == null || parameterTypes.length == 0) {
            return sb.toString();
        }
        for (Class<?> parameterType : parameterTypes) {
            sb.append(parameterType.getName());
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
