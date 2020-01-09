package com.hotpot.ioc.context.enhance;

import com.hotpot.aop.annotation.*;
import com.hotpot.ioc.model.BeanMetadata;

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

    @Override
    public int getPriority() {
        return -1;
    }

    @Override
    public void handle(Map<String, BeanMetadata> beanMap) {
        this.beanMap = beanMap;
        List<Class<?>> aspectClassList = searchAspectClass();

        for (Class clazz : aspectClassList) {
            // 切入点的缓存，键值对形如("pointcut()", "com\.hotpot\.test\..")
            Map<String, String> pointcutMap = new HashMap<>(16);
            Method[] methods = clazz.getMethods();
            Map<String, Method> beforeMethodMap = new HashMap<>(8);
            Map<String, Method> aroundMethodMap = new HashMap<>(8);
            Map<String, Method> afterMethodMap = new HashMap<>(8);
            for (Method method : methods) {
                Pointcut pointcut = method.getAnnotation(Pointcut.class);
                if (pointcut != null) {
                    pointcutMap.put(method.getName() + "()", pointcut.value());
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
            proxy(pointcutMap, beforeMethodMap);
            proxy(pointcutMap, aroundMethodMap);
            proxy(pointcutMap, afterMethodMap);
        }
    }

    /**
     * 遍历bean，找到匹配切入点的bean
     * @param pointcutMap key -> 名称，形如"pointcut()"     value -> 正则表达式，形如"com\\.hotpot\\.test\\..+"
     * @param methodMap key -> 切入点名称，形如"pointcut()"       value -> Method实例
     */
    private void proxy(Map<String, String> pointcutMap, Map<String, Method> methodMap) {
        methodMap.forEach((pointcut, method) -> {
            this.beanMap.keySet().stream()
                    .filter(className -> {
                        if (beanMap.get(className).getClassInstance().isAnnotationPresent(Aspect.class)) {
                            return false;
                        }
                        return Pattern.matches(pointcutMap.get(pointcut), className);
                    })
                    .forEach(className -> proxyBean(className, method));
        });
    }

    /**
     * 代理bean TODO
     */
    private void proxyBean(String className, Method method) {
        System.out.println("需要代理的类有：" + className);
    }


    /**
     * 从beanMap中获取能够匹配的bean的名称 TODO
     */
    private List<String> match(Map<String, Object> beanMap, String regex) {
        return null;
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
