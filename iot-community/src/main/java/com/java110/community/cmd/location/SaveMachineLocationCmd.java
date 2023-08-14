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
package com.java110.community.cmd.location;

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
import com.java110.doc.annotation.*;
import com.java110.intf.community.IMachineLocationV1InnerServiceSMO;
import com.java110.bean.po.location.MachineLocationPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 类表述：保存
 * 服务编码：machineLocation.saveMachineLocation
 * 请求路劲：/app/machineLocation.SaveMachineLocation
 * add by 吴学文 at 2023-08-15 00:49:50 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */


@Java110CmdDoc(title = "添加设备位置",
        description = "用于外系统添加设备位置功能",
        httpMethod = "post",
        url = "http://{ip}:{port}/iot/api/location.saveMachineLocation",
        resource = "communityDoc",
        author = "吴学文",
        serviceCode = "location.saveMachineLocation"
)

@Java110ParamsDoc(params = {
        @Java110ParamDoc(name = "communityId", length = 30, remark = "小区ID"),
        @Java110ParamDoc(name = "locationName", length = 64, remark = "名称"),
        @Java110ParamDoc(name = "locationType", length = 64, remark = "类型"),
        @Java110ParamDoc(name = "locationObjId", length = 64, remark = "位置ID"),
        @Java110ParamDoc(name = "locationObjName",  length = 64, remark = "位置名称"),
})

@Java110ResponseDoc(
        params = {
                @Java110ParamDoc(name = "code", type = "int", length = 11, defaultValue = "0", remark = "返回编号，0 成功 其他失败"),
                @Java110ParamDoc(name = "msg", type = "String", length = 250, defaultValue = "成功", remark = "描述"),
        }
)

@Java110ExampleDoc(
        reqBody = "{\"locationName\":\"\",\"locationType\":\"2号楼\",\"locationObjId\":\"\",\"locationObjName\":\"22\",\"communityId\":\"2022081539020475\"}",
        resBody = "{'code':0,'msg':'成功'}"
)

@Java110Cmd(serviceCode = "location.saveMachineLocation")
public class SaveMachineLocationCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(SaveMachineLocationCmd.class);

    public static final String CODE_PREFIX_ID = "10";

    @Autowired
    private IMachineLocationV1InnerServiceSMO machineLocationV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "locationName", "请求报文中未包含locationName");
        Assert.hasKeyAndValue(reqJson, "communityId", "请求报文中未包含communityId");
        Assert.hasKeyAndValue(reqJson, "locationType", "请求报文中未包含locationType");
        Assert.hasKeyAndValue(reqJson, "locationObjId", "请求报文中未包含locationObjId");
        Assert.hasKeyAndValue(reqJson, "locationObjName", "请求报文中未包含locationObjName");

    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

        MachineLocationPo machineLocationPo = BeanConvertUtil.covertBean(reqJson, MachineLocationPo.class);
        machineLocationPo.setLocationId(GenerateCodeFactory.getGeneratorId(CODE_PREFIX_ID));
        int flag = machineLocationV1InnerServiceSMOImpl.saveMachineLocation(machineLocationPo);

        if (flag < 1) {
            throw new CmdException("保存数据失败");
        }

        cmdDataFlowContext.setResponseEntity(ResultVo.success());
    }
}
