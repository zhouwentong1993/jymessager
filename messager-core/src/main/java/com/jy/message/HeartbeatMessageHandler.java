package com.jy.message;

import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// heartbeat message handler
@Component
public class HeartbeatMessageHandler implements MessageHandler {

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public void execute(MessageWrapper message) {
        // deal with heartbeat message



    }

    @Override
    public int getType() {
        return MessageType.HEARTBEAT;
    }
}
