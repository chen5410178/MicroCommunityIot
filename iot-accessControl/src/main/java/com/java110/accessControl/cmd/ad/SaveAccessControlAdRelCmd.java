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
package com.java110.accessControl.cmd.ad;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.annotation.Java110Transactional;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.ListUtil;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.intf.accessControl.IAccessControlAdRelV1InnerServiceSMO;
import com.java110.intf.accessControl.IAccessControlV1InnerServiceSMO;
import com.java110.po.accessControlAdRel.AccessControlAdRelPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 类表述：保存
 * 服务编码：accessControlAdRel.saveAccessControlAdRel
 * 请求路劲：/app/accessControlAdRel.SaveAccessControlAdRel
 * add by 吴学文 at 2024-07-16 23:23:28 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Java110Cmd(serviceCode = "ad.saveAccessControlAdRel")
public class SaveAccessControlAdRelCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(SaveAccessControlAdRelCmd.class);

    public static final String CODE_PREFIX_ID = "10";

    @Autowired
    private IAccessControlAdRelV1InnerServiceSMO accessControlAdRelV1InnerServiceSMOImpl;

    @Autowired
    private IAccessControlV1InnerServiceSMO accessControlV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "adId", "请求报文中未包含adId");
        Assert.hasKeyAndValue(reqJson, "communityId", "请求报文中未包含communityId");

        if (!reqJson.containsKey("machineIds")) {
            throw new CmdException("未包含门禁");
        }

        JSONArray machineIds = reqJson.getJSONArray("machineIds");

        if (ListUtil.isNull(machineIds)) {
            throw new CmdException("未包含门禁");
        }

    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {


        JSONArray machineIds = reqJson.getJSONArray("machineIds");

        List<AccessControlDto> accessControlDtos = null;
        AccessControlAdRelPo accessControlAdRelPo = null;
        int flag = 0;
        for (int machineIndex = 0; machineIndex < machineIds.size(); machineIndex++) {
            AccessControlDto accessControlDto = new AccessControlDto();
            accessControlDto.setMachineId(machineIds.getString(machineIndex));
            accessControlDto.setCommunityId(reqJson.getString("communityId"));
            accessControlDtos = accessControlV1InnerServiceSMOImpl.queryAccessControls(accessControlDto);
            if (ListUtil.isNull(accessControlDtos)) {
                continue;
            }
            accessControlAdRelPo = new AccessControlAdRelPo();
            accessControlAdRelPo.setRelId(GenerateCodeFactory.getGeneratorId(CODE_PREFIX_ID));
            accessControlAdRelPo.setAdId(reqJson.getString("adId"));
            accessControlAdRelPo.setMachineId(accessControlDtos.get(0).getMachineId());
            accessControlAdRelPo.setMachineCode(accessControlDtos.get(0).getMachineCode());
            accessControlAdRelPo.setRemark(reqJson.getString("remark"));
            accessControlAdRelPo.setCommunityId(accessControlDtos.get(0).getCommunityId());
            accessControlAdRelPo.setMachineName(accessControlDtos.get(0).getMachineName());
            flag = accessControlAdRelV1InnerServiceSMOImpl.saveAccessControlAdRel(accessControlAdRelPo);

            if (flag < 1) {
                throw new CmdException("保存数据失败");
            }

        }

        cmdDataFlowContext.setResponseEntity(ResultVo.success());
    }
}
