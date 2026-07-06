package com.java110.core.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

public class Base64Convert {

    private void Base64Convert() {

    }

    public static String decoder(String data){
        return new String(Base64.getDecoder().decode(data));
    }

    public static String ioToBase64(InputStream in) throws IOException {
        String strBase64 = null;
        try {
            byte[] bytes = new byte[in.available()];
            in.read(bytes);
            strBase64 = Base64.getEncoder().encodeToString(bytes);
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

    public static String byteTobase64(byte[] strBase64) {
        String bytes = Base64.getEncoder().encodeToString(strBase64);
        return bytes;
    }


}
