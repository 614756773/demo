package cn.hotpot.demo.time2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author qinzhu
 * @since 2019/12/5
 */
public class TimeDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 只有当可读字节数大于8的时候才添加消息到消费队列中
        // 因为我们知道服务端传递的是一个long类型的数据，一共8个字节
        if (in.readableBytes() < 8) {
            System.out.println("数据还不足，不予解析");
            return;
        }
        out.add(in.readBytes(8));
    }
}
