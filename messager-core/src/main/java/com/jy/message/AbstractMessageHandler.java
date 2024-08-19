package com.jy.message;

import com.jy.protocal.constants.Response;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractMessageHandler implements MessageHandler {

    @Override
    public void execute(MessageWrapper message) {
        try {
            doExecute(message);
        } catch (Exception e) {
            log.error("execute message error", e);
            message.getChannel().writeAndFlush(Response.error("Execute message error, please retry later."));
        }
    }

    protected abstract void doExecute(MessageWrapper message);

}
