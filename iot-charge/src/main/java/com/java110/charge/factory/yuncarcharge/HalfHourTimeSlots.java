package com.java110.charge.factory.yuncarcharge;

import com.java110.core.utils.BytesUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class HalfHourTimeSlots {
    public static void main(String[] args) {
        String str = "Hello, World!";
        System.out.println("字符串: " + str);
        System.out.println("ASCII 码:");
        String xxx = "";
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);  // 获取字符串中的字符
            int ascii = (int) ch;  // 转换字符为 ASCII 码
            String hex = Integer.toHexString(ascii); // 转换为十六进制
            xxx += hex;
        }
        System.out.println("字符16进制 " + xxx);

//            String dateStr = "2099-10-11 10:10:27";
//            String cp56Time2a = dateToCP56Time2a();
//            System.out.println(getYYYYMMDD(cp56Time2a));
//            System.out.println(dateStr);



    }



    public static String getYYYYMMDD(String CP56Time2a) {
        StringBuilder reversedHex = new StringBuilder();
        for (int i = CP56Time2a.length(); i > 0; i -= 2) {
            reversedHex.append(CP56Time2a, i - 2, i);
        }
        String reversedHexString = reversedHex.toString();
        byte[] timeBytes = BytesUtil.hexStringToByteArray(reversedHexString);
        System.out.println(((timeBytes[5] & 0xFF) << 8) | (timeBytes[6] & 0xFF));
        int year = (timeBytes[0] & 0x3F) + 2000; // 高 8 位为年份的最后两位
        int month = (timeBytes[1] & 0x0F); // 月份
        int day = timeBytes[2] & 0x1F; // 日
        int hour = (timeBytes[3] & 0x1F); // 小时
        int minute = (timeBytes[4] & 0x3F); // 分钟
        int second = (((timeBytes[5] & 0xFF) << 8) | (timeBytes[6] & 0xFF))/1000; // 毫秒部分
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, hour, minute, second);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = sdf.format(calendar.getTime());
        return formattedDate;
    }


    public static String dateToCP56Time2a() throws ParseException {
        // 解析输入的日期字符串
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR) % 100; // 取年份的后两位
        int month = calendar.get(Calendar.MONTH) + 1; // 月份从0开始
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND)*1000;
        byte[] cp56Time2a = new byte[7];
        cp56Time2a[0] = (byte) (year & 0x3F);
        cp56Time2a[1] = (byte) ((month & 0x0F) );
        cp56Time2a[2] = (byte) ((day & 0x1F) );
        cp56Time2a[3] = (byte) ((hour & 0x1F));
        cp56Time2a[4] = (byte) ((minute & 0x3F));
        cp56Time2a[5] = (byte) (((second >> 8) & 0xFF)); ;
        cp56Time2a[6] = (byte) (second);
//        int year = (timeBytes[0] & 0x3F) + 2000; // 高 8 位为年份的最后两位
//        int month = (timeBytes[1] & 0x0F); // 月份
//        int day = timeBytes[2] & 0x1F; // 日
//        int hour = (timeBytes[3] & 0x1F); // 小时
//        int minute = (timeBytes[4] & 0x3F); // 分钟
//        int second = (((timeBytes[5] & 0xFF) << 8) | (timeBytes[6] & 0xFF))/1000; // 毫秒部分

        String reversedHexString = reverseHexEndian(BytesUtil.bytesToHex(cp56Time2a));


        // 反转字节数组
//        byte[] reversedCP56Time2a = new byte[7];
//        for (int i = 0; i < 7; i++) {
//            reversedCP56Time2a[i] = cp56Time2a[6 - i];
//        }

        // 将字节数组转换为十六进制字符串
//        StringBuilder hexString = new StringBuilder();
//        for (byte b : reversedHexString) {
//            hexString.append(String.format("%02X", b));
//        }

        return reversedHexString;
    }



    public static String getNextHex(String hex) {
        int number = Integer.parseUnsignedInt(hex, 16);
        number++;
        if (number > 0xFFFF) {
            return "0000";
        }
        return String.format("%04X", number);
    }
//
//    private static String cryptocurrencyAmount(String price, int n) {
//        BigDecimal priceValue = new BigDecimal(price);
//        BigDecimal multiplier = BigDecimal.TEN.pow(n);
//        BigDecimal result = priceValue.multiply(multiplier);
//        String priceHex = String.format("%08x",result.intValue());
//        priceHex = reverseHexEndian(priceHex);
//        return priceHex;
//    }
//
//    private static String priceHexToString(String priceHex,int n){
//        priceHex = reverseHexEndian(priceHex);
//        int a = Integer.parseInt(priceHex,16);
//        BigDecimal priceValue = new BigDecimal(a);
//        BigDecimal divisor = BigDecimal.TEN.pow(n);
//        BigDecimal result = priceValue.divide(divisor, n, RoundingMode.HALF_UP);
//        return result.toString();
//    }


    public static String getAmount(String data) {
        data = data.substring(252, 260);
        return data;
    }

    /**
     * 获取完成交易的总电量
     * @return
     */
    public static String getTotalEnergy(String data) {
        data = data.substring(236, 244);
        return data;
    }



//    public static void main(String[] args) {
//        String data = "68a202a3003b662024110100010120241118171208426620241101000101f0550c11120b18c0da1111120b1898ab020000000000000000000000000018e403000000000000000000000000007005030012250000122500000c49000050b702000000000000000000000000007c750900008e9a09000012250000122500000c4900004c4656424131344233593330313430373801c0da1111120b1840000000000000000038d5";
//        System.out.println(data.length());
//        //System.out.println(getAmount("c8000000"));
//       // System.out.println(getTotalEnergy(data));
//
//        System.out.println(priceHexToInt("000006400"));
//        System.out.println(priceHexToInt(getTotalEnergy(data)));
////
////      int t =   Integer.parseInt("68a200".substring(2,4), 16);
////
////      System.out.println(t);
//
//
//        // 输入的十六进制字符串（假设这是小端格式）
//        String hexString = "bc020000";
//
//
//
//
//        // 步骤 1：按小端格式反转字节顺序
//        String reversedHexString = reverseHexEndian(hexString);
//
//
//
//        // 步骤 2：将反转后的十六进制字符串转换为字节数组
//        byte[] timeBytes = hexStringToByteArray(reversedHexString);
//
//        // 步骤 3：解析 CP56Time2a 时间格式
//        System.out.println(((timeBytes[5] & 0xFF) << 8) | (timeBytes[6] & 0xFF));
//
//        //int year = (timeBytes[0]>>1) + 2000; // 高 8 位为年份的最后两位
//        //140310110EB798
//        int year = (timeBytes[0] & 0x3F) + 2000; // 高 8 位为年份的最后两位
//        int month = (timeBytes[1] & 0x0F); // 月份
//        int day = timeBytes[2] & 0x1F; // 日
//        int hour = (timeBytes[3] & 0x1F); // 小时
//        int minute = (timeBytes[4] & 0x3F); // 分钟
//        int second = (((timeBytes[5] & 0xFF) << 8) | (timeBytes[6] & 0xFF))/1000; // 毫秒部分
//
//        // 使用 Calendar 创建日期
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(year, month - 1, day, hour, minute, second);
//
//        // 步骤 4：格式化输出日期
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String formattedDate = sdf.format(calendar.getTime());
//
//        System.out.println("CP56Time2a Date: " + formattedDate);
//    }


    private static String cryptocurrencyAmount(int price) {
        String priceHex = String.format("%08x",price);
        priceHex = reverseHexEndian(priceHex);
        return priceHex;
    }

    private static int priceHexToInt(String priceHex){
        priceHex = reverseHexEndian(priceHex);
        return Integer.parseInt(priceHex,16);
    }


    // 按小端格式反转字节顺序
    private static String reverseHexEndian(String hex) {
        StringBuilder reversedHex = new StringBuilder();
        for (int i = hex.length(); i > 0; i -= 2) {
            reversedHex.append(hex, i - 2, i);
        }
        return reversedHex.toString();
    }


    // 将十六进制字符串转换为字节数组
    public static byte[] hexStringToByteArray(String hexString) {
        int length = hexString.length();
        byte[] byteArray = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            byteArray[i / 2] = (byte) Integer.parseInt(hexString.substring(i, i + 2), 16);
        }
        return byteArray;
    }
//    public static void main(String[] args) {
        // 将十六进制字符串转换为十进制数
      //  String data = "682200000001667129560256780002105620332e333900000089860321247975296013025881";
//        String data = "680c000000026671295602567800163a";
//        String cmd = data.substring(10, 12);
//
//        System.out.printf(cmd);
//        int num1 = Integer.parseInt(cmd, 16);
//        if (num1 >= 19){
//            data = data.substring(56, 70);
//        }else {
//            data = data.substring(12, 26);
//        }
//        System.out.println(data);
//        long decimal = Long.parseLong(hex, 16);
//
//        // 假设基准年为2000年（这个基准年是根据实际情况定的）
//        int baseYear = 2000;
//
//        // 提取各个部分（这里的位移和掩码根据实际格式可能需要调整）
//        int year = (int) ((decimal >> 48) & 0xFFFF); // 取前16位作为年份偏移量
//        int month = (int) ((decimal >> 40) & 0xFF); // 取接下来的8位作为月份
//        int day = (int) ((decimal >> 32) & 0xFF); // 取接下来的8位作为日期
//        int hour = (int) ((decimal >> 24) & 0xFF); // 取接下来的8位作为小时
//        int minute = (int) ((decimal >> 16) & 0xFF); // 取接下来的8位作为分钟
//        int second = (int) ((decimal >> 8) & 0xFF); // 取接下来的8位作为秒
//
//        // 计算实际年份
//        year += baseYear; // 根据基准年计算实际年份
//
//        // 打印结果
//        System.out.printf("%04d-%02d-%02d %02d:%02d:%02d%n", year, month, day, hour, minute, second);;
//    }



}

