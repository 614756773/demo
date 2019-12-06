package cn.hotpot.demo.pojo;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author qinzhu
 * @since 2019/12/6
 */
public class ClientDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 4) {
            return;
        }
        int dataLen = in.readInt();
        byte[] data = new byte[dataLen];
        in.readBytes(data);
        String str = new String(data, StandardCharsets.UTF_8);
        Student student = JSONObject.parseObject(str, Student.class);
        System.out.println("解码完成");
        out.add(student);
    }
}
