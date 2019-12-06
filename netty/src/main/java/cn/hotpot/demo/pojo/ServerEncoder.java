package cn.hotpot.demo.pojo;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

import java.nio.charset.StandardCharsets;

/**
 * @author qinzhu
 * @since 2019/12/6
 */
public class ServerEncoder extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (!(msg instanceof Student)) {
            throw new RuntimeException("只能传输Student类型");
        }
        String str = JSONObject.toJSONString(msg);
        byte[] content = str.getBytes(StandardCharsets.UTF_8);
        ByteBuf buf = ctx.alloc().buffer();
        // 协议头：文本的长度
        buf.writeInt(content.length);
        // 协议内容
        buf.writeBytes(content);
        System.out.println("编码完毕");
        ctx.writeAndFlush(buf, promise);
    }
}
