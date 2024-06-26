package com.java110;

import com.java110.core.utils.UrlParamToJsonUtil;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

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

        String param = "gmt_create=2024-06-26+19%3A51%3A28&charset=GBK&seller_email=402162119%40qq.com&subject=%EF%BF%BD%EF%BF%BDADT8777%CD%A3%EF%BF%BD%EF%BF%BD%EF%BF%BD%EF%BF%BD&sign=YlaP7%2Fd0wJA9G3Zlgc%2BO7SyQpRtiXU4yUDl1pQACDk9l6p5DrqU42blm7Q4f%2Bdk1KNUXium%2BkTvyjw8zncqVL4ykpL1KxLeTzw5hvgglAzXxjnuYaObWdVNbQXnUnjV3PmBLoQH8jMNmNASEYpWZOZHSavnHIukPGuAjRkcENQ3IMJwNitnLQYgPfafgrzIVv5b0SqHgQDqyxXnnG51KBoHXOJkDUxUvIYfdCCkJfqQXAeeRBt62uQILHWUr5aeEYjO6XMz7uvy7iboEWfFqfd1yQSIpAPyDKatFtavxiz47t13No15qn5pGPfjqrqdI1CveCR5fw%2F7B388Tp%2FgPLg%3D%3D&buyer_open_id=026E-9rFSoOzw7eW_Rjz5luddVuSUGCi1nBT20ENVsVGuw5&invoice_amount=0.00&notify_id=2024062601222195129074261417855725&fund_bill_list=%5B%7B%22amount%22%3A%220.01%22%2C%22fundChannel%22%3A%22DISCOUNT%22%7D%5D&notify_type=trade_status_sync&trade_status=TRADE_SUCCESS&receipt_amount=0.01&buyer_pay_amount=0.00&app_id=2021004150679237&sign_type=RSA2&seller_id=2088940286180684&gmt_payment=2024-06-26+19%3A51%3A28&notify_time=2024-06-26+19%3A51%3A29&merchant_app_id=2021004150679237&version=1.0&voucher_detail_list=%5B%7B%22amount%22%3A%220.01%22%2C%22merchantContribute%22%3A%220.00%22%2C%22name%22%3A%22%CA%B5%EF%BF%BD%EF%BF%BD%EF%BF%BD%CD%A8%EF%BF%BD%EF%BF%BD%C8%AF%EF%BF%BD%EF%BF%BD%22%2C%22otherContribute%22%3A%220.01%22%2C%22type%22%3A%22ALIPAY_CASH_VOUCHER%22%2C%22voucherId%22%3A%22202406200007300226740P64K0T3%22%7D%5D&out_trade_no=102024062638730174&total_amount=0.01&trade_no=2024062622001474261400294151&auth_app_id=2021004150679237&buyer_logon_id=177****3942&point_amount=0.00";
        Map info = UrlParamToJsonUtil.getMap(param);
        System.out.println(info);
    }
}
