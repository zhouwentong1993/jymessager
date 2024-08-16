package com.jy.message.handler;

import com.alibaba.fastjson2.JSON;
import com.jy.message.MessageHandler;
import com.jy.message.MessageType;
import com.jy.message.MessageWrapper;
import com.jy.message.handler.validator.HandshakeValidatorManagement;
import com.jy.protocal.constants.Response;
import com.jy.registry.ChannelManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HandshakeMessageHandler implements MessageHandler {

    @Autowired
    private ChannelManager channelManager;

    @Autowired
    private HandshakeValidatorManagement handshakeValidatorManagement;

    @Override
    public void execute(MessageWrapper message) {
        log.info("receive handshake message={}", message);
        // deal with handshake message
        if (!handshakeValidatorManagement.validate(message)) {
            log.error("handshake failed, message={}", message);
            message.getChannel().writeAndFlush(JSON.toJSONString(Response.error("handshake failed")));
            message.getChannel().close();
            return;
        }
        channelManager.register(message.getClientID(), message.getChannel());

    }

    @Override
    public int getType() {
        return MessageType.HANDSHAKE;
    }
}
