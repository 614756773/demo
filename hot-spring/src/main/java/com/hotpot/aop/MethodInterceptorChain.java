package com.hotpot.aop;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * @author qinzhu
 * @since 2020/1/10
 * 参考<a href="https://www.bbsmax.com/A/WpdKwmVMdV/"></a>
 * 设计思路 ![image.png](https://img.hacpai.com/file/2020/01/image-0cd3c3a3.png)
 * TODO 用过滤器链 + 递归 实现多重代理
 */
public class MethodInterceptorChain implements MethodInterceptor {

    private Map<String, List<Method>> before;

    private Map<String, List<Method>> around;

    private Map<String, List<Method>> after;

    public MethodInterceptorChain(Map<String, List<Method>> before, Map<String, List<Method>> around, Map<String, List<Method>> after) {
        this.before = before;
        this.around = around;
        this.after = after;
    }

    @Override
    public Object intercept(Object target, Method originalMethod, Object[] args, MethodProxy methodProxy) throws Throwable {
        if (before != null && !before.isEmpty()) {
            List<Method> methods = before.get(generateKey(originalMethod, args));
            for (Method e : methods) {
                e.invoke(e.getDeclaringClass().newInstance());
            }
        }

        // TODO around怎么弄还待思考

        Object result = methodProxy.invokeSuper(target, args);
        if (after != null && !after.isEmpty()) {
            List<Method> methods = after.get(generateKey(originalMethod, args));
            for (Method e : methods) {
                e.invoke(e.getDeclaringClass().newInstance());
            }
        }
        return result;
    }

    /**
     * @return 返回值如下：
     * run:java.lang.String,java.lang,Integer 或者 run:
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
