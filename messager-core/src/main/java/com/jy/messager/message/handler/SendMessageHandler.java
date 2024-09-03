package com.jy.messager.message.handler;

import com.alibaba.fastjson2.JSON;
import com.jy.messager.config.redis.RedisKey;
import com.jy.messager.config.redis.RedisService;
import com.jy.messager.message.AbstractMessageHandler;
import com.jy.messager.message.MessagePair;
import com.jy.messager.message.MessageType;
import com.jy.messager.message.MessageWrapper;
import com.jy.messager.protocal.constants.Response;
import com.jy.messager.protocal.constants.ResponseType;
import com.jy.messager.registry.ChannelManager;
import com.jy.messager.timer.GlobalTimer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class SendMessageHandler extends AbstractMessageHandler {

    @Autowired
    private ChannelManager channelManager;
    @Autowired
    private RedisService redisService;
    @Autowired
    private GlobalTimer globalTimer;

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
            // 先把消息存储起来
            String ackId = messageSaveWithAck(message, clientID);

            if (channel == null) {
                log.error("channel is null, clientID={}, save offline message.", clientID);
            } else {
                // 发送消息，并且需要写入待 ack 记录，供 ack 使用
                log.info("send message to clientID={}, message={}", clientID, message.getBody());
                ChannelFuture channelFuture = channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(Response.success(MessagePair.builder().message(message.getBody()).messageId(ackId).build(), ResponseType.SEND_MESSAGE))));

                channelFuture.addListener(future -> {
                    if (future.isSuccess()) {
                        log.info("send message success");
                    } else {
                        log.error("send message error");
                        globalTimer.submit(timeout -> {
                            ChannelFuture channelFuture1 = channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(Response.success(MessagePair.builder().message(message.getBody()).messageId(ackId).build(), ResponseType.SEND_MESSAGE))));
                            channelFuture1.addListener(future1 -> {
                                if (future1.isSuccess()) {
                                    log.info("retry send message success");
                                } else {
                                    log.error("retry send message error");
                                }
                            });
                        },3, TimeUnit.SECONDS);
                    }
                });
            }
        } catch (Exception e) {
            log.error("send message error", e);
        }
    }

    private String messageSaveWithAck(MessageWrapper message, String clientID) {
        // 离线消息存储，默认存储 7 天
        // 不能使用 MD5 摘要，因为可能会有重复消息
//        String md5 = MD5.create().digestHex(message.getBody());
        String uuid = UUID.randomUUID().toString();
        // cache offline message for 7 days
        redisService.setAndExpire(RedisKey.messageKey(uuid), message.getBody(), 7 * 24 * 60 * 60);
        redisService.zadd(RedisKey.clientMessageKey(clientID), System.currentTimeMillis(), uuid);

        // 30s 没有 ack 就取消
        redisService.setAndExpire(RedisKey.ackKey(uuid), String.valueOf(System.currentTimeMillis()), 30);
        return uuid;
    }

    @Override
    public int getType() {
        return MessageType.SEND_MESSAGE;
    }
}
