package com.java110.charge.factory.lvcc;

import com.java110.core.utils.BytesUtil;

import java.io.UnsupportedEncodingException;

public class LvCCUtil {

    public static final String LV_CHONG_CHONG_HEAD = "7e5d7d7f";// 驴充充 针头

    public static final String LV_VERSION = "0001"; // 协议版本号

    public static final String CMD_REGISTER = "0001"; // todo 注册


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
        return LV_CHONG_CHONG_HEAD + String.format("%04x", (rs.length() / 2)) + rs+xOrCheckSum(BytesUtil.hexStringToByteArray(rs));
    }
}
