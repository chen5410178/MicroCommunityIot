package com.java110.charge.factory.yuncarcharge;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CP56Time2aConverter {

    // 假设 CP56Time2a 的参考时间是 1970-01-01 00:00:00 UTC
    private static final long REFERENCE_TIME_MS = 0L;

    // 将 yyyy-MM-dd HH:mm:ss 格式的日期字符串转换为 CP56Time2a 格式的十六进制字符串
    public static String getCP56Time2aFromYYYYMMDD(String dateString) throws ParseException {
        // 解析日期字符串
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = sdf.parse(dateString);

        // 计算自参考点以来的毫秒数
        long millisecondsSinceReference = date.getTime() - REFERENCE_TIME_MS;

        // 假设我们只保留低48位作为有效时间（这取决于具体的 CP56Time2a 实现）
        long cp56Time = millisecondsSinceReference & 0xFFFFFFFFFFFFL; // 保留低48位

        // 将48位时间转换为6字节数组（大端字节序）
        byte[] timeBytes =  new byte[6];
        timeBytes[0] = (byte) ((cp56Time >> 40) & 0xFF);
        timeBytes [1]= (byte) ((cp56Time >> 32) & 0xFF);
        timeBytes[2] = (byte) ((cp56Time >> 24) & 0xFF);
        timeBytes[3] = (byte) ((cp56Time >> 16) & 0xFF);
        timeBytes[4] = (byte) ((cp56Time >> 8) & 0xFF);
        timeBytes[5] = (byte) (cp56Time & 0xFF);

        // 将字节数组转换为十六进制字符串
        StringBuilder hexString = new StringBuilder();
        for (byte b : timeBytes) {
            hexString.append(String.format("%02X", b));
        }

        return hexString.toString();
    }

    public static void main(String[] args) {
        try {
            String dateString = "2023-04-01 12:34:56";
            String cp56Time2a = getCP56Time2aFromYYYYMMDD(dateString);
            System.out.println("CP56Time2a: " + cp56Time2a);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
