package com.hotpot.design.pattern.filter;

/**
 * @author qinzhu
 * @since 2020/3/10
 */
public interface Filter {
    Object doFilter(FilterChain filterChain);
}
