package com.jy.messager.config.redis;

public class RedisKey {

    public static String heartbeatKey(String deviceID) {
        return "heartbeat.deviceID:" + deviceID;
    }

    public static String messageKey(String messageId) {
        return "message.md5Id:" + messageId;
    }

    public static String ackKey(String md5Id) {
        return "ack.md5Id:" + md5Id;
    }

    public static String clientMessageKey(String clientID) {
        return "client.message.clientID:" + clientID;
    }

}
