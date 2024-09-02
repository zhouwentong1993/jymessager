package com.jy.messager.message.handler;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.jy.messager.config.redis.RedisKey;
import com.jy.messager.config.redis.RedisService;
import com.jy.messager.message.AbstractMessageHandler;
import com.jy.messager.message.MessageType;
import com.jy.messager.message.MessageWrapper;
import com.jy.messager.protocal.constants.Response;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AckMessageHandler extends AbstractMessageHandler {

    @Autowired
    private RedisService redisService;

    @Override
    protected void doExecute(MessageWrapper message) {
        // deal with ack message
        String ackId = message.getBody();
        if (ackId == null || ackId.isEmpty()) {
            message.getChannel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(Response.error("ackId is empty"))));
            message.getChannel().close();
        }

        String key = redisService.get(RedisKey.ackKey(ackId));
        if (StrUtil.isNotBlank(key)) {
            redisService.remove(RedisKey.ackKey(ackId));
            redisService.zrem(RedisKey.clientMessageKey(message.getClientID()), ackId);
            log.info("ack success, ackId={}, key={}", ackId, key);
            message.getChannel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(Response.success())));
        } else {
            log.error("ack failed, ackId={} not exists.", ackId);
            message.getChannel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(Response.error("ack id not exists."))));
        }
    }

    @Override
    public int getType() {
        return MessageType.MESSAGE_ACK;
    }
}
