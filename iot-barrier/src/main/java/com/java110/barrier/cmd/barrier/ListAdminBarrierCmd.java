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
package com.java110.barrier.cmd.barrier;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.*;
import com.java110.dto.barrier.BarrierDto;
import com.java110.dto.chargeMachine.ChargeMachineDto;
import com.java110.dto.community.CommunityDto;
import com.java110.dto.parking.ParkingBoxAreaDto;
import com.java110.intf.barrier.IBarrierV1InnerServiceSMO;
import com.java110.intf.barrier.IParkingBoxAreaV1InnerServiceSMO;
import com.java110.intf.community.ICommunityV1InnerServiceSMO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * 类表述：查询
 * 服务编码：barrier.listBarrier
 * 请求路劲：/app/barrier.ListBarrier
 * add by 吴学文 at 2023-09-21 01:05:11 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Java110Cmd(serviceCode = "barrier.listAdminBarrier")
public class ListAdminBarrierCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(ListAdminBarrierCmd.class);
    @Autowired
    private IBarrierV1InnerServiceSMO barrierV1InnerServiceSMOImpl;

    @Autowired
    private IParkingBoxAreaV1InnerServiceSMO parkingBoxAreaV1InnerServiceSMOImpl;

    @Autowired
    private ICommunityV1InnerServiceSMO communityV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        super.validatePageInfo(reqJson);
        super.assertAdmin(cmdDataFlowContext);

    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

        BarrierDto barrierDto = BeanConvertUtil.covertBean(reqJson, BarrierDto.class);

        containPaId(reqJson,barrierDto);

        int count = barrierV1InnerServiceSMOImpl.queryBarriersCount(barrierDto);

        List<BarrierDto> barrierDtos = null;

        if (count > 0) {
            barrierDtos = barrierV1InnerServiceSMOImpl.queryBarriers(barrierDto);
            freshMachineStateName(barrierDtos);
        } else {
            barrierDtos = new ArrayList<>();
        }

        refreshCommunityName(barrierDtos);

        ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) reqJson.getInteger("row")), count, barrierDtos);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        cmdDataFlowContext.setResponseEntity(responseEntity);
    }

    private void refreshCommunityName(List<BarrierDto> barrierDtos) {

        if(ListUtil.isNull(barrierDtos)){
            return;
        }

        List<String> communityIds = new ArrayList<>();
        for (BarrierDto barrierDto : barrierDtos) {
            communityIds.add(barrierDto.getCommunityId());
        }

        if(ListUtil.isNull(communityIds)){
            return ;
        }
        CommunityDto communityDto = new CommunityDto();
        communityDto.setCommunityIds(communityIds.toArray(new String[communityIds.size()]));
        List<CommunityDto> communityDtos = communityV1InnerServiceSMOImpl.queryCommunitys(communityDto);
        if(ListUtil.isNull(communityDtos)){
            return;
        }
        for (BarrierDto barrierDto : barrierDtos) {
            for (CommunityDto tCommunityDto : communityDtos) {
                if (!barrierDto.getCommunityId().equals(tCommunityDto.getCommunityId())) {
                    continue;
                }
                barrierDto.setCommunityName(tCommunityDto.getName());
            }
        }
    }

    private void containPaId(JSONObject reqJson, BarrierDto barrierDto) {

        if(!reqJson.containsKey("paId")){
            return;
        }

        String paId = reqJson.getString("paId");
        if(StringUtil.isEmpty(paId)){
            return ;
        }

        ParkingBoxAreaDto parkingBoxAreaDto = new ParkingBoxAreaDto();
        parkingBoxAreaDto.setPaId(paId);
        List<ParkingBoxAreaDto> parkingBoxAreaDtos = parkingBoxAreaV1InnerServiceSMOImpl.queryParkingBoxAreas(parkingBoxAreaDto);
        if(ListUtil.isNull(parkingBoxAreaDtos)){
            return ;
        }
        List<String> boxIds = new ArrayList<>();

        for(ParkingBoxAreaDto tmpParkingBoxAreaDto: parkingBoxAreaDtos){
            boxIds.add(tmpParkingBoxAreaDto.getBoxId());
        }

        barrierDto.setBoxIds(boxIds.toArray(new String[boxIds.size()]));

    }

    private void freshMachineStateName(List<BarrierDto> barrierDtos) {

        if (barrierDtos == null || barrierDtos.size() < 1) {
            return;
        }
        for (BarrierDto barrierDto : barrierDtos) {
            String heartbeatTime = barrierDto.getHeartbeatTime();
            try {
                if (StringUtil.isEmpty(heartbeatTime)) {
                    barrierDto.setStateName("设备离线");
                } else {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(DateUtil.getDateFromString(heartbeatTime, DateUtil.DATE_FORMATE_STRING_A));
                    calendar.add(Calendar.MINUTE, 2);
                    if (calendar.getTime().getTime() <= DateUtil.getCurrentDate().getTime()) {
                        barrierDto.setStateName("设备离线");
                    } else {
                        barrierDto.setStateName("设备在线");
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
                barrierDto.setStateName("设备离线");
            }
        }
    }
}
