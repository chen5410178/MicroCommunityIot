package com.java110.accessControl.manufactor.adapt.accessControl.wiegand;

import com.java110.core.utils.BytesUtil;
import com.java110.core.utils.StringUtil;

/**
 * 微耕工具类
 */
public class WiegandUtil {
    /**
     * 创建发送数据
     *
     * @param cmd
     * @param machineCode
     * @param data
     * @return
     */
    public static String createSendData(String cmd, String machineCode, String data) {

        String sendData = "";
        sendData += "17";// type
        sendData += cmd;// 功能号
        sendData += "0000";// 保留
        int machineCodeInt = Integer.parseInt(machineCode); // 要转换的十进制数
        String machineCodeHex = Integer.toHexString(machineCodeInt);
        sendData += machineCodeHex;// 设备序列号
        sendData += data;

        return padRight(sendData, 64 * 2, '0');
    }

    public static WiegandDataDto parseData(String data) {
        int typePos = 0; // 类型
        int typeLen = 1;
        int cmdPos = typePos + typeLen * 2; // 功能号
        int cmdLen = 1;
        int reservedPos = cmdPos + cmdLen * 2; // 保留
        int reservedLen = 2;
        int machineCodePos = reservedPos + reservedLen * 2; // 设备编号
        int machineCodeLen = 4;
        int dataPos = machineCodePos + machineCodeLen * 2;
        int dataLen = 32;
        int sequenceIdPos = dataPos + dataLen * 2;
        int sequenceIdLen = 4;

        String type = getTargetString(data, typePos, typeLen * 2);
        String cmd = getTargetString(data, cmdPos, cmdLen * 2);
        String machineCode = getTargetIntString(data, machineCodePos, machineCodeLen * 2);
        String businessData = getTargetString(data, dataPos, dataLen * 2);
        String sequenceId = getTargetString(data, sequenceIdPos, sequenceIdLen * 2);
        WiegandDataDto wiegandDataDto = new WiegandDataDto();
        wiegandDataDto.setCmd(cmd);
        wiegandDataDto.setMachineCode(machineCode);
        wiegandDataDto.setType(type);
        wiegandDataDto.setSequenceId(sequenceId);
        wiegandDataDto.setBusinessData(businessData);
        return wiegandDataDto;

    }

    public static String getTargetString(String dataStr, int startPos, int dataLen) {
        String targetValue = "";
        if (StringUtil.isEmpty(dataStr)) {
            return targetValue;
        }
        if (dataStr.length() < startPos + dataLen) {
            return targetValue;
        }
        targetValue = dataStr.substring(startPos, startPos + dataLen);
        return targetValue;

    }

    public static String getTargetIntString(String dataStr, int startPos, int dataLen) {
        String targetValue = "";
        if (StringUtil.isEmpty(dataStr)) {
            return targetValue;
        }
        if (dataStr.length() < startPos + dataLen) {
            return targetValue;
        }
        targetValue = dataStr.substring(startPos, startPos + dataLen);
        return Integer.parseInt(targetValue, 16) + "";

    }

    public static String padRight(String input, int length, char padChar) {
        if (input.length() >= length) {
            return input; // 如果字符串已经足够长，直接返回
        }

        StringBuilder sb = new StringBuilder(input);
        while (sb.length() < length) {
            sb.append(padChar); // 补充字符
        }
        return sb.toString();
    }

    // 方法：截取手机号后六位
    public static String getLastSixDigits(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 6) {
            return "000000";
        }
        return phoneNumber.substring(phoneNumber.length() - 6);
    }


}
