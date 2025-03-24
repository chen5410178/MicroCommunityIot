package com.java110.job.task;

import com.java110.bean.dto.task.TaskDto;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.utils.ListUtil;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.dto.community.CommunityDto;
import com.java110.intf.accessControl.IAccessControlResultV1InnerServiceSMO;
import com.java110.intf.accessControl.IAccessControlV1InnerServiceSMO;
import com.java110.job.quartz.TaskSystemQuartz;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @program: MicroCommunity
 * @description: 微耕门禁定时任务
 * @author: zcc
 * @create: 2020-06-15 13:35
 **/
@Component
public class AccessControlTaskTemplate extends TaskSystemQuartz {

    private static Logger logger = LoggerFactory.getLogger(AccessControlTaskTemplate.class);

    @Autowired
    private IAccessControlV1InnerServiceSMO accessControlV1InnerServiceSMOImpl;


    @Autowired
    private IAccessControlResultV1InnerServiceSMO accessControlResultV1InnerServiceSMOImpl;


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
        List<AccessControlDto> accessControlDtos = accessControlV1InnerServiceSMOImpl.queryAccessControls(accessControlDto);

        if(ListUtil.isNull(accessControlDtos)){
            return;
        }

        for(AccessControlDto tmpAccessControlDto:accessControlDtos){
            accessControlResultV1InnerServiceSMOImpl.task(tmpAccessControlDto);
        }
    }
}
