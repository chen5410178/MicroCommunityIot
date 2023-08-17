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
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.doc.annotation.*;
import com.java110.dto.accessControlLog.AccessControlLogDto;
import com.java110.intf.accessControl.IAccessControlLogV1InnerServiceSMO;
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
 * add by 吴学文 at 2023-08-16 17:22:49 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Java110CmdDoc(title = "查询门禁日志参数",
        description = "用于外系统查询门禁日志参数",
        httpMethod = "get",
        url = "http://{ip}:{port}/iot/api/accessControlLog.listAccessControlLogParam",
        resource = "accessControlDoc",
        author = "吴学文",
        serviceCode = "accessControlLog.listAccessControlLogParam",
        seq = 12
)

@Java110ParamsDoc(params = {
        @Java110ParamDoc(name = "communityId", length = 30, remark = "小区ID"),
        @Java110ParamDoc(name = "logId", length = 30, remark = "日志ID"),
        @Java110ParamDoc(name = "page", type = "int", length = 11, remark = "页数"),
        @Java110ParamDoc(name = "row", type = "int", length = 11, remark = "行数"),
})

@Java110ResponseDoc(
        params = {
                @Java110ParamDoc(name = "code", type = "int", length = 11, defaultValue = "0", remark = "返回编号，0 成功 其他失败"),
                @Java110ParamDoc(name = "msg", type = "String", length = 250, defaultValue = "成功", remark = "描述"),
                @Java110ParamDoc(name = "data", type = "Array", remark = "有效数据"),
                @Java110ParamDoc(parentNodeName = "data", name = "reqParam", type = "String", remark = "请求报文"),
                @Java110ParamDoc(parentNodeName = "data", name = "resParam", type = "String", remark = "返回报文"),
                @Java110ParamDoc(parentNodeName = "data", name = "logId", type = "String", remark = "日志ID"),
        }
)

@Java110ExampleDoc(
        reqBody = "http://{ip}:{port}/iot/api/accessControlLog.listAccessControlLogParam?page=1&row=10&communityId=123123",
        resBody = "{'code':0,'msg':'成功','data':[{'machineId':'123123','logId':'123213','reqParam':'','resParam':''}]}"
)
@Java110Cmd(serviceCode = "accessControlLog.listAccessControlLogParam")
public class ListAccessControlLogParamCmd extends Cmd {

  private static Logger logger = LoggerFactory.getLogger(ListAccessControlLogParamCmd.class);
    @Autowired
    private IAccessControlLogV1InnerServiceSMO accessControlLogV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        super.validatePageInfo(reqJson);
        Assert.hasKeyAndValue(reqJson, "communityId", "请求报文中未包含communityId");

    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

           AccessControlLogDto accessControlLogDto = BeanConvertUtil.covertBean(reqJson, AccessControlLogDto.class);


           List<AccessControlLogDto> accessControlLogDtos  = accessControlLogV1InnerServiceSMOImpl.queryAccessControlLogParams(accessControlLogDto);


           ResultVo resultVo = new ResultVo(1, 1, accessControlLogDtos);

           ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

           cmdDataFlowContext.setResponseEntity(responseEntity);
    }
}
