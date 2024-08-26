package com.jy.messager.controller;

import com.jy.messager.message.Message;
import com.jy.messager.registry.ChannelManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/message")
@Slf4j
public class MessageController {

    @Autowired
    private ChannelManager channelManager;

    @PostMapping("/kickOut")
    public String kickOut(@RequestBody Message message) {
        log.info("kick out message={}", message);
        channelManager.removeChannelByClientId(message.getClientID());
        return "success";
    }

}
