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
package com.java110.meter.cmd.meterType;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.doc.annotation.*;
import com.java110.dto.meter.MeterTypeDto;
import com.java110.intf.meter.IMeterTypeV1InnerServiceSMO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;


/**
 * 类表述：查询
 * 服务编码：meterType.listMeterType
 * 请求路劲：/app/meterType.ListMeterType
 * add by 吴学文 at 2021-09-21 23:01:49 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */

@Java110CmdDoc(title = "查询抄表类型",
        description = "用于外系统查询抄表类型",
        httpMethod = "get",
        url = "http://{ip}:{port}/iot/api/meterType.listMeterType",
        resource = "meterDoc",
        author = "吴学文",
        serviceCode = "meterType.listMeterType",
        seq = 1
)

@Java110ParamsDoc(params = {
        @Java110ParamDoc(name = "communityId", length = 30, remark = "小区ID"),
        @Java110ParamDoc(name = "page", type = "int", length = 11, remark = "页数"),
        @Java110ParamDoc(name = "row", type = "int", length = 11, remark = "行数"),
})

@Java110ResponseDoc(
        params = {
                @Java110ParamDoc(name = "code", type = "int", length = 11, defaultValue = "0", remark = "返回编号，0 成功 其他失败"),
                @Java110ParamDoc(name = "msg", type = "String", length = 250, defaultValue = "成功", remark = "描述"),
                @Java110ParamDoc(name = "data", type = "Array", remark = "有效数据"),
                @Java110ParamDoc(parentNodeName = "data", name = "typeId", type = "String", remark = "类型ID"),
                @Java110ParamDoc(parentNodeName = "data", name = "typeName", type = "String", remark = "类型名称"),
                @Java110ParamDoc(parentNodeName = "data", name = "price", type = "String", remark = "单价"),
                @Java110ParamDoc(parentNodeName = "data", name = "remark", type = "String", remark = "说明"),
        }
)

@Java110ExampleDoc(
        reqBody = "http://{ip}:{port}/iot/api/meterType.listMeterType?page=1&row=10&communityId=123123",
        resBody = "{'code':0,'msg':'成功','data':[{'typeId':'123123','typeName':'tq-水表','price':'1.00','remark':'水表'}]}"
)
@Java110Cmd(serviceCode = "meterType.listMeterType")
public class ListMeterTypeCmd extends Cmd {

  private static Logger logger = LoggerFactory.getLogger(ListMeterTypeCmd.class);
    @Autowired
    private IMeterTypeV1InnerServiceSMO meterTypeV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        super.validatePageInfo(reqJson);
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

           MeterTypeDto meterTypeDto = BeanConvertUtil.covertBean(reqJson, MeterTypeDto.class);

           int count = meterTypeV1InnerServiceSMOImpl.queryMeterTypesCount(meterTypeDto);

           List<MeterTypeDto> meterTypeDtos = null;

           if (count > 0) {
               meterTypeDtos = meterTypeV1InnerServiceSMOImpl.queryMeterTypes(meterTypeDto);
           } else {
               meterTypeDtos = new ArrayList<>();
           }

           ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) reqJson.getInteger("row")), count, meterTypeDtos);

           ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

           cmdDataFlowContext.setResponseEntity(responseEntity);
    }
}
