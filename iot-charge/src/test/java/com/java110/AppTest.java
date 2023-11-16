package com.java110;

import com.java110.charge.factory.lvcc.LvCCUtil;
import com.java110.core.utils.BytesUtil;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.Date;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp(){
        String aa = "7e5d7d7f00220001100f3836313535313035373030353639360001000117012400010001021d0102";
        long curUnit = new Date().getTime()/1000;

        String paramOut = String.format("%x", curUnit);
        String param = LvCCUtil.computeResultDate(BytesUtil.hexStringToByteArray(aa),"861551057005696",paramOut);
        System.out.println(param);
    }

    public void testByte(){
        byte[] aa = new byte[]{0, 1, 2, 29, 1, 12};
        System.out.println(BytesUtil.bytesToHex(aa));
    }
}
