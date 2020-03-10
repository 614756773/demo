package com.hotpot.design.pattern.filter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qinzhu
 * @since 2020/3/10
 */
public class FilterChain {
    private List<Filter> filterList;

    private int index;

    public FilterChain(List<Filter> filterList) {
        this.filterList = filterList;
    }

    public Object execute() {
        if (index >= filterList.size()) {
            System.out.println("处理完毕");
            return null;
        }
        return filterList.get(index++).doFilter(this);
    }

    public static void main(String[] args) {
        List<Filter> filters = new ArrayList<>(2);
        filters.add(new A());
        filters.add(new B());

        Object execute = new FilterChain(filters).execute();
        System.out.println(execute);
    }
}
