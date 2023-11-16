package com.java110;

import com.java110.core.utils.BytesUtil;
import com.java110.hal.nettty.DataHeader;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * Unit test for simple App.
 */
public class AppTest
        extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() {
        //7e5d7d7f0022  0e
        String aa = "0001100f383631353531303537303035363936000100011b012400010001021d01";

        String cmdContext = xOrCheckSum(BytesUtil.hexStringToByteArray(aa));
        System.out.println(cmdContext);


    }

    public void testApp1() {

        String aa = "65562fc1";

        String bb = null;
        bb = new String(BytesUtil.hexStringToByteArray(aa));

        long curUnit = new Date().getTime()/1000;

        System.out.println(String.format("%x", curUnit));
        ;

        System.out.println(Integer.parseInt(aa,16));
    }



    /**
     * 获取报文长度
     *
     * @param msg
     * @return
     */
    public static String xOrCheckSum(byte[] data) {
        byte checksum = 0;
        for (byte b : data) {
            checksum ^= b;
        }

        String hex = Integer.toHexString(checksum & 0xFF);
        if (hex.length() < 2) {
            hex = "0"+hex;
        }
        return hex;

    }
}
