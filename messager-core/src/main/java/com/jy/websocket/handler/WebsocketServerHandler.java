package com.jy.websocket.handler;

import com.alibaba.fastjson2.JSON;
import com.jy.protocal.constants.Response;
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

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpUtil.isKeepAlive;
import static io.netty.handler.codec.http.HttpUtil.setContentLength;

@Slf4j
@Component
@Deprecated
public class WebsocketServerHandler extends SimpleChannelInboundHandler<Object> {

    private WebSocketServerHandshaker handShaker;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(channelHandlerContext, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            handleWebSocketFrame(channelHandlerContext, (WebSocketFrame) msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    private void handleWebSocketFrame(ChannelHandlerContext channelHandlerContext, WebSocketFrame frame) {
        // check if close frame
        if (frame instanceof CloseWebSocketFrame) {
            handShaker.close(channelHandlerContext.channel(), (CloseWebSocketFrame) frame.retain());
        }

        // answer ping frame
        // todo remember to control ping period
        if (frame instanceof PingWebSocketFrame) {
            PingWebSocketFrame ping = (PingWebSocketFrame) frame;
            channelHandlerContext.channel().write(new PongWebSocketFrame(ping.content().retain()));
            return;
        }

        if (!(frame instanceof TextWebSocketFrame)) {
            log.error("unsupported frame type");
            throw new UnsupportedOperationException(String.format("%s frame type not supported", frame.getClass().getName()));
        }
        TextWebSocketFrame textWebSocketFrame = (TextWebSocketFrame) frame;

        String text = textWebSocketFrame.text();
        boolean valid = JSON.isValid(text);// check if json format
        if (!valid) {
            log.error("invalid json format");
            channelHandlerContext.channel().write(new TextWebSocketFrame(JSON.toJSONString(Response.error("invalid json format"))));
            return;
        }
        log.info("received message: {}", text);
        channelHandlerContext.channel().write(new TextWebSocketFrame("received: " + text));

    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
        log.info("received http request");
        if (!request.decoderResult().isSuccess() || (!"websocket".equals(request.headers().get("Upgrade")))) {
            log.error("websocket handshake error");
            sendHttpResponse(ctx, request, new DefaultFullHttpResponse(request.protocolVersion(), BAD_REQUEST));
            return;
        }
        // handle websocket handshake
        WebSocketServerHandshakerFactory webSocketServerHandshakerFactory = new WebSocketServerHandshakerFactory("ws://localhost:9090/websocket", null, false);
        handShaker = webSocketServerHandshakerFactory.newHandshaker(request);
        if (handShaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            log.info("websocket handshake success");
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
