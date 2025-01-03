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
package com.java110.accessControl.cmd.accessControlFace;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.accessControl.bmo.ISaveAccessControlFaceBMO;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.file.FileRelDto;
import com.java110.bean.dto.floor.FloorDto;
import com.java110.bean.dto.owner.OwnerDto;
import com.java110.bean.dto.unit.UnitDto;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.annotation.Java110Transactional;
import com.java110.core.cache.MappingCache;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.constant.MappingConstant;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.doc.annotation.*;
import com.java110.dto.accessControlFace.AccessControlFaceDto;
import com.java110.intf.accessControl.IAccessControlFaceV1InnerServiceSMO;
import com.java110.intf.accessControl.IAccessControlFloorV1InnerServiceSMO;
import com.java110.intf.community.IFloorV1InnerServiceSMO;
import com.java110.intf.community.IUnitV1InnerServiceSMO;
import com.java110.intf.system.IFileRelInnerServiceSMO;
import com.java110.intf.user.IOwnerV1InnerServiceSMO;
import com.java110.po.accessControlFace.AccessControlFacePo;
import com.java110.po.accessControlFloor.AccessControlFloorPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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
@Java110CmdDoc(title = "人员授权门禁",
        description = "用于外系统人员授权门禁功能",
        httpMethod = "post",
        url = "http://{ip}:{port}/iot/api/accessControlFace.saveAccessControlFace",
        resource = "accessControlDoc",
        author = "吴学文",
        serviceCode = "accessControlFace.saveAccessControlFace",
        seq = 6
)

@Java110ParamsDoc(params = {
        @Java110ParamDoc(name = "communityId", length = 30, remark = "小区ID"),
        @Java110ParamDoc(name = "machineIds", length = 64, type = "Array", remark = "设备编号"),
        @Java110ParamDoc(name = "ownerId", length = 64, remark = "人员编号"),

})

@Java110ResponseDoc(
        params = {
                @Java110ParamDoc(name = "code", type = "int", length = 11, defaultValue = "0", remark = "返回编号，0 成功 其他失败"),
                @Java110ParamDoc(name = "msg", type = "String", length = 250, defaultValue = "成功", remark = "描述"),
        }
)

@Java110ExampleDoc(
        reqBody = "{\"machineIds\":[123123,123123],\"ownerId\":\"22\",\"communityId\":\"2022081539020475\"}",
        resBody = "{'code':0,'msg':'成功'}"
)
@Java110Cmd(serviceCode = "accessControlFace.saveAccessControlFace")
public class SaveAccessControlFaceCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(SaveAccessControlFaceCmd.class);

    public static final String CODE_PREFIX_ID = "10";

    @Autowired
    private IAccessControlFloorV1InnerServiceSMO accessControlFloorV1InnerServiceSMOImpl;

    @Autowired
    private IOwnerV1InnerServiceSMO ownerV1InnerServiceSMOImpl;


    @Autowired
    private ISaveAccessControlFaceBMO saveAccessControlFaceBMOImpl;

    @Autowired
    private IAccessControlFaceV1InnerServiceSMO accessControlFaceV1InnerServiceSMOImpl;

    @Autowired
    private IFileRelInnerServiceSMO fileRelInnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "communityId", "请求报文中未包含communityId");
        Assert.hasKeyAndValue(reqJson, "ownerId", "请求报文中未包含ownerId");
        Assert.hasKeyAndValue(reqJson, "startDate", "请求报文中未包含startDate");
        Assert.hasKeyAndValue(reqJson, "endDate", "请求报文中未包含endDate");

        JSONArray machineIds = reqJson.getJSONArray("machineIds");
        if (ListUtil.isNull(machineIds)) {
            throw new CmdException("未包含门禁");
        }
        for (int machineIndex = 0; machineIndex < machineIds.size(); machineIndex++) {

            AccessControlFaceDto accessControlFaceDto = new AccessControlFaceDto();
            accessControlFaceDto.setCommunityId(reqJson.getString("communityId"));
            accessControlFaceDto.setPersonId(reqJson.getString("ownerId"));
            accessControlFaceDto.setMachineId(machineIds.getString(machineIndex));
            int flag = accessControlFaceV1InnerServiceSMOImpl.queryAccessControlFacesCount(accessControlFaceDto);

            if (flag > 0) {
                throw new CmdException("人员已经授权，请勿重复授权");
            }
        }


    }

    @Override
    // @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {


        OwnerDto ownerDto = new OwnerDto();
        ownerDto.setMemberId(reqJson.getString("ownerId"));
        ownerDto.setCommunityId(reqJson.getString("communityId"));
        List<OwnerDto> ownerDtos = ownerV1InnerServiceSMOImpl.queryOwners(ownerDto);

        if (ListUtil.isNull(ownerDtos)) {
            throw new CmdException("未包含人员");
        }

        JSONArray machineIds = reqJson.getJSONArray("machineIds");

        //todo unit


        doComputeOwner(ownerDtos.get(0), reqJson, machineIds);


        cmdDataFlowContext.setResponseEntity(ResultVo.success());
    }

    private void doComputeOwner(OwnerDto tmpOwnerDto, JSONObject reqJson, JSONArray machineIds) {

        //todo 保存 楼栋授权
        for (int machineIndex = 0; machineIndex < machineIds.size(); machineIndex++) {
            doComputeAccessControl(tmpOwnerDto, machineIds.getString(machineIndex), reqJson);
        }

    }

    /**
     * 计算 门禁授权
     *
     * @param machineId
     */
    private void doComputeAccessControl(OwnerDto tmpOwnerDto, String machineId, JSONObject reqJson) {
        String imgUrl = MappingCache.getValue(MappingConstant.FILE_DOMAIN, "IMG_PATH");

        FileRelDto fileRelDto = new FileRelDto();
        fileRelDto.setObjId(tmpOwnerDto.getMemberId());
        fileRelDto.setRelTypeCd("10000"); //人员照片
        List<FileRelDto> fileRelDtos = fileRelInnerServiceSMOImpl.queryFileRels(fileRelDto);
        if (!ListUtil.isNull(fileRelDtos)) {
            String url = fileRelDtos.get(0).getFileRealName();
            if (url.startsWith("http")) {
                tmpOwnerDto.setUrl(fileRelDtos.get(0).getFileRealName());
            } else {
                tmpOwnerDto.setUrl(imgUrl + fileRelDtos.get(0).getFileRealName());
            }
        }


        AccessControlFacePo accessControlFacePo = new AccessControlFacePo();
        accessControlFacePo.setFacePath(tmpOwnerDto.getUrl());
        accessControlFacePo.setCommunityId(tmpOwnerDto.getCommunityId());
        accessControlFacePo.setIdNumber(tmpOwnerDto.getIdCard());
        accessControlFacePo.setComeId(tmpOwnerDto.getMemberId());
        accessControlFacePo.setComeType(AccessControlFaceDto.COME_TYPE_FLOOR);
        accessControlFacePo.setEndTime(reqJson.getString("endDate"));
        accessControlFacePo.setStartTime(reqJson.getString("startDate"));
        accessControlFacePo.setCardNumber(tmpOwnerDto.getCardNumber());
        accessControlFacePo.setMachineId(machineId);
        accessControlFacePo.setMessage("待下发到门禁");
        accessControlFacePo.setMfId(GenerateCodeFactory.getGeneratorId("11"));
        accessControlFacePo.setName(tmpOwnerDto.getName());
        accessControlFacePo.setPersonId(tmpOwnerDto.getMemberId());
        accessControlFacePo.setPersonType(AccessControlFaceDto.PERSON_TYPE_OWNER);
        accessControlFacePo.setState(AccessControlFaceDto.STATE_WAIT);
        accessControlFacePo.setStartTime(reqJson.getString("startDate"));
        accessControlFacePo.setEndTime(reqJson.getString("endDate"));
        accessControlFacePo.setRoomName("");
        int flag = accessControlFaceV1InnerServiceSMOImpl.saveAccessControlFace(accessControlFacePo);

        if (flag < 1) {
            throw new CmdException("保存数据失败");
        }

    }
}
