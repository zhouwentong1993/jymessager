package com.jy.messager.message;

// 请求的消息类型集合
public class MessageType {

    public static final int HEARTBEAT = 1; // 心跳请求

    public static final int HANDSHAKE = 2; // 握手请求

    public static final int SEND_MESSAGE = 3; // 向客户端推送消息

    public static final int MESSAGE_ACK = 4; // 客户端的 ack

}
