package cn.hotpot.demo.pojo;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author qinzhu
 * @since 2019/12/6
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Student student = new Student("火锅", 12, "男");
        ChannelFuture f = ctx.writeAndFlush(student);
        System.out.println("发送消息");
        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future == f) {
                    System.out.println("消息已发送完毕");
                }
            }
        });
    }
}
