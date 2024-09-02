package com.java110.accessControl.manufactor.adapt.accessControl;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.factory.MqttFactory;

public class test {
    public static final String CLIENT_SECRET = "3ZmAq66VBgkbnPLKYwq9";
    public static void main(String[] args) {
        String seriaNo = "88";
        String viewText = "{\n" +
                " \"plateNo\":\"" + seriaNo + "\",\n" +
                " \"phone\":\"" + seriaNo + "\",\n" +
                " \"parkCode\":\"" + "1sdfsfsdfsd" + "\",\n" +
                " \"plateColor\":\"" + "1" + "\",\n" +
                " \"name\":\"" + "mingz" + "\",\n" +
                " \"bgnDate\":\"" + "2021/08/25 00:00:00" + "\",\n" +
                " \"endDate\":\"" + "1012104273GW3JADQRBB5" + "\",\n" +
                " \"remark\":\"" + "无牌车进场手动登记" + "\",\n" +
                " \"allowIn\":1,\n" +
                " \"isNeedPay\":1,\n" +
                " \"addType\":0,\n" +
                " \"thirdVisitCode\":\"" + seriaNo + "\",\n" +
                "}";




        JSONObject reqParam = JSONObject.parseObject(viewText);

        System.out.println(reqParam.toJSONString());

    }
}
