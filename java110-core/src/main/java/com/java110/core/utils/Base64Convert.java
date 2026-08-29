package com.java110.core.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;


public class Base64Convert {
    private static final org.apache.commons.codec.binary.Base64 base64 = new org.apache.commons.codec.binary.Base64();

    private void Base64Convert() {

    }

    public static String decoder(String data){
        return new String(Base64.getDecoder().decode(data));
    }

    public static String ioToBase64(InputStream in) throws IOException {
        String strBase64 = null;
        try {
            // in.available()返回文件的字节长度
            byte[] bytes = new byte[in.available()];
            // 将文件中的内容读入到数组中
            in.read(bytes);
            strBase64 = base64.encodeToString(bytes);      //将字节流数组转换为字符串
        } finally {
            if (in != null) {
                in.close();
            }
        }

        return strBase64;
    }

    public static byte[] base64ToByte(String strBase64) throws IOException {
        strBase64 = strBase64.replaceAll("\r|\n", "");
        byte[] bytes = Base64.getDecoder().decode(strBase64);
        return bytes;
    }

    public static String byteToBase64(byte[] bytes)  {
        String strBase64 = null;
        // in.available()返回文件的字节长度
        strBase64 = base64.encodeToString(bytes);      //将字节流数组转换为字符串
        return strBase64;
    }


}
