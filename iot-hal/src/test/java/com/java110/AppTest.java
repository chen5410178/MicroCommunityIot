package com.java110;

import com.java110.core.utils.BytesUtil;
import com.java110.hal.nettty.DataHeader;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.UnsupportedEncodingException;

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

        String aa = "61e0dc1d";

        String bb = null;
        bb = new String(BytesUtil.hexStringToByteArray(aa));

        System.out.println(bb);
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
