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
package com.java110.lock.cmd.lockPerson;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.lock.LockPersonDto;
import com.java110.intf.lock.ILockPersonV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.ArrayList;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 类表述：查询
 * 服务编码：lockPerson.listLockPerson
 * 请求路劲：/app/lockPerson.ListLockPerson
 * add by 吴学文 at 2023-10-31 14:11:56 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Java110Cmd(serviceCode = "lockPerson.listLockPerson")
public class ListLockPersonCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(ListLockPersonCmd.class);
    @Autowired
    private ILockPersonV1InnerServiceSMO lockPersonV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        super.validatePageInfo(reqJson);
        Assert.hasKeyAndValue(reqJson, "communityId", "communityId不能为空");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

        LockPersonDto lockPersonDto = BeanConvertUtil.covertBean(reqJson, LockPersonDto.class);

        int count = lockPersonV1InnerServiceSMOImpl.queryLockPersonsCount(lockPersonDto);

        List<LockPersonDto> lockPersonDtos = null;

        if (count > 0) {
            lockPersonDtos = lockPersonV1InnerServiceSMOImpl.queryLockPersons(lockPersonDto);
        } else {
            lockPersonDtos = new ArrayList<>();
        }

        ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) reqJson.getInteger("row")), count, lockPersonDtos);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        cmdDataFlowContext.setResponseEntity(responseEntity);
    }
}
