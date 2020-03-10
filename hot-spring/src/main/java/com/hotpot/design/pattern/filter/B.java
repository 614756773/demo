package com.hotpot.design.pattern.filter;

/**
 * @author qinzhu
 * @since 2020/3/10
 */
public class B implements Filter {
    @Override
    public Object doFilter(FilterChain filterChain) {
        System.out.println("B处理前");
        Object result = filterChain.execute();
        System.out.println("B处理后");
        return result;
    }
}
