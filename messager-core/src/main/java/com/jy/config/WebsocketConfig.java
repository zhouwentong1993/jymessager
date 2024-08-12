package com.jy.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "websocket")
@Configuration
@Data
public class WebsocketConfig {
    private Integer port;
}
