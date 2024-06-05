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
        String dataHex = "3839383630343938313032324330343038353530";
        // todo 结束原因
        String bb = null;
        try {
            bb = new String(BytesUtil.hexStringToByteArray(dataHex), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        System.out.println(bb);
    }

    public void testByte(){
        System.out.println(LvCCUtil.generatorMsgId());
    }
}
