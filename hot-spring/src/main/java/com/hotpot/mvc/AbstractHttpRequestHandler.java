package com.hotpot.mvc;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.*;

import java.lang.reflect.InvocationTargetException;

/**
 * @author qinzhu
 * @since 2020/3/13
 * <P> 解析请求后的参数类型，GET请求解析后参数是Map，POST请求解析后参数是json字符串
 */
abstract class AbstractHttpRequestHandler<P> {
    /**
     * 用于解析请求
     */
    HttpRequest request;
    /**
     * 用于向请求方输出结果
     */
    private ByteBuf byteBuf;
    /**
     * 责任链模式
     */
    private AbstractHttpRequestHandler successor;

    AbstractHttpRequestHandler(HttpRequest request, ByteBuf byteBuf) {
        this.request = request;
        this.byteBuf = byteBuf;
    }

    AbstractHttpRequestHandler(HttpRequest request, ByteBuf byteBuf, AbstractHttpRequestHandler successor) {
        this.successor = successor;
        this.request = request;
        this.byteBuf = byteBuf;
    }

    public HttpResponse handle() {
        if (request.method().equals(methodType())) {
            try {
                return doHandle();
            } catch (InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return successor.handle();
    }

    HttpResponse produceResponse(HttpHeaders headers, byte[] content) {
        byteBuf.writeBytes(content);
        // TODO 不是很懂为啥要两个headers
        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf, headers, headers);
    }

    /**
     * 子类实现真正的处理方法
     */
    abstract HttpResponse doHandle() throws InvocationTargetException, IllegalAccessException;

    abstract HttpMethod methodType();

    /**
     * 解析参数
     */
    abstract P parseParams(HttpRequest request);
}
