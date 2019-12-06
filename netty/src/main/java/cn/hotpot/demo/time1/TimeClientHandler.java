package cn.hotpot.demo.time1;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * @author qinzhu
 * @since 2019/12/5
 */
public class TimeClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        long nano = buf.readLong();
        System.out.println(nano);
        System.out.println(String.format("time：%s", convert(nano)));
    }

    private String convert(long nano) {
        LocalDateTime time = LocalDateTime.ofEpochSecond(nano / 1000, 0, ZoneOffset.ofHours(8));
        return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
