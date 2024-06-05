package com.java110.accessControl.manufactor.adapt.accessControl.hik;

import com.java110.accessControl.manufactor.AbstractAccessControlManufactorAdapt;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.po.accessControlFace.AccessControlFacePo;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.springframework.stereotype.Service;

@Service("hikInnerAccessControlAdapt")
public class HikInnerAccessControlAdapt extends AbstractAccessControlManufactorAdapt {
    @Override
    public boolean addUser(AccessControlDto accessControlDto, AccessControlFacePo accessControlFacePo) {
        return false;
    }

    @Override
    public boolean updateUser(AccessControlDto accessControlDto, AccessControlFacePo accessControlFacePo) {
        return false;
    }

    @Override
    public boolean deleteUser(AccessControlDto accessControlDto, AccessControlFacePo accessControlFacePo) {
        return false;
    }

    @Override
    public boolean openDoor(AccessControlDto accessControlDto) {

        String url = "http://"+accessControlDto.getMachineIp()+"/ISAPI/AccessControl/RemoteControl/door/1";
        HttpClient client = new HttpClient();

// 设置用户名和密码
        UsernamePasswordCredentials creds = new UsernamePasswordCredentials("admin", accessControlDto.getMachineMac());
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
        return true;
    }

    @Override
    public boolean restartMachine(AccessControlDto accessControlDto) {
        String url = "http://"+accessControlDto.getMachineIp()+"/ISAPI/System/reboot?childDevID=1&module=1";
        HttpClient client = new HttpClient();
        // 设置用户名和密码
        UsernamePasswordCredentials creds = new UsernamePasswordCredentials("admin", accessControlDto.getMachineMac());
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
        return true;
    }

    @Override
    public String accessControlResult(String topic, String param) {
        return null;
    }
}
