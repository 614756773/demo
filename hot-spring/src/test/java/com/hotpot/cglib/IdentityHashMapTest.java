package com.hotpot.cglib;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * @author qinzhu
 * @since 2020/3/10
 */
public class IdentityHashMapTest {

    public static void main(String[] args) {
        Map<String, String> map = new IdentityHashMap<>();
        map.put(new String("1"), "123");
        map.put(new String("1"),"321");
        map.forEach((k,v) -> {
            System.out.println(k);
            System.out.println(v);
        });
    }

}
