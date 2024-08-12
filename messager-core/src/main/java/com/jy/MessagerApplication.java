package com.jy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MessagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MessagerApplication.class);
        System.out.println("Message service started successfully!");
    }

}
