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
package com.java110.accessControl.cmd.accessControlLog;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.owner.OwnerRoomRelDto;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.accessControlLog.AccessControlLogDto;
import com.java110.intf.accessControl.IAccessControlLogV1InnerServiceSMO;
import com.java110.intf.user.IOwnerRoomRelV1InnerServiceSMO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;


/**
 * 类表述：查询
 * 服务编码：accessControlLog.listAccessControlLog
 * 请求路劲：/app/accessControlLog.ListAccessControlLog
 * add by 吴学文 at 2023-08-21 11:51:33 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Java110Cmd(serviceCode = "accessControlLog.listAccessControlLog")
public class ListAccessControlLogCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(ListAccessControlLogCmd.class);
    @Autowired
    private IAccessControlLogV1InnerServiceSMO accessControlLogV1InnerServiceSMOImpl;

    @Autowired
    private IOwnerRoomRelV1InnerServiceSMO ownerRoomRelV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        super.validatePageInfo(reqJson);
        Assert.hasKeyAndValue(reqJson, "communityId", "未包含小区");

    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {


        AccessControlLogDto accessControlLogDto = BeanConvertUtil.covertBean(reqJson, AccessControlLogDto.class);

        accessControlLogDto.setUserId(reqJson.getString("personId"));

        //todo 如果包含房屋信息
        ifHasRoomId(reqJson, accessControlLogDto);


        int count = accessControlLogV1InnerServiceSMOImpl.queryAccessControlLogsCount(accessControlLogDto);

        List<AccessControlLogDto> accessControlLogDtos = null;

        if (count > 0) {
            accessControlLogDtos = accessControlLogV1InnerServiceSMOImpl.queryAccessControlLogs(accessControlLogDto);
        } else {
            accessControlLogDtos = new ArrayList<>();
        }

        ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) reqJson.getInteger("row")), count, accessControlLogDtos);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        cmdDataFlowContext.setResponseEntity(responseEntity);
    }

    private void ifHasRoomId(JSONObject reqJson, AccessControlLogDto accessControlLogDto) {
        if (!reqJson.containsKey("roomId") || StringUtil.isEmpty(reqJson.getString("roomId"))) {
            return;
        }

        //todo 根据房屋ID查询房屋人员信息

        OwnerRoomRelDto ownerRoomRelDto = new OwnerRoomRelDto();
        ownerRoomRelDto.setRoomId(reqJson.getString("roomId"));
        ownerRoomRelDto.setCommunityId(reqJson.getString("communityId"));
        List<OwnerRoomRelDto> ownerRoomRelDtos = ownerRoomRelV1InnerServiceSMOImpl.queryOwnerRoomRels(ownerRoomRelDto);

        if (ListUtil.isNull(ownerRoomRelDtos)) {
            return;
        }

        List<String> ownerIds = new ArrayList<>();
        for (OwnerRoomRelDto tOwnerRoomRelDto : ownerRoomRelDtos) {
            ownerIds.add(tOwnerRoomRelDto.getOwnerId());
        }

        accessControlLogDto.setUserIds(ownerIds.toArray(new String[ownerIds.size()]));

    }
}
