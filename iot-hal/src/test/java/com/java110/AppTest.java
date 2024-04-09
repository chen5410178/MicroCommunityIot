package com.java110;

import com.java110.core.utils.BytesUtil;
import com.java110.hal.nettty.DataHeader;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

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
        String aa = "7e5d7d7f00180001120f383631353531303537303035363936000500022b";

        String cmdContext = DataHeader.getLvCCMachineCode(BytesUtil.hexStringToByteArray(aa));
        System.out.println(cmdContext);

    }

    public void testApp1() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        long timestamp = calendar.getTime().getTime();
        System.out.println(timestamp/1000);
    }


    /**
     * 获取报文长度
     *
     * @param msg
     * @return
     */
    public static String getCmdContext(byte[] msg) {
        String data = BytesUtil.bytesToHex(msg);

        int startPos = 28 + DataHeader.getLvCCMachineCodeLength(msg) * 2;
        data = data.substring(startPos, data.length() - 2);

        return data;
    }
}
