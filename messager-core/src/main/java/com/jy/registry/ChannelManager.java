package com.jy.registry;

import com.jy.config.redis.RedisKey;
import com.jy.config.redis.RedisService;
import com.jy.timer.GlobalTimer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 通道管理器
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

    public void register(String deviceID, Channel channel) {
        deviceChannelMap.put(deviceID, channel);
        channelMap.put(channel.id(), channel);
        globalTimer.submit(timeout -> {
            log.info("check device {} heartbeat", deviceID);
            Channel channelByDeviceId = getChannelByDeviceId(deviceID);
            if (channelByDeviceId == null) {
                log.info("device {} offline, remove it", deviceID);
                deviceChannelMap.remove(deviceID);
                channelMap.remove(channel.id());
            } else {
                log.info("device {} is online, now check heartbeat", deviceID);
                String heartbeat = redisService.get(RedisKey.heartbeatKey(deviceID));
                if (heartbeat == null || heartbeat.isEmpty()) {
                    log.info("device {} heartbeat timeout, remove it", deviceID);
                    deviceChannelMap.remove(deviceID);
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
