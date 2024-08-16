package com.jy.message.handler.validator;

import com.jy.message.MessageWrapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HandshakeValidatorManagement {

    private final List<HandshakeValidator> handshakeValidator;

    public HandshakeValidatorManagement(List<HandshakeValidator> handshakeValidator) {
        this.handshakeValidator = handshakeValidator;
    }

    public boolean validate(MessageWrapper message) {
        for (HandshakeValidator validator : handshakeValidator) {
            try {
                if (!validator.validate(message)) {
                    return false;
                }
            } catch (Exception e) {
                if (!validator.exceptionCaught(message, e)) {
                    return false;
                }
            }
        }
        return true;
    }
}
