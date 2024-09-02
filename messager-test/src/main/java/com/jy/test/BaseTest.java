package com.jy.test;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;

import java.net.URI;

public class BaseTest {

    public static void sendMQ(String content) throws Exception {
        //创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();

        //设置RabbitMQ相关信息
        factory.setHost("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setPort(5672);

        //创建一个新的连接
        Connection connection = factory.newConnection();

        //创建一个通道
        Channel channel = connection.createChannel();

        // 声明一个队列
//        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        //发送消息到队列中
        channel.basicPublish("messager-exchange", "", null, content.getBytes());
        System.out.println("Producer Send +'" + content + "'");

        //关闭通道和连接
        channel.close();
        connection.close();
    }

    public static io.netty.channel.Channel connect(String URL) throws Exception {
        URI uri = new URI(URL);
        String scheme = uri.getScheme() == null ? "ws" : uri.getScheme();
        final int port;
        port = uri.getPort();

        if (!"ws".equalsIgnoreCase(scheme) && !"wss".equalsIgnoreCase(scheme)) {
            System.err.println("Only WS(S) is supported.");
            return null;
        }
        EventLoopGroup group = new NioEventLoopGroup();
//        try {
            // Connect with V13 (RFC 6455 aka HyBi-17). You can change it to V08 or V00.
            // If you change it to V00, ping is not supported and remember to change
            // HttpResponseDecoder to WebSocketHttpResponseDecoder in the pipeline.
            final WebSocketClientHandler handler = new WebSocketClientHandler(WebSocketClientHandshakerFactory.newHandshaker(uri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders()));

            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) {
                    ChannelPipeline p = ch.pipeline();
                    p.addLast(new HttpClientCodec(), new HttpObjectAggregator(8192), WebSocketClientCompressionHandler.INSTANCE, handler);
                }
            });

            io.netty.channel.Channel ch = b.connect(uri.getHost(), port).sync().channel();
//            handler.handshakeFuture().sync();
//            ch.closeFuture().sync();
            return ch;

//        } finally {
//            group.shutdownGracefully();
//        }
    }

}
