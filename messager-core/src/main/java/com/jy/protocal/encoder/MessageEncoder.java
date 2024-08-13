package com.jy.protocal.encoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jy.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * 消息编码器，为方便前端解析，只支持 JSON 格式。
 */
public class MessageEncoder extends MessageToMessageEncoder<Message> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
        byte[] bytes = objectMapper.writeValueAsBytes(msg);
        ByteBuf byteBuf = ctx.alloc().buffer(bytes.length);
        byteBuf.writeBytes(bytes);
        out.add(byteBuf);
    }
}
