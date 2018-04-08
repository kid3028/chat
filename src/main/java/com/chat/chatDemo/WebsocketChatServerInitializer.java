package com.chat.chatDemo;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class WebsocketChatServerInitializer extends
        ChannelInitializer<SocketChannel> { //1

    @Override
    public void initChannel(SocketChannel ch) throws Exception {//2
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new HttpServerCodec())
                .addLast(new HttpObjectAggregator(64*1024))
                .addLast(new ChunkedWriteHandler())
                .addLast(new HttpRequestHandler("/ws"))
                .addLast(new WebSocketServerProtocolHandler("/ws"))
                .addLast(new TextWebSocketFrameHandler());

    }
}