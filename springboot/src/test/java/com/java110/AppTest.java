package com.java110;

import com.java110.core.utils.DateUtil;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

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

        Date endTime = DateUtil.getDateFromStringA("2024-05-07 12:12:12"); //1715055132
        Random random = new Random();
        int randomSec = random.nextInt(7200);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endTime);
        calendar.add(Calendar.SECOND, randomSec);
        endTime = calendar.getTime();
        long timestrap = endTime.getTime();
        timestrap = timestrap / 1000;
        System.out.println(timestrap);
    }
}
