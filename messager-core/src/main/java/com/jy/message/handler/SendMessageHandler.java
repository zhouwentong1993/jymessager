package com.jy.message.handler;

import com.jy.config.redis.RedisService;
import com.jy.message.AbstractMessageHandler;
import com.jy.message.MessageType;
import com.jy.message.MessageWrapper;
import com.jy.registry.ChannelManager;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SendMessageHandler extends AbstractMessageHandler {

    @Autowired
    private ChannelManager channelManager;
    @Autowired
    private RedisService redisService;

    /**
     * 处理发送消息，这个的触发接收到消息队列的数据，触达到下层的设备
     * 所以直接 return 即可
     */
    @Override
    protected void doExecute(MessageWrapper message) {
        try {
            // deal with send message
            String clientID = message.getClientID();
            if (clientID == null || clientID.isEmpty()) {
                log.error("clientID is empty, message={}", message);
                return;
            }
            Channel channel = channelManager.getChannelByClientId(clientID);
            if (channel == null) {
                log.error("channel is null, clientID={}, save offline message.", clientID);
                // 离线消息存储，默认存储 7 天

                return;
            } else {
                // 发送消息，并且需要写入待 ack 记录，供 ack 使用
                log.info("send message to clientID={}, message={}", clientID, message.getBody());
                channel.writeAndFlush(message.getBody());
            }
        } catch (Exception e) {
            log.error("send message error", e);
        }


    }

    @Override
    public int getType() {
        return MessageType.SEND_MESSAGE;
    }
}
