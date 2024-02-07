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
package com.java110.user.cmd.privilegeGroup;

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
import com.java110.dto.privilegeRel.PrivilegeRelDto;
import com.java110.dto.privilegeUser.PrivilegeUserDto;
import com.java110.intf.user.IPrivilegeGroupV1InnerServiceSMO;
import com.java110.intf.user.IPrivilegeRelV1InnerServiceSMO;
import com.java110.intf.user.IPrivilegeUserV1InnerServiceSMO;
import com.java110.po.privilegeGroup.PrivilegeGroupPo;
import com.java110.po.privilegeRel.PrivilegeRelPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 类表述：删除
 * 服务编码：privilegeGroup.deletePrivilegeGroup
 * 请求路劲：/app/privilegeGroup.DeletePrivilegeGroup
 * add by 吴学文 at 2023-02-11 02:18:30 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Java110Cmd(serviceCode = "privilegeGroup.deletePrivilegeGroup")
public class DeletePrivilegeGroupCmd extends Cmd {
    private static Logger logger = LoggerFactory.getLogger(DeletePrivilegeGroupCmd.class);

    @Autowired
    private IPrivilegeGroupV1InnerServiceSMO privilegeGroupV1InnerServiceSMOImpl;

    @Autowired
    private IPrivilegeUserV1InnerServiceSMO privilegeUserV1InnerServiceSMOImpl;

    @Autowired
    private IPrivilegeRelV1InnerServiceSMO privilegeRelV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson,"pgId","角色不存在");

    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

        PrivilegeUserDto privilegeUserDto = new PrivilegeUserDto();
        privilegeUserDto.setpId(reqJson.getString("pgId"));
        List<PrivilegeUserDto> privilegeUserDtos = privilegeUserV1InnerServiceSMOImpl.queryPrivilegeUsers(privilegeUserDto);
        if(!ListUtil.isNull(privilegeUserDtos)){
            throw new CmdException("该角色下有关联员工，请先删除关联员工！");
        }
        PrivilegeGroupPo privilegeGroupPo = BeanConvertUtil.covertBean(reqJson,PrivilegeGroupPo.class);
        int flag = privilegeGroupV1InnerServiceSMOImpl.deletePrivilegeGroup(privilegeGroupPo);
        if(flag  < 1){
            throw new CmdException("删除失败");
        }
        PrivilegeRelDto privilegeRelDto = new PrivilegeRelDto();
        privilegeRelDto.setPgId(reqJson.getString("pgId"));
        List<PrivilegeRelDto> privilegeRelDtos = privilegeRelV1InnerServiceSMOImpl.queryPrivilegeRels(privilegeRelDto);
        if(privilegeRelDtos == null || privilegeRelDtos.size()<1){
            return ;
        }
        PrivilegeRelPo privilegeRelPo = null;
        for(PrivilegeRelDto tmpPrivilegeDto: privilegeRelDtos){
            privilegeRelPo = new PrivilegeRelPo();
            privilegeRelPo.setRelId(tmpPrivilegeDto.getRelId());
            flag = privilegeRelV1InnerServiceSMOImpl.deletePrivilegeRel(privilegeRelPo);
            if(flag <1){
                throw new CmdException("删除失败");
            }
        }
    }
}
