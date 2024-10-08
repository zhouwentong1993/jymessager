package com.jy.messager.message;

import com.alibaba.fastjson2.JSON;
import com.jy.messager.protocal.constants.Response;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

import static com.jy.messager.protocal.constants.ResponseType.SYSTEM_ERROR;

@Slf4j
public abstract class AbstractMessageHandler implements MessageHandler {

    @Override
    public void execute(MessageWrapper message) {
        try {
            log.info("execute message={}", message);
            doExecute(message);
        } catch (Exception e) {
            log.error("execute message error", e);
            message.getChannel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(Response.error("Execute message error, please retry later.", SYSTEM_ERROR))));
        }
    }

    protected abstract void doExecute(MessageWrapper message);

}
