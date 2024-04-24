package com.java110;

import com.alibaba.fastjson.JSONObject;
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
        String body = "{\n" +
                "    \"code\": 200,\n" +
                "    \"msg\": \"success\",\n" +
                "    \"data\": {\n" +
                "        \"rooms\": [\n" +
                "            {\n" +
                "                \"b_id\": 288,\n" +
                "                \"b_name\": [\n" +
                "                    \"2栋4单元\"\n" +
                "                ],\n" +
                "                \"add\": [\n" +
                "                    {\n" +
                "                        \"id\": 392188,\n" +
                "                        \"uid\": \"20ce9b7368ad918515650baee75420fd\",\n" +
                "                        \"name\": \"1003\",\n" +
                "                        \"num\": \"0101-1003\",\n" +
                "                        \"user\": [\n" +
                "                            {\n" +
                "                                \"id\": 416290,\n" +
                "                                \"uid\": \"6003e234c6f988a6fc7ab5275f766d1e\",\n" +
                "                                \"phone\": \"17782426083\",\n" +
                "                                \"expired\": 4294967295\n" +
                "                            },\n" +
                "                            {\n" +
                "                                \"id\": 1004352,\n" +
                "                                \"uid\": \"7247871cfb24f48bf1877d8f6795cc23\",\n" +
                "                                \"phone\": \"17728043791\",\n" +
                "                                \"expired\": 1589205120\n" +
                "                            }\n" +
                "                        ]\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"id\": 392146,\n" +
                "                        \"uid\": \"9621c06d7a09214da1809019dfa49533\",\n" +
                "                        \"name\": \"0201\",\n" +
                "                        \"num\": \"0101-0201\",\n" +
                "                        \"user\": [\n" +
                "                            {\n" +
                "                                \"id\": 976586,\n" +
                "                                \"uid\": \"7a6cbdceb1225595b9d99373762301d7\",\n" +
                "                                \"phone\": \"17776125054\",\n" +
                "                                \"expired\": 1618965646\n" +
                "                            }\n" +
                "                        ]\n" +
                "                    }],\n" +
                "                \"del\": {\n" +
                "                    \"del_room\": [],\n" +
                "                    \"del_user\": []\n" +
                "                }\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    \"timestamp\": 1626702918\n" +
                "}";
        JSONObject result = JSONObject.parseObject(body);
    }
}
