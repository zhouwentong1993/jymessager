package com.jy.config.message;

import com.jy.message.MessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class MessageHandlerConfiguration {

    // 将所有的 MessageHandler 放入 map 中
    @Autowired
    private List<MessageHandler> messageHandlers;

    @Bean
    public Map<Integer, MessageHandler> messageHandlerMap() {
        Map<Integer, MessageHandler> messageHandlerMap = new HashMap<>();
        for (MessageHandler handler : messageHandlers) {
            messageHandlerMap.put(handler.getType(), handler);
        }
        return messageHandlerMap;
    }
}
