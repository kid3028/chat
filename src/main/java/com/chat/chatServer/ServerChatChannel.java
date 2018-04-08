package com.chat.chatServer;

import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;


/**
 * 处理消息类
 */
public class ServerChatChannel extends ChannelInitializer<SocketChannel> {

    private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private WebSocketServerHandshaker handshaker;
    private String wsUrl;
    // websocket握手升级绑定页面
    String wsFactoryUrl = "";

    private static final File file;

    static {
        String path = "/home/qull/socket.html";
        file = new File(path);
    }

    public ServerChatChannel() {}

    public ServerChatChannel(String wsUrl){this.wsUrl = wsUrl;}

    /**
     * 断开连接
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel out = ctx.channel();
        channels.remove(out);
    }

    /**
     * 建立连接
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        channels.add(incoming);
    }

    /**
     * Channel 活跃
     * 当客户端主动链接服务端的链接后，这个通道就是活跃的了
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().localAddress().toString() + "通道已经激活");
    }

    /**
     * channel 不活跃
     * 当客户端主动断开服务端的链接后，这个通道就是不活跃的。
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().localAddress().toString() + "通道不活跃");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof FullHttpRequest) {
            handlerHttpRequest(ctx, (FullHttpRequest) msg);
        } else if(msg instanceof WebSocketFrame) {
            handlerWebsocketFrame(ctx, (WebSocketFrame)msg);
        }
    }

    private void handlerWebsocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        if(frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }

        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }

        if(frame instanceof TextWebSocketFrame) {
            String request = ((TextWebSocketFrame) frame).text();
            System.out.println("websocket=========>" + request);
            String[] array = request.split(",");
            UserInfoManager.addChannel(ctx.channel(), array[0]);
            UserInfo userInfo = UserInfoManager.getUserInfo(ctx.channel());
            if(array.length == 3) {
                String send = array[0];
                String friend = array[1];
                String message = array[2];
                UserInfoManager.broadCastMsg(friend, message, send);
            }
        }
    }

    private void handlerHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) throws IOException {
        if(request instanceof HttpRequest) {
//            HttpMethod method = request.getMethod();
//            if(wsUrl.equalsIgnoreCase(request.getUri())) {
//                System.out.println("request instanceof HttpRequest");
//                WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(wsFactoryUrl, null, false);
//                handshaker = wsFactory.newHandshaker(request);
//                if(handshaker == null) {
//                    WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
//                } else {
//                    handshaker.handshake(ctx.channel(), request);
//                }
//            }

            if(HttpHeaders.is100ContinueExpected(request)){
                send100Continue(ctx);
            }
            RandomAccessFile file = new RandomAccessFile("file", "r");
            HttpResponse response = new DefaultFullHttpResponse(request.getProtocolVersion(), HttpResponseStatus.OK);
            response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html; charset=UTF-8");
            boolean keepAlive = HttpHeaders.isKeepAlive(request);

            if(keepAlive) {
                response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, file.length());
                response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
            }

            ctx.write(response);

            if(ctx.pipeline().get(SslHandler.class) == null) {
                ctx.write(new DefaultFileRegion(file.getChannel(), 0 , file.length()));
            } else {
                ctx.write(new ChunkedNioFile(file.getChannel()));
            }


            ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            if(!keepAlive) {
                future.addListener(ChannelFutureListener.CLOSE);
            }

            file.close();
        }
    }

    private void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
        ctx.writeAndFlush(response);
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if(event.state().equals(IdleState.READER_IDLE)) {
                UserInfoManager.removeChannel(ctx.channel());
            }
        }
        ctx.fireUserEventTriggered(evt);

    }

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast("http-codec", new HttpServerCodec())
                .addLast("aggregator", new HttpObjectAggregator(64*1024))
                .addLast("http-chunked", new ChunkedWriteHandler())
                .addLast(new ServerChatChannel("/ws"));//自定义处理类
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("exception:" + cause.getMessage());
        ctx.close();
    }
}