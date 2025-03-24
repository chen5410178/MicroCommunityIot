package com.java110.hal.smo.impl;

import com.java110.bean.ResultVo;
import com.java110.core.factory.LoggerFactory;
import com.java110.dto.data.NettyReplyDataDto;
import com.java110.hal.nettty.TcpNettySocketHolder;
import com.java110.hal.nettty.UdpNettySocketHolder;
import com.java110.intf.hal.INotifyNettyDataV1InnerServiceSMO;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户服务信息管理业务信息实现
 * Created by wuxw on 2017/4/5.
 */
@RestController
public class NotifyNettyDataV1InnerServiceSMOImpl implements INotifyNettyDataV1InnerServiceSMO {
    protected final static Logger logger = LoggerFactory.getLogger(NotifyNettyDataV1InnerServiceSMOImpl.class);


    @Override
    public ResultVo sendData(@RequestBody NettyReplyDataDto nettyReplyDataDto) {

        TcpNettySocketHolder.sendData(nettyReplyDataDto.getMachineCode(), nettyReplyDataDto.getData());
        return new ResultVo(ResultVo.CODE_OK, ResultVo.MSG_OK);
    }

    @Override
    public boolean hasOnLine(@RequestBody String machineCode) {
        return TcpNettySocketHolder.hasOnLine(machineCode);
    }

    @Override
    public ResultVo sendUdpData(@RequestBody NettyReplyDataDto nettyReplyDataDto) {

        UdpNettySocketHolder.sendData(nettyReplyDataDto.getMachineCode(), nettyReplyDataDto.getData());
        return new ResultVo(ResultVo.CODE_OK, ResultVo.MSG_OK);
    }

    @Override
    public boolean hasOnLineUdp(@RequestBody String machineCode) {
        return UdpNettySocketHolder.hasOnLine(machineCode);
    }
}
