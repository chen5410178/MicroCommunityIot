package com.java110;

import com.java110.core.utils.BytesUtil;
import com.java110.hal.nettty.DataHeader;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
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
        String aa = "02000A6F6E4D65746144617461080000000800086475726174696F6E004060428F5C28F5C30005776964746800409E0000000000000006686569676874004090E00000000000000D766964656F646174617261746500000000000000000000096672616D657261746500403900000000\u0090\u0090\u0090\u00900000000C766964656F636F646563696400401C0000000000000007656E636F64657202000C4C61766636302E332E313030000866696C6573697A6500418F18A8A0000000\u0090\u0090\u0090\u0090\u0090\u0090\u0090000009";
        aa = new String(aa.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);

        System.out.println(aa.length());

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
