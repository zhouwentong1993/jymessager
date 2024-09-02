package com.jy.messager.message.handler;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.jy.messager.config.logout.LogoutHosts;
import com.jy.messager.message.AbstractMessageHandler;
import com.jy.messager.message.MessagePair;
import com.jy.messager.message.MessageType;
import com.jy.messager.message.MessageWrapper;
import com.jy.messager.message.handler.validator.HandshakeValidatorManagement;
import com.jy.messager.message.redis.MessageStorageService;
import com.jy.messager.protocal.constants.Response;
import com.jy.messager.registry.ChannelManager;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class HandshakeMessageHandler extends AbstractMessageHandler {

    @Autowired
    private ChannelManager channelManager;
    @Autowired
    private HandshakeValidatorManagement handshakeValidatorManagement;
    @Autowired
    private LogoutHosts logoutHosts;
    @Autowired
    private MessageStorageService messageStorageService;

    @Override
    public void doExecute(MessageWrapper message) {
        log.info("receive handshake message={}", message);
        // deal with handshake message
        if (!handshakeValidatorManagement.validate(message)) {
            log.error("handshake failed, message={}", message);
            message.getChannel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(Response.error("handshake failed"))));
            message.getChannel().close();
            return;
        }
        channelManager.register(message.getClientID(), message.getChannel());
        message.getChannel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(Response.success())));
        // 向其他所有节点发送登出命令，避免多点登录
        try {
            logoutHosts.getHosts().forEach(host -> HttpUtil.post(host + "/message/kickOut", JSON.toJSONString(message)));
        } catch (Exception e) {
            log.error("kick out error", e);
        }
        // 向客户端发送离线消息，获取所有离线消息
        List<MessagePair> offlineMessage = messageStorageService.getOfflineMessage(message.getClientID());
        offlineMessage.forEach(msg -> message.getChannel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(msg))));
    }



    @Override
    public int getType() {
        return MessageType.HANDSHAKE;
    }
}
