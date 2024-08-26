package com.jy.messager.message.handler;

import com.jy.messager.message.AbstractMessageHandler;
import com.jy.messager.message.MessageType;
import com.jy.messager.message.MessageWrapper;
import org.springframework.stereotype.Component;

@Component
public class AckMessageHandler extends AbstractMessageHandler {

    @Override
    protected void doExecute(MessageWrapper message) {
        // deal with ack message

    }

    @Override
    public int getType() {
        return MessageType.MESSAGE_ACK;
    }
}
