package com.jy.messager.listener;

import com.alibaba.fastjson2.JSON;
import com.jy.messager.message.Message;
import com.jy.messager.message.MessageWrapper;
import com.jy.messager.message.handler.SendMessageHandler;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageListener {

    @Autowired
    private SendMessageHandler sendMessageHandler;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "messager-queue", durable = "true"),
            exchange = @Exchange(value = "messager-exchange", type = "direct", durable = "true", ignoreDeclarationExceptions = "true")))
    public void onMessage(String msg) {
        System.out.println("Received msg: " + msg);
        Message message = JSON.parseObject(msg, Message.class);
        MessageWrapper wrap = MessageWrapper.wrap(message, null);
        sendMessageHandler.execute(wrap);
    }

}
