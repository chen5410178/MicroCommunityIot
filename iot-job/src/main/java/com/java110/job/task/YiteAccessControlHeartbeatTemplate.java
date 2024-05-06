package com.java110.job.task;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.dto.task.TaskDto;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.factory.MqttFactory;
import com.java110.core.utils.ListUtil;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.dto.community.CommunityDto;
import com.java110.intf.accessControl.IAccessControlV1InnerServiceSMO;
import com.java110.job.quartz.TaskSystemQuartz;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * @program: MicroCommunity
 * @description: 伊特门禁 心跳
 * @author: zcc
 * @create: 2020-06-15 13:35
 **/
@Component
public class YiteAccessControlHeartbeatTemplate extends TaskSystemQuartz {

    private static Logger logger = LoggerFactory.getLogger(YiteAccessControlHeartbeatTemplate.class);

    @Autowired
    private IAccessControlV1InnerServiceSMO accessControlV1InnerServiceSMOImpl;



    @Override
    protected void process(TaskDto taskDto) {
        logger.debug("开始执行微信模板信息推送" + taskDto.toString());

        // 获取小区
        List<CommunityDto> communityDtos = getAllCommunity();

        for (CommunityDto communityDto : communityDtos) {
            try {
                publishMsg(taskDto, communityDto);
            } catch (Exception e) {
                logger.error("推送消息失败", e);
            }
        }
    }

    private void publishMsg(TaskDto taskDto, CommunityDto communityDto) throws Exception {


        AccessControlDto accessControlDto = new AccessControlDto();
        accessControlDto.setCommunityId(communityDto.getCommunityId());
        accessControlDto.setImplBean("25");
        List<AccessControlDto> accessControlDtos = accessControlV1InnerServiceSMOImpl.queryAccessControls(accessControlDto);

        if(ListUtil.isNull(accessControlDtos)){
            return;
        }

        /**
         * {
         * 	"f": "server_php",
         * 	"t": "1fe02032aeab84eb021cdbfabcf26966",  //登录接口里的 订阅主题的群发主题 （项目UID）
         * 	"c": "heartbeat",
         * 	"m": {
         * 		"d": 36,   //自定义 项目编号
         * 		"t": "fa51d3e321508a7d9ad3252058df9ee6"   //自定义 消息UID
         * 	    }
         * }
         */

        JSONObject paramIn = new JSONObject();
        paramIn.put("t", communityDto.getCommunityId());
        paramIn.put("c", "heartbeat");
        paramIn.put("f", "server_php");

        JSONObject m = new JSONObject();
        m.put("d", communityDto.getCommunityId());
        m.put("t", GenerateCodeFactory.getUUID());
        paramIn.put("m", m);

        MqttFactory.publish("zghl_door/" + communityDto.getCommunityId(), paramIn.toJSONString());
    }
}
