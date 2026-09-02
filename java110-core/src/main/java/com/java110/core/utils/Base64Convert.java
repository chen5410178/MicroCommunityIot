package com.java110.core.utils;

import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Base64Convert {

    // 使用 Apache Commons 的 Base64 实例（线程安全）
    private static final Base64 BASE_64 = new Base64();

    // 私有构造函数，防止实例化（修正了原来的写法）
    private Base64Convert() {
    }

    /**
     * 解码 Base64 字符串为普通字符串
     */
    public static String decoder(String data) {
        byte[] decodedBytes = BASE_64.decode(data);
        return new String(decodedBytes);
    }

    /**
     * 将 InputStream 转换为 Base64 字符串
     * 使用 Apache Commons 的 Base64
     */
    public static String ioToBase64(InputStream in) throws IOException {
        if (in == null) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        try {
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            byte[] bytes = out.toByteArray();
            return BASE_64.encodeToString(bytes);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                // 日志记录关闭异常，此处忽略或打印
            }
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                // 日志记录关闭异常，此处忽略或打印
            }
        }
    }

    /**
     * 将 Base64 字符串转换为字节数组
     * 使用 Apache Commons 的 Base64
     */
    public static byte[] base64ToByte(String strBase64) {
        if (strBase64 == null) {
            return null;
        }
        // 移除换行符和回车符（Apache Base64 默认会忽略，但为了明确处理）
        String cleaned = strBase64.replaceAll("\r|\n", "");
        return BASE_64.decode(cleaned);
    }

    /**
     * 将字节数组转换为 Base64 字符串
     * 使用 Apache Commons 的 Base64
     */
    public static String byteToBase64(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return BASE_64.encodeToString(bytes);
    }
}