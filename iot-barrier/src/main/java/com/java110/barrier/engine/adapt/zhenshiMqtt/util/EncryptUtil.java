package com.java110.barrier.engine.adapt.zhenshiMqtt.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class EncryptUtil {

    public static final String DEFAULT_ENCODING = "UTF8";
    public static final String ENCODE_UTF8 = "UTF8";
    public static final String ENCODE_GBK = "GBK";
    public static String encryptUserInfo(String username, String password, String accessKeySecret) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");

            byte[] encryptKey = BinaryUtil.calculateMd5(accessKeySecret.getBytes(DEFAULT_ENCODING));
            SecretKeySpec keySpec = new SecretKeySpec(encryptKey, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);

            byte[] userinfo = (username + ":" + password).getBytes(DEFAULT_ENCODING);
            int userinfoLength = userinfo.length;
            if (userinfoLength % cipher.getBlockSize() != 0) {
                userinfoLength += cipher.getBlockSize() - (userinfoLength % cipher.getBlockSize());
            }
            byte[] fixedUserinfo = new byte[userinfoLength];
            System.arraycopy(userinfo, 0, fixedUserinfo, 0, userinfo.length);

            byte[] encrypted = cipher.doFinal(fixedUserinfo);

            return BinaryUtil.toBase64String(encrypted);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
