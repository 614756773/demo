package cn.hotpot.demo.time1;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * @author qinzhu
 * @since 2019/12/5
 */
public class TimeServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ByteBuf buf = ctx.alloc().buffer(4);
        long nano = System.currentTimeMillis();
        System.out.println(nano);
        buf.writeLong(nano);
        ChannelFuture future = ctx.writeAndFlush(buf);
        future.addListener(new ChannelFutureListener() {
            // 若客户端调用了 f.channel().closeFuture().sync();
            // 只有当服务端显示调用ctx.close()时，才会断开连接
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                System.out.println("通知客户端断开连接");
                ctx.close();
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
