package com.jy.messager.config.redis;

import cn.hutool.core.collection.CollUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Component
public class RedisService {

    @Autowired
    private JedisPool jedisPool;

    public void setAndExpire(String key, String value, int seconds) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.setex(key, seconds, value);
        }
    }

    public void expire(String key, int seconds) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.expire(key, seconds);
        }
    }

    public void remove(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(key);
        }
    }

    public String get(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        }
    }

    public void zadd(String key, double score, String member) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.zadd(key, score, member);
        }
    }

    public void zrem(String key, String... members) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.zrem(key, members);
        }
    }

    public List<String> zrangeByScore(String key, long start, long end) {
        try (Jedis jedis = jedisPool.getResource()) {
            Set<String> zrange = jedis.zrangeByScore(key, (double) start, (double) end);
            if (CollUtil.isEmpty(zrange)) {
                return Collections.emptyList();
            } else {
                return new ArrayList<>(zrange);
            }
        }
    }

}
