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
package com.java110.accessControl.cmd.accessControlFloor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.accessControl.bmo.ISaveAccessControlFaceBMO;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.floor.FloorDto;
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
import com.java110.core.utils.StringUtil;
import com.java110.doc.annotation.*;
import com.java110.intf.accessControl.IAccessControlFloorV1InnerServiceSMO;
import com.java110.intf.community.IFloorV1InnerServiceSMO;
import com.java110.intf.community.IUnitV1InnerServiceSMO;
import com.java110.po.accessControlFloor.AccessControlFloorPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 类表述：保存
 * 服务编码：accessControlFloor.saveAccessControlFloor
 * 请求路劲：/app/accessControlFloor.SaveAccessControlFloor
 * add by 吴学文 at 2023-08-15 11:22:51 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Java110CmdDoc(title = "楼栋授权门禁",
        description = "用于外系统楼栋授权门禁功能",
        httpMethod = "post",
        url = "http://{ip}:{port}/iot/api/accessControlFloor.saveAccessControlFloor",
        resource = "accessControlDoc",
        author = "吴学文",
        serviceCode = "accessControlFloor.saveAccessControlFloor",
        seq = 6
)

@Java110ParamsDoc(params = {
        @Java110ParamDoc(name = "communityId", length = 30, remark = "小区ID"),
        @Java110ParamDoc(name = "machineIds", length = 64, type = "Array", remark = "设备编号"),
        @Java110ParamDoc(name = "unitId", length = 64, remark = "单元编号"),
        @Java110ParamDoc(name = "floorId", length = 64, remark = "楼栋编号"),

})

@Java110ResponseDoc(
        params = {
                @Java110ParamDoc(name = "code", type = "int", length = 11, defaultValue = "0", remark = "返回编号，0 成功 其他失败"),
                @Java110ParamDoc(name = "msg", type = "String", length = 250, defaultValue = "成功", remark = "描述"),
        }
)

@Java110ExampleDoc(
        reqBody = "{\"machineIds\":[123123,123123],\"unitId\":\"22\",\"floorId\":\"22\",\"communityId\":\"2022081539020475\"}",
        resBody = "{'code':0,'msg':'成功'}"
)
@Java110Cmd(serviceCode = "accessControlFloor.saveAccessControlFloor")
public class SaveAccessControlFloorCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(SaveAccessControlFloorCmd.class);

    public static final String CODE_PREFIX_ID = "10";

    @Autowired
    private IAccessControlFloorV1InnerServiceSMO accessControlFloorV1InnerServiceSMOImpl;

    @Autowired
    private IUnitV1InnerServiceSMO unitV1InnerServiceSMOImpl;

    @Autowired
    private IFloorV1InnerServiceSMO floorV1InnerServiceSMOImpl;

    @Autowired
    private ISaveAccessControlFaceBMO saveAccessControlFaceBMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "communityId", "请求报文中未包含communityId");
        Assert.hasKeyAndValue(reqJson, "startDate", "请求报文中未包含startDate");
        Assert.hasKeyAndValue(reqJson, "endDate", "请求报文中未包含endDate");

        String floorId = reqJson.getString("floorId");
        String unitId = reqJson.getString("unitId");

        if (StringUtil.isEmpty(floorId) && StringUtil.isEmpty(unitId)) {
            throw new CmdException("未包含楼栋或者单元");
        }

        JSONArray machineIds = reqJson.getJSONArray("machineIds");
        if (ListUtil.isNull(machineIds)) {
            throw new CmdException("未包含门禁");
        }

    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {


        UnitDto unitDto = new UnitDto();
        unitDto.setFloorId(reqJson.getString("floorId"));
        unitDto.setUnitId(reqJson.getString("unitId"));
        unitDto.setCommunityId(reqJson.getString("communityId"));
        List<UnitDto> unitDtos = unitV1InnerServiceSMOImpl.queryUnits(unitDto);

        if (unitDtos == null || unitDtos.size() < 1) {
            throw new CmdException("未包含楼栋单元");
        }

        JSONArray machineIds = reqJson.getJSONArray("machineIds");

        //todo unit

        for (UnitDto tmpUnitDto : unitDtos) {
            doComputeUnit(tmpUnitDto, reqJson, machineIds);
        }


        cmdDataFlowContext.setResponseEntity(ResultVo.success());
    }

    private void doComputeUnit(UnitDto tmpUnitDto, JSONObject reqJson, JSONArray machineIds) {

        FloorDto floorDto = new FloorDto();
        floorDto.setFloorId(tmpUnitDto.getFloorId());
        floorDto.setCommunityId(tmpUnitDto.getCommunityId());
        List<FloorDto> floorDtos = floorV1InnerServiceSMOImpl.queryFloors(floorDto);

        Assert.listOnlyOne(floorDtos, "楼栋不存在");

        //todo 保存 楼栋授权
        for (int machineIndex = 0; machineIndex < machineIds.size(); machineIndex++) {
            doComputeAccessControl(tmpUnitDto, machineIds.getString(machineIndex), floorDtos,reqJson);
        }

    }

    /**
     * 计算 门禁授权
     *
     * @param tmpUnitDto
     * @param floorDtos
     * @param machineId
     */
    private void doComputeAccessControl(UnitDto tmpUnitDto, String machineId, List<FloorDto> floorDtos,JSONObject reqJson) {
        AccessControlFloorPo accessControlFloorPo = new AccessControlFloorPo();
        accessControlFloorPo.setAcfId(GenerateCodeFactory.getGeneratorId(CODE_PREFIX_ID));
        accessControlFloorPo.setFloorId(floorDtos.get(0).getFloorId());
        accessControlFloorPo.setFloorNum(floorDtos.get(0).getFloorNum());
        accessControlFloorPo.setUnitId(tmpUnitDto.getUnitId());
        accessControlFloorPo.setUnitNum(tmpUnitDto.getUnitNum());
        accessControlFloorPo.setCommunityId(floorDtos.get(0).getCommunityId());
        accessControlFloorPo.setMachineId(machineId);
        int flag = accessControlFloorV1InnerServiceSMOImpl.saveAccessControlFloor(accessControlFloorPo);

        if (flag < 1) {
            throw new CmdException("保存数据失败");
        }

        //todo  查询房屋人员信息
        saveAccessControlFaceBMOImpl.save(accessControlFloorPo, machineId,reqJson.getString("startDate"),reqJson.getString("endDate"));
    }
}
