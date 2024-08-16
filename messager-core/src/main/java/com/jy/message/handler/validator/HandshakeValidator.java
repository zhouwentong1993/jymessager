package com.jy.message.handler.validator;

import com.jy.message.MessageWrapper;

/**
 * 握手消息验证器
 */
public interface HandshakeValidator {

    boolean validate(MessageWrapper message);

    boolean exceptionCaught(MessageWrapper message, Throwable cause);

}
