package com.java110.hal.nettty;

import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.utils.*;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.dto.accessControl.AccessControlResultDto;
import com.java110.dto.chargeMachine.ChargeMachineByteDataDto;
import com.java110.dto.chargeMachine.ChargeMachineDto;
import com.java110.dto.chargeMachine.LicenseMachineByteDataDto;
import com.java110.dto.workLicenseMachine.WorkLicenseMachineDto;
import com.java110.intf.accessControl.IAccessControlResultV1InnerServiceSMO;
import com.java110.intf.accessControl.IAccessControlV1InnerServiceSMO;
import com.java110.intf.charge.IChargeMachineV1InnerServiceSMO;
import com.java110.intf.charge.INotifyChargeV1InnerServiceSMO;
import com.java110.intf.charge.INotifyWorkLicenseV1InnerServiceSMO;
import com.java110.intf.charge.IWorkLicenseMachineV1InnerServiceSMO;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TcpHeartBeatSimpleHandler extends ChannelInboundHandlerAdapter {

    private final static Logger LOGGER = LoggerFactory.getLogger(TcpHeartBeatSimpleHandler.class);


    public static final String MACHINE_TYPE_CHARGE = "CHARGE"; // todo 充电桩
    public static final String MACHINE_TYPE_METER = "METER"; // todo 水电表
    public static final String MACHINE_TYPE_WORK_LICENSE = "WORK_LICENSE"; // todo 员工牌
    public static final String MACHINE_TYPE_ACCESS_CONTROL = "ACCESS_CONTROL"; // todo 门禁

    private final static Map<String, ChargeMachineDto> chargeCache = new ConcurrentHashMap<>();
    private final static Map<String, AccessControlDto> accessControlCache = new ConcurrentHashMap<>();
    private final static Map<String, WorkLicenseMachineDto> workLicenseCache = new ConcurrentHashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
        String ip = address.toString();

        LOGGER.debug("channelActive -->  RamoteAddress : " + ip + " connected ");

    }


    /**
     * 取消绑定
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
        String ip = address.toString();
        LOGGER.debug("channelInactive -->  RamoteAddress : " + ip + " channelInactive ");

        TcpNettySocketHolder.remove((NioSocketChannel) ctx.channel());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
        String ip = address.toString();
        LOGGER.debug("channelActive -->  RamoteAddress : " + ip + " userEventTriggered ");

        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
        String ip = address.toString();
        LOGGER.debug("channelActive -->  RamoteAddress : " + ip + " channelRead ");

        try {
            doChannelRead(ctx, msg);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("设备数据处理异常", e);
        }


    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (ctx == null) {
            LOGGER.error("ChannelHandlerContext is null");
            return;
        }
        if (cause instanceof NullPointerException) {
            LOGGER.error("NullPointerException occurred in channel: {}", ctx.channel().id(), cause);
        } else {
            LOGGER.error("Exception occurred in channel: {}", ctx.channel().id(), cause);
        }
        ctx.close(); // 关闭发生异常的连接
    }

    private void doChannelRead(ChannelHandlerContext ctx, Object msg) {
        LOGGER.info("收到ByteBuf={}", msg);
        byte[] bytes = (byte[]) msg;
        LOGGER.info("收到字节={}", Arrays.toString(bytes));

        String machineCode = "";
        String machineType = "";

        if (DataHeader.isSbCharge(bytes)) {
            machineCode = DataHeader.getSbMachineCode(bytes);
            machineType = MACHINE_TYPE_CHARGE;
        } else if (DataHeader.isYunKuaiChongCharge(bytes)) {
            machineCode = DataHeader.getYKMachineCode(bytes);
            machineType = MACHINE_TYPE_CHARGE;
        } else if (DataHeader.isLvChongChong(bytes)) {
            machineCode = DataHeader.getLvCCMachineCode(bytes);
            machineType = MACHINE_TYPE_CHARGE;
        } else if (DataHeader.isWorkLicense(bytes)) {
            bytes = DataHeader.clearWorkLicense(bytes);
            machineCode = DataHeader.getWorkLicenseMachineCode(bytes);
            machineType = MACHINE_TYPE_WORK_LICENSE;
        } else if (DataHeader.isWiegand(bytes)) {
            machineCode = DataHeader.getWieGandMachineCode(bytes);
            machineType = MACHINE_TYPE_ACCESS_CONTROL;
        }
        if (StringUtil.isEmpty(machineCode)) {
            return;
        }
        System.out.println("---------------------111111111111111------------------------" + machineCode);
        //todo 保存设备和连接的关系
        initMachineToCache(ctx, machineCode, false);

        //todo 通知充电桩数据给适配器
        if (MACHINE_TYPE_CHARGE.equals(machineType)) {
            toNotifyCharge(machineCode, bytes);
            return;
        }
        //todo 通知员工牌数据给适配器
        if (MACHINE_TYPE_WORK_LICENSE.equals(machineType)) {
            toNotifyWorkLicense(machineCode, bytes);
            return;
        }
        //todo 通知门禁适配器
        if (MACHINE_TYPE_ACCESS_CONTROL.equals(machineType)) {
            toNotifyAccessControl(machineCode, bytes);
            return;
        }
        //todo 通知 水电表
        if (toNotifyMeter(machineCode, bytes)) {
            return;
        }
    }



    private void toNotifyWorkLicense(String machineCode, byte[] bytes) {
        WorkLicenseMachineDto workLicenseMachineDto = null;
        if (!workLicenseCache.containsKey(machineCode)) {
            IWorkLicenseMachineV1InnerServiceSMO workLicenseMachineV1InnerServiceSMOImpl = ApplicationContextFactory.getBean("workLicenseMachineV1InnerServiceSMOImpl", IWorkLicenseMachineV1InnerServiceSMO.class);
            if (workLicenseMachineV1InnerServiceSMOImpl == null) {
                workLicenseMachineV1InnerServiceSMOImpl = ApplicationContextFactory.getBean(IWorkLicenseMachineV1InnerServiceSMO.class.getName(), IWorkLicenseMachineV1InnerServiceSMO.class);
            }
            workLicenseMachineDto = new WorkLicenseMachineDto();
            workLicenseMachineDto.setMachineCode(machineCode);
            List<WorkLicenseMachineDto> machineDtos = workLicenseMachineV1InnerServiceSMOImpl.queryWorkLicenseMachines(workLicenseMachineDto);
            if (ListUtil.isNull(machineDtos)) {
                return;
            }
            workLicenseCache.put(machineCode, machineDtos.get(0));
        }

        workLicenseMachineDto = workLicenseCache.get(machineCode);

        INotifyWorkLicenseV1InnerServiceSMO notifyWorkLicenseV1InnerServiceSMOImpl = ApplicationContextFactory.getBean("notifyWorkLicenseV1InnerServiceSMOImpl", INotifyWorkLicenseV1InnerServiceSMO.class);

        if (notifyWorkLicenseV1InnerServiceSMOImpl == null) {
            notifyWorkLicenseV1InnerServiceSMOImpl = ApplicationContextFactory.getBean(INotifyWorkLicenseV1InnerServiceSMO.class.getName(), INotifyWorkLicenseV1InnerServiceSMO.class);
        }

        notifyWorkLicenseV1InnerServiceSMOImpl.licenseResult(new LicenseMachineByteDataDto(workLicenseMachineDto, bytes));


    }

    private boolean toNotifyMeter(String machineCode, byte[] bytes) {
        return false;
    }


    /**
     * 通知 充电桩
     *
     * @param machineCode
     * @param bytes
     * @return
     */
    private boolean toNotifyCharge(String machineCode, byte[] bytes) {


        ChargeMachineDto chargeMachineDto = null;

        if (!chargeCache.containsKey(machineCode)) {
            IChargeMachineV1InnerServiceSMO chargeMachineV1InnerServiceSMO = ApplicationContextFactory.getBean("chargeMachineV1InnerServiceSMOImpl", IChargeMachineV1InnerServiceSMO.class);
            if (chargeMachineV1InnerServiceSMO == null) {
                chargeMachineV1InnerServiceSMO = ApplicationContextFactory.getBean(IChargeMachineV1InnerServiceSMO.class.getName(), IChargeMachineV1InnerServiceSMO.class);
            }
            chargeMachineDto = new ChargeMachineDto();
            chargeMachineDto.setMachineCode(machineCode);
            List<ChargeMachineDto> machineDtos = chargeMachineV1InnerServiceSMO.queryChargeMachines(chargeMachineDto);
            if (ListUtil.isNull(machineDtos)) {
                return false;
            }
            chargeCache.put(machineCode, machineDtos.get(0));
        }

        chargeMachineDto = chargeCache.get(machineCode);


        INotifyChargeV1InnerServiceSMO notifyChargeV1InnerServiceSMOImpl = ApplicationContextFactory.getBean("notifyChargeV1InnerServiceSMOImpl", INotifyChargeV1InnerServiceSMO.class);

        if (notifyChargeV1InnerServiceSMOImpl == null) {
            notifyChargeV1InnerServiceSMOImpl = ApplicationContextFactory.getBean(INotifyChargeV1InnerServiceSMO.class.getName(), INotifyChargeV1InnerServiceSMO.class);
        }
        // 这里深度拷贝下，不然后面 改了对象内容导致 出错
        chargeMachineDto = BeanConvertUtil.covertBean(chargeMachineDto, ChargeMachineDto.class);
        notifyChargeV1InnerServiceSMOImpl.machineResult(new ChargeMachineByteDataDto(chargeMachineDto, bytes));


        return true;
    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
        String ip = address.toString();
        LOGGER.debug("channelActive -->  RamoteAddress : " + ip + " channelReadComplete ");

        ctx.flush();
    }


    /**
     * 保存 设备和连接的关系
     *
     * @param ctx
     * @param machineCode
     * @param force
     */
    private void initMachineToCache(ChannelHandlerContext ctx, String machineCode, boolean force) {
        //保存客户端与 Channel 之间的关系
        InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
        String ip = address.toString();
        NioSocketChannel channel = TcpNettySocketHolder.get(machineCode, ip);
        if (channel == null || force) {
            TcpNettySocketHolder.put(machineCode, ip, (NioSocketChannel) ctx.channel());
        }

        if (channel != null && channel.isShutdown()) {
            TcpNettySocketHolder.put(machineCode, ip, (NioSocketChannel) ctx.channel());
        }
    }

    private boolean toNotifyAccessControl(String machineCode, byte[] bytes) {
        AccessControlDto accessControlDto = null;

        if (!accessControlCache.containsKey(machineCode)) {
            IAccessControlV1InnerServiceSMO accessControlV1InnerServiceSMOImpl
                    = ApplicationContextFactory.getBean("accessControlV1InnerServiceSMOImpl", IAccessControlV1InnerServiceSMO.class);
            if (accessControlV1InnerServiceSMOImpl == null) {
                accessControlV1InnerServiceSMOImpl = ApplicationContextFactory.getBean(IAccessControlV1InnerServiceSMO.class.getName(), IAccessControlV1InnerServiceSMO.class);
            }
            accessControlDto = new AccessControlDto();
            accessControlDto.setMachineCode(machineCode);
            List<AccessControlDto> accessControlDtos = accessControlV1InnerServiceSMOImpl.queryAccessControls(accessControlDto);
            if (ListUtil.isNull(accessControlDtos)) {
                return false;
            }
            accessControlCache.put(machineCode, accessControlDtos.get(0));
        }

        accessControlDto = accessControlCache.get(machineCode);


        IAccessControlResultV1InnerServiceSMO accessControlResultV1InnerServiceSMO =
                ApplicationContextFactory.getBean("accessControlResultV1InnerServiceSMOImpl",
                        IAccessControlResultV1InnerServiceSMO.class);
        if (accessControlResultV1InnerServiceSMO == null) {
            accessControlResultV1InnerServiceSMO = ApplicationContextFactory.getBean(IAccessControlResultV1InnerServiceSMO.class.getName(),
                    IAccessControlResultV1InnerServiceSMO.class);
        }

        String data = BytesUtil.bytesToHex(bytes);

        accessControlResultV1InnerServiceSMO.accessControlResult(new AccessControlResultDto("102025032420940003", "wiegand/tcp", data));

        return true;
    }


}
