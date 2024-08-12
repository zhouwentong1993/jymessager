package com.jy.config.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class NettyConfig {

    private final Logger logger = LoggerFactory.getLogger(NettyConfig.class);

    @Autowired
    private WebsocketConfig websocketConfig;
    @Autowired
    private WebSocketChannelInitializer webSocketChannelInitializer;

    private final NioEventLoopGroup bossGroup = new NioEventLoopGroup();
    private final NioEventLoopGroup workerGroup = new NioEventLoopGroup();

    @Bean
    public ServerBootstrap serverBootstrap() {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(webSocketChannelInitializer);
        serverBootstrap.bind(websocketConfig.getPort());
        logger.info("netty start on port: {}", websocketConfig.getPort());
        return serverBootstrap;
    }

}
