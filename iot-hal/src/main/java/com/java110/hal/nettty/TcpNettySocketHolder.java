package com.java110.hal.nettty;

import com.java110.core.utils.BytesUtil;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TcpNettySocketHolder {
    private static Logger logger = LoggerFactory.getLogger(TcpNettySocketHolder.class);

    private static final Map<String, NioSocketChannel> MAP = new ConcurrentHashMap<>();

    public static void put(String machineCode,String ip,NioSocketChannel socketChannel){

        for (String key : MAP.keySet()) {
            if (key.startsWith(machineCode)) {
                MAP.remove(key);
            }
        }
        put(machineCode+"@@"+ip,socketChannel);
    }

    public static void put(String id, NioSocketChannel socketChannel) {
        MAP.put(id, socketChannel);
    }

    public static NioSocketChannel get(String id,String ip) {
        return MAP.get(id+"@@"+ip);
    }

    public static NioSocketChannel get(String id) {
        for (String key : MAP.keySet()) {
            if (key.startsWith(id+"@@")) {
                return MAP.get(key);
            }
        }
        return null;
    }

    public static NioSocketChannel getByMachineCode(String id) {
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


    public static boolean hasOnLine(String machineCode) {
        NioSocketChannel channel = get(machineCode);
        if (channel == null) {
            return false;
        }

        if(!channel.isActive()){
            return false;
        }
        return true;
    }
}