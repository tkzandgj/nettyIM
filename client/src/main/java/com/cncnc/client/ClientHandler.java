package com.cncnc.client;

import com.cncnc.protobuf.chat.Chat;
import com.cncnc.protobuf.login.Auth;
import com.cncnc.utils.Utils;
import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;

public class ClientHandler extends SimpleChannelInboundHandler<Message>{

    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    private static ChannelHandlerContext _gateClientConnection;
    String _userId = "";
    boolean _verify = false;
    private static int count = 0;

    public static AtomicLong increased = new AtomicLong(1);

    /**
     * Channel处于活动状态（已经连接到它的远程节点）。它现在可以接收和发送消息了
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        _gateClientConnection = ctx;
        String passwd = "123";
        _userId = Long.toString(increased.getAndIncrement());

        sendRegister(ctx, _userId, passwd);
        sendLogin(ctx, _userId, passwd);
    }


    /**
     * 发送注册信息
     * @param ctx
     * @param userId
     * @param passwd
     */
    public void sendRegister(ChannelHandlerContext ctx, String userId, String passwd){
        Auth.CRegister.Builder cb = Auth.CRegister.newBuilder();
        cb.setUserid(userId);
        cb.setPasswd(passwd);

        ByteBuf byteBuf = Utils.pack2Client(cb.build());
        ctx.writeAndFlush(byteBuf);
        logger.info("send CRegister userid : {}", _userId);
    }

    /**
     * 发送登录信息
     * @param ctx
     * @param userId
     * @param passwd
     */
    public void sendLogin(ChannelHandlerContext ctx, String userId, String passwd){
        Auth.CLogin.Builder cb = Auth.CLogin.newBuilder();
        cb.setUserid(userId);
        cb.setPasswd(passwd);
        cb.setPlatform("android");
        cb.setAppVersion("1.0.0");

        ByteBuf byteBuf = Utils.pack2Client(cb.build());
        ctx.writeAndFlush(byteBuf);

        logger.info("send CLogin userid:{}", _userId);
    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

    }


    /**
     * 读取服务端发送来的消息
     * @param channelHandlerContext
     * @param message
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message message) throws Exception {
        logger.info("received server message : {}", message.getClass());
        if (message instanceof Auth.SResponse){
            Auth.SResponse response = (Auth.SResponse) message;
            int code = response.getCode();
            String desc = response.getDesc();

            switch (code){
                // 登录成功
                case Common.LOGIN_SUCCESS:
                    logger.info("Login success, description is : {}", desc);
                    _verify = true;
                    break;
                // 登录失败或者密码错误
                case Common.LOGIN_ERROR:
                    logger.info("Account or Password Error, description is : {}", desc);
                    break;
                // 登录账号不存在
                case Common.ACCOUNT_NOT_EXIST:
                    logger.info("Account is not exist, description is : {}", desc);
                    break;
                // 注册成功
                case Common.REGISTER_OK:
                    logger.info("Register success, description is : {}", desc);
                    break;
                // 账号已经被注册
                case Common.ACCOUNT_DUMPLICATED:
                    logger.info("Account is be registered, description is : {}", desc);
                    break;
                // 聊天消息发送成功
                case Common.MSG_SEND_RESPONSE:
                    logger.info("Chat message send success, description is : {}", desc);
                    break;
                default:
                    logger.info("Unknow code : {}", code);
            }
        } else if (message instanceof Chat.SPrivateChat) {
            logger.info("{} received chat message : {}, total message number : {}", _userId,
                    ((Chat.SPrivateChat)message).getContent(), ++count);
        }

        // 防止两方都阻塞在输入上，输入之间间隔1s在输入
        if (_verify){
            sendMessage();
            Thread.sleep(1000);
        }
    }


    public void sendMessage(){
        String content = "Hello, I am Tom!!";
        Chat.CPrivateChat.Builder cp = Chat.CPrivateChat.newBuilder();
        cp.setContent(content);
        cp.setSelf(_userId);  //自己给自己发送消息
        cp.setDest(_userId);

        ByteBuf byteBuf = Utils.pack2Client(cp.build());
        _gateClientConnection.writeAndFlush(byteBuf);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
