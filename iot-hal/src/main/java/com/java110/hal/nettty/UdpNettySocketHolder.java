package com.java110.hal.nettty;

import com.java110.core.utils.BytesUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UdpNettySocketHolder {
    private static Logger logger = LoggerFactory.getLogger(UdpNettySocketHolder.class);

    // 存储 machineCode 到 InetSocketAddress 的映射
    private static final Map<String, InetSocketAddress> ADDRESS_MAP = new ConcurrentHashMap<>();

    // 保存全局的 UDP Channel（假设服务器只有一个 UDP Channel）
    private static NioDatagramChannel udpChannel;

    // 设置全局 UDP Channel
    public static void setUdpChannel(NioDatagramChannel channel) {
        udpChannel = channel;
    }

    // 存储设备地址
    public static void put(String machineCode, String ip, InetSocketAddress address) {
        for (String key : ADDRESS_MAP.keySet()) {
            if (key.startsWith(machineCode)) {
                ADDRESS_MAP.remove(key);
            }
        }
        ADDRESS_MAP.put(machineCode + "@@" + ip, address);
    }

    // 获取设备的地址
    public static InetSocketAddress getAddress(String machineCode, String ip) {
        return ADDRESS_MAP.get(machineCode + "@@" + ip);
    }

    // 根据 machineCode 获取地址（忽略 IP 的情况）
    public static InetSocketAddress getAddress(String machineCode) {
        for (String key : ADDRESS_MAP.keySet()) {
            if (key.startsWith(machineCode + "@@")) {
                return ADDRESS_MAP.get(key);
            }
        }
        return null;
    }

    public static Map<String, InetSocketAddress> getAddressMap() {
        return ADDRESS_MAP;
    }

    // 移除设备记录
    public static void remove(String machineCode, String ip) {
        ADDRESS_MAP.remove(machineCode + "@@" + ip);
    }

    // 发送数据到指定设备
    public static void sendData(String machineCode, byte[] data) {
        if (udpChannel == null || !udpChannel.isActive()) {
            throw new IllegalStateException("UDP Channel 未初始化或未激活");
        }

        InetSocketAddress targetAddress = getAddress(machineCode);
        if (targetAddress == null) {
            throw new IllegalArgumentException("设备 " + machineCode + " 未注册地址");
        }

        logger.debug("向 {} 设备发送数据，{}", machineCode, data);
        logger.debug("向 {} 设备发送数据Hex，{}", machineCode, BytesUtil.bytesToHex(data));

        DatagramPacket packet = new DatagramPacket(
                Unpooled.copiedBuffer(data),
                targetAddress
        );
        udpChannel.writeAndFlush(packet);
    }

    // 检查设备是否“在线”（仅检查是否注册了地址）
    public static boolean hasOnLine(String machineCode) {
        InetSocketAddress address = getAddress(machineCode);
        return address != null;
    }
}