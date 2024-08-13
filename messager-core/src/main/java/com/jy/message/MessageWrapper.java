package com.jy.message;

import lombok.Data;

import java.nio.channels.Channel;

@Data
public class MessageWrapper {

    private Long id; // 唯一标识

    private String messageType; // 请求

    private String clientID; // 设备id

    private String token; // 携带的授权信息

    private String body; // body 体

    private int clientType; // 设备类型，比如安卓机、POS 机之类的。

    private Channel channel; // 通道

}
