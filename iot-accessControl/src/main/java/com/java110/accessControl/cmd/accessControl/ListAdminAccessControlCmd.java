/*
 * Copyright 2017-2020 吴学文 and java110 team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.java110.accessControl.cmd.accessControl;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.*;
import com.java110.doc.annotation.*;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.dto.community.CommunityDto;
import com.java110.intf.accessControl.IAccessControlV1InnerServiceSMO;
import com.java110.intf.community.ICommunityV1InnerServiceSMO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * 类表述：查询
 * 服务编码：accessControl.listAccessControl
 * 请求路劲：/app/accessControl.ListAccessControl
 * add by 吴学文 at 2023-08-15 02:16:27 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Java110Cmd(serviceCode = "accessControl.listAdminAccessControl")
public class ListAdminAccessControlCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(ListAdminAccessControlCmd.class);
    @Autowired
    private IAccessControlV1InnerServiceSMO accessControlV1InnerServiceSMOImpl;

    @Autowired
    private ICommunityV1InnerServiceSMO communityV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        super.validatePageInfo(reqJson);
        super.assertAdmin(cmdDataFlowContext);

    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

        AccessControlDto accessControlDto = BeanConvertUtil.covertBean(reqJson, AccessControlDto.class);

        int count = accessControlV1InnerServiceSMOImpl.queryAccessControlsCount(accessControlDto);

        List<AccessControlDto> accessControlDtos = null;

        if (count > 0) {
            accessControlDtos = accessControlV1InnerServiceSMOImpl.queryAccessControls(accessControlDto);
            freshMachineStateName(accessControlDtos);
        } else {
            accessControlDtos = new ArrayList<>();
        }
        refreshCommunityName(accessControlDtos);

        ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) reqJson.getInteger("row")), count, accessControlDtos);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        cmdDataFlowContext.setResponseEntity(responseEntity);
    }

    private void refreshCommunityName(List<AccessControlDto> accessControlDtos) {
        if(ListUtil.isNull(accessControlDtos)){
            return;
        }

        List<String> communityIds = new ArrayList<>();
        for (AccessControlDto accessControlDto : accessControlDtos) {
            communityIds.add(accessControlDto.getCommunityId());
        }

        if(ListUtil.isNull(communityIds)){
            return ;
        }
        CommunityDto communityDto = new CommunityDto();
        communityDto.setCommunityIds(communityIds.toArray(new String[communityIds.size()]));
        List<CommunityDto> communityDtos = communityV1InnerServiceSMOImpl.queryCommunitys(communityDto);
        if(ListUtil.isNull(communityDtos)){
            return;
        }
        for (AccessControlDto accessControlDto : accessControlDtos) {
            for (CommunityDto tCommunityDto : communityDtos) {
                if (!accessControlDto.getCommunityId().equals(tCommunityDto.getCommunityId())) {
                    continue;
                }
                accessControlDto.setCommunityName(tCommunityDto.getName());
            }
        }
    }

    private void freshMachineStateName(List<AccessControlDto> accessControlDtos) {

        if (ListUtil.isNull(accessControlDtos)) {
            return;
        }
        for (AccessControlDto accessControlDto : accessControlDtos) {
            String heartbeatTime = accessControlDto.getHeartbeatTime();
            try {

                if (StringUtil.isEmpty(heartbeatTime)) {
                    accessControlDto.setStateName("设备离线");
                    continue;
                }
                Date hTime = DateUtil.getDateFromString(heartbeatTime, DateUtil.DATE_FORMATE_STRING_B);
                //todo 宇凡设备特殊处理，因为宇凡没有心跳
                if ("4".equals(accessControlDto.getImplBean())) {
                    accessControlDto.setStateName("设备在线");
                    if ("2020-01-01".equals(DateUtil.getFormatTimeStringB(hTime))){
                        accessControlDto.setStateName("设备离线");
                    }
                    continue;
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(DateUtil.getDateFromString(heartbeatTime, DateUtil.DATE_FORMATE_STRING_A));
                calendar.add(Calendar.MINUTE, 2);
                if (calendar.getTime().getTime() <= DateUtil.getCurrentDate().getTime()) {
                    accessControlDto.setStateName("设备离线");
                } else {
                    accessControlDto.setStateName("设备在线");
                }

            } catch (ParseException e) {
                e.printStackTrace();
                accessControlDto.setStateName("设备离线");
            }
        }
    }
}
