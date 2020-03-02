package com.hotpot.ioc.context.enhance;

import com.hotpot.ioc.model.BeanMetadata;

import java.util.Map;

/**
 * @author qinzhu
 * @since 2020/1/8
 */
public interface EnhanceHandler {

    /**
     * EnhanceHandler的优先级，默认为{@code Integer.MAX_VALUE}，即优先级最低
     */
    default int getPriority() {
        return Integer.MAX_VALUE;
    }

    /**
     * 具体的处理方法
     * @param beanMap ioc容器中的bean，key为className，value为bean及其元数据
     */
    void handle(Map<String, BeanMetadata> beanMap, Map<String, Class> classMap);

}
