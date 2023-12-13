package com.java110.hal.nettty;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.utils.BytesUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

public class NettySocketHolder {
    private static Logger logger = LoggerFactory.getLogger(NettySocketHolder.class);

    private static final Map<String, NioSocketChannel> MAP = new ConcurrentHashMap<>();

    public static void put(String id, NioSocketChannel socketChannel) {
        MAP.put(id, socketChannel);
    }

    public static NioSocketChannel get(String id) {
        return MAP.get(id);
    }

    public static Map<String, NioSocketChannel> getMAP() {
        return MAP;
    }

    public static void remove(NioSocketChannel nioSocketChannel) {
        MAP.entrySet().stream().filter(entry -> entry.getValue() == nioSocketChannel).forEach(entry -> MAP.remove(entry.getKey()));
    }

    public static void sendData(String machineCode, byte[] data) {
        NioSocketChannel channel = get(machineCode);
        if (channel == null) {
            throw new IllegalArgumentException("设备未连接");
        }

        if(!channel.isActive()){
            throw new IllegalArgumentException("设备未连接");
        }

        // todo 发送数据
        logger.debug("向 {} 设备发送数据，{}", machineCode, data);
        logger.debug("向 {} 设备发送数据Hex，{}", machineCode, BytesUtil.bytesToHex(data));

        channel.writeAndFlush(data);
    }


}