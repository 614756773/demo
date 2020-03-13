package com.hotpot.ioc.context.enhance;

import com.hotpot.aop.annotation.*;
import com.hotpot.aop.cglib.MethodInterceptorChain;
import com.hotpot.aop.model.PointcutMetadata;
import com.hotpot.aop.model.joinpoint.ProceedingJoinPoint;
import com.hotpot.aop.model.joinpoint.SimpleJoinPoint;
import com.hotpot.exception.HotSpringException;
import com.hotpot.ioc.model.BeanMetadata;
import com.hotpot.ioc.model.MethodGroup;
import net.sf.cglib.proxy.Enhancer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
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
    public void handle(Map<String, BeanMetadata> beanMap, Map<String, Class> classMap) {
        this.beanMap = beanMap;
        this.classMap = classMap;
        List<Class<?>> aspectClassList = searchAspectClass();

        for (Class clazz : aspectClassList) {
            // 切入点的缓存，键值对形如("pointcut()", "com\.hotpot\.test\..")
            Map<String, PointcutMetadata> pointcutMap = new HashMap<>(16);
            // 使用IdentityHashMap，当键值相同但是引用地址不同时，任然可以put进容器，并且不会覆盖
            // 之所以这么做是因为一个切入点可以被同一类型的切入方法多次使用，比如：
            // @Before("pointcut()") public void before_1() { }
            // @Before("pointcut()") public void before_2() { }
            // 如果不使用IdentityHashMap，before_1代理方法就会被before_2代理方法覆盖掉
            Map<String, Method> beforeMethodMap = new IdentityHashMap<>(16);
            Map<String, Method> aroundMethodMap = new IdentityHashMap<>(16);
            Map<String, Method> afterMethodMap = new IdentityHashMap<>(16);
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
                    checkProxyMethod(method, Before.class);
                    beforeMethodMap.put(before.value(), method);
                }
                if (around != null) {
                    checkProxyMethod(method, Around.class);
                    aroundMethodMap.put(around.value(), method);
                }
                if (after != null) {
                    checkProxyMethod(method, After.class);
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
     *
     * @param pointcutMap key -> 名称，形如"pointcut()"     value -> PointcutMetadata
     * @param methodMap   key -> 切入点名称，形如"pointcut()"       value -> 代理方法
     */
    private void findBeanMethodForProxy(Map<String, PointcutMetadata> pointcutMap, Map<String, Method> methodMap, Class<? extends Annotation> annotationClass) {
        methodMap.forEach((pointcut, proxyMethod) -> this.beanMap.keySet().stream()
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
     * 代理bean
     */
    private void proxyBean(Map<String, MethodGroup> beanMethodMap) {
        beanMethodMap.forEach((beanName, methodGroup) -> {
            BeanMetadata beanMetadata = beanMap.get(beanName);
            Class clazz = beanMetadata.getClassInstance();
            MethodInterceptorChain chain = new MethodInterceptorChain(methodGroup);
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(clazz);
            enhancer.setCallback(chain);
            beanMetadata.replaceBeanInstance(enhancer.create());
        });
    }

    /**
     * 检查代理方法的参数是否合法
     */
    private void checkProxyMethod(Method method, Class<?> clz) {
        String message = "%s代理方法的参数必须是%s类型:%s#%s";
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (clz == Before.class && parameterTypes != null) {
            if (parameterTypes.length != 1 || !parameterTypes[0].isAssignableFrom(SimpleJoinPoint.class)) {
                throw new HotSpringException(String.format(message,
                        "before", "SimpleJoinPoint或者其父类",
                        method.getDeclaringClass().getName(), method.getName()));
            }
        } else if (clz == Around.class) {
            if (parameterTypes == null || parameterTypes.length != 1 || parameterTypes[0] != ProceedingJoinPoint.class) {
                throw new HotSpringException(String.format(message,
                        "around", "ProceedingJoinPoint",
                        method.getDeclaringClass().getName(), method.getName()));
            }
        } else if (clz == After.class && parameterTypes != null) {
            if (parameterTypes.length != 1 || !parameterTypes[0].isAssignableFrom(SimpleJoinPoint.class)) {
                throw new HotSpringException(String.format(message,
                        "after", "SimpleJoinPoint或者其父类",
                        method.getDeclaringClass().getName(), method.getName()));
            }
        } else {
            throw new RuntimeException("only Before/Around/After");
        }
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
            throw new HotSpringException(e);
        }
        return aspectClassList;
    }
}
