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
package com.java110.dev.cmd.route;

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
import com.java110.intf.dev.IRouteV1InnerServiceSMO;
import com.java110.po.route.RoutePo;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 类表述：保存
 * 服务编码：route.saveRoute
 * 请求路劲：/app/route.SaveRoute
 * add by 吴学文 at 2023-02-11 01:15:12 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Java110Cmd(serviceCode = "route.saveRoute")
public class SaveRouteCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(SaveRouteCmd.class);

    public static final String CODE_PREFIX_ID = "10";

    @Autowired
    private IRouteV1InnerServiceSMO routeV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "appId", "请求报文中未包含appId");
Assert.hasKeyAndValue(reqJson, "serviceId", "请求报文中未包含serviceId");
Assert.hasKeyAndValue(reqJson, "orderTypeCd", "请求报文中未包含orderTypeCd");
Assert.hasKeyAndValue(reqJson, "invokeModel", "请求报文中未包含invokeModel");

    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

       RoutePo routePo = BeanConvertUtil.covertBean(reqJson, RoutePo.class);
        routePo.setId(GenerateCodeFactory.getGeneratorId(CODE_PREFIX_ID));
        int flag = routeV1InnerServiceSMOImpl.saveRoute(routePo);

        if (flag < 1) {
            throw new CmdException("保存数据失败");
        }

        cmdDataFlowContext.setResponseEntity(ResultVo.success());
    }
}
