package com.cncnc.client;


import com.cncnc.code.PacketDecoder;
import com.cncnc.code.PacketEncoder;
import com.cncnc.utils.ParseRegistryMap;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {

    private static Logger logger = LoggerFactory.getLogger(Client.class);

    private static final String HOST = "127.0.0.1";
    private static final Integer PORT = 9090;
    private static final Integer CLIENT_NUM = 10;


    public static void main(String[] args){
        start();
    }


    public static void start(){
        EventLoopGroup work = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(work)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline p = socketChannel.pipeline();
                        p.addLast("MessageDecoder", new PacketDecoder());
                        p.addLast("MessageEncoder", new PacketEncoder());
                        p.addLast(new ClientHandler());

                    }
                });

        for (int i = 1; i <= CLIENT_NUM; i++){
            connection(bootstrap, i);
        }
    }

    private static void connection(Bootstrap bootstrap, final int index){
        bootstrap.connect(HOST, PORT)
                .addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture)
                            throws Exception {
                        if (channelFuture.isSuccess()){
                            // init registry
                            ParseRegistryMap.initRegistry();
                            logger.info("Client [{}] connected gateServer success...", index);
                        } else {
                            logger.info("Client [{}] connected gateServer failed...", index);
                        }
                    }
                });
    }
}
