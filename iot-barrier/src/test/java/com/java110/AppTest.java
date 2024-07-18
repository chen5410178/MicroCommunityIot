package com.java110;

import com.java110.barrier.engine.adapt.huaxiamqtt.HuaxiaMqttSend;
import com.java110.core.utils.Base64Convert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import sun.misc.BASE64Decoder;

import java.io.IOException;
import org.apache.commons.codec.binary.Base64;

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
    public void testApp() throws IOException {

    }
}
