package com.java110;

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

        String param = "gmt_create=2024-06-26+19%3A51%3A28&charset=GBK&seller_email=402162119%40qq.com&subject=%EF%BF%BD%EF%BF%BDADT8777%CD%A3%EF%BF%BD%EF%BF%BD%EF%BF%BD%EF%BF%BD&sign=YlaP7%2Fd0wJA9G3Zlgc%2BO7SyQpRtiXU4yUDl1pQACDk9l6p5DrqU42blm7Q4f%2Bdk1KNUXium%2BkTvyjw8zncqVL4ykpL1KxLeTzw5hvgglAzXxjnuYaObWdVNbQXnUnjV3PmBLoQH8jMNmNASEYpWZOZHSavnHIukPGuAjRkcENQ3IMJwNitnLQYgPfafgrzIVv5b0SqHgQDqyxXnnG51KBoHXOJkDUxUvIYfdCCkJfqQXAeeRBt62uQILHWUr5aeEYjO6XMz7uvy7iboEWfFqfd1yQSIpAPyDKatFtavxiz47t13No15qn5pGPfjqrqdI1CveCR5fw%2F7B388Tp%2FgPLg%3D%3D&buyer_open_id=026E-9rFSoOzw7eW_Rjz5luddVuSUGCi1nBT20ENVsVGuw5&invoice_amount=0.00&notify_id=2024062601222195129074261417855725&fund_bill_list=%5B%7B%22amount%22%3A%220.01%22%2C%22fundChannel%22%3A%22DISCOUNT%22%7D%5D&notify_type=trade_status_sync&trade_status=TRADE_SUCCESS&receipt_amount=0.01&buyer_pay_amount=0.00&app_id=2021004150679237&sign_type=RSA2&seller_id=2088940286180684&gmt_payment=2024-06-26+19%3A51%3A28&notify_time=2024-06-26+19%3A51%3A29&merchant_app_id=2021004150679237&version=1.0&voucher_detail_list=%5B%7B%22amount%22%3A%220.01%22%2C%22merchantContribute%22%3A%220.00%22%2C%22name%22%3A%22%CA%B5%EF%BF%BD%EF%BF%BD%EF%BF%BD%CD%A8%EF%BF%BD%EF%BF%BD%C8%AF%EF%BF%BD%EF%BF%BD%22%2C%22otherContribute%22%3A%220.01%22%2C%22type%22%3A%22ALIPAY_CASH_VOUCHER%22%2C%22voucherId%22%3A%22202406200007300226740P64K0T3%22%7D%5D&out_trade_no=102024062638730174&total_amount=0.01&trade_no=2024062622001474261400294151&auth_app_id=2021004150679237&buyer_logon_id=177****3942&point_amount=0.00";
        //param = URLDecoder.decode(param);
        Map info = UrlParamToJsonUtil.getMap(param);

        String sign = info.get("sign").toString();
        System.out.println(sign);
        info.remove("sign");
        info.remove("sign_type");
        //sign_type

        String publicKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDgakqzjbM7ztLkAqqlUSWk38hh+EN1s2d4y689VuGgG015O9EQ9R6R63/5M5iKw3hFxjETgRvPKywJ8rfMqrwpjr7M62EFMgN4k+y80L1Ev2fK1Waw2O4htxvWW7cclzraUlHJQsR1iW62KSscaKg6qREPhn97x3F6gNfYmLlq5kGOZU2bg65yJ0IU096L4R3H6pPfRj6XdVUAF6ljLBNBsP6rwdH/OldKU9vEaWYjSQHtnGht9VMLfb+wkkj5jxRUGN2bLmu57pNUsoITLcBhHvn6h0sOgYG7O1JRue3d2pRiU4ocZH2+Blioh5wPlUvga/euQefcTIGCF9WbkpenAgMBAAECggEAEye/fJgJxn8e7St1GWtVxT78wbAJQhCwMlVgfeHenGX4NPLwI3xowZfScpj+2klZCMukAMAyeXp75dV77tJcAUgX7THiNClStoYZ3PwMS8PyvhN9EndRzDru+1sUxDH8agpAxOcYEdhLRO06qtAcG/W2aRbX5t7dRtGcmvZeTUt1Mgaap4n/L7lb+I1ZHoJ9pHBEDVVUW3Fag1aELqARRbIpe/WXAl78ubfxLsaHo37Gap5kNVXnIln4HZAJ0rsu0BH5odaylPSAIjeNhj7D3K/h/uPeNy57uqVDDMlbVE/fyaDYvvInED7cwOopn4ADGmfRGwJNpDlKKwzh2wpm4QKBgQD7ZP0fefUscs33p317Jrp/NwtwoLWwOOzOrvUn8rAEwo6m1eiT8/aYpGbSzEkCakTnN1f+yNPR+7eZy1oH5iEHGElD7GX1kQ10lXZ0DvqXuFHw5yHYy3OXLwTnffNDq/7jBi7If4mQvU/RiBXzV4sI4mOu0MKzVVqWpu9pTnPtzwKBgQDkhsX2wDvq5lZIyJ7kdEVGG9AyRepEMoWweqQJq5lw/XeAGfyebF/ifon2Q9ULTo03HOxTghIynJVAXjX+gDWCVJQfIxkrNxult0k/FXwxMkkdRI1kk5DZYWydcHN2OQiguZAxApd4YS969sHnPz3a6ExnH2xSKFr42BbmZrxGqQKBgDWXcsqKAjUmiDf6Uaq2N920y1CIQhM+H2V0MLkCaHmrTCPOyBT6ViB2M6FOgWXtZ6Du0Gh1QYqrIZm8mwXCDlpn1/6N37J46Kho0GavZsZ/OaCkdTe1L4AUOvRqijhr6xtUjxaY8aeUY/akDyHgELilImD7CfaXBIfHMJR/cAFLAoGBAKGblZ+b7kN9xIMQLR2OA/dCcAc9XiAAQn69HAgirA41kS6sMfOu3FgF+HaJHb4zPLY+4/liQJA+H2Y3uaNS84RJX75tSb0/6XNVnJzaCsuXrhDei8cWfBbeSLtF7MEsMCfJUmj2VjuxLqycmB96n7mREYbTt/yZwUu9NOSWvoqRAoGBAJM7KxJwWIWEhskSCOzn9QqN6tB/TKHXXIkPagBoMqprSogBBsQVzfvtrXlcKZN3dPjRmcRwFWAtF0ZfFSomk9JNyB7f0oNZQ4AHFfwf7PJkyjjD+jMfNJlM5qfuhqVXRd2yvFXbEgQaYeCSl4NL8TAdp1R/0qskQGA+9YofOyyK";
        try {
            String verify_result = AlipaySignature.rsaSign(AlipaySignature.getSignContent(info), publicKey, "GBK","RSA2");
            System.out.println(verify_result);
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }


    }
}
