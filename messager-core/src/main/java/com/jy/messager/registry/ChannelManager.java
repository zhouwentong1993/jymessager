package com.jy.messager.registry;

import com.jy.messager.config.redis.RedisKey;
import com.jy.messager.config.redis.RedisService;
import com.jy.messager.timer.GlobalTimer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * channel 管理器
 */
@Slf4j
@Component
public class ChannelManager {

    // 设备 id 和通道之间的映射关系
    private final Map<String, Channel> deviceChannelMap = new ConcurrentHashMap<>();
    private final Map<ChannelId, Channel> channelMap = new ConcurrentHashMap<>();

    @Autowired
    private GlobalTimer globalTimer;
    @Autowired
    private RedisService redisService;

    public Channel getChannelByClientId(String clientId) {
        return deviceChannelMap.get(clientId);
    }

    public Channel removeChannelByChannelId(ChannelId channelId) {
        return channelMap.remove(channelId);
    }

    public Channel removeChannelByClientId(String clientId) {
        if (clientId == null || clientId.isEmpty()) {
            return null;
        }
        Channel channelRemoved = deviceChannelMap.remove(clientId);
        if (channelRemoved != null) {
            removeChannelByChannelId(channelRemoved.id());
            channelRemoved.close();
        }
        return channelRemoved;
    }

    public void register(String clientId, Channel channel) {
        deviceChannelMap.put(clientId, channel);
        channelMap.put(channel.id(), channel);
        globalTimer.submit(timeout -> {
            log.info("check device {} heartbeat", clientId);
            Channel channelByDeviceId = getChannelByDeviceId(clientId);
            if (channelByDeviceId == null) {
                log.info("device {} offline, remove it", clientId);
                deviceChannelMap.remove(clientId);
                channelMap.remove(channel.id());
            } else {
                log.info("device {} is online, now check heartbeat", clientId);
                String heartbeat = redisService.get(RedisKey.heartbeatKey(clientId));
                if (heartbeat == null || heartbeat.isEmpty()) {
                    log.info("device {} heartbeat timeout, remove it", clientId);
                    deviceChannelMap.remove(clientId);
                    channelMap.remove(channel.id());
                    channelByDeviceId.close();
                }
            }
        }, 30, TimeUnit.SECONDS);
    }

    public Channel getChannelByDeviceId(String deviceID) {
        return deviceChannelMap.get(deviceID);
    }

    public void unRegister(Channel channel) {
        deviceChannelMap.remove(channel.id().asLongText());
        channelMap.remove(channel.id());
        channel.close();
    }
}
