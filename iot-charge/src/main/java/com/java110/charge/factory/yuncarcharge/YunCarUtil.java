package com.java110.charge.factory.yuncarcharge;

import com.java110.core.utils.BytesUtil;

import java.io.UnsupportedEncodingException;
import java.util.Random;

public class YunCarUtil {

    public static final String LV_CHONG_CHONG_HEAD = "7eedaf";//帧头

    public static final String LV_VERSION = "0086"; // 协议版本号

    public static final String CMD_REGISTER = "0005"; // todo 注册
    public static final String CMD_HEARTBEAT = "0008"; // todo 心跳
    public static final String CMD_START_CHARGE = "0006"; // todo 开启充电

    public static final String CMD_STOP_CHARGE = "0003"; // todo 停止充电


    public static final String CMD_CHARGE_END = "0010"; // todo 充电结束
    public static final String CMD_UPLOAD_CHARGE_DATA = "0030"; // todo 充电过程中 上报充电功率
    public static final String CMD_RESTART = "0017"; // todo 成功设备


    public static final String CMD_CHANGE_PORT_STATE = "0021"; // todo 插座状态改变



    /**
     * 获取设备ID长度
     *
     * @param msg
     * @return
     */
    public static int getLvCCMachineCodeLength(byte[] msg) {
        String data = BytesUtil.bytesToHex(msg);
        data = data.substring(18, 20);
        return Integer.parseInt(data, 16);
    }

    /**
     * 获取设备ID
     *
     * @param msg
     * @return
     */
    public static String getLvCCMachineCode(byte[] msg) {
        String data = BytesUtil.bytesToHex(msg);
        data = data.substring(20, 20 + getLvCCMachineCodeLength(msg)*2);
        String bb = null;
        try {
            bb = new String(BytesUtil.hexStringToByteArray(data), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return bb;
    }

    /**
     * 获取设备ID
     *
     * @param msg
     * @return
     */
    public static String getLvCCMsgId(byte[] msg) {
        String data = BytesUtil.bytesToHex(msg);
        int startPos = 20 + getLvCCMachineCodeLength(msg) * 2;
        data = data.substring(startPos, startPos + 4);
        return data;
    }

    /**
     * 获取指令
     *
     * @param msg
     * @return
     */
    public static String getCmd(byte[] msg) {
        String data = BytesUtil.bytesToHex(msg);

        int startPos = 24 + getLvCCMachineCodeLength(msg) * 2;
        int endPos = startPos + 4;
        data = data.substring(startPos, endPos);
        return data;
    }

    /**
     * 获取指令报文
     *
     * @param msg
     * @return
     */
    public static String getCmdContext(byte[] msg) {
        String data = BytesUtil.bytesToHex(msg);

        int startPos = 28 + getLvCCMachineCodeLength(msg) * 2;
        data = data.substring(startPos, data.length() - 2);

        return data;
    }

    public static String xOrCheckSum(byte[] data) {
        byte checksum = 0;
        for (byte b : data) {
            checksum ^= b;
        }

        String hex = Integer.toHexString(checksum & 0xFF);
        if (hex.length() < 2) {
            hex = "0"+hex;
        }
        return hex;

    }

    /**
     * 计算返回内容
     *
     * @param data
     * @param rsData
     * @return
     */
    public static String computeResultDate(byte[] data,String machineCode, String rsData) {
        //7e 5d 7d 7f
        // 00 1c
        // 00 01
        // 01
        // 0f
        // 38 36 38 30 38 39 30 35 37 36 37 32 34 39 39
        // 00 01
        // 00 01
        // 61 e0 dc 1d 7d

        //todo 计算设备编号 长度
        byte[] mcBytes = null;
        try {
            mcBytes = machineCode.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        String machineCodeHex = BytesUtil.bytesToHex(mcBytes);

        String machineCodeLen = String.format("%02x",machineCodeHex.length()/2);

        String rs = LV_VERSION + "01" + machineCodeLen + machineCodeHex + getLvCCMsgId(data) + getCmd(data) + rsData;
        return LV_CHONG_CHONG_HEAD + String.format("%04x", (rs.length() / 2+1)) + rs+xOrCheckSum(BytesUtil.hexStringToByteArray(rs));
    }

    public static String requestMachineData(String cmd, String machineCode, String data) {

        byte[] mcBytes = null;
        try {
            mcBytes = machineCode.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        String machineCodeHex = BytesUtil.bytesToHex(mcBytes);

        String machineCodeLen = String.format("%02x",machineCodeHex.length()/2);

        String rs = LV_VERSION + "10" + machineCodeLen + machineCodeHex + generatorMsgId() + cmd + data;
        return LV_CHONG_CHONG_HEAD + String.format("%04x", (rs.length() / 2+1)) + rs+xOrCheckSum(BytesUtil.hexStringToByteArray(rs));
    }

    public static String generatorMsgId() {

        Random random = new Random();
        int msgId = random.nextInt(256);

        return String.format("%04x",msgId);
    }


}
