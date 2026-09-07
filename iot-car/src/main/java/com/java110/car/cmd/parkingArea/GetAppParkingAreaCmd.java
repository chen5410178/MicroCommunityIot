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
package com.java110.car.cmd.parkingArea;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.ListUtil;
import com.java110.doc.annotation.*;
import com.java110.dto.parking.ParkingAreaDto;
import com.java110.intf.car.IParkingAreaV1InnerServiceSMO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 类表述：手机端查询停车场信息（扫码临停缴费默认车牌）
 * 服务编码：parkingArea.getAppParkingArea
 * 请求路劲：/iot/api/parkingArea.getAppParkingArea
 * add by 吴学文 at 2026-09-07 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Java110CmdDoc(title = "手机端查询停车场信息",
        description = "用于手机端扫码临停缴费查询停车场默认省份/默认字母配置",
        httpMethod = "get",
        url = "http://{ip}:{port}/iot/api/parkingArea.getAppParkingArea",
        resource = "carDoc",
        author = "吴学文",
        serviceCode = "parkingArea.getAppParkingArea",
        seq = 5
)

@Java110ParamsDoc(params = {
        @Java110ParamDoc(name = "paId", length = 30, remark = "停车场ID"),
        @Java110ParamDoc(name = "communityId", length = 30, remark = "小区ID"),
})

@Java110ResponseDoc(
        params = {
                @Java110ParamDoc(name = "code", type = "int", length = 11, defaultValue = "0", remark = "返回编号，0 成功 其他失败"),
                @Java110ParamDoc(name = "msg", type = "String", length = 250, defaultValue = "成功", remark = "描述"),
                @Java110ParamDoc(name = "data", type = "Object", remark = "有效数据"),
                @Java110ParamDoc(parentNodeName = "data", name = "paId", type = "String", remark = "停车场ID"),
                @Java110ParamDoc(parentNodeName = "data", name = "num", type = "String", remark = "停车场编号"),
                @Java110ParamDoc(parentNodeName = "data", name = "defaultProv", type = "String", remark = "默认省份（车牌第1位）"),
                @Java110ParamDoc(parentNodeName = "data", name = "defaultChar", type = "String", remark = "默认字母（车牌第2位）"),
        }
)

@Java110ExampleDoc(
        reqBody = "http://{ip}:{port}/iot/api/parkingArea.getAppParkingArea?paId=1020260821xxxxx&communityId=102023021386560001",
        resBody = "{'code':0,'msg':'成功','data':{'paId':'1020260821xxxxx','num':'1','defaultProv':'青','defaultChar':'A'}}"
)
@Java110Cmd(serviceCode = "parkingArea.getAppParkingArea")
public class GetAppParkingAreaCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(GetAppParkingAreaCmd.class);

    @Autowired
    private IParkingAreaV1InnerServiceSMO parkingAreaV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "paId", "paId不能为空");
        Assert.hasKeyAndValue(reqJson, "communityId", "communityId不能为空");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

        ParkingAreaDto parkingAreaDto = BeanConvertUtil.covertBean(reqJson, ParkingAreaDto.class);
        parkingAreaDto.setStatusCd("0");
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaV1InnerServiceSMOImpl.queryParkingAreas(parkingAreaDto);
        if (ListUtil.isNull(parkingAreaDtos)) {
            cmdDataFlowContext.setResponseEntity(ResultVo.error("停车场不存在"));
            return;
        }

        cmdDataFlowContext.setResponseEntity(ResultVo.createResponseEntity(new ResultVo(parkingAreaDtos.get(0))));
    }
}
