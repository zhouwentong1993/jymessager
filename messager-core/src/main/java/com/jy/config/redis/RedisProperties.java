package com.jy.config.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "spring.redis")
@Configuration
@Data
public class RedisProperties {

    private String host;
    private Integer port;

}
