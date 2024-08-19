package com.jy.message;

import io.netty.channel.Channel;
import lombok.Data;

@Data
public class MessageWrapper {

    private Long id; // 唯一标识

    private int messageType; // 请求

    private String clientID; // 设备id

    private String token; // 携带的授权信息

    private String body; // body 体

    private int clientType; // 设备类型，比如安卓机、POS 机之类的。

    private Channel channel; // 通道

    public static MessageWrapper wrap(Message message, Channel channel) {
        MessageWrapper wrapper = new MessageWrapper();
        wrapper.setId(message.getId());
        wrapper.setMessageType(message.getMessageType());
        wrapper.setClientID(message.getClientID());
        wrapper.setToken(message.getToken());
        wrapper.setBody(message.getBody());
        wrapper.setClientType(message.getClientType());
        wrapper.setChannel(channel);
        return wrapper;
    }

}
