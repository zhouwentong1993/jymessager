package com.jy.websocket.handler;

import com.jy.message.MessageHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpUtil.isKeepAlive;
import static io.netty.handler.codec.http.HttpUtil.setContentLength;

@Slf4j
@Component
public class MessagerChannelHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private WebSocketServerHandshaker handShaker;

    @Resource
    private Map<String, MessageHandler> messageHandlerMap;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("channelRead");
        if (msg instanceof FullHttpRequest) {
            // 处理 websocket 握手
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) { // 处理部分 websocket frame
            // check if close frame
            if (msg instanceof CloseWebSocketFrame) {
                CloseWebSocketFrame closeWebSocketFrame = (CloseWebSocketFrame) msg;
                log.info("close frame received, now clean up resources");
                handShaker.close(ctx.channel(), closeWebSocketFrame.retain());
            }
            // answer ping frame
            if (msg instanceof PingWebSocketFrame) {
                PingWebSocketFrame ping = (PingWebSocketFrame) msg;
                ctx.channel().write(new PongWebSocketFrame(ping.content().retain()));
            }
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {


    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
        if (!request.decoderResult().isSuccess() || (!"websocket".equals(request.headers().get("Upgrade")))) {
            log.error("websocket handshake error");
            sendHttpResponse(ctx, request, new DefaultFullHttpResponse(request.protocolVersion(), BAD_REQUEST));
            return;
        }
        // handle websocket handshake
        WebSocketServerHandshakerFactory webSocketServerHandshakerFactory = new WebSocketServerHandshakerFactory("ws://localhost:8080/websocket", null, false);
        handShaker = webSocketServerHandshakerFactory.newHandshaker(request);
        if (handShaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handShaker.handshake(ctx.channel(), request);
        }
    }

    private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest request, FullHttpResponse response) {

        if (response.status().code() != 200) {
            ByteBuf byteBuf = Unpooled.copiedBuffer(response.status().toString().getBytes());
            response.content().writeBytes(byteBuf);
            byteBuf.release();
            setContentLength(response, response.content().readableBytes());
        }

        ChannelFuture cf = ctx.channel().writeAndFlush(response);

        if (!isKeepAlive(request) || response.status().code() != 200) {
            cf.addListener(ChannelFutureListener.CLOSE);
        }

    }
}
