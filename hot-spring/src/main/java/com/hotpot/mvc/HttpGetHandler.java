package com.hotpot.mvc;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author qinzhu
 * @since 2020/3/13
 * get请求处理器
 */
public class HttpGetHandler extends AbstractHttpRequestHandler<Map<String, String>> {

    public HttpGetHandler(HttpRequest request, ByteBuf byteBuf) {
        super(request, byteBuf);
    }

    public HttpGetHandler(HttpRequest request, ByteBuf byteBuf, AbstractHttpRequestHandler successor) {
        super(request, byteBuf, successor);
    }

    @Override
    public HttpResponse doHandle() throws InvocationTargetException, IllegalAccessException {
        String uri = request.uri();
        Map<String, String> params = parseParams(request);

        Object bean = mappingController(uri);
        Method method = mappingMethod(bean.getClass(), uri);
        String result = (String) method.invoke(bean, convertArgs(params));

        HttpHeaders headers = new DefaultHttpHeaders(false);
        headers.add("Content-Type", "text/plain;charset=utf-8");
        return produceResponse(headers, result.getBytes());
    }

    private Object convertArgs(Map<String, String> params) {
        return null;
    }

    private Method mappingMethod(Class<?> aClass, String uri) {
        return null;
    }

    private Object mappingController(String uri) {
        return null;
    }

    @Override
    HttpMethod methodType() {
        return HttpMethod.GET;
    }

    @Override
    Map<String, String> parseParams(HttpRequest request) {
        return null;
    }
}
