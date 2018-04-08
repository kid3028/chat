package com.chat.chatServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ChatServer {
    private int port;

    public ChatServer() {}

    public ChatServer(int port) {this.port = port;}

    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ServerChatChannel())
                .option(ChannelOption.SO_BACKLOG, 128)
                .option(ChannelOption.SO_KEEPALIVE, true);

        System.out.println("server start ...");

        try {
            ChannelFuture future = bootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        } finally {
            workGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();

            System.out.println("server stop...");
        }


    }


    public static void main(String[] args) {
        int port;
        if(args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8080;
        }

        new ChatServer(port).start();
    }
}


