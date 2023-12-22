package com.java110.hal.rtsp.holder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RtspHolder {

    private static final Map<String, Channel> MAP = new ConcurrentHashMap<>();


    public static void put(String url, Channel socketChannel){

        for (String key : MAP.keySet()) {
            if (key.startsWith(url)) {
                MAP.remove(key);
            }
        }
        MAP.put(url,socketChannel);
    }


    public static Channel get(String url) {
        for (String key : MAP.keySet()) {
            if (key.equals(url)) {
                return MAP.get(key);
            }
        }
        return null;
    }

    /**
     * 发送指令
     * @param url
     * @param bytes
     */
    public static void send(String url, byte[] bytes) {

        Channel channel = get(url);

        if(channel == null || !channel.isActive()){
            throw new IllegalArgumentException("连接断开,url="+url);
        }

        ByteBuf byteBuf = UnpooledByteBufAllocator.DEFAULT.buffer();
        byteBuf.writeBytes(bytes);

        channel.writeAndFlush(byteBuf);
        //channel.flush();
    }
}
