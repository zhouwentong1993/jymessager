package com.jy.messager.protocal.constants;

public class ResponseType {

    public static final int HANDSHAKE = 1; // 握手请求

    public static final int HEARTBEAT = 2; // 心跳请求

    public static final int SEND_MESSAGE = 3; // 向客户端推送消息

    public static final int MESSAGE_ACK = 4; // 客户端的 ack

    public static final int SYSTEM_ERROR = -1; // 系统错误

}
