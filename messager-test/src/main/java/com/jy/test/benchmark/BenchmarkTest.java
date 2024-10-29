package com.jy.test.benchmark;

import com.alibaba.fastjson2.JSON;
import com.jy.messager.message.Message;
import com.jy.test.BaseTest;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.util.concurrent.TimeUnit;

public class BenchmarkTest {

    static String message = "This is a message, 这是一条消息";
//    static String clientA = "a";
    static String clientB = "b";

    public static void main(String[] args) throws Exception {
//        //创建连接工厂
//        ConnectionFactory factory = new ConnectionFactory();
//
//        //设置RabbitMQ相关信息
//        factory.setHost("localhost");
//        factory.setUsername("guest");
//        factory.setPassword("guest");
//        factory.setPort(5672);
//
//        //创建一个新的连接
//        Connection connection = factory.newConnection();
//
//        //创建一个通道
//        Channel channel = connection.createChannel();
//        // 推送 100w 消息
//        for (int i = 0; i < 1000000; i++) {
//            BaseTest.sendMQ(createMessage(String.valueOf(i / 100000), message, 3), channel);
//        }
//        System.out.println("producer send message success");
//        System.out.println(System.currentTimeMillis());

        Channel[] channels = new Channel[100000];
        // 建立 10w 连接
        for (int i = 0; i < 100000; i++) {
            Channel channel = BaseTest.connect("ws://127.0.0.1:9092/websocket");
            channels[i] = channel;
        }
        System.out.println("连接成功~");
        for (int i = 0; i < 100000; i++) {
            Channel channel = channels[i];
            String handshakeMessage = createMessage(String.valueOf(i), message, 1);
            WebSocketFrame frame = new TextWebSocketFrame(handshakeMessage);
            channel.writeAndFlush(frame);
//            System.out.println("发送握手消息: " + handshakeMessage);
//            sendHeartbeatMessage(channel, String.valueOf(i));
        }
        System.out.println("消费开始~");
    }

    private static void sendHeartbeatMessage(Channel channel,String clientA) {
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
