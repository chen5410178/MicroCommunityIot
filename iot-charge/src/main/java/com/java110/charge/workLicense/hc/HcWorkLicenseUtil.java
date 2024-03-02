package com.java110.charge.workLicense.hc;

import com.java110.core.utils.BytesUtil;
import com.java110.core.utils.StringUtil;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Random;

public class HcWorkLicenseUtil {

    public static final String WL_VERSION = "7e";

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
     * @param msg
     * @return
     */
    public static String getCmd(byte[] msg) {
        String data = BytesUtil.bytesToHex(msg);
        data = data.substring(2, 6);
        return data;
    }

    public static String machineCode(byte[] msg) {
        String data = BytesUtil.bytesToHex(msg);
        data = data.substring(10, 22);
        return data.replaceAll("^0+", "");
    }

    public static String tranId(byte[] msg) {
        String data = BytesUtil.bytesToHex(msg);
        data = data.substring(22, 26);
        return data;
    }

    public static String getData(byte[] msg) {
        int start = 13;
        int end = 0;

        // todo 计算消息长度
        String data = BytesUtil.bytesToHex(msg);
        String lenHex = data.substring(6, 10);
        String lenBin = Integer.toBinaryString(Integer.parseInt(lenHex, 16));
        DecimalFormat decimalFormat = new DecimalFormat("0000000000000000");
        String bit = decimalFormat.format(Integer.valueOf(lenBin)); // 00 0 000 0000110110
        /**
         * 15 14 |  13   |   12 11 10  |  9 8 7 6 5 4 3 2 1 0
         * 保留  | 分包    |数据加密方式   |    消息体长度
         */
        String fengbao = String.valueOf(bit.charAt(2));
        String encrypt = bit.substring(3, 6);
        String bitLen = bit.substring(6);

        int len = Integer.parseInt(bitLen, 2);

        if ("1".equals(fengbao)) {
            start += 2;
        }
        start = start * 2;

        end = start + len * 2;


        return data.substring(start, end);
    }

    /**
     * 计算返回内容
     *
     * @param rsData
     * @return
     */
    public static String computeReqData(String machineCode, String rsData) {
        /**
         *    * 7e
         *      * -- 消息头
         *      * 0100 -- 消息 ID
         *      * 0036 -- 消息体属性 消息长度计算
         *      * 016059631666 -- 终端手机号
         *      * 0000 -- 消息流水号
         *      * -- 消息头完
         *      * -- 消息体
         *      * 002c -- 省域 ID
         *      * 012c -- 市县域 ID
         *      * 455656494e -- 制造商 ID
         *      * 5441323031000000000000000000000000000000 -- 终端型号
         *      * 39363331363636 --终端ID
         *      * 00 -- 车牌颜色
         *      * 3030383632303136303539363331363636
         *      * 77
         *      * 7e
         */

        //todo 计算设备编号 长度
        String msgLen = getMsgLen(rsData);

        String resMachineCode = "0" + machineCode;

        String msg = "8300" + msgLen + resMachineCode + generatorMsgId() + rsData;

        return WL_VERSION + msg + xOrCheckSum(BytesUtil.hexStringToByteArray(msg)) + WL_VERSION;
    }

    public static String computeResultDate(byte[] data, String machineCode, String rsData) {
        return computeResultDate(data, machineCode, rsData, "8001");
    }

    /**
     * 计算返回内容
     *
     * @param data
     * @param rsData
     * @return
     */
    public static String computeResultDate(byte[] data, String machineCode, String rsData, String cmd) {
        /**
         *    * 7e
         *      * -- 消息头
         *      * 0100 -- 消息 ID
         *      * 0036 -- 消息体属性 消息长度计算
         *      * 016059631666 -- 终端手机号
         *      * 0000 -- 消息流水号
         *      * -- 消息头完
         *      * -- 消息体
         *      * 002c -- 省域 ID
         *      * 012c -- 市县域 ID
         *      * 455656494e -- 制造商 ID
         *      * 5441323031000000000000000000000000000000 -- 终端型号
         *      * 39363331363636 --终端ID
         *      * 00 -- 车牌颜色
         *      * 3030383632303136303539363331363636
         *      * 77
         *      * 7e
         */

        //todo 计算设备编号 长度
        String msgLen = getMsgLen(rsData);

        String dataHex = BytesUtil.bytesToHex(data);
        String resMachineCode = dataHex.substring(10, 22);

        String msg = cmd + msgLen + resMachineCode + tranId(data) + rsData;

        return WL_VERSION + msg + xOrCheckSum(BytesUtil.hexStringToByteArray(msg)) + WL_VERSION;
    }

    private static String getMsgLen(String rsData) {
        DecimalFormat decimalFormat = new DecimalFormat("0000000000");
        String bit = "000000" + decimalFormat.format(Integer.valueOf(Integer.toBinaryString(rsData.length() / 2)));
        int aa = Integer.parseInt(bit, 2);
        return String.format("%04x", aa);
    }

    public static String xOrCheckSum(byte[] data) {
        byte checksum = 0;
        for (byte b : data) {
            checksum ^= b;
        }

        String hex = Integer.toHexString(checksum & 0xFF);
        if (hex.length() < 2) {
            hex = "0" + hex;
        }
        return hex;

    }

    public static String getSettingHeartbeatParam() {
        String reqType = "00000001";
        int aa = Integer.parseInt(reqType, 2);
        reqType = String.format("%02x", aa);

        String reqData = "HBT,60#";
        try {
            byte[] data = reqData.getBytes("GBK");
            reqData = reqType + BytesUtil.bytesToHex(data);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return reqData;
    }

    public static String getSettingWorkSec(int sec) {
        String reqType = "00000001";
        int aa = Integer.parseInt(reqType, 2);
        reqType = String.format("%02x", aa);

        String reqData = "FREQ,"+sec+"#";
        try {
            byte[] data = reqData.getBytes("GBK");
            reqData = reqType + BytesUtil.bytesToHex(data);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return reqData;
    }


    public static String[] getMacs(String uploadData) {


        return null;
    }


    public static void main(String[] args) throws UnsupportedEncodingException {
        String aa = "7e02000092016059631666004600000000800000400000000000000000000000000000240302153330f01fe60b044c50773aa67d013f3868be0e40d1493868bed9d6c94c88c3974d14fc4d150f38363230313630353936333136363660150201cc00971209a336012d01cc00971209a3360325fe12e60200015020000a898604b02622703416490104000000120402015005010130014a3101002b040f3f0f3f717e";
        byte[] data = BytesUtil.hexStringToByteArray(aa);

        String resultData = HcWorkLicenseUtil.getData(data);
        System.out.println(resultData);



    }

    public static String generatorMsgId() {

        Random random = new Random();
        int msgId = random.nextInt(256);

        return String.format("%04x", msgId);
    }


    /**
     * * 设置工作模式
     * * MODE,x,y,A,B,C,D#
     * * x-工作模式，1定时，2智能
     * * y-汇报间隔，单位秒
     * * A-GPS，1开启，0关闭
     * * B-WiFi，1开启，0关闭
     * * C-LBS，1开启，0关闭
     * * D-GPRS，1开启，0关闭
     */
    public static String getSettingWorkModeParam(int sec) {

        String reqType = "00000001";
        int aa = Integer.parseInt(reqType, 2);
        reqType = String.format("%02x", aa);

        String reqData = "MODE,1,"+sec+",1,1,1,1#";
        try {
            byte[] data = reqData.getBytes("GBK");
            reqData = reqType + BytesUtil.bytesToHex(data);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return reqData;
    }

}

