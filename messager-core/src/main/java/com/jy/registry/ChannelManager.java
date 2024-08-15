package com.jy.registry;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通道管理器
 */
@Component
public class ChannelManager {

    // 设备 id 和通道之间的映射关系
    private final Map<String, Channel> deviceChannelMap = new ConcurrentHashMap<>();
    private final Map<ChannelId, Channel> channelMap = new ConcurrentHashMap<>();

    public void register(String deviceID, Channel channel) {
        deviceChannelMap.put(deviceID, channel);
        channelMap.put(channel.id(), channel);
    }

    public void unRegister(Channel channel) {
        deviceChannelMap.remove(channel.id().asLongText());
        channelMap.remove(channel.id());
        channel.close();
    }

}
