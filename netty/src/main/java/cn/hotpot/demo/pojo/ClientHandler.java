package cn.hotpot.demo.pojo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author qinzhu
 * @since 2019/12/6
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Student student = (Student) msg;
        System.out.println(String.format("收到消息，%s", student));
        ctx.close();
    }
}
