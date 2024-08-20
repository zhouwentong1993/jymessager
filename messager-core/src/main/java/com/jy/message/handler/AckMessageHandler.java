package com.jy.message.handler;

import com.jy.message.AbstractMessageHandler;
import com.jy.message.MessageType;
import com.jy.message.MessageWrapper;
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
