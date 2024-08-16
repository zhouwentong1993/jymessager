package com.jy.config.websocket;

import com.jy.websocket.handler.WebsocketServerHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WebSocketChannelInitializer extends ChannelInitializer<Channel> {

    @Autowired
    private WebsocketServerHandler websocketServerHandler;

    @Override
    protected void initChannel(Channel channel) {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast("http-codec", new HttpServerCodec());
        pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
        pipeline.addLast("http-chucked", new ChunkedWriteHandler());
        pipeline.addLast("handler", websocketServerHandler);
    }
}
