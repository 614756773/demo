package com.hotpot.ioc.context;

import com.hotpot.ioc.context.enhance.EnhanceHandler;
import com.hotpot.ioc.utils.ClassScanner;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author qinzhu
 * @since 2020/1/8
 */
public class ContextFactory {

    private static IocContext context;

    static {
        List<EnhanceHandler> enhanceHandlers = new ArrayList<>();
        Map<String, Class> classMap = ClassScanner.listClass("com.hotpot.ioc.context.enhance");
        classMap.values().stream()
                .filter(EnhanceHandler.class::isAssignableFrom)
                .forEach(clazz -> {
                    try {
                        enhanceHandlers.add((EnhanceHandler) clazz.newInstance());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
        ContextFactory.context = new IocContext(enhanceHandlers);
    }

    private ContextFactory(){}

    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }
}
