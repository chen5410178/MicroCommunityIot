package com.java110.hal.nettty;

import com.java110.core.utils.BytesUtil;
import com.java110.core.utils.StringUtil;

import java.io.UnsupportedEncodingException;

public class DataHeader {

    public static final String LV_CHONG_CHONG_HEAD = "7e5d7d7f";// 驴充充 针头

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
}
