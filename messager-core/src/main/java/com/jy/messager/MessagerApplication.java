package com.jy.messager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class MessagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MessagerApplication.class);
        log.info("Message service started successfully!");
    }
}
