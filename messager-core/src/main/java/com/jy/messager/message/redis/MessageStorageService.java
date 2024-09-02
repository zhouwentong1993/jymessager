package com.jy.messager.message.redis;

import cn.hutool.core.collection.CollUtil;
import com.jy.messager.config.redis.RedisKey;
import com.jy.messager.config.redis.RedisService;
import com.jy.messager.message.MessagePair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MessageStorageService {

    @Autowired
    private RedisService redisService;

    public List<MessagePair> getOfflineMessage(String clientID) {
        long now = System.currentTimeMillis();
        List<String> strings = redisService.zrangeByScore(RedisKey.clientMessageKey(clientID), now - 7 * 24 * 60 * 60 * 1000, now);
        if (CollUtil.isEmpty(strings)) {
            return Collections.emptyList();
        } else {
            List<MessagePair> messages = strings.stream().map(messageId -> {
                String realMessage = redisService.get(RedisKey.messageKey(messageId));
                // 重建 ack 时间，30s 内 ack
                redisService.expire(RedisKey.ackKey(realMessage), 30);
                return new MessagePair(messageId, realMessage);
            }).collect(Collectors.toList());
            log.info("get message, clientID={}, messages={}", clientID, messages);
            return messages;
        }
    }

}
