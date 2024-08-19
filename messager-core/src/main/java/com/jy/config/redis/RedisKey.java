package com.jy.config.redis;

public class RedisKey {

    public static String heartbeatKey(String deviceID) {
        return "heartbeat:deviceID:" + deviceID;
    }

}
