package com.cncnc.gateserver.handler;

import com.cncnc.gateserver.ClientMessage;
import com.cncnc.gateserver.utils.ClientConnection;
import com.cncnc.gateserver.utils.ClientConnectionMap;
import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;


public class GateServerHandler extends SimpleChannelInboundHandler<Message> {

    /**
     * Channel处于活动状态（已经连接到它的远程节点）。它现在可以接收和发送数据了
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 保存客户端的连接   方便以后去做处理
        ClientConnectionMap.addClientConnetion(ctx);
    }

    /**
     * 作用：当从Channel中读取数据的时候调用
     *
     * ChannelRead 和 ChannelRead0的区别是：
     * 1、ChannelRead 是ChannelInboundHandlerAdapter这个类下的方法，每次调用之后需要手动去释放资源
     * （通过调用ReferenceCountUtil.release(msg)去释放资源）
     * 2、ChannelRead0 是SimpleChannelInboundHandler这个类里面的方法，每次调用之后不需要去手动释放资源，
     * 内部会自动去释放资源（也是通过调用ReferenceCountUtil.release(msg)去释放资源）
     *
     * @param ctx
     * @param message
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {
        // 获取客户端的连接
        ClientConnection conn = ClientConnectionMap.getClientConnection(ctx);
        ClientMessage.processTransferHandler(message, conn);

        // 通知客户端已经收到消息
        String responseMessage = "Hello, I have received the message!!";
        ByteBuf byteBuf = Unpooled.copiedBuffer(responseMessage, CharsetUtil.UTF_8);
        ctx.writeAndFlush(byteBuf);
    }

    /**
     * Channel没有连接到远程节点
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

    }
}
