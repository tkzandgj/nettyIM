package com.cncnc.gateserver;

import com.cncnc.code.PacketDecoder;
import com.cncnc.code.PacketEncoder;
import com.cncnc.gateserver.handler.GateToLogicConnectionHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * gate作为客户端，去连接logic服务端
 */
public class GateToLogicConnection {

    private static final Logger logger = LoggerFactory.getLogger(GateToLogicConnection.class);

    public static void startGateToLogicConnection(String host, Integer port){
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
                        pipeline.addLast(new GateToLogicConnectionHandler());
                    }
                });

        bootstrap.connect(host, port)
                .addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        if (channelFuture.isSuccess()){
                            logger.info("gateserver connect logicserver success...");
                        } else {
                            logger.error("gateserver connect logicserver failed...");
                        }
                    }
                });
    }
}
