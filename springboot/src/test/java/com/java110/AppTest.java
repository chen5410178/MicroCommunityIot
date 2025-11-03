package com.java110;

import com.alibaba.fastjson.JSONObject;
import com.java110.barrier.engine.adapt.zhenshiMqtt.http.HttpRequest;
import com.java110.core.utils.DateUtil;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import java.io.UnsupportedEncodingException;
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

        String port = String.format("%X", 15);
        System.out.println(port);
    }

    public void testOpenDoor() throws UnsupportedEncodingException {
        String url = "http://192.168.31.41:80/ISAPI/AccessControl/RemoteControl/door/1";
        HttpClient client = new HttpClient();

// 设置用户名和密码

        UsernamePasswordCredentials creds = new UsernamePasswordCredentials("admin", "wuxw2015");
        client.getState().setCredentials(AuthScope.ANY, creds);
        PutMethod method = new PutMethod(url);

        method.setDoAuthentication(true);

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "\n" +
                "<RemoteControlDoor xmlns=\"http://www.isapi.org/ver20/XMLSchema\" version=\"2.0\">\n" +
                "\n" +
                " <!--req, object, 远程控门, attr:version{req, string, 协议版本}-->\n" +
                "\n" +
                " <cmd>open</cmd>\n" +
                "\n" +
                "</RemoteControlDoor>";

        StringRequestEntity requestEntity = new StringRequestEntity(xml);
        method.setRequestEntity(requestEntity);



        try {
            int statusCode = client.executeMethod(method);

            byte[] responseData = method.getResponseBodyAsString().getBytes(method.getResponseCharSet());
            String strResponseData = new String(responseData, "utf-8");
            method.releaseConnection();

// 输出接收的消息

            System.out.println(strResponseData);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void testRestart() throws UnsupportedEncodingException {
        String url = "http://192.168.31.41:80/ISAPI/System/reboot?childDevID=1&module=1";
        HttpClient client = new HttpClient();

// 设置用户名和密码

        UsernamePasswordCredentials creds = new UsernamePasswordCredentials("admin", "wuxw2015");
        client.getState().setCredentials(AuthScope.ANY, creds);
        PutMethod method = new PutMethod(url);

        method.setDoAuthentication(true);




        try {
            int statusCode = client.executeMethod(method);

            byte[] responseData = method.getResponseBodyAsString().getBytes(method.getResponseCharSet());
            String strResponseData = new String(responseData, "utf-8");
            method.releaseConnection();

// 输出接收的消息

            System.out.println(strResponseData);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
