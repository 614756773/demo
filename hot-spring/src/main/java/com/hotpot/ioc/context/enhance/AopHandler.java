package com.hotpot.ioc.context.enhance;

import com.hotpot.aop.annotation.Aspect;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author qinzhu
 * @since 2020/1/8
 */
public class AopHandler implements EnhanceHandler {
    @Override
    public int getPriority() {
        return -1;
    }

    @Override
    public void handle(Map<String, Object> beanMap) {
        List<Class> aspectClassList = new ArrayList<>();
        filterAspectClass(beanMap, aspectClassList);

    }

    /**
     * 筛选出切面类
     */
    private void filterAspectClass(Map<String, Object> beanMap, List<Class> aspectClassList) {
        try {
            for (String className : beanMap.keySet()) {
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(Aspect.class)) {
                    aspectClassList.add(clazz);
                }
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
