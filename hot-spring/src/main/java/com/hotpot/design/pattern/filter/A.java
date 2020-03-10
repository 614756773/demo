package com.hotpot.design.pattern.filter;

/**
 * @author qinzhu
 * @since 2020/3/10
 */
public class A implements Filter {
    @Override
    public Object doFilter(FilterChain filterChain) {
        System.out.println("A处理前");
        Object result = filterChain.execute();
        System.out.println("A处理后");
        return result;
    }
}
