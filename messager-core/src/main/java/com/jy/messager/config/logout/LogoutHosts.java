package com.jy.messager.config.logout;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "logout")
@Data
public class LogoutHosts {

    private List<String> hosts;

}
