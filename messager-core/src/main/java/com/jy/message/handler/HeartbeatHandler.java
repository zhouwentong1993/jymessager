package com.jy.message.handler;

import com.alibaba.fastjson2.JSON;
import com.jy.config.redis.RedisKey;
import com.jy.config.redis.RedisService;
import com.jy.message.AbstractMessageHandler;
import com.jy.message.MessageType;
import com.jy.message.MessageWrapper;
import com.jy.protocal.constants.Response;
import com.jy.registry.ChannelManager;
import com.jy.timer.GlobalTimer;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

// heartbeat message handler
@Component
@Slf4j
public class HeartbeatHandler extends AbstractMessageHandler {

    @Autowired
    private RedisService redisService;
    @Autowired
    private GlobalTimer globalTimer;
    @Autowired
    private ChannelManager channelManager;

    @Override
    public void doExecute(MessageWrapper message) {
        // deal with heartbeat message
        String clientID = message.getClientID();
        Channel handshakeChannel = channelManager.getChannelByClientId(clientID);
        if (handshakeChannel == null) {
            log.error("clientID={} not online", clientID);
            message.getChannel().writeAndFlush(JSON.toJSONString(Response.error("client not online, please handshake first")));
            return;
        } else if (!handshakeChannel.equals(message.getChannel())) { // 需要保持长连接，新建连接必须重新握手再重试
            log.error("clientID={} not online", clientID);
            message.getChannel().writeAndFlush(JSON.toJSONString(Response.error("client not online, please handshake first")));
            return;
        }
        redisService.setAndExpire(RedisKey.heartbeatKey(clientID), String.valueOf(System.currentTimeMillis()), 60);
        message.getChannel().writeAndFlush(JSON.toJSONString(Response.success()));
        // 心跳消息，添加 60s 的时间轮
        globalTimer.submit(timeout -> {
            String heartbeat = redisService.get(RedisKey.heartbeatKey(clientID));
            if (heartbeat == null || heartbeat.isEmpty()) {
                log.info("device {} heartbeat timeout, remove it", clientID);
                Channel channel = channelManager.removeChannelByClientId(clientID);
                if (channel != null) {
                    channelManager.removeChannelByChannelId(channel.id());
                    channel.close();
                }
            }
        }, 60, TimeUnit.SECONDS);
    }

    @Override
    public int getType() {
        return MessageType.HEARTBEAT;
    }
}
