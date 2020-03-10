package com.hotpot.exception;

/**
 * @author qinzhu
 * @since 2020/3/3
 */
public class HotSpringException extends RuntimeException {
    public HotSpringException(String msg) {
        super(msg);
    }

    public HotSpringException(Throwable throwable) {
        super(throwable);
    }
}
