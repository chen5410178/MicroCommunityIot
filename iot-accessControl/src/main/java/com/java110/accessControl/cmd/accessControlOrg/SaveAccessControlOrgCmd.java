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
package com.java110.accessControl.cmd.accessControlOrg;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.accessControl.bmo.ISaveAccessControlFaceBMO;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.org.OrgDto;
import com.java110.bean.dto.unit.UnitDto;
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
import com.java110.doc.annotation.*;
import com.java110.intf.accessControl.IAccessControlOrgV1InnerServiceSMO;
import com.java110.intf.user.IOrgStaffRelV1InnerServiceSMO;
import com.java110.intf.user.IOrgV1InnerServiceSMO;
import com.java110.po.accessControlFloor.AccessControlFloorPo;
import com.java110.po.accessControlOrg.AccessControlOrgPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 类表述：保存
 * 服务编码：accessControlOrg.saveAccessControlOrg
 * 请求路劲：/app/accessControlOrg.SaveAccessControlOrg
 * add by 吴学文 at 2023-08-16 11:08:24 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Java110CmdDoc(title = "部门授权门禁",
        description = "用于外系统部门授权门禁功能",
        httpMethod = "post",
        url = "http://{ip}:{port}/iot/api/accessControlOrg.saveAccessControlOrg",
        resource = "accessControlDoc",
        author = "吴学文",
        serviceCode = "accessControlOrg.saveAccessControlOrg",
        seq = 8
)

@Java110ParamsDoc(params = {
        @Java110ParamDoc(name = "communityId", length = 30, remark = "小区ID"),
        @Java110ParamDoc(name = "machineIds", length = 64, type = "Array", remark = "设备编号"),
        @Java110ParamDoc(name = "orgId", length = 64, remark = "部门ID"),

})

@Java110ResponseDoc(
        params = {
                @Java110ParamDoc(name = "code", type = "int", length = 11, defaultValue = "0", remark = "返回编号，0 成功 其他失败"),
                @Java110ParamDoc(name = "msg", type = "String", length = 250, defaultValue = "成功", remark = "描述"),
        }
)

@Java110ExampleDoc(
        reqBody = "{\"machineIds\":[123123,123123],\"orgId\":\"22\",\"communityId\":\"2022081539020475\"}",
        resBody = "{'code':0,'msg':'成功'}"
)
@Java110Cmd(serviceCode = "accessControlOrg.saveAccessControlOrg")
public class SaveAccessControlOrgCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(SaveAccessControlOrgCmd.class);

    public static final String CODE_PREFIX_ID = "10";

    @Autowired
    private IAccessControlOrgV1InnerServiceSMO accessControlOrgV1InnerServiceSMOImpl;


    @Autowired
    private IOrgV1InnerServiceSMO orgV1InnerServiceSMOImpl;

    @Autowired
    private ISaveAccessControlFaceBMO saveAccessControlFaceBMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "communityId", "请求报文中未包含communityId");
        Assert.hasKeyAndValue(reqJson, "orgId", "请求报文中未包含orgId");
        Assert.hasKeyAndValue(reqJson, "startDate", "请求报文中未包含startDate");
        Assert.hasKeyAndValue(reqJson, "endDate", "请求报文中未包含endDate");
        JSONArray machineIds = reqJson.getJSONArray("machineIds");
        if (ListUtil.isNull(machineIds)) {
            throw new CmdException("未包含门禁");
        }

        String startDate = reqJson.getString("startDate");
        String endDate = reqJson.getString("endDate");


        if(!startDate.contains(":")){
            startDate += " 00:00:00";
            reqJson.put("startDate", startDate);
        }
        if(!endDate.contains(":")){
            endDate += " 23:59:59";
            reqJson.put("endDate", endDate);
        }
    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

        OrgDto orgDto = new OrgDto();
        orgDto.setOrgId(reqJson.getString("orgId"));
        List<OrgDto> orgDtos = orgV1InnerServiceSMOImpl.queryOrgs(orgDto);

        if (ListUtil.isNull(orgDtos)) {
            throw new CmdException("未包含组织");
        }

        JSONArray machineIds = reqJson.getJSONArray("machineIds");

        //todo unit

        //todo 保存 楼栋授权
        for (int machineIndex = 0; machineIndex < machineIds.size(); machineIndex++) {
            doComputeAccessControl(orgDtos.get(0), machineIds.getString(machineIndex), reqJson);
        }

        cmdDataFlowContext.setResponseEntity(ResultVo.success());
    }

    private void doComputeAccessControl(OrgDto orgDto, String machineId, JSONObject reqJson) {

        AccessControlOrgPo accessControlOrgPo = new AccessControlOrgPo();
        accessControlOrgPo.setAcoId(GenerateCodeFactory.getGeneratorId(CODE_PREFIX_ID));
        accessControlOrgPo.setOrgName(orgDto.getOrgName());
        accessControlOrgPo.setOrgId(orgDto.getOrgId());
        accessControlOrgPo.setCommunityId(reqJson.getString("communityId"));
        accessControlOrgPo.setMachineId(machineId);
        int flag = accessControlOrgV1InnerServiceSMOImpl.saveAccessControlOrg(accessControlOrgPo);

        if (flag < 1) {
            throw new CmdException("保存数据失败");
        }

        //todo  查询房屋人员信息
        saveAccessControlFaceBMOImpl.saveOrgStaff(accessControlOrgPo, machineId, reqJson.getString("startDate"), reqJson.getString("endDate"));
    }
}
