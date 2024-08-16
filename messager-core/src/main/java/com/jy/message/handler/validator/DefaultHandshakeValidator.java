package com.jy.message.handler.validator;

import com.jy.message.MessageWrapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class DefaultHandshakeValidator implements HandshakeValidator {

    @Override
    public boolean validate(MessageWrapper message) {
        return !StringUtils.isEmpty(message.getClientID());
    }

    @Override
    public boolean exceptionCaught(MessageWrapper message, Throwable cause) {
        return false;
    }
}
