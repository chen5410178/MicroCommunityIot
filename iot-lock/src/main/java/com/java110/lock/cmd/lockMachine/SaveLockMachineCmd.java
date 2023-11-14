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
package com.java110.lock.cmd.lockMachine;

import com.alibaba.fastjson.JSONArray;
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
import com.java110.core.utils.StringUtil;
import com.java110.dto.lock.LockMachineFactorySpecDto;
import com.java110.intf.lock.ILockMachineFactorySpecV1InnerServiceSMO;
import com.java110.intf.lock.ILockMachineParamV1InnerServiceSMO;
import com.java110.intf.lock.ILockMachineV1InnerServiceSMO;
import com.java110.po.lock.LockMachineParamPo;
import com.java110.po.lock.LockMachinePo;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 类表述：保存
 * 服务编码：lockMachine.saveLockMachine
 * 请求路劲：/app/lockMachine.SaveLockMachine
 * add by 吴学文 at 2023-10-30 17:23:38 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Java110Cmd(serviceCode = "lockMachine.saveLockMachine")
public class SaveLockMachineCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(SaveLockMachineCmd.class);

    public static final String CODE_PREFIX_ID = "10";

    @Autowired
    private ILockMachineV1InnerServiceSMO lockMachineV1InnerServiceSMOImpl;

    @Autowired
    private ILockMachineParamV1InnerServiceSMO lockMachineParamV1InnerServiceSMOImpl;

    @Autowired
    private ILockMachineFactorySpecV1InnerServiceSMO lockMachineFactorySpecV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "machineId", "machineId不能为空");
        Assert.hasKeyAndValue(reqJson, "machineName", "machineName不能为空");
        Assert.hasKeyAndValue(reqJson, "machineCode", "machineCode不能为空");
        Assert.hasKeyAndValue(reqJson, "roomId", "roomId不能为空");
        Assert.hasKeyAndValue(reqJson, "roomName", "roomName不能为空");
        Assert.hasKeyAndValue(reqJson, "implBean", "implBean不能为空");
        Assert.hasKeyAndValue(reqJson, "communityId", "communityId不能为空");
        Assert.hasKeyAndValue(reqJson, "lockMachineFactorySpecList", "lockMachineFactorySpecList不能为空");
        JSONArray lockMachineFactorySpecList = reqJson.getJSONArray("lockMachineFactorySpecList");

        LockMachineFactorySpecDto lockMachineFactorySpecDto = new LockMachineFactorySpecDto();
        lockMachineFactorySpecDto.setFactoryId(reqJson.getString("implBean"));
        int row = lockMachineFactorySpecV1InnerServiceSMOImpl.queryLockMachineFactorySpecsCount(lockMachineFactorySpecDto);
        if (row < 1) {
            throw new CmdException("厂家规格未添加");
        }

        for (int i = 0; i < lockMachineFactorySpecList.size(); i++) {
            lockMachineFactorySpecDto = BeanConvertUtil.covertBean(lockMachineFactorySpecList.getJSONObject(i), LockMachineFactorySpecDto.class);
            if (StringUtil.isEmpty(lockMachineFactorySpecDto.getValue())) {
                throw new CmdException("请将厂家规格值填写完整");
            }
        }
    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

        LockMachinePo lockMachinePo = BeanConvertUtil.covertBean(reqJson, LockMachinePo.class);
        lockMachinePo.setHeartbeatTime(new Date());
        int flag = lockMachineV1InnerServiceSMOImpl.saveLockMachine(lockMachinePo);

        if (flag < 1) {
            throw new CmdException("保存数据失败");
        }

        JSONArray lockMachineFactorySpecList = reqJson.getJSONArray("lockMachineFactorySpecList");
        List<LockMachineParamPo> lockMachineParamPos = new ArrayList<>();
        lockMachineFactorySpecList.forEach(lockMachineFactorySpec -> {
            LockMachineFactorySpecDto lockMachineFactorySpecDto = BeanConvertUtil.covertBean(lockMachineFactorySpec, LockMachineFactorySpecDto.class);
            LockMachineParamPo lockMachineParamPo = new LockMachineParamPo();
            lockMachineParamPo.setParamId(GenerateCodeFactory.getGeneratorId(CODE_PREFIX_ID));
            lockMachineParamPo.setMachineId(lockMachinePo.getMachineId());
            lockMachineParamPo.setCommunityId(reqJson.getString("communityId"));
            lockMachineParamPo.setSpecCd(lockMachineFactorySpecDto.getSpecCd());
            lockMachineParamPo.setValue(lockMachineFactorySpecDto.getValue());
            lockMachineParamPos.add(lockMachineParamPo);
        });

        flag = lockMachineParamV1InnerServiceSMOImpl.saveLockMachineParamList(lockMachineParamPos);
        if (flag != lockMachineParamPos.size()) {
            throw new CmdException("保存数据失败");
        }

        cmdDataFlowContext.setResponseEntity(ResultVo.success());
    }
}
