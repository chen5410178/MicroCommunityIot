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
import com.java110.core.cache.MappingCache;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.*;
import com.java110.dto.parking.ParkingAreaDto;
import com.java110.intf.car.IOwnerCarV1InnerServiceSMO;
import com.java110.intf.car.IParkingAreaV1InnerServiceSMO;
import com.java110.intf.car.IOwnerCarInnerServiceSMO;
import com.java110.po.ownerCar.OwnerCarPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * 类表述：保存
 * 服务编码：ownerCar.saveOwnerCar
 * 请求路劲：/app/ownerCar.SaveOwnerCar
 * add by 吴学文 at 2023-08-23 15:16:36 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Java110Cmd(serviceCode = "ownerCar.saveOwnerCar")
public class SaveOwnerCarCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(SaveOwnerCarCmd.class);

    @Autowired
    private IOwnerCarInnerServiceSMO ownerCarInnerServiceSMOImpl;


    @Autowired
    private IOwnerCarV1InnerServiceSMO ownerCarV1InnerServiceSMOImpl;

    @Autowired
    private IParkingAreaV1InnerServiceSMO parkingAreaV1InnerServiceSMOImpl;


    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        Assert.hasKeyAndValue(reqJson, "communityId", "未包含小区ID");
        Assert.hasKeyAndValue(reqJson, "ownerId", "请求报文中未包含ownerId");
        Assert.hasKeyAndValue(reqJson, "carNum", "请求报文中未包含carNum");
        Assert.hasKeyAndValue(reqJson, "carType", "请求报文中未包含carType");
        Assert.hasKeyAndValue(reqJson, "paId", "未包含paId");

        //校验停车场是否存在
        ParkingAreaDto parkingAreaDto = new ParkingAreaDto();
        parkingAreaDto.setPaId(reqJson.getString("paId"));
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaV1InnerServiceSMOImpl.queryParkingAreas(parkingAreaDto);
        Assert.listOnlyOne(parkingAreaDtos, "查询停车场错误！");

        if (OwnerCarDto.LEASE_TYPE_MONTH.equals(reqJson.getString("leaseType"))) {
            Assert.hasKeyAndValue(reqJson, "startTime", "请求报文中未包含开始时间");
            Assert.hasKeyAndValue(reqJson, "endTime", "请求报文中未包含结束时间");
            Assert.isDate(reqJson.getString("startTime"), DateUtil.DATE_FORMATE_STRING_B,"开始时间格式错误");
            Assert.isDate(reqJson.getString("endTime"),DateUtil.DATE_FORMATE_STRING_B,"结束时间格式错误");
        }

        //联系电话格式校验
        if (!StringUtil.isEmpty(reqJson.getString("tel")) && !ValidatorUtil.isMobile(reqJson.getString("tel"))) {
            throw new IllegalArgumentException("联系电话格式不正确");
        }

        //校验车牌号是否存在
        OwnerCarDto ownerCarDto = new OwnerCarDto();
        ownerCarDto.setCommunityId(reqJson.getString("communityId"));
        ownerCarDto.setCarNum(reqJson.getString("carNum"));
        ownerCarDto.setPaIds(new String[]{reqJson.getString("paId")});
        ownerCarDto.setCarTypeCds(new String[]{OwnerCarDto.CAR_TYPE_PRIMARY, OwnerCarDto.CAR_TYPE_MEMBER}); // 临时车除外
        List<OwnerCarDto> ownerCars = ownerCarInnerServiceSMOImpl.queryOwnerCars(ownerCarDto);

        if (!ListUtil.isNull(ownerCars)) {
            throw new IllegalArgumentException("车辆已存在");
        }

        //判断临时车 是否在场
        String parkingIn = MappingCache.getValue("TEMP_CAR_IN_PARKING");

        if (!"ON".equals(parkingIn)) {
            return;
        }

        ownerCarDto = new OwnerCarDto();
        ownerCarDto.setCommunityId(reqJson.getString("communityId"));
        ownerCarDto.setCarNum(reqJson.getString("carNum"));
        ownerCarDto.setCarTypeCds(new String[]{OwnerCarDto.CAR_TYPE_TEMP}); // 临时车除外
       int count = ownerCarInnerServiceSMOImpl.queryOwnerCarsCount(ownerCarDto);
        if (count < 1) {
            return;
        }

//        CarInoutDto carInoutDto = new CarInoutDto();
//        carInoutDto.setCarNum(reqJson.getString("carNum"));
//        carInoutDto.setStates(new String[]{CarInoutDto.STATE_PAY, CarInoutDto.STATE_IN, CarInoutDto.STATE_REPAY});
//        List<CarInoutDto> carInoutDtos = carInoutInnerServiceSMOImpl.queryCarInouts(carInoutDto);
//        if (carInoutDtos != null && carInoutDtos.size() > 0) {
//            throw new CmdException("车辆在场，请出场后再办理月租车");
//        }
    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        if (!reqJson.containsKey("leaseType")) {
            reqJson.put("leaseType", OwnerCarDto.LEASE_TYPE_MONTH);
        }

        if (!OwnerCarDto.LEASE_TYPE_MONTH.equals(reqJson.getString("leaseType"))) {
            reqJson.put("startTime", DateUtil.getFormatTimeString(new Date(), DateUtil.DATE_FORMATE_STRING_B));
            reqJson.put("endTime", "2050-01-01");
        }

        JSONObject businessOwnerCar = new JSONObject();
        businessOwnerCar.putAll(reqJson);
        businessOwnerCar.put("memberId", GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_carId));
        if (!reqJson.containsKey("carId") || reqJson.getString("carId").startsWith("-")) {
            businessOwnerCar.put("carId", businessOwnerCar.getString("memberId"));
        }
        OwnerCarPo ownerCarPo = BeanConvertUtil.covertBean(businessOwnerCar, OwnerCarPo.class);
        ownerCarPo.setState(OwnerCarDto.STATE_NORMAL);
        ownerCarPo.setContact(StringUtil.isEmpty(ownerCarPo.getContact()) ? "" : ownerCarPo.getContact());
        ownerCarPo.setTel(StringUtil.isEmpty(ownerCarPo.getTel()) ? "" : ownerCarPo.getTel());

        //没有指定时为主要车辆
        if (!reqJson.containsKey("carTypeCd") || StringUtil.isEmpty(reqJson.getString("carTypeCd"))) {
            ownerCarPo.setCarTypeCd(OwnerCarDto.CAR_TYPE_PRIMARY);
        }
        //添加车辆属性
        //dealOwnerCarAttr(reqJson, ownerCarPo);


        int flag = ownerCarV1InnerServiceSMOImpl.saveOwnerCar(ownerCarPo);
        if (flag < 1) {
            throw new CmdException("保存车辆属性失败");
        }

    }



}
