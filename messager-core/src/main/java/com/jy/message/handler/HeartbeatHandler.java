package com.jy.message.handler;

import com.alibaba.fastjson2.JSON;
import com.jy.config.redis.RedisKey;
import com.jy.config.redis.RedisService;
import com.jy.message.AbstractMessageHandler;
import com.jy.message.MessageType;
import com.jy.message.MessageWrapper;
import com.jy.protocal.constants.Response;
import com.jy.timer.GlobalTimer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

// heartbeat message handler
@Component
public class HeartbeatHandler extends AbstractMessageHandler {

    @Autowired
    private RedisService redisService;
    @Autowired
    private GlobalTimer globalTimer;

    @Override
    public void doExecute(MessageWrapper message) {
        // deal with heartbeat message
        String clientID = message.getClientID();
        redisService.setAndExpire(RedisKey.heartbeatKey(clientID), String.valueOf(System.currentTimeMillis()), 60);
        message.getChannel().writeAndFlush(JSON.toJSONString(Response.success()));
        // 心跳消息，添加 60s 的时间轮
        globalTimer.submit(timeout -> {
            String heartbeat = redisService.get(RedisKey.heartbeatKey(clientID));
            if (heartbeat == null || heartbeat.isEmpty()) {
                message.getChannel().close();
            }
        }, 60, TimeUnit.SECONDS);
    }

    @Override
    public int getType() {
        return MessageType.HEARTBEAT;
    }
}
