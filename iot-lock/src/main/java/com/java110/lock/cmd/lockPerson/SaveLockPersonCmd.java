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
import com.java110.core.annotation.Java110Transactional;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.AuthenticationFactory;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.DateUtil;
import com.java110.dto.lock.LockPersonDto;
import com.java110.intf.lock.ILockPersonV1InnerServiceSMO;
import com.java110.po.lock.LockPersonPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * 类表述：保存
 * 服务编码：lockPerson.saveLockPerson
 * 请求路劲：/app/lockPerson.SaveLockPerson
 * add by 吴学文 at 2023-10-31 14:11:56 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Java110Cmd(serviceCode = "lockPerson.saveLockPerson")
public class SaveLockPersonCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(SaveLockPersonCmd.class);

    public static final String CODE_PREFIX_ID = "10";

    @Autowired
    private ILockPersonV1InnerServiceSMO lockPersonV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "machineId", "machineId不能为空");
        Assert.hasKeyAndValue(reqJson, "personId", "personId不能为空");
        Assert.hasKeyAndValue(reqJson, "name", "name不能为空");
        Assert.hasKeyAndValue(reqJson, "startTime", "startTime不能为空");
        Assert.hasKeyAndValue(reqJson, "endTime", "endTime不能为空");
        Assert.hasKeyAndValue(reqJson, "openModel", "openModel不能为空");
        Assert.hasKeyAndValue(reqJson, "state", "state不能为空");
        Assert.hasKeyAndValue(reqJson, "communityId", "communityId不能为空");

        if (LockPersonDto.OPEN_MODEL_PW.equals(reqJson.getString("openModel"))) {
            Assert.hasKeyAndValue(reqJson, "cardNumber", "cardNumber不能为空");
        }

        Date startTime = DateUtil.getDateFromStringB(reqJson.getString("startTime"));
        Date endTime = DateUtil.getDateFromStringB(reqJson.getString("endTime"));
        if (startTime.after(endTime) || reqJson.getString("startTime").equals(reqJson.getString("endTime"))) {
            throw new CmdException("结束时间不能早于开始时间");
        }
    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

        LockPersonPo lockPersonPo = BeanConvertUtil.covertBean(reqJson, LockPersonPo.class);

        LockPersonDto lockPersonDto = new LockPersonDto();
        lockPersonDto.setMachineId(lockPersonPo.getMachineId());
        lockPersonDto.setPersonId(lockPersonPo.getPersonId());
        lockPersonDto.setOpenModel(lockPersonPo.getOpenModel());
        int row = lockPersonV1InnerServiceSMOImpl.queryLockPersonsCount(lockPersonDto);
        if (row > 0) {
            throw new CmdException("该业主对应门锁的此种开锁方式已授权,不可重复授权!");
        }

        lockPersonPo.setLpId(GenerateCodeFactory.getGeneratorId(CODE_PREFIX_ID));
        if (LockPersonDto.OPEN_MODEL_PW.equals(reqJson.getString("openModel"))) {
            lockPersonPo.setCardNumber(AuthenticationFactory.passwdMd5(reqJson.getString("cardNumber")));
        }
        int flag = lockPersonV1InnerServiceSMOImpl.saveLockPerson(lockPersonPo);

        if (flag < 1) {
            throw new CmdException("保存数据失败");
        }



        cmdDataFlowContext.setResponseEntity(ResultVo.success());
    }
}
