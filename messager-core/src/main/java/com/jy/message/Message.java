package com.jy.message;

import lombok.Data;

/**
 * 通信协议，请求体
 */
@Data
public class Message {

    private Long id; // 唯一标识

    private int messageType; // 请求

    private String clientID; // 设备id

    private String token; // 携带的授权信息

    private String body; // body 体

    private int clientType; // 设备类型，比如安卓机、POS 机之类的。

}
