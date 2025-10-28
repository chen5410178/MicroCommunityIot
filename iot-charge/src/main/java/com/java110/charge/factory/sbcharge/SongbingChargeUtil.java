package com.java110.charge.factory.sbcharge;

import com.java110.core.utils.BytesUtil;

import java.util.LinkedHashMap;
import java.util.Map;

public class SongbingChargeUtil {

    public static final String SB_CHARGE_HEAD_6800 = "6800";


    public static final String KEY_0A0103 = "0A0103";//  数据键层

    public static final String SB_VERSION = "0001"; // 协议版本号

    public static final String CMD_REGISTER = "02"; // todo 注册
    public static final String CMD_HEARTBEAT_AND_CHANGE_PORT = "07"; // todo 心跳 && 插座状态改变
    public static final String CMD_SERVER_CONTROL = "05"; // todo 服务器控制  开启或者关闭


    public static final String CMD_SWIPE = "11";
    public static final String CMD_CHARGE_END = "0010"; // todo 充电结束
    public static final String CMD_UPLOAD_CHARGE_DATA = "0030"; // todo 充电过程中 上报充电功率
    public static final String CMD_RESTART = "0017"; // todo 成功设备

    /**
     * 解析收到的数据
     *
     * @param buf
     * @return
     */
    public static Map<String, String> getDataItems(byte[] buf) {
        Map<String, String> decodedMap = new LinkedHashMap<>();
        String original = CodecUtil.bytesToHex(buf);
        original = original.substring(8);
        original = original.substring(0, original.length() - 4);
        UnpackBKVResult result = BKV.unpack(CodecUtil.hexToBytes(original));
        BKV bkv = result.getBKV();
        if (bkv.getItems().size() == 3) {
            UnpackBKVResult result2 = BKV.unpack(CodecUtil.hexToBytes(CodecUtil.bytesToHex(bkv.getItems().get(2).getValue())));
            BKV bkv2 = result2.getBKV();
            for (KV kv1 : bkv2.getItems()) {
                bkv.getItems().add(kv1);
            }
        }
        int i = 1;
        for (KV kv1 : bkv.getItems()) {
            decodedMap.put("key" + i, String.format("%02X", Integer.parseInt(Integer.toHexString(kv1.getNumberKey().intValue()), 16)) + ":" + CodecUtil.bytesToHex(kv1.getValue()));
            i++;
        }
        return decodedMap;
    }

    /**
     * 16进制转ASCII，过滤非字母数字，不符合规则返回空
     *
     * @param original
     * @return
     */
    public static String convertHexToASCII(String original) {
        StringBuilder converted = new StringBuilder();
        if (original.length() % 2 == 0) {
            for (int i = 0; i < original.length(); i += 2) {
                String hex = original.substring(i, i + 2);
                int decimal = Integer.parseInt(hex, 16);
                char ascii = (char) decimal;
                if (Character.isLetterOrDigit(ascii)) {
                    converted.append(ascii);
                }
            }
        }
        return converted.toString();
    }


    /**
     * 收到的信息解密，报文收
     *
     * @param msg
     * @return
     */
    public static Map<String, String> decodeMessage(byte[] msg) {
        String input = BytesUtil.bytesToHex(msg);
        int j = 0;
        int index = 0;
        Map<String, String> decodedMap = new LinkedHashMap<>();
        decodedMap.put("key" + j, input.substring(index, index + 8));//帧头，特殊处理下
        index += 8;
        j++;
        while (index < input.length() - 4) {
            if (j == 3) {//数据层键，特殊处理下
                decodedMap.put("key" + j, input.substring(index, index + 6));
                index += 6;
                j++;
            }
            String key = input.substring(index, index + 2);
            index += 2;
            int valueLength = Integer.parseInt(key, 16) * 2;
            String value = input.substring(index, index + valueLength);
            index += valueLength;
            decodedMap.put("key" + j, key + value);
            if (j == 11) {
                j++;
                decodedMap.put("key" + j, convertHexToASCII(value));
            }
            j++;
        }
        return decodedMap;
    }

    //登录注册应答
    public static String registerResponseDate(String machineCode) {
        String dataBody = "0701010100000000" + machineCode + KEY_0A0103 + "03010402" + "03010500";
        String calculateBit = calculateBitGroupSum(dataBody);
        return SB_CHARGE_HEAD_6800 + calculateFrameHeaderLength(dataBody) + "68" + dataBody + calculateBit + "16";
    }

    //心跳应答
    public static String heartBeatResponse(String machineCode) {
        String dataBody = "0701010100000000" + machineCode + KEY_0A0103 + "03010407" + "03010500";
        String calculateBit = calculateBitGroupSum(dataBody);
        return SB_CHARGE_HEAD_6800 + calculateFrameHeaderLength(dataBody) + "68" + dataBody + calculateBit + "16";
    }


    public static String startCharge(String machineCode, String portHex, String chargeType, String chargeTime) {
        String dataBody = "0701010100000000070102" + machineCode + "130103" + "03010405" + "0301" + portHex + "01030159" + chargeType + "04015A" + chargeTime;
        String calculateBit = calculateBitGroupSum(dataBody);
        return SB_CHARGE_HEAD_6800 + calculateFrameHeaderLength(dataBody) + "68" + dataBody + calculateBit + "16";
    }

    //刷卡应答
    public static String swiperResponseDate(String machineCode,String errcode) {
        String dataBody = "0701010100000000070102" + machineCode + "120103" + "03010411" + "030105"+errcode+"07012101000003E8";
        String calculateBit = calculateBitGroupSum(dataBody);
        return SB_CHARGE_HEAD_6800 + calculateFrameHeaderLength(dataBody) + "68" + dataBody + calculateBit + "16";
    }


    public static String stopCharge(String machineCode,String portHex) {
        String dataBody = "0701010100000000070102" + machineCode + "0A0103" + "03010405" + "0301" + portHex + "00";
        String calculateBit = calculateBitGroupSum(dataBody);
        return SB_CHARGE_HEAD_6800 + calculateFrameHeaderLength(dataBody) + "68" + dataBody + calculateBit + "16";
    }


    public static String stateChangeResponse(String machineCode) {
        String dataBody = "0701010100000000070102" + machineCode + "0A0103" + "03010407" + "03010500";
        String calculateBit = calculateBitGroupSum(dataBody);
        return SB_CHARGE_HEAD_6800 + calculateFrameHeaderLength(dataBody) + "68" + dataBody + calculateBit + "16";
    }


    public static String restartMachine(String machineCode) {
        String dataBody = "0701010100000000070102" + machineCode + "0A0103" + "03010405" + "03018001";
        String calculateBit = calculateBitGroupSum(dataBody);
        return SB_CHARGE_HEAD_6800 + calculateFrameHeaderLength(dataBody) + "68" + dataBody + calculateBit + "16";
    }

    //校验位计算
    private static String calculateBitGroupSum(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        int sum = 0;
        for (byte b : data) {
            int val = b & 0xFF; // 确保为无符号整数
            sum += val;
        }
        String hex = Integer.toHexString(sum).toUpperCase(); // 将十进制数转换为十六进制字符串
        String result = hex.substring(hex.length() - 2); // 获取十六进制字符串的最后两位
        return result;
    }

    //计算帧头部分长度
    private static String calculateFrameHeaderLength(String s) {
        int len = s.length();
        String hex = Integer.toHexString(len / 2).toUpperCase();
        return hex;
    }
}
