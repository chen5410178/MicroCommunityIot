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
package com.java110.charge.cmd.chargeMachine;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cache.UrlCache;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.ListUtil;
import com.java110.doc.annotation.*;
import com.java110.dto.chargeMachine.ChargeMachineDto;
import com.java110.dto.chargeMachine.ChargeMachinePortDto;
import com.java110.intf.charge.IChargeMachinePortV1InnerServiceSMO;
import com.java110.intf.charge.IChargeMachineV1InnerServiceSMO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;


/**
 * 类表述：查询
 * 服务编码：chargeMachinePort.listChargeMachinePort
 * 请求路劲：/app/chargeMachinePort.ListChargeMachinePort
 * add by 吴学文 at 2023-03-02 01:17:43 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */

@Java110Cmd(serviceCode = "chargeMachine.listAdminChargeMachinePort")
public class ListAdminChargeMachinePortCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(ListAdminChargeMachinePortCmd.class);
    @Autowired
    private IChargeMachinePortV1InnerServiceSMO chargeMachinePortV1InnerServiceSMOImpl;

    @Autowired
    private IChargeMachineV1InnerServiceSMO chargeMachineV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        super.validatePageInfo(reqJson);
        super.assertAdmin(cmdDataFlowContext);

    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

        ChargeMachinePortDto chargeMachinePortDto = BeanConvertUtil.covertBean(reqJson, ChargeMachinePortDto.class);

        int count = chargeMachinePortV1InnerServiceSMOImpl.queryChargeMachinePortsCount(chargeMachinePortDto);

        List<ChargeMachinePortDto> chargeMachinePortDtos = null;

        if (count > 0) {
            chargeMachinePortDtos = chargeMachinePortV1InnerServiceSMOImpl.queryChargeMachinePorts(chargeMachinePortDto);
            freshQrCodeUrl(chargeMachinePortDtos);

            //调用 第三方查询 插槽状态
           // queryPortState(chargeMachinePortDtos);
        } else {
            chargeMachinePortDtos = new ArrayList<>();
        }

        ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) reqJson.getInteger("row")), count, chargeMachinePortDtos);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        cmdDataFlowContext.setResponseEntity(responseEntity);
    }

//    private void queryPortState(List<ChargeMachinePortDto> chargeMachinePortDtos) {
//        if (chargeMachinePortDtos == null || chargeMachinePortDtos.size() < 1) {
//            return;
//
//        }
//
//        for (ChargeMachinePortDto chargeMachinePortDto : chargeMachinePortDtos) {
//            chargeMachinePortDto.setStateName("空闲");
//        }
//    }

    /**
     * 端口双码地址（小程序/公众号）
     *
     * @param chargeMachinePortDtos
     */
    private void freshQrCodeUrl(List<ChargeMachinePortDto> chargeMachinePortDtos) {

        if (ListUtil.isNull(chargeMachinePortDtos)) {
            return;
        }

        // 按 machineId 查设备，取 chargeType 与 machineCode
        ChargeMachineDto chargeMachineDto = new ChargeMachineDto();
        chargeMachineDto.setMachineId(chargeMachinePortDtos.get(0).getMachineId());
        List<ChargeMachineDto> chargeMachineDtos = chargeMachineV1InnerServiceSMOImpl.queryChargeMachines(chargeMachineDto);
        if (ListUtil.isNull(chargeMachineDtos)) {
            return;
        }

        ChargeMachineDto machine = chargeMachineDtos.get(0);
        String ownerUrl = UrlCache.getOwnerUrl();
        for (ChargeMachinePortDto port : chargeMachinePortDtos) {
            if (ChargeMachineDto.CHARGE_TYPE_BIKE.equals(machine.getChargeType())) {
                port.setMiniUrl(ownerUrl + "/app/chargeBikeMiniPort/" + machine.getMachineCode() + "/" + port.getPortCode());
                port.setUrl(ownerUrl + "/app/chargeBikeH5Port/" + machine.getMachineCode() + "/" + port.getPortCode());
            } else if (ChargeMachineDto.CHARGE_TYPE_CAR.equals(machine.getChargeType())) {
                port.setMiniUrl(ownerUrl + "/app/chargeCarMiniPort/" + machine.getMachineCode() + "/" + port.getPortCode());
                port.setUrl(ownerUrl + "/app/chargeCarH5Port/" + machine.getMachineCode() + "/" + port.getPortCode());
            }
            // 其他类型不设置（保持空，前端显示"暂无二维码"）
        }
    }
}
