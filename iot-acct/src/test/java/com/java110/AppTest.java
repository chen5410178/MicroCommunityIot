package com.java110;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.java110.core.utils.UrlParamToJsonUtil;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.net.URLDecoder;
import java.util.Map;

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

        //param = URLDecoder.decode(param);
        Map info = JSONObject.parseObject("{\"gmt_create\":\"2024-06-26 21:50:21\",\"charset\":\"GBK\",\"seller_email\":\"402162119@qq.com\",\"subject\":\"@@ADT8777ͣ@@@@\",\"sign\":\"QqJXMHMuftjIpu0ADnFJ+OC+ZOUrVr3aZHmwbL6syWX3nepaJ9CuyUi8r3bxipQJZYz9ZxEzlKMLIsAtMoMKncQv6W2cDuxqaryOxu4g85ppA7bhaSTJiBSPo+jnn38g/fGSgNlwXLSWaUqWg2faDuYT5J38L2KPm/f+xIaxE8/iTsJX3VRE+Ee2N4J6iHo+RsMlFvzNwQg8ho35hTjaIBPvKAzBPv2ooQ+/gzvJN+I67fEKV+ZqFx8iKUuoaVXUNxQkZm4mTRdtklWL/ryqdX1ECbR9XVU2g7JDu25XerQwCkOALMqypajoO5l614SwM3u8ms1MjaAPXGYPlUEMsQ==\",\"buyer_open_id\":\"026E-9rFSoOzw7eW_Rjz5luddVuSUGCi1nBT20ENVsVGuw5\",\"invoce_amount\":\"0.01\",\"notify_id\":\"2024062601222215022074261417323303\",\"fund_bill_list\":\"[{\\\"amount\\\":\\\"0.01\\\",\\\"fundChannel\\\":\\\"ALIPAYACCOUNT\\\"}]\",\"notify_type\":\"trade_status_sync\",\"trade_status\":\"TRADE_SUCCESS\",\"receipt_amount\":\"0.01\",\"buyer_pay_amount\":\"0.01\",\"app_id\":\"2021004150679237\",\"sign_type\":\"RSA2\",\"seller_id\":\"2088940286180684\",\"gmt_payment\":\"2024-06-26 21:50:22\",\"notify_time\":\"2024-06-26 22:04:08\",\"merchant_app_id\":\"2021004150679237\",\"version\":\"1.0\",\"out_trade_no\":\"102024062695110046\",\"total_amount\":\"0.01\",\"trade_no\":\"2024062622001474261403820725\",\"auth_app_id\":\"2021004150679237\",\"buyer_logon_id\":\"177****3942\",\"point_amount\":\"0.00\"}");

        String sign = info.get("sign").toString();
        System.out.println(sign);
        info.remove("sign");
        info.remove("sign_type");
        //sign_type

        String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAn/o/NNHm/+ArqZyWJj4LWGJoc5kHoQNi+Qrw39MeH70v4Cdtsun/Sc8ZZMo86IE12qHz0t6+Lh918hZQKzBT1Sa5KJjh6AX59Pc3VFeCK7YqV5elFFSR1hU9S75EU4j/fETIQnrZpDPnOdVQ6TMNkC52Frm7DKTGh3lVzh90qgNLJLJfd7giOC0NEdwraT2wMmsEZovsdG2WGNJcx7wdRl0HwsjQoWOvlFFh4OpM7ZWJ3bvbitKVX4VaAR9TGFRH2Kw6maR4kdc6vhFkK+xeLw4GDkczUfMciLMDrz7onw5YupcP6mtTgUGw12hc9NLtCFI+UKxMAkbFFl3hBKgFnwIDAQAB";
        try {
            boolean verify_result = AlipaySignature.rsaCheckV1(info, publicKey, "UTF-8","RSA2");
            System.out.println(verify_result);
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }


    }
}
