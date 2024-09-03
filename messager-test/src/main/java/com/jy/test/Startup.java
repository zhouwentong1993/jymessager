package com.jy.test;

import com.alibaba.fastjson2.JSON;
import com.jy.messager.message.Message;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.util.concurrent.TimeUnit;

public class Startup {

    static String message = "This is a message, 这是一条消息";
    static String clientA = "a";
    static String clientB = "b";

    public static void main(String[] args) throws Exception {
        BaseTest.sendMQ(createMessage(clientA, message, 3));

        Channel channel = BaseTest.connect("ws://127.0.0.1:9092/websocket");
        TimeUnit.SECONDS.sleep(3);
        String handshakeMessage = createMessage(clientA, message, 1);
        WebSocketFrame frame = new TextWebSocketFrame(handshakeMessage);
        channel.writeAndFlush(frame);
        System.out.println("发送握手消息: " + handshakeMessage);

        sendHeartbeatMessage(channel);


    }

    private static void sendHeartbeatMessage(Channel channel) {
        new Thread(() -> {
            String heartbeatMessage = createMessage(clientA, message, 2);
            WebSocketFrame heartbeatFrame = new TextWebSocketFrame(heartbeatMessage);
            channel.writeAndFlush(heartbeatFrame);
            System.out.println("发送心跳消息: " + heartbeatMessage);
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public static String createMessage(String clientId, String body, int messageType) {
        Message message = new Message();
        message.setBody(body);
        message.setClientID(clientId);
        message.setClientType(1);
        message.setMessageType(messageType);
        message.setToken("token");
        return JSON.toJSONString(message);
    }
}
