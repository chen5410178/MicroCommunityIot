package com.java110.hal.nettty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class HeartbeatInitializer extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel channel) throws Exception {
        ByteBuf delimiter = Unpooled.copiedBuffer("}".getBytes());

        channel.pipeline()

        .addLast("decoder",new ByteArrayDecoder())
        .addLast("encoder",new ByteArrayEncoder())
        //IdleStateHandler心跳机制,如果超时触发Handle中userEventTrigger()方法
        .addLast("idleStateHandler", new IdleStateHandler(15,0,0, TimeUnit.MINUTES))
        .addLast(new CustomDecoder())
        .addLast("serverChannelHandler",new HeartBeatSimpleHandle());
    }
}