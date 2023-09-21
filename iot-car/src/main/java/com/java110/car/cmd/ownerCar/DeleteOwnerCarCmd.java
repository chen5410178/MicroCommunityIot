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
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.StringUtil;
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
    private IParkingSpaceV1InnerServiceSMO parkingSpaceV1InnerServiceSMOImpl;

    @Autowired
    private IOwnerCarV1InnerServiceSMO ownerCarV1InnerServiceSMOImpl;

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
        reqJson.put("psId", ownerCarDtos.get(0).getPsId());
    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        if (reqJson.containsKey("psId") && !StringUtil.isEmpty(reqJson.getString("psId"))) {
            ParkingSpaceDto parkingSpaceDto = new ParkingSpaceDto();
            parkingSpaceDto.setPsId(reqJson.getString("psId"));
            List<ParkingSpaceDto> parkingSpaceDtos = parkingSpaceV1InnerServiceSMOImpl.queryParkingSpaces(parkingSpaceDto);
            if (parkingSpaceDtos != null && parkingSpaceDtos.size() > 0 &&
                    "2".equals(parkingSpaceDtos.get(0).getParkingType())
                    && "1001".equals(reqJson.getString("carTypeCd"))) { //子母车位
                OwnerCarDto ownerCarDto = new OwnerCarDto();
                ownerCarDto.setCarId(reqJson.getString("carId"));
                ownerCarDto.setPsId(reqJson.getString("psId"));
                ownerCarDto.setOwnerId(reqJson.getString("ownerId"));
                ownerCarDto.setCarTypeCd("1002"); //成员车辆
                List<OwnerCarDto> ownerCarDtos = ownerCarInnerServiceSMOImpl.queryOwnerCars(ownerCarDto);
                if (ownerCarDtos != null && ownerCarDtos.size() > 0) {
                    throw new IllegalArgumentException("该车位下含有子车辆，请先删除子车辆后再进行操作！");
                }
            }
        }
        OwnerCarPo ownerCarPo = new OwnerCarPo();
        ownerCarPo.setCommunityId(reqJson.getString("communityId"));
        ownerCarPo.setCarId(reqJson.getString("carId"));
        ownerCarPo.setMemberId(reqJson.getString("memberId"));
        int flag = ownerCarV1InnerServiceSMOImpl.deleteOwnerCar(ownerCarPo);
        if (flag < 1) {
            throw new IllegalArgumentException("删除车辆出错");
        }
        if (StringUtil.isEmpty(reqJson.getString("psId")) || "-1".equals(reqJson.getString("psId"))) {
            return;
        }
        // todo 释放车位
        releaseParkSpace(reqJson);

        // todo 删除车辆费用
        //deleteCarFee(reqJson);
    }

    /**
     * 释放车位信息
     *
     * @param reqJson
     */
    private void releaseParkSpace(JSONObject reqJson) {
        int flag;
        if (!reqJson.getString("carId").equals(reqJson.getString("memberId"))) {
            return;
        }
        reqJson.put("carNumType", ParkingSpaceDto.STATE_FREE);//修改为空闲
        ParkingSpaceDto parkingSpaceDto = new ParkingSpaceDto();
        parkingSpaceDto.setCommunityId(reqJson.getString("communityId"));
        parkingSpaceDto.setPsId(reqJson.getString("psId"));
        List<ParkingSpaceDto> parkingSpaceDtos = parkingSpaceV1InnerServiceSMOImpl.queryParkingSpaces(parkingSpaceDto);

        if (parkingSpaceDtos == null || parkingSpaceDtos.size() != 1) {
            //throw new ListenerExecuteException(ResponseConstant.RESULT_CODE_ERROR, "未查询到停车位信息" + JSONObject.toJSONString(parkingSpaceDto));
            return;
        }

        parkingSpaceDto = parkingSpaceDtos.get(0);
        JSONObject businessParkingSpace = new JSONObject();
        businessParkingSpace.putAll(BeanConvertUtil.beanCovertMap(parkingSpaceDto));
        businessParkingSpace.put("state", reqJson.getString("carNumType"));
        ParkingSpacePo parkingSpacePo = BeanConvertUtil.covertBean(businessParkingSpace, ParkingSpacePo.class);
        flag = parkingSpaceV1InnerServiceSMOImpl.updateParkingSpace(parkingSpacePo);
        if (flag < 1) {
            throw new IllegalArgumentException("修改车辆出错");
        }
    }

}
