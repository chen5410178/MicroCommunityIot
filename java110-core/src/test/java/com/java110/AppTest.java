package com.java110;

import com.java110.core.utils.Base64Convert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

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
    public void testApp()
    {
        String xx = Base64Convert.decoder("AGT//2IbAgEBABABAQMB/wAAAAAAAAAACL371rnNqNDQvV0=");
        System.out.println(xx);
        assertTrue( true );
    }
}
