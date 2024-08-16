package com.jy.message.handler;

import com.jy.config.redis.RedisKey;
import com.jy.config.redis.RedisService;
import com.jy.message.MessageHandler;
import com.jy.message.MessageType;
import com.jy.message.MessageWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// heartbeat message handler
@Component
public class HeartbeatHandler implements MessageHandler {

    @Autowired
    private RedisService redisService;

    @Override
    public void execute(MessageWrapper message) {
        // deal with heartbeat message
        String clientID = message.getClientID();
        redisService.setAndExpire(RedisKey.heartbeatKey(clientID), String.valueOf(System.currentTimeMillis()), 60);
    }

    @Override
    public int getType() {
        return MessageType.HEARTBEAT;
    }
}
