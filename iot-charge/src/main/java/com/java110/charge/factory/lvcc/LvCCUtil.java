package com.java110.charge.factory.lvcc;

import com.java110.core.utils.BytesUtil;

public class LvCCUtil {

    public static final String LV_CHONG_CHONG_HEAD = "7e5d7d7f";// 驴充充 针头


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
        data = data.substring(startPos, data.length()-2);

        return data;
    }
}
