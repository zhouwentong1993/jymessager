package com.jy.protocal.decoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jy.protocal.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class MessageDecoder extends MessageToMessageDecoder<ByteBuf> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        int length = msg.readableBytes();
        byte[] bytes = new byte[length];
        msg.getBytes(msg.readerIndex(), bytes);
        Message object = objectMapper.readValue(bytes, Message.class);
        out.add(object);
    }
}
