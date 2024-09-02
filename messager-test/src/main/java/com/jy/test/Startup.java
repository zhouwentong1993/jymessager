package com.jy.test;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.util.concurrent.TimeUnit;

public class Startup {

    public static void main(String[] args) throws Exception {
        BaseTest.sendMQ("{\n" +
                "    \"body\": \"hello world\",\n" +
                "    \"clientID\": \"b\",\n" +
                "    \"clientType\": 1,\n" +
                "    \"id\": 1,\n" +
                "    \"messageType\": 1,\n" +
                "    \"token\": \"token\"\n" +
                "}");
        Channel channel = BaseTest.connect("ws://127.0.0.1:9092/websocket");
        TimeUnit.SECONDS.sleep(3);
        String message = "{\n" +
                "    \"body\": \"c25f3ec3-85ea-44a8-9f67-03e10e072b30\",\n" +
                "    \"clientID\": \"b\",\n" +
                "    \"clientType\": 1,\n" +
                "    \"id\": 1,\n" +
                "    \"messageType\": 1,\n" +
                "    \"token\": \"token\"\n" +
                "}";
        WebSocketFrame frame = new TextWebSocketFrame(message);
        channel.writeAndFlush(frame);
        System.out.println("发送握手消息: " + message);
    }
}
