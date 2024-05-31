package com.java110.openapi.bmo.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cache.CommonCache;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.DistributedLock;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.user.UserDto;
import com.java110.intf.lamp.ITelCallLogV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import com.java110.openapi.bmo.IIotCommonApiBmo;
import com.java110.po.tel.TelCallLogPo;
import org.apache.catalina.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Java110Cmd(serviceCode = "telMachineMsgBmoImpl")
public class TelMachineMsgBmoImpl implements IIotCommonApiBmo {
    private static Logger logger = LoggerFactory.getLogger(TelMachineMsgBmoImpl.class);

    @Autowired
    private ITelCallLogV1InnerServiceSMO telCallLogV1InnerServiceSMOImpl;

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Override
    public void validate(ICmdDataFlowContext context, JSONObject reqJson) {

        Assert.hasKeyAndValue(reqJson, "communityId", "未包含小区ID");
        Assert.hasKeyAndValue(reqJson, "machineId", "未包含设备ID");


    }

    @Override
    public void doCmd(ICmdDataFlowContext context, JSONObject reqJson) {

        logger.debug("接受到话机消息：{}", reqJson.toJSONString());

        String name = reqJson.getString("name");

        if (!"Call".equals(name)) {
            return;
        }

        JSONObject param = reqJson.getJSONObject("param");

        String status = param.getString("status");

        if (!"CallEnd".equals(status)) {
            return;
        }

        //[NoAnswer|CallId:18909711443|CallTime:|TalkTime:00:00:00|Key:|ClientOnHook|CCID:89860319949712767834]
        String cdr = param.getString("CDR");

        cdr = cdr.replaceAll("\\[", "").replaceAll("]", "");

        String[] cdrs = cdr.split("\\|");

        if (cdrs.length != 7) {
            return;
        }
        String state = cdrs[0];
        String tel = cdrs[1].replaceAll("CallId:", "");
        String talkTime = cdrs[3].replaceAll("TalkTime:", "");
        String ccid = cdrs[6].replaceAll("CCID:", "");

        String callType = "CALL_IN";
        String machineId = reqJson.getString("machineId");

        String telCache = CommonCache.getValue(tel + "::" + machineId);

        if (!StringUtil.isEmpty(telCache)) {
            return;
        }


        String requestId = DistributedLock.getLockUUID();
        String key = tel + this.getClass().getName() + machineId;
        try {
            DistributedLock.waitGetDistributedLock(key, requestId);
            telCache = CommonCache.getValue(tel + "::" + machineId);
            if (!StringUtil.isEmpty(telCache)) {
                return;
            }

            //todo 缓存十秒，10秒内的请求 要拒绝 以免重复记录问题
            CommonCache.setValue(tel + "::" + machineId, tel, 10);


        } finally {
            DistributedLock.releaseDistributedLock(key, requestId);
        }

        UserDto userDto = new UserDto();
        userDto.setUserId(reqJson.getString("propertyUserId"));
        List<UserDto> userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);
        if(ListUtil.isNull(userDtos)){
            return ;
        }

        TelCallLogPo telCallLogPo = new TelCallLogPo();
        telCallLogPo.setPersonName(userDtos.get(0).getName());
        telCallLogPo.setTalkTime(talkTime);
        telCallLogPo.setMachineId(reqJson.getString("machineId"));
        telCallLogPo.setCcid(ccid);
        telCallLogPo.setVoicePath("无");
        telCallLogPo.setClId(GenerateCodeFactory.getGeneratorId("11"));
        telCallLogPo.setTel(tel);
        telCallLogPo.setPersonId(userDtos.get(0).getUserId());
        telCallLogPo.setState(state);
        telCallLogPo.setCommunityId(reqJson.getString("communityId"));
        telCallLogPo.setCallType(callType);

        telCallLogV1InnerServiceSMOImpl.saveTelCallLog(telCallLogPo);



    }
}
