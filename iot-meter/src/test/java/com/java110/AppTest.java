package com.java110;

import com.alibaba.fastjson.JSONArray;
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
        /**
         *[{"opr_id":"112024051413551038","resolv
         * e_time":"2024-05-14 10:08:27","status":"SUCCESS","data":[{"type":42,"value":["0.09|0.00|0.00"],"dsp":"总用量：0.09 m³  本月用量：0.00 m³ 阀门状态：关阀 表类型：远程预付费 购买次数：0 累计消费金额：0.00 元 剩
         * 金额:0.00 元 电池电压：3.6V 信号强度：-88"}]}
         */
        //double degree = contentObject.getJSONArray("data").getJSONObject(0).getJSONArray("value").getDouble(0);

        String aa = "[{\"opr_id\":\"112024051484290018\",\"resolve_time\":\"2024-05-14 10:33:28\",\"status\":\"SUCCESS\",\"data\":[{\"type\":42,\"value\":[\"0.25|0.10|54.45\"],\"dsp\":\"总用量：0.25 m³  本月用量：0.10 m³ 阀门状态：开阀 表类型：远程预付费 购买次数：2 累计消费金额：0.55 元 剩余金额:54.45 元 电池电压：3.8V 信号强度：-91\"}]}]";
        JSONObject contentObject = JSONArray.parseArray(aa).getJSONObject(0);
        String value = contentObject.getJSONArray("data").getJSONObject(0).getJSONArray("value").getString(0);
        String[] values = value.split("\\|", 3);
        String degree = "0.0";
        if (values.length == 3) {
            degree = values[2];
        }
        String dsp = contentObject.getJSONArray("data").getJSONObject(0).getString("dsp");
        //剩余金额
        String remainingAmount = "0";
        if (dsp.contains("剩余金额")) {
            remainingAmount = dsp.substring(dsp.indexOf("剩余金额:") + 5);
            remainingAmount = remainingAmount.substring(0,remainingAmount.indexOf("元"));
        }
        System.out.println(remainingAmount);
        System.out.println(degree);

    }
}
