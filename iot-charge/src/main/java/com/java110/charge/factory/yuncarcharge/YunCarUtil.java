package com.java110.charge.factory.yuncarcharge;

import com.java110.core.utils.BytesUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.chargePeakAlleyPrice.ChargePeakAlleyPriceDto;
import com.java110.dto.chargeRulePrice.ChargeRulePriceDto;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class YunCarUtil {

    public static final String YUN_KUAI_CHONG_HEAD = "68";//帧头
    public static final String CMD_REGISTER = "01"; // todo 注册
    public static final String CMD_HEARTBEAT = "03"; // todo 充电桩心跳包
    public static final String CMD_BILLING_MODEL_VALIDATION = "05"; // todo 计费模型验证请求
    public static final String CMD_BILLING_MODEL_REQUEST = "09"; // todo 充电桩计费模型请求
    public static final String CMD_REAL_MONITORING_DATA = "13";
    public static final String CMD_START_CHARGING_COMMAND_REPLY = "33";
    public static final String CMD_CHARGING_HANDSHAKE = "15";//充电握手
    public static final String CMD_CHARGING_PROCESS_INFORMATION = "25";//充电过程BMS信息
    public static final String CMD_STOP_CHARGE_REPLY = "35";//远程停机命令回复
    public static final String CMD_STOP_CHARGE_UPLOAD = "19";//充电结束
    public static final String CMD_TRANSACTION_RECORD_UPLOAD = "3B";//交易记录


    /**
     * 获取指令
     *
     * @param msg
     * @return
     */
    public static String getCmd(byte[] msg) {
        String data = BytesUtil.bytesToHex(msg);
        data = data.substring(10, 12);
        return data;
    }

    /**
     * 获取序列号阈
     *
     * @param msg
     * @return
     */
    public static String getSerialNumber(byte[] msg) {
        String data = BytesUtil.bytesToHex(msg);
        data = data.substring(4, 8);
        return data;
    }

    /**
     * 获取开启充电结果
     *
     * @param msg
     * @return
     */
    public static String getStartChargeState(byte[] msg) {
        String data = BytesUtil.bytesToHex(msg);
        String cmd = data.substring(10, 12);
        int num1 = Integer.parseInt(cmd, 16);
        if (num1 >= 19) {
            data = data.substring(60, 62);
        } else {
            data = "";
        }
        return data;
    }

    /**
     * 获取开启充电结果原因
     *
     * @param msg
     * @return
     */
    public static String getStartChargeStateReason(byte[] msg) {
        String data = BytesUtil.bytesToHex(msg);
        String cmd = data.substring(10, 12);
        int num1 = Integer.parseInt(cmd, 16);
        if (num1 >= 19) {
            data = data.substring(62, 64);
        } else {
            data = "";
        }
        return data;
    }

    /**
     * 获取枪标号
     *
     * @param msg
     * @return
     */
    public static String getGunNumber(byte[] msg) {
        String data = BytesUtil.bytesToHex(msg);
        String cmd = data.substring(10, 12);
        int num1 = Integer.parseInt(cmd, 16);
        if (num1 >= 19) {
            data = data.substring(58, 60);
        } else {
            data = data.substring(26, 28);
        }
        return data;
    }

    /**
     * 获取枪状态
     *
     * @param msg
     * @return
     */
    public static String getGunState(byte[] msg) {
        String data = BytesUtil.bytesToHex(msg);
        data = data.substring(28, 30);
        return data;
    }

    /**
     * 获取计费模型编号
     *
     * @param msg
     * @return
     */
    public static String getBillingmodelNo(byte[] msg) {
        String data = BytesUtil.bytesToHex(msg);
        data = data.substring(26, 30);
        return data;
    }

    /**
     * 获取交易流水号
     *
     * @param msg
     * @return
     */
    public static String getTradeNo(byte[] msg) {
        String data = BytesUtil.bytesToHex(msg);
        String cmd = data.substring(10, 12);
        int num1 = Integer.parseInt(cmd, 16);
        if (num1 >= 19) {
            data = data.substring(12, 44);
        } else {
            data = "";
        }
        return data;
    }

    /**
     * 获取完成交易的总金额
     *
     * @param msg
     * @return
     */
    public static String getAmount(byte[] msg) {
        String data = BytesUtil.bytesToHex(msg);
        data = data.substring(252, 260);
        return data;
    }

    /**
     * 获取完成交易的总电量
     *
     * @param msg
     * @return
     */
    public static String getTotalEnergy(byte[] msg) {
        String data = BytesUtil.bytesToHex(msg);
        data = data.substring(236, 244);
        return data;
    }

    /**
     * 获取充电完成时间
     *
     * @param msg
     * @return
     */
    public static String getChargeEndTime(byte[] msg) {
        String data = BytesUtil.bytesToHex(msg);
        data = data.substring(86, 100);
        return data;
    }


    /**
     * 获取结束时间
     *
     * @param CP56Time2a
     * @return
     */
    public static String getYYYYMMDD(String CP56Time2a) {
        String reversedHexString = reverseHexEndian(CP56Time2a);
        byte[] timeBytes = BytesUtil.hexStringToByteArray(reversedHexString);
        System.out.println(((timeBytes[5] & 0xFF) << 8) | (timeBytes[6] & 0xFF));
        int year = (timeBytes[0] & 0x3F) + 2000; // 高 8 位为年份的最后两位
        int month = (timeBytes[1] & 0x0F); // 月份
        int day = timeBytes[2] & 0x1F; // 日
        int hour = (timeBytes[3] & 0x1F); // 小时
        int minute = (timeBytes[4] & 0x3F); // 分钟
        int second = (((timeBytes[5] & 0xFF) << 8) | (timeBytes[6] & 0xFF)) / 1000; // 毫秒部分
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, hour, minute, second);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = sdf.format(calendar.getTime());
        return formattedDate;
    }

    //    68(起始标志) 5E(数据长度) 02 00(序列号域) 00( 加密标志) 0A(帧类型标志) 55 03 14 12 78
//            23 05（桩编码） 01 00（计费模型编号） 40 0D 03 00（尖电费费率）9C 40 00 00（尖服务
//    费费率） E0 93 04 00（峰电费费率）9C 40 00 00（峰服务费费率）80 1A 06 00（平电费费
//    率）9C 40 00 00（平服务费费率）20 A1 07 00（谷电费费率）9C 40 00 00（谷服务费费率）
//            00（计损比例） 00（时段费率号 48个）00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
//            00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
//            00 00 00 5E 60（帧校验域）
    public static String machineBillingmodelRequestRespon(String serialNumber, String machineCode, List<ChargeRulePriceDto> chargeRulePriceDtos, List<ChargePeakAlleyPriceDto> chargePeakAlleyPriceDtos) {
        String dataContent = serialNumber + "00" + "0A" + machineCode + "0100";//TODO 费率换算 一共八个要加上

        dataContent += cryptocurrencyAmount(chargePeakAlleyPriceDtos.get(0).getPrice(), 5);
        dataContent += cryptocurrencyAmount(chargePeakAlleyPriceDtos.get(0).getServicePrice(), 5);
        dataContent += cryptocurrencyAmount(chargePeakAlleyPriceDtos.get(1).getPrice(), 5);
        dataContent += cryptocurrencyAmount(chargePeakAlleyPriceDtos.get(1).getServicePrice(), 5);
        dataContent += cryptocurrencyAmount(chargePeakAlleyPriceDtos.get(2).getPrice(), 5);
        dataContent += cryptocurrencyAmount(chargePeakAlleyPriceDtos.get(2).getServicePrice(), 5);
        dataContent += cryptocurrencyAmount(chargePeakAlleyPriceDtos.get(3).getPrice(), 5);
        dataContent += cryptocurrencyAmount(chargePeakAlleyPriceDtos.get(3).getServicePrice(), 5);

        dataContent = dataContent + "00";
        List<ChargeRulePriceDto> chargeRulePrices = assembleMachinePrice(chargeRulePriceDtos, chargePeakAlleyPriceDtos);
        for (int j = 0; j < chargeRulePrices.size(); j++) {
            ChargeRulePriceDto _tmpPrice = chargeRulePrices.get(j);
            dataContent = dataContent + _tmpPrice.getCpaType();
        }
        String dataLength = calculateLength(dataContent);
        byte[] byteArray = BytesUtil.hexStringToByteArray(dataContent);
        String CRCCode = BytesUtil.bytesToHex(YunCrcUtil.calculateCrc(byteArray));
        return YUN_KUAI_CHONG_HEAD + dataLength + dataContent + CRCCode;
    }


    public static String cryptocurrencyAmount(String price, int n) {
        BigDecimal priceValue = new BigDecimal(price);
        BigDecimal multiplier = BigDecimal.TEN.pow(n);
        BigDecimal result = priceValue.multiply(multiplier);
        String priceHex = String.format("%08x", result.intValue());
        priceHex = reverseHexEndian(priceHex);
        return priceHex;
    }

    public static String priceHexToPrice(String priceHex, int n) {
        priceHex = reverseHexEndian(priceHex);
        int a = Integer.parseInt(priceHex, 16);
        BigDecimal priceValue = new BigDecimal(a);
        BigDecimal divisor = BigDecimal.TEN.pow(n);
        BigDecimal result = priceValue.divide(divisor, n, RoundingMode.HALF_UP);
        return result.toString();
    }

    // 按小端格式反转字节顺序
    public static String reverseHexEndian(String hex) {
        // 每两个字符代表一个字节，将其反转
        StringBuilder reversedHex = new StringBuilder();
        for (int i = hex.length(); i > 0; i -= 2) {
            reversedHex.append(hex, i - 2, i);
        }
        return reversedHex.toString();
    }


    private static List<ChargeRulePriceDto> assembleMachinePrice(List<ChargeRulePriceDto> chargeRulePriceDtos, List<ChargePeakAlleyPriceDto> chargePeakAlleyPriceDtos) {
        List<ChargeRulePriceDto> chargeRulePrices = new ArrayList<ChargeRulePriceDto>();
        ChargeRulePriceDto chargeRulePrice;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime time = LocalTime.of(0, 0); // 从00:00开始
        for (int i = 0; i < 48; i++) { //组装48个时段的值，用开始时间比较
            chargeRulePrice = new ChargeRulePriceDto();
            chargeRulePrice.setStartTime(time.format(formatter));
            for (int j = 0; j < chargeRulePriceDtos.size(); j++) {
                ChargeRulePriceDto _tmpPrice = chargeRulePriceDtos.get(j);
                if (chargeRulePrice.getStartTime().equals(_tmpPrice.getStartTime())) {//开始时间匹配上
                    chargeRulePrice.setCpaType(_tmpPrice.getCpaType());
                }
            }
            if (StringUtil.isEmpty(chargeRulePrice.getCpaType())) {
                chargeRulePrice.setCpaType(chargePeakAlleyPriceDtos.get(2).getType());//默认给平费率
            }
            time = time.plusMinutes(30);
            chargeRulePrices.add(chargeRulePrice);
        }
        return chargeRulePrices;
    }


    //    68（起始标志）12（数据长度）00DF（序列号域）00（加密标志）56（类型）55031412782305
//            （桩编码）98B70E11100314（当前时间：2020-03-16 17:14:47）8A13（帧校验域）
    public static String calibrationTime(String machineCode) {
        String dataContent = "CDEF" + "00" + "56" + machineCode + dateToCP56Time2a();
        String dataLength = calculateLength(dataContent);
        byte[] byteArray = BytesUtil.hexStringToByteArray(dataContent);
        String CRCCode = BytesUtil.bytesToHex(YunCrcUtil.calculateCrc(byteArray));
        return YUN_KUAI_CHONG_HEAD + dataLength + dataContent + CRCCode;
    }

    public static String sendQrCode(String machineCode, String url) {
        String urlASCII = getUrlASCII(url);
        String dataContent = "00CE" + "00" + "F0" + machineCode + "01" + calculateLength(urlASCII) + urlASCII;
        String dataLength = calculateLength(dataContent);
        byte[] byteArray = BytesUtil.hexStringToByteArray(dataContent);
        String CRCCode = BytesUtil.bytesToHex(YunCrcUtil.calculateCrc(byteArray));
        return YUN_KUAI_CHONG_HEAD + dataLength + dataContent + CRCCode;
    }


    private static String getUrlASCII(String url) {
        String urlHex = "";
        for (int i = 0; i < url.length(); i++) {
            char ch = url.charAt(i);  // 获取字符串中的字符
            int ascii = (int) ch;  // 转换字符为 ASCII 码
            String hex = Integer.toHexString(ascii); // 转换为十六进制
            urlHex += hex;
        }
        return urlHex;
    }


    public static String dateToCP56Time2a() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR) % 100; // 取年份的后两位
        int month = calendar.get(Calendar.MONTH) + 1; // 月份从0开始
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND) * 1000;
        byte[] cp56Time2a = new byte[7];
        cp56Time2a[0] = (byte) (year & 0x3F);
        cp56Time2a[1] = (byte) ((month & 0x0F));
        cp56Time2a[2] = (byte) ((day & 0x1F));
        cp56Time2a[3] = (byte) ((hour & 0x1F));
        cp56Time2a[4] = (byte) ((minute & 0x3F));
        cp56Time2a[5] = (byte) (((second >> 8) & 0xFF));
        ;
        cp56Time2a[6] = (byte) (second);
        String reversedHexString = reverseHexEndian(BytesUtil.bytesToHex(cp56Time2a));
        return reversedHexString;
    }


    public static String machineBillingmodelRespon(String serialNumber, String machineCode, String billModeNo) {
        String verifyResult = "01";
//        String verifyResult = "00";
//        if (! billModeNo.equals("0100")){//0100 平台下发的固定值
//            verifyResult = "01";
//        }
        String dataContent = serialNumber + "00" + "06" + machineCode + "0100" + verifyResult;
        String dataLength = calculateLength(dataContent);
        byte[] byteArray = BytesUtil.hexStringToByteArray(dataContent);
        String CRCCode = BytesUtil.bytesToHex(YunCrcUtil.calculateCrc(byteArray));
        return YUN_KUAI_CHONG_HEAD + dataLength + dataContent + CRCCode;
    }

    //68（起始标志）0D（数据长度）3600（序列号域）00（加密标志）（类型）55031412782305桩编码）01(枪号) 00（心跳应答）65B2（帧校验域）
    public static String machineHeartbeatRespon(String serialNumber, String machineCode, String gunNo) {
        String dataContent = serialNumber + "00" + "04" + machineCode + gunNo + "00";
        String dataLength = calculateLength(dataContent);
        byte[] byteArray = BytesUtil.hexStringToByteArray(dataContent);
        String CRCCode = BytesUtil.bytesToHex(YunCrcUtil.calculateCrc(byteArray));
        return YUN_KUAI_CHONG_HEAD + dataLength + dataContent + CRCCode;
    }


    //        68（起始标志）15（数据长度）0002（序列号域）00（加密标志）40（类型）
//            55031412782305012018061910262392（交易流水号）00（确认结果）48B1（帧校验域）
    public static String confirmTransactionRespon(String serialNumber, String tradeNo) {
        String dataContent = serialNumber + "00" + "40" + tradeNo + "00";
        String dataLength = calculateLength(dataContent);
        byte[] byteArray = BytesUtil.hexStringToByteArray(dataContent);
        String CRCCode = BytesUtil.bytesToHex(YunCrcUtil.calculateCrc(byteArray));
        return YUN_KUAI_CHONG_HEAD + dataLength + dataContent + CRCCode;
    }

    public static String machineRegisterRespon(String machineCode) {
        String dataContent = "0000" + "00" + "02" + machineCode + "00";
        String dataLength = calculateLength(dataContent);
        byte[] byteArray = BytesUtil.hexStringToByteArray(dataContent);
        String CRCCode = BytesUtil.bytesToHex(YunCrcUtil.calculateCrc(byteArray));
        return YUN_KUAI_CHONG_HEAD + dataLength + dataContent + CRCCode;
    }

    //    68（起始标志）0C（数据长度）0011（序列号域）00（加密标志）92（类型）32010200000001
//            （桩编码）01（执行控制）C1A9（帧校验域）
    public static String restartMachine(String machineCode) {
        String dataContent = "0011" + "00" + "92" + machineCode + "01";
        String dataLength = calculateLength(dataContent);
        byte[] byteArray = BytesUtil.hexStringToByteArray(dataContent);
        String CRCCode = BytesUtil.bytesToHex(YunCrcUtil.calculateCrc(byteArray));
        return YUN_KUAI_CHONG_HEAD + dataLength + dataContent + CRCCode;
    }

    private static String calculateLength(String s) {
        String machineCodeLen = String.format("%02x", s.length() / 2);
        return machineCodeLen;
    }


//    68（起始标志）30（数据长度）007C（序列号域）00（加密标志）34（类型）
//            55031412782305012018061914444680（交易流水号）55031412782305（桩编码）01（枪号：1
//    枪）0000001000000573（逻辑卡号：1000000573）00000000D14B0A54（物理卡号：D14B0A54）
//    A0860100（账户余额：1000.00）4622

    public static String startCharge(String serialNumber, String machineCode, String orderNo, String gunNo, String logicalCardNum, String physicalCardNum, String money) {
        money = cryptocurrencyAmount(money, 2);
        String dataContent = serialNumber + "00" + "34" + orderNo + machineCode + gunNo + logicalCardNum + physicalCardNum + money;
        String dataLength = calculateLength(dataContent);
        byte[] byteArray = BytesUtil.hexStringToByteArray(dataContent);
        String CRCCode = BytesUtil.bytesToHex(YunCrcUtil.calculateCrc(byteArray));
        return YUN_KUAI_CHONG_HEAD + dataLength + dataContent + CRCCode;
    }

    //68（起始标志）0C（数据长度）0003（序列号域）00（加密标志）36（类型）32010200000001
    //            （桩编码）01（枪号：1枪）C1A9（帧校验域）
    public static String stopCharge(String serialNumber, String machineCode, String gunNo) {
        String dataContent = serialNumber + "00" + "36" + machineCode + gunNo;
        String dataLength = calculateLength(dataContent);
        byte[] byteArray = BytesUtil.hexStringToByteArray(dataContent);
        String CRCCode = BytesUtil.bytesToHex(YunCrcUtil.calculateCrc(byteArray));
        return YUN_KUAI_CHONG_HEAD + dataLength + dataContent + CRCCode;
    }

    public static String generatorMsgId() {

        Random random = new Random();
        int msgId = random.nextInt(256);

        return String.format("%04x", msgId);
    }


    /**
     * 查询自定位置字符串
     *
     * @param data
     * @param startPos 开始位置
     * @param dataLen  字段长度
     * @return
     */
    public static String getTargetString(byte[] data, int startPos, int dataLen) {
        String dataStr = BytesUtil.bytesToHex(data);
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

    /**
     *
     * @param data
     * @param startPos
     * @param dataLen
     * @return
     */
    public static String getTargetBin(byte[] data, int startPos, int dataLen) {
        String dataStr = BytesUtil.bytesToHex(data);
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
}
