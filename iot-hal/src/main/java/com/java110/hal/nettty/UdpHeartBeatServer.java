package com.java110.hal.nettty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;

@Component
public class UdpHeartBeatServer {

    private final static Logger LOGGER = LoggerFactory.getLogger(UdpHeartBeatServer.class);

    private EventLoopGroup work = new NioEventLoopGroup();

    @Value("${netty.server.port}")
    private int nettyPort;

    /**
     * 启动 Netty
     *
     * @return
     * @throws InterruptedException
     */
    @PostConstruct
    public void start() throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap()
                .group(work) // UDP 只需要一个 EventLoopGroup
                .channel(NioDatagramChannel.class) // 使用 NioDatagramChannel 来处理 UDP
                .localAddress(new InetSocketAddress(nettyPort))
                .handler(new UdpChannelInitializer()); // 使用相同的初始化器或自定义的 UDP 初始化器

        //绑定并开始接受传入的连接。
        ChannelFuture future = bootstrap.bind().sync();
        if (future.isSuccess()) {
            LOGGER.info("启动 Netty UDP 服务成功, 端口: {}", nettyPort);
        }
        UdpNettySocketHolder.setUdpChannel((NioDatagramChannel) future.channel());
    }

    /**
     * 销毁
     */
    @PreDestroy
    public void destroy() {
        work.shutdownGracefully().syncUninterruptibly();
        LOGGER.info("关闭 Netty 成功");
    }
}
