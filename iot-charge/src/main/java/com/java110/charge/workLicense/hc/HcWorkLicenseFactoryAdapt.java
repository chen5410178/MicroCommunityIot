package com.java110.charge.workLicense.hc;

import com.java110.bean.ResultVo;
import com.java110.charge.workLicense.IWorkLicenseAdapt;
import com.java110.core.utils.BytesUtil;
import com.java110.core.utils.DateUtil;
import com.java110.dto.data.NettyReplyDataDto;
import com.java110.dto.workLicenseMachine.WorkLicenseMachineDto;
import com.java110.intf.charge.IWorkLicenseMachineV1InnerServiceSMO;
import com.java110.intf.hal.INotifyNettyDataV1InnerServiceSMO;
import com.java110.po.chargeMachine.ChargeMachinePo;
import com.java110.po.workLicenseMachine.WorkLicenseMachinePo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 员工牌
 */
@Service("hcWorkLicenseFactoryAdapt")
public class HcWorkLicenseFactoryAdapt implements IWorkLicenseAdapt {

    public static final String CMD_REGISTER = "0100"; // todo 注册

    public static final String CMD_AUTH = "0102"; // todo 终端鉴权

    public static final String CMD_HEARTBEAT = "0002";//todo 设备心跳

    public static final String CMD_PLATFORM_RESULT = "8001";// todo 平台应答

    public static final String CMD_MACHINE_RESULT = "0001";// todo 设备应答

    public static final String AUTH_CODE = "123456";


    @Autowired
    private INotifyNettyDataV1InnerServiceSMO notifyNettyDataV1InnerServiceSMOImpl;

    @Autowired
    private IWorkLicenseMachineV1InnerServiceSMO workLicenseMachineV1InnerServiceSMOImpl;


    @Override
    public ResultVo settingParam(WorkLicenseMachineDto workLicenseMachineDto) {
        return null;
    }

    @Override
    public ResultVo playTts(WorkLicenseMachineDto workLicenseMachineDto, String ttsText) {
        return null;
    }

    /**
     * 7e
     * -- 消息头
     * 0100 -- 消息 ID
     * 0036 -- 消息体属性 消息长度计算
     * 016059631666 -- 终端手机号
     * 0000 -- 消息流水号
     * -- 消息头完
     * -- 消息体
     * 002c -- 省域 ID
     * 012c -- 市县域 ID
     * 455656494e -- 制造商 ID
     * 5441323031000000000000000000000000000000 -- 终端型号
     * 39363331363636 --终端ID
     * 00 -- 车牌颜色
     * 3030383632303136303539363331363636
     * 77
     * 7e
     * 标识位 消息头 消息体 检验码 标识位
     *
     * @param workLicenseMachineDto
     * @param data
     * @return
     */
    @Override
    public ResultVo result(WorkLicenseMachineDto workLicenseMachineDto, byte[] data) {

        String cmd = HcWorkLicenseUtil.getCmd(data);

        switch (cmd) {
            case CMD_REGISTER:
                machineRegister(workLicenseMachineDto, cmd, data);
                break;
            case CMD_AUTH:
                machineAuth(workLicenseMachineDto, cmd, data);
                break;
            case CMD_HEARTBEAT:
                heartbeat(workLicenseMachineDto, cmd, data);
                break;
        }

        return null;
    }

    /**
     * 心跳
     *
     * @param workLicenseMachineDto
     * @param cmd
     * @param data
     */
    private void heartbeat(WorkLicenseMachineDto workLicenseMachineDto, String cmd, byte[] data) {

        String resultData = HcWorkLicenseUtil.tranId(data) + cmd + "0";

        WorkLicenseMachinePo workLicenseMachinePo = new WorkLicenseMachinePo();
        workLicenseMachinePo.setHeartbeatTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        workLicenseMachinePo.setMachineId(workLicenseMachineDto.getMachineId());
        workLicenseMachinePo.setMachineCode(workLicenseMachineDto.getMachineCode());
        workLicenseMachineV1InnerServiceSMOImpl.updateWorkLicenseMachine(workLicenseMachinePo);


        resultData = HcWorkLicenseUtil.computeResultDate(data, workLicenseMachineDto.getMachineCode(), resultData);

        notifyNettyDataV1InnerServiceSMOImpl.sendData(new NettyReplyDataDto(workLicenseMachineDto.getMachineCode(), BytesUtil.hexStringToByteArray(resultData)));
    }

    /**
     * 设备鉴权
     *
     * @param workLicenseMachineDto
     * @param cmd
     * @param data
     */
    private void machineAuth(WorkLicenseMachineDto workLicenseMachineDto, String cmd, byte[] data) {

        String authCode = HcWorkLicenseUtil.getData(data);
        String resultData = HcWorkLicenseUtil.tranId(data) + cmd;
        if (!AUTH_CODE.equals(authCode)) {
            resultData += "1";
        } else {
            resultData += "0";
        }

        resultData = HcWorkLicenseUtil.computeResultDate(data, workLicenseMachineDto.getMachineCode(), resultData);

        notifyNettyDataV1InnerServiceSMOImpl.sendData(new NettyReplyDataDto(workLicenseMachineDto.getMachineCode(), BytesUtil.hexStringToByteArray(resultData)));
    }

    /**
     * 设备注册
     *
     * @param workLicenseMachineDto
     * @param cmd
     * @param data
     */
    private void machineRegister(WorkLicenseMachineDto workLicenseMachineDto, String cmd, byte[] data) {

        String resultData = HcWorkLicenseUtil.tranId(data) + "0" + AUTH_CODE;

        resultData = HcWorkLicenseUtil.computeResultDate(data, workLicenseMachineDto.getMachineCode(), resultData, "8100");

        notifyNettyDataV1InnerServiceSMOImpl.sendData(new NettyReplyDataDto(workLicenseMachineDto.getMachineCode(), BytesUtil.hexStringToByteArray(resultData)));

    }
}
