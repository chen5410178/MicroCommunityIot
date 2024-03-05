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
package com.java110.car.cmd.parkingSpaceMachine;

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
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.dto.parkingSpaceMachine.ParkingSpaceMachineDto;
import com.java110.dto.parkingSpaceMachineRel.ParkingSpaceMachineRelDto;
import com.java110.intf.car.IParkingSpaceMachineRelV1InnerServiceSMO;
import com.java110.intf.car.IParkingSpaceMachineV1InnerServiceSMO;
import com.java110.po.parkingSpaceMachine.ParkingSpaceMachinePo;
import com.java110.po.parkingSpaceMachineRel.ParkingSpaceMachineRelPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 类表述：保存
 * 服务编码：parkingSpaceMachine.saveParkingSpaceMachine
 * 请求路劲：/app/parkingSpaceMachine.SaveParkingSpaceMachine
 * add by 吴学文 at 2024-03-05 20:57:20 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Java110Cmd(serviceCode = "parkingSpaceMachine.saveParkingSpaceMachine")
public class SaveParkingSpaceMachineCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(SaveParkingSpaceMachineCmd.class);

    public static final String CODE_PREFIX_ID = "10";

    @Autowired
    private IParkingSpaceMachineV1InnerServiceSMO parkingSpaceMachineV1InnerServiceSMOImpl;

    @Autowired
    private IParkingSpaceMachineRelV1InnerServiceSMO parkingSpaceMachineRelV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "machineCode", "请求报文中未包含machineCode");
        Assert.hasKeyAndValue(reqJson, "machineName", "请求报文中未包含machineName");
        Assert.hasKeyAndValue(reqJson, "communityId", "请求报文中未包含communityId");
        Assert.hasKeyAndValue(reqJson, "implBean", "请求报文中未包含implBean");

        if (!reqJson.containsKey("parkingSpaces")) {
            throw new CmdException("未包含车位");
        }

        JSONArray parkingSpaces = reqJson.getJSONArray("parkingSpaces");

        if (ListUtil.isNull(parkingSpaces)) {
            throw new CmdException("未包含车位");
        }
        JSONObject psObj = null;
        for (int psIndex = 0; psIndex < parkingSpaces.size(); psIndex++) {
            psObj = parkingSpaces.getJSONObject(psIndex);

            Assert.hasKeyAndValue(psObj, "psId", "未包含车位ID");
            Assert.hasKeyAndValue(psObj, "psName", "未包含车位名称");

            ParkingSpaceMachineRelDto parkingSpaceMachineRelDto = new ParkingSpaceMachineRelDto();
            parkingSpaceMachineRelDto.setPsId(psObj.getString("psId"));
            parkingSpaceMachineRelDto.setCommunityId(reqJson.getString("communityId"));
            int count = parkingSpaceMachineRelV1InnerServiceSMOImpl.queryParkingSpaceMachineRelsCount(parkingSpaceMachineRelDto);
            if (count > 0) {
                throw new CmdException("车位已经绑定地磁");
            }
        }

    }

    /**
     * {"machineId":"","machineName":"1","machineCode":"11","machineIp":"1","machineMac":"1","implBean":"102024030543190016",
     * "parkingSpaces":[{"psId":"792024021833322262","psName":"1-16"},{"psId":"792024021869862263","psName":"1-17"}],"communityId":"2023052267100146"}
     *
     * @param event              事件对象
     * @param cmdDataFlowContext 数据上文对象
     * @param reqJson            请求报文
     * @throws CmdException
     */
    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

        ParkingSpaceMachinePo parkingSpaceMachinePo = BeanConvertUtil.covertBean(reqJson, ParkingSpaceMachinePo.class);
        parkingSpaceMachinePo.setMachineId(GenerateCodeFactory.getGeneratorId(CODE_PREFIX_ID));
        int flag = parkingSpaceMachineV1InnerServiceSMOImpl.saveParkingSpaceMachine(parkingSpaceMachinePo);

        if (flag < 1) {
            throw new CmdException("保存数据失败");
        }

        JSONArray parkingSpaces = reqJson.getJSONArray("parkingSpaces");

        JSONObject psObj = null;
        ParkingSpaceMachineRelPo parkingSpaceMachineRelPo = null;
        for (int psIndex = 0; psIndex < parkingSpaces.size(); psIndex++) {
            psObj = parkingSpaces.getJSONObject(psIndex);
            parkingSpaceMachineRelPo = new ParkingSpaceMachineRelPo();
            parkingSpaceMachineRelPo.setLockBattery("0");
            parkingSpaceMachineRelPo.setRelId(GenerateCodeFactory.getGeneratorId("11"));
            parkingSpaceMachineRelPo.setPsState(ParkingSpaceMachineRelDto.PS_STATE_FREE);
            parkingSpaceMachineRelPo.setMachineId(parkingSpaceMachinePo.getMachineId());
            parkingSpaceMachineRelPo.setPsName(psObj.getString("psName"));
            parkingSpaceMachineRelPo.setCarNum("无");
            parkingSpaceMachineRelPo.setPsId(psObj.getString("psId"));
            parkingSpaceMachineRelPo.setUpdateTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
            parkingSpaceMachineRelPo.setCommunityId(reqJson.getString("communityId"));
            parkingSpaceMachineRelPo.setLockState(ParkingSpaceMachineRelDto.LOCK_STATE_OFF);
            parkingSpaceMachineRelPo.setImagePhoto("");
            int count = parkingSpaceMachineRelV1InnerServiceSMOImpl.saveParkingSpaceMachineRel(parkingSpaceMachineRelPo);

            if (count < 1) {
                throw new CmdException("保存车位失败");
            }

        }

        cmdDataFlowContext.setResponseEntity(ResultVo.success());
    }
}
