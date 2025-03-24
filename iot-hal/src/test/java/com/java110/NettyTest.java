package com.java110;

import com.java110.hal.nettty.TcpChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.net.InetSocketAddress;

/**
 * Unit test for simple App.
 */
public class NettyTest
    extends TestCase
{

    private EventLoopGroup boss = new NioEventLoopGroup();
    private EventLoopGroup work = new NioEventLoopGroup();
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public NettyTest(String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( NettyTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(boss, work)
                .channel(NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(20012))
                .option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(1 * 1024 * 1024))
                //保持长连接
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(1, 1024 * 1024 * 8))
                .childHandler(new TcpChannelInitializer());
        //绑定并开始接受传入的连接。
        ChannelFuture future = null;
        try {
            future = bootstrap.bind().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (future.isSuccess()) {
            System.out.println("启动 Netty 成功");
        }

        while (true);

    }
}
