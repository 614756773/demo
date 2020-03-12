package com.hotpot.mvc;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;

import java.nio.charset.StandardCharsets;

/**
 * @author qinzhu
 * @since 2020/3/12
 */
public class HttpServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        HttpRequest request = (HttpRequest) msg;
        HttpMethod method = request.method();
        // TODO
        if (method.equals(HttpMethod.GET)) {
            // xxx
        } else if (method.equals(HttpMethod.POST)) {
            // xxx
        } else if (method.equals(HttpMethod.PUT)) {
            // xxx
        } else if (method.equals(HttpMethod.DELETE)) {
            // xxx
        } else if (method.equals(HttpMethod.OPTIONS)) {
            // xxx
        }
        ByteBuf buffer = ctx.alloc().buffer();
        buffer.writeBytes("你你你要跳舞吗".getBytes(StandardCharsets.UTF_8));
        HttpHeaders headers = new DefaultHttpHeaders(false);
        headers.add("Content-Type", "text/plain;charset=utf-8");
        HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_0, HttpResponseStatus.OK, buffer, headers, headers);
        ctx.writeAndFlush(response);
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        ctx.close();
    }
}
