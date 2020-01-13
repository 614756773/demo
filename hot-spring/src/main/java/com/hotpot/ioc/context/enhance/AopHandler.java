package com.hotpot.ioc.context.enhance;

import com.hotpot.aop.annotation.*;
import com.hotpot.aop.model.PointcutMetadata;
import com.hotpot.ioc.model.BeanMetadata;
import com.hotpot.ioc.model.MethodGroup;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author qinzhu
 * @since 2020/1/8
 */
public class AopHandler implements EnhanceHandler {
    private Map<String, BeanMetadata> beanMap;

    private Map<String, Class> classMap;

    /**
     * key -> origin bean名称，value 需要被代理的方法
     */
    private Map<String, MethodGroup> beanMethodMap = new HashMap<>();

    @Override
    public int getPriority() {
        return -1;
    }

    @Override
    public void handle(Map<String, BeanMetadata> beanMap, Map<String, Class> classMap) {
        this.beanMap = beanMap;
        this.classMap = classMap;
        List<Class<?>> aspectClassList = searchAspectClass();

        for (Class clazz : aspectClassList) {
            // 切入点的缓存，键值对形如("pointcut()", "com\.hotpot\.test\..")
            Map<String, PointcutMetadata> pointcutMap = new HashMap<>(16);
            Method[] methods = clazz.getMethods();
            Map<String, Method> beforeMethodMap = new HashMap<>(8);
            Map<String, Method> aroundMethodMap = new HashMap<>(8);
            Map<String, Method> afterMethodMap = new HashMap<>(8);
            for (Method method : methods) {
                Pointcut pointcut = method.getAnnotation(Pointcut.class);
                if (pointcut != null) {
                    pointcutMap.put(method.getName() + "()", PointcutMetadata.of(pointcut));
                    continue;
                }

                Before before = method.getAnnotation(Before.class);
                Around around = method.getAnnotation(Around.class);
                After after = method.getAnnotation(After.class);
                if (before != null) {
                    beforeMethodMap.put(before.value(), method);
                }
                if (around != null) {
                    aroundMethodMap.put(around.value(), method);
                }
                if (after != null) {
                    afterMethodMap.put(after.value(), method);
                }
            }
            proxy(pointcutMap, beforeMethodMap, Before.class);
            proxy(pointcutMap, aroundMethodMap, Around.class);
            proxy(pointcutMap, afterMethodMap, After.class);
        }
    }

    /**
     * 遍历bean，找到匹配切入点的bean
     * @param pointcutMap key -> 名称，形如"pointcut()"     value -> 正则表达式，形如"com\\.hotpot\\.test\\..+"
     * @param proxyMethods key -> 切入点名称，形如"pointcut()"       value -> proxyMethod实例
     */
    private void proxy(Map<String, PointcutMetadata> pointcutMap, Map<String, Method> proxyMethods, Class<? extends Annotation> annotationClass) {
        proxyMethods.forEach((pointcut, proxyMethod) -> this.beanMap.keySet().stream()
                // 保留需要代理的bean
                .filter(className -> {
                    if (beanMap.get(className).getClassInstance().isAnnotationPresent(Aspect.class)) {
                        return false;
                    }
                    PointcutMetadata pointcutMetadata = pointcutMap.get(pointcut);
                    return Pattern.matches(pointcutMetadata.getClassRegex(), className);
                })
                // bean作为维度，
                .forEach(className -> {
                    Class clazz = this.classMap.get(className);
                    PointcutMetadata pointcutMetadata = pointcutMap.get(pointcut);
                    for (Method m : clazz.getMethods()) {
                        // TODO - 1. 正则匹配的时候应该还要去匹配参数
                        // TODO - 2. 有bug，会把service接口和serviceImpl的同一个方法都代理了。。。
                        // TODO - 2续. 应该只代理serviceImpl的方法，可能还要去改ioc的实现，可以考虑给接口bean加个标志
                        System.out.println(pointcutMetadata.getMethodRegex() + "  ::::  " + m.getName()); // TODO RM
                        if (Pattern.matches(pointcutMetadata.getMethodRegex(), m.getName())) {
                            MethodGroup methodGroup = this.beanMethodMap.computeIfAbsent(className, key -> new MethodGroup());
                            methodGroup.addMethod(annotationClass, m.getName(), m.getParameterTypes(), proxyMethod);
                        }
                    }
                })
        );
    }


    /**
     * 代理bean TODO
     */
    private void proxyBean(String className, Method method) {
        System.out.println("需要代理的类有：" + className);
    }


    /**
     * 筛选出切面类
     */
    private List<Class<?>> searchAspectClass() {
        List<Class<?>> aspectClassList = new ArrayList<>();
        try {
            for (String className : this.beanMap.keySet()) {
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(Aspect.class)) {
                    aspectClassList.add(clazz);
                }
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return aspectClassList;
    }
}
