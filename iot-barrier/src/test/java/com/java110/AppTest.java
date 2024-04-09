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
        String aa = "X+aXoF8=";
        byte[] bytes = new BASE64Decoder().decodeBuffer(aa);   //将字符串转换为byte数组
        Base64 decoder = new Base64();
        //System.out.println(Base64Convert.decoder("aHR0cDovL3d1aGFvZmhjLm9zcy1jbi1iZWlqaW5nLmFsaXl1bmNzLmNv bS85MjZiYzMxMC0yOWIyMjFlMS8xNzEyNjc2MDAwXzkyNmJjMzEwLTI5YjIyMWUxXzIxMDJfMl9mdWxsLmpwZz9FeHBpcmVzPTE3MTI2Nzk2MDAmT1NTQWNjZXNzS2V5SWQ9TFRBSTV0ODk1ejhLWjE2TldnR1BQcFVCJlNpZ25hdHVyZT00S2dVUWM4aXYyN3VvRGNCUUFlRDBX cE9XbzAlM2Q="));
        System.out.println(HuaxiaMqttSend.generateUniqueId());
    }
}
