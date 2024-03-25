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

        //String reqData = "HBT,60#";
        String reqData = "#2014*SET*X:60#";
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


    /**
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
     * @return
     */
    public static String[] getMacs(String uploadData) {
        int extStartPos = 8*4+4*3+12;
        String exId = uploadData.substring(extStartPos,extStartPos + 2);

        if(!"f0".equals(exId)){
            return null;
        }

        extStartPos += 10;

        String mac1 = uploadData.substring(extStartPos,extStartPos + 14);
        extStartPos += 14;
        String mac2 = uploadData.substring(extStartPos,extStartPos + 14);
        extStartPos += 14;
        String mac3 = uploadData.substring(extStartPos,extStartPos + 14);
        extStartPos += 14;
        String mac4 = uploadData.substring(extStartPos,extStartPos + 14);

        mac1 = mac1.substring(0,2)+":"+mac1.substring(2,4)+":"+mac1.substring(4,6)
                +":"+mac1.substring(6,8)+":"+mac1.substring(8,10)
                +":"+mac1.substring(10,12)+"|"+mac1.substring(12,14) ;
        mac2 = mac2.substring(0,2)+":"+mac2.substring(2,4)+":"+mac2.substring(4,6)
                +":"+mac2.substring(6,8)+":"+mac2.substring(8,10)
                +":"+mac2.substring(10,12)+"|"+mac2.substring(12,14) ;
        mac3 = mac3.substring(0,2)+":"+mac3.substring(2,4)+":"+mac3.substring(4,6)
                +":"+mac3.substring(6,8)+":"+mac3.substring(8,10)
                +":"+mac3.substring(10,12)+"|"+mac3.substring(12,14) ;
        mac4 = mac4.substring(0,2)+":"+mac4.substring(2,4)+":"+mac4.substring(4,6)
                +":"+mac4.substring(6,8)+":"+mac4.substring(8,10)
                +":"+mac4.substring(10,12)+"|"+mac4.substring(12,14) ;
        return new String[]{mac1,mac2,mac3,mac4};
    }


    public static void main(String[] args) throws UnsupportedEncodingException {
        String aa = "7e02000092016059631666004600000000800000400000000000000000000000000000240302153330f01fe60b044c50773aa67d013f3868be0e40d1493868bed9d6c94c88c3974d14fc4d150f38363230313630353936333136363660150201cc00971209a336012d01cc00971209a3360325fe12e60200015020000a898604b02622703416490104000000120402015005010130014a3101002b040f3f0f3f717e";
        byte[] data = BytesUtil.hexStringToByteArray(aa);

        String resultData = HcWorkLicenseUtil.getData(data);

        String[] macs = getMacs(resultData);

        System.out.println(macs[0]);
        System.out.println(macs[1]);
        System.out.println(macs[2]);
        System.out.println(macs[3]);



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

        //String reqData = "MODE,2,"+sec+",1,1,1,1#";
        String reqData = "#2014*SET*M2:30,300#";
        try {
            byte[] data = reqData.getBytes("GBK");
            reqData = reqType + BytesUtil.bytesToHex(data);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return reqData;
    }

}

