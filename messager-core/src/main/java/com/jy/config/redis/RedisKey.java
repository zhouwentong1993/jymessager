package com.jy.config.redis;

public class RedisKey {

    public static String heartbeatKey(String deviceID) {
        return "heartbeat:deviceID:" + deviceID;
    }

    public static String offlineKey(String deviceID) {
        return "offline:deviceID:" + deviceID;
    }

}
