package com.java110.hal.nettty;

import com.java110.core.utils.BytesUtil;
import com.java110.core.utils.StringUtil;

import java.io.UnsupportedEncodingException;

public class DataHeader {

    public static final String LV_CHONG_CHONG_HEAD = "7e5d7d7f";// 驴充充 针头

    public static final String SONG_BING_HEAD = "68";// 松饼头
    public static final String SONG_BING_END = "16";// 松饼尾

    /**
     * 判断是否为松饼充电桩
     *
     * @param msg
     * @return
     */
    public static boolean isSbCharge(byte[] msg) {

        String data = BytesUtil.bytesToHex(msg);

        if (StringUtil.isEmpty(data)) {
            return false;
        }

        if (data.startsWith(SONG_BING_HEAD)) {
            return true;
        }

        return false;
    }


    /**
     * 判断是否为驴充充
     *
     * @param msg
     * @return
     */
    public static boolean isLvChongChong(byte[] msg) {

        String data = BytesUtil.bytesToHex(msg);

        if (StringUtil.isEmpty(data)) {
            return false;
        }

        if (data.startsWith(LV_CHONG_CHONG_HEAD)) {
            return true;
        }

        return false;
    }

    /**
     * 是否为员工工牌
     * @param msg
     * @return
     */
    public static boolean isWorkLicense(byte[] msg){
        String data = BytesUtil.bytesToHex(msg);

        if (StringUtil.isEmpty(data)) {
            return false;
        }

        if (data.startsWith("7e") && data.endsWith("7e")) {
            return true;
        }

        return false;

    }

    /**
     * 获取报文长度
     *
     * @param msg
     * @return
     */
    public static int getLvChongChongLength(byte[] msg) {
        String data = BytesUtil.bytesToHex(msg);
        data = data.substring(LV_CHONG_CHONG_HEAD.length(), LV_CHONG_CHONG_HEAD.length() + 4);
        return Integer.parseInt(data, 16);
    }

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
    public static String getSbMachineCode(byte[] msg) {
        String data = BytesUtil.bytesToHex(msg);
        try {
            data = data.substring(30, 40);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return data;
    }

    public static String getWorkLicenseMachineCode(byte[] msg) {
        String data = BytesUtil.bytesToHex(msg);
        data = data.substring(10, 10 + 12);
        return data.replaceAll("^0+", "");
    }

    public static byte[] clearWorkLicense(byte[] bytes) {
        String data = BytesUtil.bytesToHex(bytes);
        data = data.replaceAll("7d01","7d");
        data = data.replaceAll("7d02","7e");
        return BytesUtil.hexStringToByteArray(data);
    }
}
