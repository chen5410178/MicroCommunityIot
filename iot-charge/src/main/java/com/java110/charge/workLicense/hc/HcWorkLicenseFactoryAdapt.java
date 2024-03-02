package com.java110.charge.workLicense.hc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.charge.factory.lvcc.LvCCChargeMachineFactoryAdapt;
import com.java110.charge.workLicense.IWorkLicenseAdapt;
import com.java110.core.cache.MappingCache;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.BytesUtil;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.dto.data.NettyReplyDataDto;
import com.java110.dto.workLicenseFactory.WorkLicenseFactoryDto;
import com.java110.dto.workLicenseMachine.WorkLicenseMachineDto;
import com.java110.dto.workLicensePos.WorkLicensePosDto;
import com.java110.intf.charge.IWorkLicenseLogV1InnerServiceSMO;
import com.java110.intf.charge.IWorkLicenseMachineV1InnerServiceSMO;
import com.java110.intf.charge.IWorkLicensePosV1InnerServiceSMO;
import com.java110.intf.hal.INotifyNettyDataV1InnerServiceSMO;
import com.java110.po.chargeMachine.ChargeMachinePo;
import com.java110.po.workLicenseLog.WorkLicenseLogPo;
import com.java110.po.workLicenseMachine.WorkLicenseMachinePo;
import com.java110.po.workLicensePos.WorkLicensePosPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 员工牌
 * <p>
 * 查询参数：PARAM#
 * 查询状态：STATUS#
 * <p>
 * 设置主IP:IP,58.61.154.237,7018#
 * 58.61.154.237-IP地址
 * 7018-端口
 * 查询主IP:IP#
 * <p>
 * 设置副IP
 * FIP,58.61.154.237,7018#
 * 58.61.154.237-IP地址
 * 7018-端口
 * 查询副IP:FIP#
 * <p>
 * 取消副IP：FIP,#
 * <p>
 * 配置主副IP的要注意下，主IP可以下发指令可执行，副IP不执行指令，只接收数据。其中一个ip连接不上都会影响另外一个
 * <p>
 * 汇报间隔设置:FREQ,30#
 * 30-汇报间隔，单位秒
 * 查询汇报间隔:FREQ#
 * <p>
 * 心跳间隔设置:HBT,180#
 * 180-心跳间隔，单位秒
 * 查询心跳间隔:HBT#
 * <p>
 * 设置工作模式
 * MODE,x,y,A,B,C,D#
 * x-工作模式，1定时，2智能
 * y-汇报间隔，单位秒
 * A-GPS，1开启，0关闭
 * B-WiFi，1开启，0关闭
 * C-LBS，1开启，0关闭
 * D-GPRS，1开启，0关闭
 * 查询工作模式：MODE#
 * <p>
 * 重启：RESET#
 * 恢复出厂：FACTORY#
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

    public static final String CMD_UPLOAD_POS_ATT_OFF = "0204";// todo 打开

    public static final String CMD_UPLOAD_POS_ATT_ON = "0203";// todo 打开


    public static final String CMD_REQ = "8300"; // todo 请求下发数据

    public static final String AUTH_CODE = "123456";

    public static final String TENCENT_MAP_URL = "https://apis.map.qq.com/ws/location/v1/network";


    @Autowired
    private INotifyNettyDataV1InnerServiceSMO notifyNettyDataV1InnerServiceSMOImpl;

    @Autowired
    private IWorkLicenseMachineV1InnerServiceSMO workLicenseMachineV1InnerServiceSMOImpl;

    @Autowired
    private IWorkLicensePosV1InnerServiceSMO workLicensePosV1InnerServiceSMOImpl;

    @Autowired
    private IWorkLicenseLogV1InnerServiceSMO workLicenseLogV1InnerServiceSMOImpl;

    @Autowired
    private RestTemplate outRestTemplate;


    @Override
    public ResultVo settingParam(WorkLicenseMachineDto workLicenseMachineDto) {
        return null;
    }

    @Override
    public ResultVo playTts(WorkLicenseMachineDto workLicenseMachineDto, String ttsText) {
        //todo 下发tts
        String reqType = "00001000";
        int aa = Integer.parseInt(reqType, 2);
        reqType = String.format("%02x", aa);
        String reqData = null;
        try {
            byte[] data = ttsText.getBytes("GBK");
            reqData = reqType + BytesUtil.bytesToHex(data);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        reqData = HcWorkLicenseUtil.computeReqData(workLicenseMachineDto.getMachineCode(), reqData);
        notifyNettyDataV1InnerServiceSMOImpl.sendData(new NettyReplyDataDto(workLicenseMachineDto.getMachineCode(), BytesUtil.hexStringToByteArray(reqData)));

        JSONObject reqJson = new JSONObject();
        reqJson.put("ttsText",ttsText);
        reqJson.put("ttsTextBin",reqData);

        WorkLicenseLogPo workLicenseLogPo = new WorkLicenseLogPo();
        workLicenseLogPo.setLogCmd("8300");
        workLicenseLogPo.setMachineId(workLicenseMachineDto.getMachineId());
        workLicenseLogPo.setStaffName(workLicenseMachineDto.getStaffName());
        workLicenseLogPo.setStaffId(workLicenseMachineDto.getStaffId());
        workLicenseLogPo.setLogId(GenerateCodeFactory.getGeneratorId("11"));
        workLicenseLogPo.setReqParam(reqJson.toJSONString());
        workLicenseLogPo.setState("W");
        workLicenseLogPo.setStoreId(workLicenseMachineDto.getStoreId());
        workLicenseLogPo.setLogAction("tts");
        workLicenseLogV1InnerServiceSMOImpl.saveWorkLicenseLog(workLicenseLogPo);
        return new ResultVo(ResultVo.CODE_OK, "已提交工牌");
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
            case CMD_UPLOAD_POS_ATT_OFF:
                uploadPos(workLicenseMachineDto, cmd, data);
                break;
            case CMD_UPLOAD_POS_ATT_ON:
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

        String state = uploadData.substring( 8, 2 * 8);

        long decimal = Long.parseLong(state, 16); // 先将十六进制转换为十进制
        String binary = Long.toBinaryString(decimal); // 再将十进制转换为二进制

        String gps =  binary.charAt(binary.length() - 2)+"";

        String lon = "0";
        String lat = "0";
        if("0".equals(gps)){ // 没有gps
            Map<String,String> info = getFromWifi(uploadData,workLicenseMachineDto.getMachineId());
            lon = info.get("lon");
            lat = info.get("lat");
            if(Double.parseDouble(lon) <=0 ){
                commonReturn(workLicenseMachineDto, cmd, data);
                return;
            }
        }else{ //todo gps 定位
             lon = uploadData.substring(2 * 8, 3 * 8);
             lat = uploadData.substring(3 * 8, 4 * 8);
            //015b4555::06cf3331
            lon = (Long.parseLong(lon, 16) / 1000000.0) + "";
            lat = (Long.parseLong(lat, 16) / 1000000.0) + "";
            if(Integer.parseInt(lon, 16) <=0 ){
                commonReturn(workLicenseMachineDto, cmd, data);
                return;
            }
        }



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
     * 7e020000920160596316660046
     * 00000000
     * 80000040
     * 00000000 -- 精度
     * 00000000 -- 维度
     * 0000 高度
     * 0000 速度
     * 0000 方向角
     * 240302153330
     * -- 附加信息
     * f0 -- 扩展WIFI定位获取的MAC地址及信号强度
     * 1f -- 长度 31位
     * e6 -- 0XE6特定标识
     * 0b -- 0X0B特定标识
     * 04 -- 扫描到的WIFI数据组数，最多4组
     * 4c50773aa67d -- MAC地址
     * 01 -- 强度
     * 3f3868be0e40
     * d1
     * 493868bed9d6
     * c9
     * 4c88c3974d14
     * fc
     * -- 附加信息
     * 4d150f38363230313630353936333136363660150201cc00971209a336012d01cc00971209a3360325
     * fe12e60200015020000a898604b02622703416490104000000120402015005010130014a3101002b040f3f0f3f717e
     * @param uploadData
     * @return
     */
    private Map<String, String> getFromWifi(String uploadData,String machineId) {
        Map<String,String> info = new HashMap<>();
        info.put("lon","0");
        info.put("lat","0");

        String[] macs = HcWorkLicenseUtil.getMacs(uploadData);
        JSONObject body = new JSONObject();
        body.put("key","ZUXBZ-7F5AU-WLXV6-GRX62-RZ6P7-XOB3N");
        body.put("device_id",machineId);

        JSONArray wifiinfos = new JSONArray();
        JSONObject wifiinfo = null;
        for(String mac :macs){
            wifiinfo = new JSONObject();
            wifiinfo.put("mac",mac.split("\\|")[0]);
            wifiinfo.put("rssi",Long.parseLong(mac.split("\\|")[1],16));
            wifiinfos.add(wifiinfo);
        }

        body.put("wifiinfo",wifiinfos);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type","application/json");
        HttpEntity httpEntity = new HttpEntity(body.toJSONString(), httpHeaders);
        ResponseEntity<String> response = null;
        response = outRestTemplate.exchange(TENCENT_MAP_URL, HttpMethod.POST, httpEntity, String.class);
        JSONObject resJson = JSONObject.parseObject(response.getBody());
        if(resJson.getIntValue("status") != 0){
            return info;
        }

       JSONObject location = resJson.getJSONObject("result").getJSONObject("location");

        String latitude = location.getString("latitude");
        String longitude = location.getString("longitude");
        info.put("lon",longitude);
        info.put("lat",latitude);
        return info;
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

        //todo 休息一秒
        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //todo 下发心跳时间
        resultData = HcWorkLicenseUtil.getSettingHeartbeatParam();
        resultData = HcWorkLicenseUtil.computeReqData(workLicenseMachineDto.getMachineCode(), resultData);
        notifyNettyDataV1InnerServiceSMOImpl.sendData(new NettyReplyDataDto(workLicenseMachineDto.getMachineCode(), BytesUtil.hexStringToByteArray(resultData)));

        //todo 休息一秒
        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        WorkLicenseMachineDto workLicenseMachineDto1 = new WorkLicenseMachineDto();
        workLicenseMachineDto1.setMachineId(workLicenseMachineDto.getMachineId());
        workLicenseMachineDto1.setMachineCode(workLicenseMachineDto.getMachineCode());
        List<WorkLicenseMachineDto> workLicenseMachineDtos = workLicenseMachineV1InnerServiceSMOImpl.queryWorkLicenseMachines(workLicenseMachineDto1);

        if (ListUtil.isNull(workLicenseMachineDtos)) {
            return;
        }
        //todo 汇报时间间隔
        int sec = Integer.parseInt(workLicenseMachineDtos.get(0).getPosUploadSec());
        resultData = HcWorkLicenseUtil.getSettingWorkModeParam(sec);
        resultData = HcWorkLicenseUtil.computeReqData(workLicenseMachineDto.getMachineCode(), resultData);
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
