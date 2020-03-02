package com.hotpot.ioc.context.enhance;

import com.hotpot.aop.MethodInterceptorChain;
import com.hotpot.aop.annotation.*;
import com.hotpot.aop.model.PointcutMetadata;
import com.hotpot.ioc.model.BeanMetadata;
import com.hotpot.ioc.model.MethodGroup;
import net.sf.cglib.proxy.Enhancer;

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
            Map<String, Method> beforeMethodMap = new HashMap<>(16);
            Map<String, Method> aroundMethodMap = new HashMap<>(16);
            Map<String, Method> afterMethodMap = new HashMap<>(16);
            Method[] methods = clazz.getMethods();
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
            findBeanMethodForProxy(pointcutMap, beforeMethodMap, Before.class);
            findBeanMethodForProxy(pointcutMap, aroundMethodMap, Around.class);
            findBeanMethodForProxy(pointcutMap, afterMethodMap, After.class);
            proxyBean(beanMethodMap);
        }
    }

    /**
     * 遍历bean，找到匹配切入点的method，缓存在{@code beanMethodMap}中
     * @param pointcutMap key -> 名称，形如"pointcut()"     value -> PointcutMetadata
     * @param proxyMethods key -> 切入点名称，形如"pointcut()"       value -> proxyMethod实例
     */
    private void findBeanMethodForProxy(Map<String, PointcutMetadata> pointcutMap, Map<String, Method> proxyMethods, Class<? extends Annotation> annotationClass) {
        proxyMethods.forEach((pointcut, proxyMethod) -> this.beanMap.keySet().stream()
                // 筛选出需要代理的bean
                .filter(className -> {
                    if (classMap.get(className).isInterface()) {
                        return false;
                    }
                    if (beanMap.get(className).getClassInstance().isAnnotationPresent(Aspect.class)) {
                        return false;
                    }
                    PointcutMetadata pointcutMetadata = pointcutMap.get(pointcut);
                    return Pattern.matches(pointcutMetadata.getClassRegex(), className);
                })
                // bean作为维度
                .forEach(className -> {
                    Class clazz = this.classMap.get(className);
                    PointcutMetadata pointcutMetadata = pointcutMap.get(pointcut);
                    for (Method m : clazz.getMethods()) {
                        if (Pattern.matches(pointcutMetadata.getMethodRegex(), m.getName() + "()")) {
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
    private void proxyBean(Map<String, MethodGroup> beanMethodMap) {
        beanMethodMap.forEach((beanName, methodGroup) -> {
            BeanMetadata beanMetadata = beanMap.get(beanName);
            Class clazz = beanMetadata.getClassInstance();
            Map<String, List<Method>> beforeMethods = methodGroup.getBeforeMethods();
            Map<String, List<Method>> aroundMethods = methodGroup.getAroundMethods();
            Map<String, List<Method>> afterMethods = methodGroup.getAfterMethods();
            MethodInterceptorChain chain = new MethodInterceptorChain(beforeMethods, aroundMethods, afterMethods);
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(clazz);
            enhancer.setCallback(chain);
            beanMetadata.replaceBeanInstance(enhancer.create());
//            map.forEach((methodName, methods) -> {
//                MethodInterceptorChain chain = new MethodInterceptorChain(methods, null, null);
//                Enhancer enhancer = new Enhancer();
//                enhancer.setSuperclass(clazz);
//                enhancer.setCallback(chain);
//                beanMetadata.replaceBeanInstance(enhancer.create());
//            });
        });
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
