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
package com.java110.system.cmd.system;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.registerProtocol.RegisterProtocolDto;
import com.java110.bean.dto.systemInfo.SystemInfoDto;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.dto.community.CommunityDto;
import com.java110.intf.community.ICommunityInnerServiceSMO;
import com.java110.intf.system.IRegisterProtocolV1InnerServiceSMO;
import com.java110.intf.system.ISystemInfoV1InnerServiceSMO;
import com.java110.system.AppInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类表述：查询系统隐私信息（小区联系电话、版本号、用户服务协议、隐私保护指引、版权信息）
 * 服务编码：system.listSystemPrivacy
 * 请求路劲：/iot/api/system.listSystemPrivacy
 * add by java110 team
 */
@Java110Cmd(serviceCode = "system.listSystemPrivacy")
public class ListSystemPrivacyCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(ListSystemPrivacyCmd.class);

    @Autowired
    private ICommunityInnerServiceSMO communityInnerServiceSMOImpl;

    @Autowired
    private ISystemInfoV1InnerServiceSMO systemInfoV1InnerServiceSMOImpl;

    @Autowired
    private IRegisterProtocolV1InnerServiceSMO registerProtocolV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "communityId", "小区ID不能为空");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

        String communityId = reqJson.getString("communityId");

        CommunityDto communityDto = new CommunityDto();
        communityDto.setCommunityId(communityId);
        communityDto.setPage(1);
        communityDto.setRow(1);
        List<CommunityDto> communityDtos = communityInnerServiceSMOImpl.queryCommunitys(communityDto);

        SystemInfoDto systemInfoDto = new SystemInfoDto();
        systemInfoDto.setPage(1);
        systemInfoDto.setRow(1);
        List<SystemInfoDto> systemInfoDtos = systemInfoV1InnerServiceSMOImpl.querySystemInfos(systemInfoDto);

        Map data = new HashMap();
        data.put("tel", communityDtos != null && !communityDtos.isEmpty() ? communityDtos.get(0).getTel() : "");

        if (systemInfoDtos != null && !systemInfoDtos.isEmpty()) {
            SystemInfoDto systemInfo = systemInfoDtos.get(0);
            data.put("version", AppInfo.VERSION);
            data.put("copyrightInfo", systemInfo.getCompanyName());
            data.put("companyName", systemInfo.getCompanyName());
            data.put("systemTitle", systemInfo.getSystemTitle());
            data.put("subSystemTitle", systemInfo.getSubSystemTitle());
            data.put("logoUrl", systemInfo.getLogoUrl());
            data.put("imgUrl", systemInfo.getImgUrl());
            // 协议内容从 register_protocol 表读取
            RegisterProtocolDto registerProtocolDto = new RegisterProtocolDto();
            registerProtocolDto.setStatusCd("0");
            List<RegisterProtocolDto> protocols = registerProtocolV1InnerServiceSMOImpl.queryRegisterProtocols(registerProtocolDto);
            if (protocols != null && !protocols.isEmpty()) {
                data.put("userProtocol", protocols.get(0).getUserProtocol());
                data.put("merchantProtocol", protocols.get(0).getMerchantProtocol());
            }
        }

        cmdDataFlowContext.setResponseEntity(ResultVo.createResponseEntity(data));
    }
}
