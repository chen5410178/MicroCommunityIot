package com.java110.hal.http.yuFan;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.LoggerFactory;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.dto.callingContact.CallingContactDto;
import com.java110.intf.accessControl.IAccessControlV1InnerServiceSMO;
import com.java110.intf.accessControl.ICallingContactV1InnerServiceSMO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
public class yuFanHal {

    Logger logger = LoggerFactory.getLogger(yuFanHal.class);

    @Autowired
    private ICallingContactV1InnerServiceSMO callingContactV1InnerServiceSMOImpl;

    @Autowired
    private IAccessControlV1InnerServiceSMO accessControlV1InnerServiceSMOImpl;


    /**
     * 门禁室内机设备查询
     *
     * @param request
     * @return
     */
    @RequestMapping(path = "/api/sipex/callback/offline/msg", method = RequestMethod.POST)
    public ResponseEntity<String> sipexAccountQuery(@RequestBody String reqJson,
                                                    HttpServletRequest request) {
        AccessControlDto accessControlDto = new AccessControlDto();
        JSONObject jsonObject = JSON.parseObject(reqJson);
        String accountObj = jsonObject.getString("deviceKey");
        String reqId = jsonObject.getString("reqId");
        String type = jsonObject.getString("type");
        accessControlDto.setMachineCode(accountObj);
        int count = accessControlV1InnerServiceSMOImpl.queryAccessControlsCount(accessControlDto);
        List<AccessControlDto> accessControlDtos = null;
        List<CallingContactDto> callingContactDtos = null;
        JSONObject contact = new JSONObject();
        if (count > 0) {
            accessControlDtos = accessControlV1InnerServiceSMOImpl.queryAccessControls(accessControlDto);
            contact.put("password", accessControlDtos.get(0).getRemark());
            contact.put("accountName", accessControlDtos.get(0).getMachineName());
            contact.put("accountObj", accountObj);
            contact.put("account", accessControlDtos.get(0).getCallingNum());
            contact.put("address", accessControlDtos.get(0).getIpAddr() + ":" + accessControlDtos.get(0).getPort());
            CallingContactDto callingContactDto = new CallingContactDto();
            callingContactDto.setMachineId(accessControlDtos.get(0).getMachineId());
            if ("QueryContacts".equals(type)) {
                type = "ResponseContacts";
                callingContactDtos = callingContactV1InnerServiceSMOImpl.queryCallingContacts(callingContactDto);
                JSONArray deviceList = new JSONArray();
                for (CallingContactDto callingContact : callingContactDtos) {
                    JSONObject device = new JSONObject();
                    device.put("address", callingContact.getIpAddr() + ":" + callingContact.getPort());
                    device.put("accountName", callingContact.getDeviceName());
                    device.put("accountObj", callingContact.getCallingNum());
                    device.put("contactType", callingContact.getCallingType());
                    device.put("labelName", callingContact.getDeviceName());
                    device.put("account", callingContact.getCallingNum());
                    device.put("password", callingContact.getRemark());
                    device.put("aliasCode", callingContact.getCallingNum());
                    device.put("gmtCreate", null);
                    deviceList.add(device);
                }
                contact.put("contacts", deviceList);
            } else {
                type = "ResponseSelfSipAccount";
            }
        } else {
            throw new CmdException("未查询到设备信息。");
        }
        JSONObject reobj = new JSONObject();
        JSONObject data = new JSONObject();
        reobj.put("result", 1);
        reobj.put("message", "OK");
        reobj.put("success", true);

        data.put("reqId", reqId);
        data.put("code", "200");
        data.put("msg", "OK");
        data.put("type", type);
        data.put("deviceKey", accountObj);
        data.put("content", contact.toJSONString());
        reobj.put("data", data);
        ResponseEntity<String> responseEntity = new ResponseEntity<String>(reobj.toString(), HttpStatus.OK);
        return responseEntity;
    }

}
