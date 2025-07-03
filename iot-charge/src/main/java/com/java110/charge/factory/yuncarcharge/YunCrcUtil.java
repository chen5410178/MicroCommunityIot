package com.java110.charge.factory.yuncarcharge;

import com.java110.core.utils.BytesUtil;
import com.java110.core.utils.ListUtil;
import com.java110.dto.chargeMachine.ChargeMachineOrderDto;
import com.java110.dto.chargePeakAlleyPrice.ChargePeakAlleyPriceDto;
import com.java110.dto.data.NettyReplyDataDto;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class YunCrcUtil {


    private YunCrcUtil() {

    }

    public static final byte[] gabyCRCHi = {
            (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40,
            (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41,
            (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41,
            (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40,
            (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41,
            (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40,
            (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40,
            (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41,
            (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41,
            (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40,
            (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40,
            (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41,
            (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40,
            (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41,
            (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41,
            (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40

    };

    public static final byte[] gabyCRCLo = {
            (byte) 0x00, (byte) 0xC0, (byte) 0xC1, (byte) 0x01, (byte) 0xC3, (byte) 0x03, (byte) 0x02, (byte) 0xC2, (byte) 0xC6, (byte) 0x06, (byte) 0x07, (byte) 0xC7, (byte) 0x05, (byte) 0xC5, (byte) 0xC4, (byte) 0x04,
            (byte) 0xCC, (byte) 0x0C, (byte) 0x0D, (byte) 0xCD, (byte) 0x0F, (byte) 0xCF, (byte) 0xCE, (byte) 0x0E, (byte) 0x0A, (byte) 0xCA, (byte) 0xCB, (byte) 0x0B, (byte) 0xC9, (byte) 0x09, (byte) 0x08, (byte) 0xC8,
            (byte) 0xD8, (byte) 0x18, (byte) 0x19, (byte) 0xD9, (byte) 0x1B, (byte) 0xDB, (byte) 0xDA, (byte) 0x1A, (byte) 0x1E, (byte) 0xDE, (byte) 0xDF, (byte) 0x1F, (byte) 0xDD, (byte) 0x1D, (byte) 0x1C, (byte) 0xDC,
            (byte) 0x14, (byte) 0xD4, (byte) 0xD5, (byte) 0x15, (byte) 0xD7, (byte) 0x17, (byte) 0x16, (byte) 0xD6, (byte) 0xD2, (byte) 0x12, (byte) 0x13, (byte) 0xD3, (byte) 0x11, (byte) 0xD1, (byte) 0xD0, (byte) 0x10,
            (byte) 0xF0, (byte) 0x30, (byte) 0x31, (byte) 0xF1, (byte) 0x33, (byte) 0xF3, (byte) 0xF2, (byte) 0x32, (byte) 0x36, (byte) 0xF6, (byte) 0xF7, (byte) 0x37, (byte) 0xF5, (byte) 0x35, (byte) 0x34, (byte) 0xF4,
            (byte) 0x3C, (byte) 0xFC, (byte) 0xFD, (byte) 0x3D, (byte) 0xFF, (byte) 0x3F, (byte) 0x3E, (byte) 0xFE, (byte) 0xFA, (byte) 0x3A, (byte) 0x3B, (byte) 0xFB, (byte) 0x39, (byte) 0xF9, (byte) 0xF8, (byte) 0x38,
            (byte) 0x28, (byte) 0xE8, (byte) 0xE9, (byte) 0x29, (byte) 0xEB, (byte) 0x2B, (byte) 0x2A, (byte) 0xEA, (byte) 0xEE, (byte) 0x2E, (byte) 0x2F, (byte) 0xEF, (byte) 0x2D, (byte) 0xED, (byte) 0xEC, (byte) 0x2C,
            (byte) 0xE4, (byte) 0x24, (byte) 0x25, (byte) 0xE5, (byte) 0x27, (byte) 0xE7, (byte) 0xE6, (byte) 0x26, (byte) 0x22, (byte) 0xE2, (byte) 0xE3, (byte) 0x23, (byte) 0xE1, (byte) 0x21, (byte) 0x20, (byte) 0xE0,
            (byte) 0xA0, (byte) 0x60, (byte) 0x61, (byte) 0xA1, (byte) 0x63, (byte) 0xA3, (byte) 0xA2, (byte) 0x62, (byte) 0x66, (byte) 0xA6, (byte) 0xA7, (byte) 0x67, (byte) 0xA5, (byte) 0x65, (byte) 0x64, (byte) 0xA4,
            (byte) 0x6C, (byte) 0xAC, (byte) 0xAD, (byte) 0x6D, (byte) 0xAF, (byte) 0x6F, (byte) 0x6E, (byte) 0xAE, (byte) 0xAA, (byte) 0x6A, (byte) 0x6B, (byte) 0xAB, (byte) 0x69, (byte) 0xA9, (byte) 0xA8, (byte) 0x68,
            (byte) 0x78, (byte) 0xB8, (byte) 0xB9, (byte) 0x79, (byte) 0xBB, (byte) 0x7B, (byte) 0x7A, (byte) 0xBA, (byte) 0xBE, (byte) 0x7E, (byte) 0x7F, (byte) 0xBF, (byte) 0x7D, (byte) 0xBD, (byte) 0xBC, (byte) 0x7C,
            (byte) 0xB4, (byte) 0x74, (byte) 0x75, (byte) 0xB5, (byte) 0x77, (byte) 0xB7, (byte) 0xB6, (byte) 0x76, (byte) 0x72, (byte) 0xB2, (byte) 0xB3, (byte) 0x73, (byte) 0xB1, (byte) 0x71, (byte) 0x70, (byte) 0xB0,
            (byte) 0x50, (byte) 0x90, (byte) 0x91, (byte) 0x51, (byte) 0x93, (byte) 0x53, (byte) 0x52, (byte) 0x92, (byte) 0x96, (byte) 0x56, (byte) 0x57, (byte) 0x97, (byte) 0x55, (byte) 0x95, (byte) 0x94, (byte) 0x54,
            (byte) 0x9C, (byte) 0x5C, (byte) 0x5D, (byte) 0x9D, (byte) 0x5F, (byte) 0x9F, (byte) 0x9E, (byte) 0x5E, (byte) 0x5A, (byte) 0x9A, (byte) 0x9B, (byte) 0x5B, (byte) 0x99, (byte) 0x59, (byte) 0x58, (byte) 0x98,
            (byte) 0x88, (byte) 0x48, (byte) 0x49, (byte) 0x89, (byte) 0x4B, (byte) 0x8B, (byte) 0x8A, (byte) 0x4A, (byte) 0x4E, (byte) 0x8E, (byte) 0x8F, (byte) 0x4F, (byte) 0x8D, (byte) 0x4D, (byte) 0x4C, (byte) 0x8C,
            (byte) 0x44, (byte) 0x84, (byte) 0x85, (byte) 0x45, (byte) 0x87, (byte) 0x47, (byte) 0x46, (byte) 0x86, (byte) 0x82, (byte) 0x42, (byte) 0x43, (byte) 0x83, (byte) 0x41, (byte) 0x81, (byte) 0x80, (byte) 0x40

    };

//        public static byte[] calculateCrc(byte[] data) {
//        int crc;
//        int ucCRCHi = 0x00ff;
//        int ucCRCLo = 0x00ff;
//        int iIndex;
//        for (int i = 0; i < data.length; ++i) {
//            iIndex = (ucCRCLo ^ data[i]) & 0x00ff;
//            ucCRCLo = ucCRCHi ^ gabyCRCHi[iIndex];
//            ucCRCHi = gabyCRCLo[iIndex];
//        }
//        crc = ((ucCRCHi & 0x00ff) << 8) | (ucCRCLo & 0x00ff) & 0xffff;
//        crc = ((crc & 0xFF00) >> 8) | ((crc & 0x00FF) << 8);
//        byte[] bytes = toBytes(crc);
//        return Arrays.copyOfRange(bytes, 2, 4);
//    }
//
//    public static byte[] toBytes(int value) {
//        byte[] result = new byte[]{(byte) (value >> 24), (byte) (value >> 16), (byte) (value >> 8), (byte) value};
//        return result;
//    }
public static byte[] calculateCrcLH(byte[] data) {
    byte byCRCHi = (byte) 0xff;
    byte byCRCLo = (byte) 0xff;
    byte byIdx;
    int len = data.length;

    for (byte b : data) {
        byIdx = (byte) ((byCRCHi ^ b) & 0xFF);
        byCRCHi = (byte) ((byCRCLo ^ gabyCRCHi[byIdx & 0xFF]) & 0xFF);
        byCRCLo =(byte) (gabyCRCLo[byIdx & 0xFF]& 0xFF);
    }

    // 将 CRC 值拆分为两个字节并返回
    return new byte[] { byCRCLo,byCRCHi};
    //return new byte[] { byCRCHi,byCRCLo};
}
    public static byte[] calculateCrc(byte[] data) {
        byte byCRCHi = (byte) 0xff;
        byte byCRCLo = (byte) 0xff;
        byte byIdx;
        int len = data.length;

        for (byte b : data) {
            byIdx = (byte) ((byCRCHi ^ b) & 0xFF);
            byCRCHi = (byte) ((byCRCLo ^ gabyCRCHi[byIdx & 0xFF]) & 0xFF);
            byCRCLo =(byte) (gabyCRCLo[byIdx & 0xFF]& 0xFF);
        }

        // 将 CRC 值拆分为两个字节并返回
        //return new byte[] { byCRCLo,byCRCHi};
        return new byte[] { byCRCHi,byCRCLo};
    }



    public static void main(String[] args) {
        // 输入数据 指令：680d 00010005550314127823000001 5043
        // 6822 00000001662024110100030002105620332e33370000008986084210244255771504 0109
        // 680c 000000026620241101000300 4921
        // 680C 000000025503141278230500 DA4C -- 注册放回，能对上
        // 68（起始标志）0D（数据长度）0001（序列号域）00（加密标志）03（类型）32010200000001（桩编码）0x01（枪号：1 号枪）00（枪状态：正常）6890（帧校验域）
        // 680d 00050003662024110100030100 41d5
        // 680d 00050004662024110100030100 649b
        // 680D 36000004550314127823050100 65B2 -- 心跳 对不上
        // 680d 00120004662024110100030200 5f50
         String dataStr = "68a20069003b667129560256780220250412163910526671295602567802102727100c041988132b100c041920c5010000000000000000000000000040960100000000000000000000000000084c010038090000380900006c070000d0010100000000000000000000000000ba86230000f28f23000038090000380900006c0700004c47424839324530314c593834383537360188132b100c041942000000000000000017b9";
        // String dataStr = "68a2cf24003b320106007839920220250411075540893201060078399202709437070b0419b80b31080b041920c50100000000000000000000000000409601003632010036320100f83d0100084c0100127f0000127f00006c6b0000d0010100000000000000000000000000fe62300000461432000048b1010048b1010064a90100000000000000000000000000000000000001b80b31080b041942000000000000000035b3";
        byte[] data =  BytesUtil.hexStringToByteArray(dataStr);


        int prexPos = 0; // 起始标志
        int prexLen = 12;
        int transId = prexPos + prexLen; // 交易流水号
        int transLen = 32;
        int machineCodePos = transId + transLen;
        int machineCodeLen = 14;
        int portCodePos = machineCodePos + machineCodeLen; // 桩编号
        int portCodeLen = 2;
        int startTimePos = portCodePos + portCodeLen;
        int startTimeLen = 14;
        int endTimePos = startTimePos + startTimeLen;
        int endTimeLen = 14;
        int jianPricePos = endTimePos + endTimeLen; //尖单价
        int jianPriceLen = 8;
        int jianEnergyPos = jianPricePos + jianPriceLen;
        int jianEnergyLen = 8;
        int jisunjianEnergyPos = jianEnergyPos + jianEnergyLen;
        int jisunjianEnergyLen = 8;
        int jianAmountPos = jisunjianEnergyPos + jisunjianEnergyLen; // 尖金额
        int jianAmountLen = 8;
        int fenPricePos = jianAmountPos + jianAmountLen; //峰单价
        int fenPriceLen = 8;
        int fenEnergyPos = fenPricePos + fenPriceLen;
        int fenEnergyLen = 8;
        int jisunfenEnergyPos = fenEnergyPos + fenEnergyLen;
        int jisunfenEnergyLen = 8;
        int fenAmountPos = jisunfenEnergyPos + jisunfenEnergyLen; // 尖金额
        int fenAmountLen = 8;
        int pingPricePos = fenAmountPos + fenAmountLen; //峰单价
        int pingPriceLen = 8;
        int pingEnergyPos = pingPricePos + pingPriceLen;
        int pingEnergyLen = 8;
        int jisunpingEnergyPos = pingEnergyPos + pingEnergyLen;
        int jisunpingEnergyLen = 8;
        int pingAmountPos = jisunpingEnergyPos + jisunpingEnergyLen; // 平金额
        int pingAmountLen = 8;
        int guPricePos = pingAmountPos + pingAmountLen; //峰单价
        int guPriceLen = 8;
        int guEnergyPos = guPricePos + guPriceLen;
        int guEnergyLen = 8;
        int jisunguEnergyPos = guEnergyPos + guEnergyLen;
        int jisunguEnergyLen = 8;
        int guAmountPos = jisunguEnergyPos + jisunguEnergyLen; // 平金额
        int guAmountLen = 8;
        int biaoStartValuePos = guAmountPos + guAmountLen;
        int biaoStartValueLen = 10;
        int biaoEndValuePos = biaoStartValuePos + biaoStartValueLen;
        int biaoEndValueLen = 10;
        int energyPos = biaoEndValuePos + biaoEndValueLen;
        int energyLen = 8;
        int sunEnergyPos = energyPos + energyLen;
        int sunEnergyLen = 8;
        int amountPos = sunEnergyPos + sunEnergyLen;
        int amountLen = 8;
        int carOnlyIdPos = amountPos + amountLen; // 电动汽车唯一标识
        int carOnlyIdLen = 34;
        int tranFlagPos = carOnlyIdPos + carOnlyIdLen;
        int tranFlagLen = 2;
        int transTimePos = tranFlagPos + tranFlagLen;
        int transTimeLen = 14;
        int stopReasonPos = transTimePos + transTimeLen;
        int stopReasonLen = 2;

        String portCode = YunCarUtil.getTargetString(data, portCodePos, portCodeLen);
        String tradeNo = YunCarUtil.getTradeNo(data);
        System.out.println("tradeNo="+tradeNo);
        String amount = YunCarUtil.getTargetBin(data, amountPos, amountLen, 4);
        String energy = YunCarUtil.getTargetBin(data, energyPos, energyLen, 4);
        String reason = YunCarUtil.getTargetString(data, stopReasonPos, stopReasonLen);


//        00	尖费率
//        01	峰费率
//        02	平费率
//        03	谷费率
        String jianServicePrice = "0";
        String fenServicePrice = "0";
        String pingServicePrice = "0";
        String guServicePrice = "0";
                jianServicePrice = "0.16";
                fenServicePrice = "0.14";
                pingServicePrice = "0.15";
                guServicePrice = "0.16";

        // 尖电量
        String jianEnergy = YunCarUtil.getTargetBin(data, jianEnergyPos, jianEnergyLen, 4);
        // 峰电量
        String fenEnergy = YunCarUtil.getTargetBin(data, fenEnergyPos, fenEnergyLen, 4);
        // 平电量
        String pingEnergy = YunCarUtil.getTargetBin(data, pingEnergyPos, pingEnergyLen, 4);
        // 谷电量
        String guEnergy = YunCarUtil.getTargetBin(data, guEnergyPos, guEnergyLen, 4);

        System.out.println("jianEnergy="+jianEnergy);
        System.out.println("fenEnergy="+fenEnergy);
        System.out.println("pingEnergy="+pingEnergy);
        System.out.println("guEnergy="+guEnergy);
        BigDecimal jianDec = new BigDecimal(jianEnergy).multiply(new BigDecimal(jianServicePrice)).setScale(4, BigDecimal.ROUND_HALF_UP);
        BigDecimal fenDec = new BigDecimal(fenEnergy).multiply(new BigDecimal(fenServicePrice)).setScale(4, BigDecimal.ROUND_HALF_UP);
        BigDecimal pingDec = new BigDecimal(pingEnergy).multiply(new BigDecimal(pingServicePrice)).setScale(4, BigDecimal.ROUND_HALF_UP);
        BigDecimal guDec = new BigDecimal(guEnergy).multiply(new BigDecimal(guServicePrice)).setScale(4, BigDecimal.ROUND_HALF_UP);
        BigDecimal serviceFeeDec = jianDec.add(fenDec).add(pingDec).add(guDec);
        BigDecimal feeDec = new BigDecimal(amount).subtract(serviceFeeDec);


        System.out.println(feeDec.doubleValue());
        System.out.println("amount="+amount);
        System.out.println("energy="+energy);


    }
}