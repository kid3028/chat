package com.chat.ChatRoom;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;


public class WebChannelServerInitializer extends ChannelInitializer {
    @Override
    protected void initChannel(Channel channel) throws Exception {
//        SocketChannel socketChannel = (SocketChannel) channel;
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(new HttpServerCodec())
                .addLast(new HttpObjectAggregator(64*1024))
                .addLast(new ChunkedWriteHandler())
                .addLast(new HttpRequestHandler("/chat"));
    }

}