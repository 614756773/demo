package com.hotpot.aop.cglib;

import com.hotpot.aop.model.MethodFilter;
import com.hotpot.aop.model.joinpoint.ProceedingJoinPoint;
import com.hotpot.aop.model.joinpoint.SimpleJoinPoint;
import com.hotpot.exception.HotSpringException;
import com.hotpot.ioc.model.MethodGroup;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author qinzhu
 * @since 2020/1/10
 * 参考<a href="https://www.bbsmax.com/A/WpdKwmVMdV/"></a>
 * 设计思路 ![image.png](https://img.hacpai.com/file/2020/01/image-0cd3c3a3.png)
 */
public class MethodInterceptorChain implements MethodInterceptor {

    private Map<String, List<Method>> before;

    private Map<String, List<Method>> around;

    private Map<String, List<Method>> after;

    public MethodInterceptorChain(MethodGroup methodGroup) {
        this.before = methodGroup.getBeforeMethods();
        this.around = methodGroup.getAroundMethods();
        this.after = methodGroup.getAfterMethods();
    }

    @Override
    public Object intercept(Object target, Method originalMethod, Object[] args, MethodProxy methodProxy) throws Throwable {
        if (needToProxy(originalMethod, args)) {
            return doProxy(target, originalMethod, args, methodProxy);
        }
        return methodProxy.invokeSuper(target, args);
    }

    /**
     * 判断{@code originalMethod}是否需要被织入代理
     */
    private boolean needToProxy(Method originalMethod, Object[] args) {
        String key = generateKey(originalMethod, args);
        return before.get(key) != null
                || around.get(key) != null
                || after.get(key) != null;
    }

    /**
     * 对{@code originalMethod}进行前置/环绕/后置代理
     */
    private Object doProxy(Object target, Method originalMethod, Object[] args, MethodProxy methodProxy) throws Throwable {
        Object result;
        invokeProxyMethod(target, originalMethod, args, before);

        if (around == null || around.isEmpty()) {
            result = methodProxy.invokeSuper(target, args);
        } else {
            result = invokeAroundMethod(target, originalMethod, args, around, methodProxy);
        }

        invokeProxyMethod(target, originalMethod, args, after);
        return result;
    }

    private Object invokeAroundMethod(Object target, Method originalMethod, Object[] args, Map<String, List<Method>> around, MethodProxy methodProxy) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        List<Method> methods = around.get(generateKey(originalMethod, args));
        List<MethodFilter> filters = new ArrayList<>(methods.size());
        methods.forEach(e -> filters.add(new MethodFilter(e)));
        ProceedingJoinPoint chain = new ProceedingJoinPoint(target, args, methodProxy, filters);
        return chain.process();
    }

    /**
     * 调用增强的方法
     *
     * @param target         bean
     * @param originalMethod bean的原方法
     * @param args           bean原方法的参数
     * @param methodMap      代理方法      key -> 由bean原方法生成，value -> 代理方法
     */
    private void invokeProxyMethod(Object target, Method originalMethod, Object[] args, Map<String, List<Method>> methodMap) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        if (methodMap != null && !methodMap.isEmpty()) {
            List<Method> methods = methodMap.get(generateKey(originalMethod, args));
            for (Method e : methods) {
                Class<?>[] parameterTypes = e.getParameterTypes();
                if (parameterTypes == null || parameterTypes.length == 0) {
                    e.invoke(e.getDeclaringClass().newInstance());
                } else if (parameterTypes.length == 1 && parameterTypes[0].isAssignableFrom(SimpleJoinPoint.class)) {
                    e.invoke(e.getDeclaringClass().newInstance(), new SimpleJoinPoint(target, args));
                } else {
                    throw new HotSpringException(e.getDeclaringClass().getName() + "#" + e.getName()
                            + "()     前置/后置代理方法的参数只能是空或者SimpleJoinPoint");
                }
            }
        }
    }

    /**
     * 根据方法名和参数类型生成key
     *
     * @return run:java.lang.String,java.lang,Integer 或者 run:
     * 该方法与 {@link com.hotpot.ioc.model.MethodGroup#generateKey(String, Class[])} 关联
     */
    private String generateKey(Method method, Object[] args) {
        StringBuilder sb = new StringBuilder(method.getName() + ":");
        if (args == null || args.length == 0) {
            return sb.toString();
        }
        for (Object arg : args) {
            sb.append(arg.getClass().getName()).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
