package com.java110.charge.workLicense.hc;

import com.java110.bean.ResultVo;
import com.java110.charge.factory.lvcc.LvCCChargeMachineFactoryAdapt;
import com.java110.charge.workLicense.IWorkLicenseAdapt;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.BytesUtil;
import com.java110.core.utils.DateUtil;
import com.java110.dto.data.NettyReplyDataDto;
import com.java110.dto.workLicenseMachine.WorkLicenseMachineDto;
import com.java110.dto.workLicensePos.WorkLicensePosDto;
import com.java110.intf.charge.IWorkLicenseMachineV1InnerServiceSMO;
import com.java110.intf.charge.IWorkLicensePosV1InnerServiceSMO;
import com.java110.intf.hal.INotifyNettyDataV1InnerServiceSMO;
import com.java110.po.chargeMachine.ChargeMachinePo;
import com.java110.po.workLicenseMachine.WorkLicenseMachinePo;
import com.java110.po.workLicensePos.WorkLicensePosPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 员工牌
 */
@Service("hcWorkLicenseFactoryAdapt")
public class HcWorkLicenseFactoryAdapt implements IWorkLicenseAdapt {
    private static Logger logger = LoggerFactory.getLogger(HcWorkLicenseFactoryAdapt.class);

    public static final String CMD_REGISTER = "0100"; // todo 注册

    public static final String CMD_AUTH = "0102"; // todo 终端鉴权

    public static final String CMD_HEARTBEAT = "0002";//todo 设备心跳

    public static final String CMD_PLATFORM_RESULT = "8001";// todo 平台应答

    public static final String CMD_MACHINE_RESULT = "0001";// todo 设备应答

    public static final String CMD_UPLOAD_POS = "0200";// todo 汇报位置

    public static final String AUTH_CODE = "123456";


    @Autowired
    private INotifyNettyDataV1InnerServiceSMO notifyNettyDataV1InnerServiceSMOImpl;

    @Autowired
    private IWorkLicenseMachineV1InnerServiceSMO workLicenseMachineV1InnerServiceSMOImpl;

    @Autowired
    private IWorkLicensePosV1InnerServiceSMO workLicensePosV1InnerServiceSMOImpl;


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

        logger.debug("收到工牌消息，machineCode = {}，data = {}", workLicenseMachineDto.getMachineCode(), BytesUtil.bytesToHex(data));

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
            case CMD_UPLOAD_POS:
                uploadPos(workLicenseMachineDto, cmd, data);
                break;
        }

        return null;
    }

    /**
     * 上报位置
     *
     * @param workLicenseMachineDto
     * @param cmd
     * @param data
     */
    private void uploadPos(WorkLicenseMachineDto workLicenseMachineDto, String cmd, byte[] data) {

        //00000000 80000040 015b4555 06cf3331 0000000c0000240301011213010400000012040201640501023001533101002b0410271027000c00b2898604b0262270341649
        String uploadData = HcWorkLicenseUtil.getData(data);

        /**
         * 0 报警标志 DWORD 报警标志位定义见 表 24
         * 4 状态 DWORD 状态位定义见 表 25
         * 8 纬度 DWORD 以度为单位的纬度值乘以 10 的 6 次方，精确到百万
         * 分之一度
         * 12 经度 DWORD 以度为单位的经度值乘以 10 的 6 次方，精确到百万
         * 分之一度
         * 16 高程 WORD 海拔高度，单位为米（m）
         * 18 速度 WORD 1/10km/h
         * 20 方向 WORD 0-359，正北为 0，顺时针
         * 21 时间 BCD[6] YY-MM-DD-hh-mm-ss（GMT+8 时间，本标准中之后涉
         * 及的时间均采用此时区）
         */
        String  lon= uploadData.substring(2*8,3*8);

        String lat = uploadData.substring(3*8,4*8);
        //015b4555::06cf3331

        lon = (Integer.parseInt(lon,16)/1000000.0) +"";
        lat = (Integer.parseInt(lat,16)/1000000.0) +"";

        WorkLicensePosPo workLicensePosPo = new WorkLicensePosPo();
        workLicensePosPo.setMachineId(workLicenseMachineDto.getMachineId());
        workLicensePosPo.setMachineCode(workLicenseMachineDto.getMachineCode());
        workLicensePosPo.setUploadDate(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_B));
        workLicensePosPo.setWltId(GenerateCodeFactory.getGeneratorId("11"));
        workLicensePosPo.setStaffName(workLicenseMachineDto.getStaffName());
        workLicensePosPo.setLon(lon);
        workLicensePosPo.setLat(lat);
        workLicensePosPo.setStoreId(workLicenseMachineDto.getStoreId());
        workLicensePosPo.setStaffId(workLicenseMachineDto.getStaffId());
        workLicensePosPo.setPosType(WorkLicensePosDto.POS_TYPE_PUBLIC);

        workLicensePosV1InnerServiceSMOImpl.saveWorkLicensePos(workLicensePosPo);

        WorkLicenseMachinePo workLicenseMachinePo = new WorkLicenseMachinePo();
        workLicenseMachinePo.setLon(lon);
        workLicenseMachinePo.setLat(lat);
        workLicenseMachinePo.setMachineId(workLicenseMachineDto.getMachineId());
        workLicenseMachinePo.setMachineCode(workLicenseMachineDto.getMachineCode());
        workLicenseMachineV1InnerServiceSMOImpl.updateWorkLicenseMachine(workLicenseMachinePo);

        commonReturn(workLicenseMachineDto, cmd, data);

    }

    /**
     * 心跳
     *
     * @param workLicenseMachineDto
     * @param cmd
     * @param data
     */
    private void heartbeat(WorkLicenseMachineDto workLicenseMachineDto, String cmd, byte[] data) {


        WorkLicenseMachinePo workLicenseMachinePo = new WorkLicenseMachinePo();
        workLicenseMachinePo.setHeartbeatTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        workLicenseMachinePo.setMachineId(workLicenseMachineDto.getMachineId());
        workLicenseMachinePo.setMachineCode(workLicenseMachineDto.getMachineCode());
        workLicenseMachineV1InnerServiceSMOImpl.updateWorkLicenseMachine(workLicenseMachinePo);


        commonReturn(workLicenseMachineDto, cmd, data);
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
            resultData += "01";
        } else {
            resultData += "00";
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

        String resultData = HcWorkLicenseUtil.tranId(data) + "00" + AUTH_CODE;

        resultData = HcWorkLicenseUtil.computeResultDate(data, workLicenseMachineDto.getMachineCode(), resultData, "8100");

        notifyNettyDataV1InnerServiceSMOImpl.sendData(new NettyReplyDataDto(workLicenseMachineDto.getMachineCode(), BytesUtil.hexStringToByteArray(resultData)));

    }

    private void commonReturn(WorkLicenseMachineDto workLicenseMachineDto, String cmd, byte[] data) {
        String resultData = HcWorkLicenseUtil.tranId(data) + cmd + "00";
        resultData = HcWorkLicenseUtil.computeResultDate(data, workLicenseMachineDto.getMachineCode(), resultData);

        notifyNettyDataV1InnerServiceSMOImpl.sendData(new NettyReplyDataDto(workLicenseMachineDto.getMachineCode(), BytesUtil.hexStringToByteArray(resultData)));
    }
}
