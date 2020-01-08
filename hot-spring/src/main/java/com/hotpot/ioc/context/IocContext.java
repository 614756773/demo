package com.hotpot.ioc.context;

import com.hotpot.ioc.annotation.Autowired;
import com.hotpot.ioc.annotation.Component;
import com.hotpot.ioc.context.enhance.EnhanceHandler;
import com.hotpot.ioc.utils.ClassScanner;
import com.sun.istack.internal.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Stream;

/**
 * @author qinzhu
 * @since 2020/1/6
 */
public class IocContext implements ContextInterface {
    private final String basePackage = "com.hotpot.test";

    private Map<String, Object> beanMap = new HashMap<>();

    private List<EnhanceHandler> enhanceHandlers;

    protected IocContext(List<EnhanceHandler> enhanceHandlers) {
        this.enhanceHandlers = enhanceHandlers;
        this.enhanceHandlers.sort(Comparator.comparingInt(EnhanceHandler::getPriority));
        try {
            registerBean();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assembleBean();
    }

    @SuppressWarnings("unchecked")
    <T> T getBean(Class<T> clazz) {
        return (T) beanMap.get(clazz.getName());
    }

    @Override
    public void registerBean() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        // 1.扫描class文件
        Map<String, Class> classMap = ClassScanner.listClass(basePackage);
        // 2.反射实例化
        instanceSingleBean(classMap.keySet());
        // 3.子类实现更丰富的操作（比如aop，权限校验等）
        this.enhanceHandlers.forEach(e -> e.handle(this.beanMap));

    }

    @Override
    public void assembleBean() {
        beanMap.forEach((className, bean) -> {
            for (Field field : bean.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Autowired annotation = field.getAnnotation(Autowired.class);
                if (annotation == null) {
                    continue;
                }
                String injectBeanName = annotation.value();
                if ("".equals(injectBeanName)) {
                    injectBeanName = field.getType().getName();
                }
                try {
                    field.set(bean, beanMap.get(injectBeanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 实例化，如果没有注解或者是抽象类/接口 则不予实例化
     */
    private void instanceSingleBean(Collection<String> beanClass) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        for (String className : beanClass) {
            Class<?> aClass = Class.forName(className);
            if (!aClass.isAnnotationPresent(Component.class)) {
                continue;
            }
            int modifiers = aClass.getModifiers();
            if (Modifier.isInterface(modifiers) || Modifier.isAbstract(modifiers)) {
                continue;
            }
            Object bean = aClass.newInstance();
            Class<?>[] interfaces = aClass.getInterfaces();
            Class<?> superclass = aClass.getSuperclass();
            cacheBean(className, bean, interfaces, superclass);
        }
    }

    /**
     * 将实例化的bean缓存在全局{@code beanMap}中
     * 其中key为这个bean的类名、上级父类名、上级接口名
     * value为bean
     */
    private void cacheBean(String beanClassName, Object bean, @Nullable Class<?>[] interfaces, @Nullable Class<?> superclass) {
        List<String> list = new ArrayList<>();
        list.add(beanClassName);
        if (interfaces != null && interfaces.length != 0) {
            Stream.of(interfaces).forEach(e -> list.add(e.getName()));
        }
        if (superclass != null && superclass != Object.class) {
            list.add(superclass.getName());
        }

        list.forEach(className -> {
            Object existedBean = beanMap.get(className);
            if (existedBean != null) {
                throw new RuntimeException("required a single bean, but more were found:\n" +
                        "- " + beanClassName + "\n" +
                        "- " + existedBean.getClass().getName());
            }
            beanMap.put(className, bean);
        });
    }
}
