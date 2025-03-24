package com.java110.hal.nettty;

import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.utils.*;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.dto.accessControl.AccessControlResultDto;
import com.java110.intf.accessControl.IAccessControlResultV1InnerServiceSMO;
import com.java110.intf.accessControl.IAccessControlV1InnerServiceSMO;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UdpHeartBeatSimpleHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private final static Logger LOGGER = LoggerFactory.getLogger(UdpHeartBeatSimpleHandler.class);


    public static final String MACHINE_TYPE_ACCESS_CONTROL = "ACCESS_CONTROL";

    private final static Map<String, AccessControlDto> accessControlCache = new ConcurrentHashMap<>();
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.debug("UDP Channel activated");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.debug("UDP Channel deactivated");
        // UDP 是无连接的，这里不需要像 TCP 那样移除特定的 channel
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
        InetSocketAddress sender = packet.sender();
        String ip = sender.toString();
        LOGGER.debug("Received UDP packet from: " + ip);

        try {
            ByteBuf byteBuf = packet.content();
            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(bytes);
            doChannelRead(ctx, bytes, sender);
        } catch (Exception e) {
            LOGGER.error("Error processing UDP packet from " + ip, e);
            throw e;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (ctx == null) {
            LOGGER.error("ChannelHandlerContext is null");
            return;
        }
        LOGGER.error("Exception occurred in UDP channel: {}", ctx.channel().id(), cause);
        // UDP 不需要关闭连接，但可以记录异常
    }

    private void doChannelRead(ChannelHandlerContext ctx, byte[] bytes, InetSocketAddress sender) {
        LOGGER.info("Received bytes={}", Arrays.toString(bytes));

        String machineCode = "";
        String machineType = "";

         if (DataHeader.isWiegand(bytes)) {
            machineCode = DataHeader.getWieGandMachineCode(bytes);
            machineType = MACHINE_TYPE_ACCESS_CONTROL;
        }

        if (StringUtil.isEmpty(machineCode)) {
            LOGGER.warn("No valid machine code found in packet from {}", sender);
            return;
        }

        LOGGER.info("Processing machineCode={} from {}", machineCode, sender);

        // UDP 不需要保存连接状态，只记录设备信息
        initMachineToCache(machineCode, sender);

         if (MACHINE_TYPE_ACCESS_CONTROL.equals(machineType)) {
            toNotifyAccessControl(machineCode, bytes);
        } else if (toNotifyMeter(machineCode, bytes)) {
            // 处理水电表
        }
    }



    private boolean toNotifyMeter(String machineCode, byte[] bytes) {
        return false;
    }

    private boolean toNotifyAccessControl(String machineCode, byte[] bytes) {
        AccessControlDto accessControlDto = getAccessControlDto(machineCode);
        if (accessControlDto == null) {
            return false;
        }

        IAccessControlResultV1InnerServiceSMO accessControlResultV1InnerServiceSMO =
                ApplicationContextFactory.getBean("accessControlResultV1InnerServiceSMOImpl",
                        IAccessControlResultV1InnerServiceSMO.class);
        if (accessControlResultV1InnerServiceSMO == null) {
            accessControlResultV1InnerServiceSMO = ApplicationContextFactory.getBean(
                    IAccessControlResultV1InnerServiceSMO.class.getName(), IAccessControlResultV1InnerServiceSMO.class);
        }

        String data = BytesUtil.bytesToHex(bytes);
        accessControlResultV1InnerServiceSMO.accessControlResult(
                new AccessControlResultDto(accessControlDto.getImplBean(), "wiegand/udp", data));
        return true;
    }

    private void initMachineToCache(String machineCode, InetSocketAddress sender) {
        UdpNettySocketHolder.put(machineCode, sender.getHostString(), sender);
        LOGGER.debug("Machine {} registered from {}", machineCode, sender);
    }

    // 辅助方法：获取或查询 AccessControlDto
    private AccessControlDto getAccessControlDto(String machineCode) {
        if (!accessControlCache.containsKey(machineCode)) {
            IAccessControlV1InnerServiceSMO accessControlV1InnerServiceSMOImpl =
                    ApplicationContextFactory.getBean("accessControlV1InnerServiceSMOImpl", IAccessControlV1InnerServiceSMO.class);
            if (accessControlV1InnerServiceSMOImpl == null) {
                accessControlV1InnerServiceSMOImpl = ApplicationContextFactory.getBean(
                        IAccessControlV1InnerServiceSMO.class.getName(), IAccessControlV1InnerServiceSMO.class);
            }
            AccessControlDto accessControlDto = new AccessControlDto();
            accessControlDto.setMachineCode(machineCode);
            List<AccessControlDto> accessControlDtos = accessControlV1InnerServiceSMOImpl.queryAccessControls(accessControlDto);
            if (ListUtil.isNull(accessControlDtos)) {
                return null;
            }
            accessControlCache.put(machineCode, accessControlDtos.get(0));
        }
        return accessControlCache.get(machineCode);
    }
}