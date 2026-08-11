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
import com.java110.bean.ResultVo;
import com.java110.bean.dto.car.OwnerCarDto;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.annotation.Java110Transactional;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.core.utils.ValidatorUtil;
import com.java110.dto.parking.ParkingAreaDto;
import com.java110.dto.parking.ParkingSpaceDto;
import com.java110.intf.car.IOwnerCarV1InnerServiceSMO;
import com.java110.intf.car.IParkingAreaV1InnerServiceSMO;
import com.java110.intf.car.IParkingSpaceV1InnerServiceSMO;
import com.java110.po.ownerCar.OwnerCarPo;
import com.java110.po.parking.ParkingSpacePo;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;


/**
 * 类表述：更新
 * 服务编码：ownerCar.updateOwnerCar
 * 请求路劲：/app/ownerCar.UpdateOwnerCar
 * add by 吴学文 at 2023-08-23 15:16:36 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Java110Cmd(serviceCode = "ownerCar.updateOwnerCar")
public class UpdateOwnerCarCmd extends Cmd {

  private static Logger logger = LoggerFactory.getLogger(UpdateOwnerCarCmd.class);

    @Autowired
    private IOwnerCarV1InnerServiceSMO ownerCarV1InnerServiceSMOImpl;

    @Autowired
    private IParkingAreaV1InnerServiceSMO parkingAreaV1InnerServiceSMOImpl;

    @Autowired
    private IParkingSpaceV1InnerServiceSMO parkingSpaceV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        Assert.jsonObjectHaveKey(reqJson, "communityId", "未包含小区ID");
        Assert.jsonObjectHaveKey(reqJson, "carNum", "请求报文中未包含carNum");
        Assert.jsonObjectHaveKey(reqJson, "carId", "请求报文中未包含carId");
        Assert.jsonObjectHaveKey(reqJson, "memberId", "请求报文中未包含memberId");
        Assert.jsonObjectHaveKey(reqJson, "carType", "请求报文中未包含carType");
        Assert.hasLength(reqJson.getString("communityId"), "小区ID不能为空");

        //联系电话格式校验
        if (!StringUtil.isEmpty(reqJson.getString("tel")) && !ValidatorUtil.isMobile(reqJson.getString("tel"))) {
            throw new IllegalArgumentException("联系电话格式不正确");
        }

        OwnerCarDto ownerCarDto = new OwnerCarDto();
        ownerCarDto.setMemberId(reqJson.getString("memberId"));
        ownerCarDto.setCommunityId(reqJson.getString("communityId"));
        ownerCarDto.setCarId(reqJson.getString("carId"));
        List<OwnerCarDto> ownerCarDtos = ownerCarV1InnerServiceSMOImpl.queryOwnerCars(ownerCarDto);

        Assert.listOnlyOne(ownerCarDtos, "未找到车辆信息");

        //校验停车场是否存在
        if (!StringUtil.isEmpty(reqJson.getString("paId"))) {
            ParkingAreaDto parkingAreaDto = new ParkingAreaDto();
            parkingAreaDto.setPaId(reqJson.getString("paId"));
            List<ParkingAreaDto> parkingAreaDtos = parkingAreaV1InnerServiceSMOImpl.queryParkingAreas(parkingAreaDto);
            Assert.listOnlyOne(parkingAreaDtos, "查询停车场错误！");
        }

        //选择了有效车位时校验车位空闲及归属停车场（保持原车位不重复校验）
        if (!StringUtil.isEmpty(reqJson.getString("psId")) && !"-1".equals(reqJson.getString("psId"))) {
            ParkingSpaceDto parkingSpaceDto = new ParkingSpaceDto();
            parkingSpaceDto.setPsId(reqJson.getString("psId"));
            List<ParkingSpaceDto> parkingSpaceDtos = parkingSpaceV1InnerServiceSMOImpl.queryParkingSpaces(parkingSpaceDto);
            Assert.listOnlyOne(parkingSpaceDtos, "查询车位错误！");
            //原车位继续使用时不校验空闲状态
            boolean keepOriginPs = !StringUtil.isEmpty(ownerCarDtos.get(0).getPsId()) && ownerCarDtos.get(0).getPsId().equals(reqJson.getString("psId"));
            if (!keepOriginPs) {
                String state = parkingSpaceDtos.get(0).getState();
                if (StringUtil.isEmpty(state) || !"F".equals(state)) {
                    throw new IllegalArgumentException("该车位不是空闲状态！");
                }
                if (!StringUtil.isEmpty(reqJson.getString("paId")) && !reqJson.getString("paId").equals(parkingSpaceDtos.get(0).getPaId())) {
                    throw new IllegalArgumentException("所选车位不属于所选停车场！");
                }
            }
        }

    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

        if (!reqJson.containsKey("leaseType")) {
            reqJson.put("leaseType", OwnerCarDto.LEASE_TYPE_MONTH);
        }

        if (!OwnerCarDto.LEASE_TYPE_MONTH.equals(reqJson.getString("leaseType"))) {
            reqJson.put("startTime", DateUtil.getFormatTimeString(new Date(), DateUtil.DATE_FORMATE_STRING_B));
            reqJson.put("endTime", "2037-01-01");
        }

        //没有选择车位时置为 -1
        if (StringUtil.isEmpty(reqJson.getString("psId"))) {
            reqJson.put("psId", "-1");
        }

        //查询原车辆信息，用于释放原车位
        OwnerCarDto originCarDto = new OwnerCarDto();
        originCarDto.setMemberId(reqJson.getString("memberId"));
        originCarDto.setCommunityId(reqJson.getString("communityId"));
        originCarDto.setCarId(reqJson.getString("carId"));
        List<OwnerCarDto> originCarDtos = ownerCarV1InnerServiceSMOImpl.queryOwnerCars(originCarDto);

        OwnerCarPo ownerCarPo = BeanConvertUtil.covertBean(reqJson, OwnerCarPo.class);
        int flag = ownerCarV1InnerServiceSMOImpl.updateOwnerCar(ownerCarPo);

        if (flag < 1) {
            throw new CmdException("修改数据失败");
        }

        //原车位不再使用时恢复空闲状态
        String newPsId = ownerCarPo.getPsId();
        if (!ListUtil.isNull(originCarDtos)
                && !StringUtil.isEmpty(originCarDtos.get(0).getPsId())
                && !"-1".equals(originCarDtos.get(0).getPsId())
                && !originCarDtos.get(0).getPsId().equals(newPsId)) {
            ParkingSpaceDto originParkingSpaceDto = new ParkingSpaceDto();
            originParkingSpaceDto.setCommunityId(ownerCarPo.getCommunityId());
            originParkingSpaceDto.setPsId(originCarDtos.get(0).getPsId());
            List<ParkingSpaceDto> originParkingSpaceDtos = parkingSpaceV1InnerServiceSMOImpl.queryParkingSpaces(originParkingSpaceDto);
            if (originParkingSpaceDtos != null && originParkingSpaceDtos.size() == 1) {
                ParkingSpacePo originParkingSpacePo = BeanConvertUtil.covertBean(BeanConvertUtil.beanCovertMap(originParkingSpaceDtos.get(0)), ParkingSpacePo.class);
                originParkingSpacePo.setState(ParkingSpaceDto.STATE_FREE);
                parkingSpaceV1InnerServiceSMOImpl.updateParkingSpace(originParkingSpacePo);
            }
        }

        //选择了有效车位时更新车位状态为出租
        if (!"-1".equals(newPsId)) {
            ParkingSpaceDto parkingSpaceDto = new ParkingSpaceDto();
            parkingSpaceDto.setCommunityId(ownerCarPo.getCommunityId());
            parkingSpaceDto.setPsId(ownerCarPo.getPsId());
            List<ParkingSpaceDto> parkingSpaceDtos = parkingSpaceV1InnerServiceSMOImpl.queryParkingSpaces(parkingSpaceDto);
            if (parkingSpaceDtos != null && parkingSpaceDtos.size() == 1) {
                ParkingSpacePo parkingSpacePo = BeanConvertUtil.covertBean(BeanConvertUtil.beanCovertMap(parkingSpaceDtos.get(0)), ParkingSpacePo.class);
                parkingSpacePo.setState("H"); //S 出售  H 出租  F 空闲
                parkingSpaceV1InnerServiceSMOImpl.updateParkingSpace(parkingSpacePo);
            }
        }

        cmdDataFlowContext.setResponseEntity(ResultVo.success());
    }
}
