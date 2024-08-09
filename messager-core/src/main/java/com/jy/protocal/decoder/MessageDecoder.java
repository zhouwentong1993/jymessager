package com.jy.protocal.decoder;

import com.jy.protocal.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class MessageDecoder extends MessageToMessageDecoder<Message> {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, Message message, List<Object> list) throws Exception {

    }
}
