package com.jy.messager.controller;

import com.jy.messager.message.Message;
import com.jy.messager.registry.ChannelManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private ChannelManager channelManager;

    @GetMapping("/kickOut")
    public String kickOut(@RequestBody Message message) {
        channelManager.removeChannelByClientId(message.getClientID());
        return "success";
    }

}
