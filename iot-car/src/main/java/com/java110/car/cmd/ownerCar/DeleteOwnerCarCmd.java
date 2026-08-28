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
package com.java110.car.cmd.ownerCar;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.dto.car.OwnerCarDto;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.annotation.Java110Transactional;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.Environment;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.ListUtil;
import com.java110.dto.parking.ParkingSpaceDto;
import com.java110.intf.car.IOwnerCarInnerServiceSMO;
import com.java110.intf.car.IOwnerCarV1InnerServiceSMO;
import com.java110.intf.car.IParkingSpaceV1InnerServiceSMO;
import com.java110.po.ownerCar.OwnerCarPo;
import com.java110.po.parking.ParkingSpacePo;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 类表述：删除
 * 服务编码：ownerCar.deleteOwnerCar
 * 请求路劲：/app/ownerCar.DeleteOwnerCar
 * add by 吴学文 at 2023-08-23 15:16:36 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Java110Cmd(serviceCode = "ownerCar.deleteOwnerCar")
public class DeleteOwnerCarCmd extends Cmd {
    private static Logger logger = LoggerFactory.getLogger(DeleteOwnerCarCmd.class);


    @Autowired
    private IOwnerCarInnerServiceSMO ownerCarInnerServiceSMOImpl;


    @Autowired
    private IOwnerCarV1InnerServiceSMO ownerCarV1InnerServiceSMOImpl;

    @Autowired
    private IParkingSpaceV1InnerServiceSMO parkingSpaceV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        Environment.isDevEnv();

        Assert.hasKeyAndValue(reqJson, "carId", "carId不能为空");
        Assert.hasKeyAndValue(reqJson, "memberId", "memberId不能为空");
        Assert.hasKeyAndValue(reqJson, "communityId", "小区ID不能为空");


        OwnerCarDto ownerCarDto = new OwnerCarDto();
        ownerCarDto.setCarId(reqJson.getString("carId"));
        ownerCarDto.setMemberId(reqJson.getString("memberId"));
        ownerCarDto.setCommunityId(reqJson.getString("communityId"));

        List<OwnerCarDto> ownerCarDtos = ownerCarInnerServiceSMOImpl.queryOwnerCars(ownerCarDto);

        Assert.listOnlyOne(ownerCarDtos, "当前未找到需要删除车辆");
        reqJson.put("carTypeCd",ownerCarDtos.get(0).getCarTypeCd());
        if (OwnerCarDto.CAR_TYPE_MEMBER.equals(ownerCarDtos.get(0).getCarTypeCd())) {
            return;
        }

        ownerCarDto = new OwnerCarDto();
        ownerCarDto.setCarId(reqJson.getString("memberId"));
        ownerCarDto.setCarTypeCd(OwnerCarDto.CAR_TYPE_MEMBER);
        ownerCarDtos = ownerCarInnerServiceSMOImpl.queryOwnerCars(ownerCarDto);

        if (!ListUtil.isNull(ownerCarDtos)) {
            throw new CmdException("存在成员车辆，请先删除成员车辆");
        }



    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        OwnerCarPo ownerCarPo = new OwnerCarPo();
        ownerCarPo.setCommunityId(reqJson.getString("communityId"));
        ownerCarPo.setCarId(reqJson.getString("carId"));
        ownerCarPo.setMemberId(reqJson.getString("memberId"));
        int flag = ownerCarV1InnerServiceSMOImpl.deleteOwnerCar(ownerCarPo);
        if (flag < 1) {
            throw new IllegalArgumentException("删除车辆出错");
        }

        // 删除车辆后联动清空车位绑定：按车辆ID查询车位，有绑定则清空车辆/业主/租期字段并置状态为空闲 F
        String communityId = reqJson.getString("communityId");
        ParkingSpaceDto parkingSpaceDto = new ParkingSpaceDto();
        parkingSpaceDto.setCommunityId(communityId);
        parkingSpaceDto.setCarId(reqJson.getString("carId"));
        List<ParkingSpaceDto> parkingSpaceDtos = parkingSpaceV1InnerServiceSMOImpl.queryParkingSpaces(parkingSpaceDto);
        if (!ListUtil.isNull(parkingSpaceDtos)) {
            ParkingSpacePo parkingSpacePo = new ParkingSpacePo();
            parkingSpacePo.setPsId(parkingSpaceDtos.get(0).getPsId());
            parkingSpacePo.setCommunityId(communityId);
            parkingSpacePo.setState(ParkingSpaceDto.STATE_FREE);
            parkingSpacePo.setOwnerId("");
            parkingSpacePo.setOwnerName("");
            parkingSpacePo.setCarId("");
            parkingSpacePo.setCarNum("");
            parkingSpacePo.setStartTime("");
            parkingSpacePo.setEndTime("");
            flag = parkingSpaceV1InnerServiceSMOImpl.updateParkingSpace(parkingSpacePo);
            if (flag < 1) {
                throw new CmdException("清空车位绑定失败");
            }
        }
    }

}
