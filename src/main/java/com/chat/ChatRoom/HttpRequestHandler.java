package com.chat.ChatRoom;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;

import java.io.File;
import java.io.RandomAccessFile;

public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final String chatUri;
    private static File indexFile;

    static {
//        try {
//            URL location = HttpRequestHandler.class.getProtectionDomain().getCodeSource().getLocation();
//            String temp = location.toURI() + "chat.html";
//            System.out.println(temp);
//            String path = temp.substring(temp.indexOf(":") + 1);
            String path = "/home/qull/chat.html";
            System.out.println(path);
            indexFile = new File(path);
//        } catch (URISyntaxException e) {
//            System.out.println(e.getMessage());
//        }
    }

    public HttpRequestHandler(String chatUri) {
        this.chatUri = chatUri;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        System.out.println(request.getUri());
        if(chatUri.equalsIgnoreCase(request.getUri())) {
            System.out.println("websocket eequest....");
            ctx.fireChannelRead(request.retain());
        } else {
            System.out.println("http request...");
            if(HttpHeaders.is100ContinueExpected(request)) {
                FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
                ctx.writeAndFlush(response);
            }
//            RandomAccessFile file = new RandomAccessFile(indexFile, "r");
//            HttpResponse response = new DefaultFullHttpResponse(request.getProtocolVersion(), HttpResponseStatus.OK);
//            response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html;charset=utf-8");
////            ctx.write(response);
////            ctx.write(new ChunkedNioFile(file.getChannel()));
////            ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);

//            file.close();




            RandomAccessFile file = new RandomAccessFile(indexFile, "r");//4

            HttpResponse response = new DefaultHttpResponse(request.getProtocolVersion(), HttpResponseStatus.OK);
            response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html; charset=UTF-8");

            boolean keepAlive = HttpHeaders.isKeepAlive(request);

            if (keepAlive) {                                        //5
                response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, file.length());
                response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
            }
            ctx.write(response);                    //6

            if (ctx.pipeline().get(SslHandler.class) == null) {     //7
                ctx.write(new DefaultFileRegion(file.getChannel(), 0, file.length()));
            } else {
                ctx.write(new ChunkedNioFile(file.getChannel()));
            }
            ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);           //8
            if (!keepAlive) {
                future.addListener(ChannelFutureListener.CLOSE);        //9
            }

            file.close();
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {

    }
}