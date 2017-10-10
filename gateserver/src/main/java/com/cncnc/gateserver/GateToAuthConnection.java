package com.cncnc.gateserver;

import com.cncnc.code.PacketDecoder;
import com.cncnc.code.PacketEncoder;
import com.cncnc.gateserver.handler.GateToAuthConnectionHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GateToAuthConnection {

    private static Logger logger = LoggerFactory.getLogger(GateToAuthConnection.class);


    public static void startGateToAuthConnection(String host, Integer port){
        EventLoopGroup work = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(work)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast("MessageDecoder", new PacketDecoder());
                        pipeline.addLast("MessageEncoder", new PacketEncoder());
                        pipeline.addLast("GateAuthConnectionHandler", new GateToAuthConnectionHandler());
                    }
                });

        bootstrap.connect(host, port)
                .addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        if (channelFuture.isSuccess()){
                            logger.info("gateserver connect authserver success...");
                        } else {
                            logger.error("gateserver connect authserver failed...");
                        }
                    }
                });

    }

}
