package com.java110;

import com.java110.charge.factory.lvcc.LvCCUtil;
import com.java110.core.utils.BytesUtil;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.UnsupportedEncodingException;
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
        String dataHex = "0500484331313230323331313230353832313030303500000000655b08ca";
        // todo 结束原因
        int reason = Integer.parseInt(dataHex.substring(0, 2), 16);

        // todo 插座
        int port = Integer.parseInt(dataHex.substring(2, 4), 16) + 1;

        //todo 订单号
        String orderHex = dataHex.substring(4, 44);

        String orderNum = null;
        try {
            orderNum = new String(BytesUtil.hexStringToByteArray(orderHex), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        //todo 结束时功率
        int w = Integer.parseInt(dataHex.substring(48, 52), 16);
    }

    public void testByte(){
        System.out.println(LvCCUtil.generatorMsgId());
    }
}
