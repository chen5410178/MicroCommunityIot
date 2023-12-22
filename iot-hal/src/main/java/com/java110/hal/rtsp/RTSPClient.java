package com.java110.hal.rtsp;

import com.java110.hal.rtsp.command.RtspCommand;
import com.java110.hal.rtsp.handler.RtspHandler;
import com.java110.hal.rtsp.holder.RtspHolder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;

public class RTSPClient {

    private static final String VERSION = " RTSP/1.0";
    private static final String RTSP_OK = "RTSP/1.0 200 OK";

    private String transport = "RTP/AVP/TCP;unicast;interleaved=0-1";

    private int seq = 2;


    private String url;

    private String address;

    private String ip;

    private int port;

    private String username;

    private String password;

    private String machineId;

    /** */
    /**
     * 连接通道
     */
    private Channel socketChannel;

    public RTSPClient() {
    }

    public RTSPClient(String url, String address, String username, String password,String machineId) {
        this.url = url;
        this.port = 554;
        this.ip = url;
        this.address = address;
        this.username = username;
        this.password = password;
        this.machineId = machineId;
        if(url.contains(":")){
            this.ip = url.split(":")[0];
            this.port = Integer.parseInt(url.split(":")[1]);
        }

        this.initClient();
    }

    public void initClient(){
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    //.handler(new LoggingHandler(LogLevel.INFO))
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        public void initChannel(Channel ch) throws Exception {
//                            ch.pipeline().addLast(new StringDecoder());
//                            ch.pipeline().addLast(new StringEncoder());
                            ch.pipeline() .addLast("decoder",new ByteArrayDecoder());
                            ch.pipeline() .addLast("encoder",new ByteArrayEncoder());
                            ch.pipeline().addLast(new RtspHandler(url,address,username,password,machineId));
                        }
                    });
            ChannelFuture future = bootstrap.connect(ip, port).sync();
            socketChannel = future.channel();
            RtspHolder.put(url,socketChannel);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
           // group.shutdownGracefully();
        }
    }

    public void startup() {

        // todo 下发

        RtspCommand.sendOptions(url,address);


    }


    public static void main(String[] args) {

        RTSPClient rtspClientA = new RTSPClient("192.168.31.7:554","rtsp://192.168.31.7:554","admin","wuxw2015","");

        rtspClientA.startup();
        System.out.println("启动一个客户端");
    }
}
