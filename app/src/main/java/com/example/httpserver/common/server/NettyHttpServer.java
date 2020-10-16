package com.example.httpserver.common.server;

import android.util.Log;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyHttpServer {

    private ChannelFuture channel;
    private ChannelFuture future;

    public NettyHttpServer() {

    }

    public void init() {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new HttpStaticFileServerInitializer())
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.AUTO_CLOSE, true);
        channel = bootstrap.bind(8080);
        Log.d("SERVER", "CREATE");
    }

    public void start() {
//        future = channel.syncUninterruptibly();
        Log.d("SERVER", "START");
    }

    public void stop() {
//        future.channel().close();
        Log.d("SERVER", "STOP");
    }

}

