package com.java110.hal.nettty;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.chargeMachine.ChargeMachineByteDataDto;
import com.java110.dto.chargeMachine.ChargeMachineDto;
import com.java110.intf.charge.IChargeMachineV1InnerServiceSMO;
import com.java110.intf.charge.INotifyChargeV1InnerServiceSMO;
import com.java110.intf.system.IHardwareManufacturerV1InnerServiceSMO;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;

public class HeartBeatSimpleHandle extends ChannelInboundHandlerAdapter {

    private final static Logger LOGGER = LoggerFactory.getLogger(HeartBeatSimpleHandle.class);

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
        NettySocketHolder.remove((NioSocketChannel) ctx.channel());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
//        ByteBuf heartbeat = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(new Java110CarProtocol("1", "http://www.homecommunity.cn").toString(), CharsetUtil.UTF_8));
//        if (evt instanceof IdleStateEvent) {
//            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
//            if (idleStateEvent.state() == IdleState.READER_IDLE) {
//                LOGGER.info("已经5秒没有收到信息！");
//                //向客户端发送消息
//                ctx.writeAndFlush(heartbeat).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
//            }
//        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            doChannelRead(ctx, msg);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("设备数据处理异常", e);
        }


    }

    private void doChannelRead(ChannelHandlerContext ctx, Object msg) {
        LOGGER.info("收到ByteBuf={}", msg);
        byte[] bytes = (byte[]) msg;
        LOGGER.info("收到字节={}", Arrays.toString(bytes));

        String machineCode = "";
        if (DataHeader.isLvChongChong(bytes)) {
            machineCode = DataHeader.getLvCCMachineCode(bytes);
        }

        if (StringUtil.isEmpty(machineCode)) {
            return;
        }

        //todo 保存设备和连接的关系
        initMachineToCache(ctx, machineCode, false);

        //todo 通知充电桩数据给适配器
        if (toNotifyCharge(machineCode, bytes)) {
            return;
        }
        //todo 通知 水电表
        if (toNotifyMeter(machineCode, bytes)) {
            return;
        }
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

        IChargeMachineV1InnerServiceSMO chargeMachineV1InnerServiceSMO = ApplicationContextFactory.getBean("chargeMachineV1InnerServiceSMOImpl", IChargeMachineV1InnerServiceSMO.class);

        if (chargeMachineV1InnerServiceSMO == null) {
            chargeMachineV1InnerServiceSMO = ApplicationContextFactory.getBean(IChargeMachineV1InnerServiceSMO.class.getName(), IChargeMachineV1InnerServiceSMO.class);
        }

        ChargeMachineDto chargeMachineDto = new ChargeMachineDto();
        chargeMachineDto.setMachineCode(machineCode);
        List<ChargeMachineDto> machineDtos = chargeMachineV1InnerServiceSMO.queryChargeMachines(chargeMachineDto);
        if (ListUtil.isNull(machineDtos)) {
            return false;
        }

        INotifyChargeV1InnerServiceSMO notifyChargeV1InnerServiceSMOImpl = ApplicationContextFactory.getBean("notifyChargeV1InnerServiceSMOImpl", INotifyChargeV1InnerServiceSMO.class);

        if (notifyChargeV1InnerServiceSMOImpl == null) {
            notifyChargeV1InnerServiceSMOImpl = ApplicationContextFactory.getBean(INotifyChargeV1InnerServiceSMO.class.getName(), INotifyChargeV1InnerServiceSMO.class);
        }

        notifyChargeV1InnerServiceSMOImpl.machineResult(new ChargeMachineByteDataDto(machineDtos.get(0), bytes));


        return true;
    }

    private String analyseMachine(String content) {
        JSONObject paramIn = JSONObject.parseObject(content);
        String parkId = paramIn.getString("parkid");
        return parkId;
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        String servcie = "whitelist_sync";

        System.out.printf("query_price,whitelist_sync".contains(servcie) + "");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
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
        Object channel = NettySocketHolder.get(machineCode);
        if (channel == null || force) {
            NettySocketHolder.put(machineCode, (NioSocketChannel) ctx.channel());
        }
    }

}
