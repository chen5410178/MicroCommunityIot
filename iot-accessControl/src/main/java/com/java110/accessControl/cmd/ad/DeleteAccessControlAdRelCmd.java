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

import com.alibaba.fastjson.JSONObject;
import com.java110.accessControl.manufactor.IAccessControlManufactor;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.annotation.Java110Transactional;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.ListUtil;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.dto.accessControlAdRel.AccessControlAdRelDto;
import com.java110.dto.hardwareManufacturer.HardwareManufacturerDto;
import com.java110.intf.accessControl.IAccessControlAdRelV1InnerServiceSMO;
import com.java110.intf.accessControl.IAccessControlV1InnerServiceSMO;
import com.java110.intf.system.IHardwareManufacturerV1InnerServiceSMO;
import com.java110.po.accessControlAdRel.AccessControlAdRelPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 类表述：删除
 * 服务编码：accessControlAdRel.deleteAccessControlAdRel
 * 请求路劲：/app/accessControlAdRel.DeleteAccessControlAdRel
 * add by 吴学文 at 2024-07-16 23:23:28 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Java110Cmd(serviceCode = "ad.deleteAccessControlAdRel")
public class DeleteAccessControlAdRelCmd extends Cmd {
    private static Logger logger = LoggerFactory.getLogger(DeleteAccessControlAdRelCmd.class);

    @Autowired
    private IAccessControlAdRelV1InnerServiceSMO accessControlAdRelV1InnerServiceSMOImpl;

    @Autowired
    private IAccessControlV1InnerServiceSMO accessControlV1InnerServiceSMOImpl;

    @Autowired
    private IHardwareManufacturerV1InnerServiceSMO hardwareManufacturerV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "relId", "relId不能为空");
        Assert.hasKeyAndValue(reqJson, "communityId", "communityId不能为空");

    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

        AccessControlAdRelDto accessControlAdRelDto = new AccessControlAdRelDto();
        accessControlAdRelDto.setRelId(reqJson.getString("relId"));
        accessControlAdRelDto.setCommunityId(reqJson.getString("communityId"));
        List<AccessControlAdRelDto> accessControlAdRelDtos = accessControlAdRelV1InnerServiceSMOImpl.queryAccessControlAdRels(accessControlAdRelDto);

        if (ListUtil.isNull(accessControlAdRelDtos)) {
            return;
        }
        AccessControlAdRelPo accessControlAdRelPo = BeanConvertUtil.covertBean(reqJson, AccessControlAdRelPo.class);
        int flag = accessControlAdRelV1InnerServiceSMOImpl.deleteAccessControlAdRel(accessControlAdRelPo);

        if (flag < 1) {
            throw new CmdException("删除数据失败");
        }
        AccessControlDto accessControlDto = new AccessControlDto();
        accessControlDto.setMachineId(accessControlAdRelDtos.get(0).getMachineId());
        accessControlDto.setCommunityId(reqJson.getString("communityId"));
        List<AccessControlDto> accessControlDtos = accessControlV1InnerServiceSMOImpl.queryAccessControls(accessControlDto);
        if (ListUtil.isNull(accessControlDtos)) {
            return;
        }

        //todo 调用门禁适配器 同步广告
        HardwareManufacturerDto hardwareManufacturerDto = new HardwareManufacturerDto();
        hardwareManufacturerDto.setHmId(accessControlDtos.get(0).getImplBean());
        List<HardwareManufacturerDto> hardwareManufacturerDtos = hardwareManufacturerV1InnerServiceSMOImpl.queryHardwareManufacturers(hardwareManufacturerDto);

        Assert.listOnlyOne(hardwareManufacturerDtos, "协议不存在");

        IAccessControlManufactor accessControlManufactor = ApplicationContextFactory.getBean(hardwareManufacturerDtos.get(0).getProtocolImpl(), IAccessControlManufactor.class);
        accessControlManufactor.addAd(accessControlDtos.get(0), accessControlAdRelPo);


        cmdDataFlowContext.setResponseEntity(ResultVo.success());
    }
}
