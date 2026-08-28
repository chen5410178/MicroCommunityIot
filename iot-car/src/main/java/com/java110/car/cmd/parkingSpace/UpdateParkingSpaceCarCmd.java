package com.java110.car.cmd.parkingSpace;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.car.OwnerCarDto;
import com.java110.bean.dto.owner.OwnerDto;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.annotation.Java110Transactional;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.dto.parking.ParkingSpaceDto;
import com.java110.intf.car.IOwnerCarInnerServiceSMO;
import com.java110.intf.car.IParkingSpaceV1InnerServiceSMO;
import com.java110.intf.user.IOwnerInnerServiceSMO;
import com.java110.po.parking.ParkingSpacePo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.util.List;

@Java110Cmd(serviceCode = "parkingSpace.updateParkingSpaceCar")
public class UpdateParkingSpaceCarCmd extends Cmd {

    @Autowired
    private IParkingSpaceV1InnerServiceSMO parkingSpaceV1InnerServiceSMOImpl;

    @Autowired
    private IOwnerCarInnerServiceSMO ownerCarInnerServiceSMOImpl;

    @Autowired
    private IOwnerInnerServiceSMO ownerInnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        Assert.hasKeyAndValue(reqJson, "communityId", "未包含小区ID");
        Assert.hasKeyAndValue(reqJson, "psId", "未包含psId");
        Assert.hasKeyAndValue(reqJson, "carId", "未包含carId");
        Assert.hasKeyAndValue(reqJson, "ownerId", "未包含ownerId");

        //校验车位存在且当前状态必须为出租 H 或出售 S 才可修改车辆
        ParkingSpaceDto parkingSpaceDto = new ParkingSpaceDto();
        parkingSpaceDto.setCommunityId(reqJson.getString("communityId"));
        parkingSpaceDto.setPsId(reqJson.getString("psId"));
        List<ParkingSpaceDto> parkingSpaceDtos = parkingSpaceV1InnerServiceSMOImpl.queryParkingSpaces(parkingSpaceDto);
        if (ListUtil.isNull(parkingSpaceDtos) || parkingSpaceDtos.size() != 1) {
            throw new CmdException("未查询到停车位信息");
        }
        if (ParkingSpaceDto.STATE_FREE.equals(parkingSpaceDtos.get(0).getState())) {
            throw new CmdException("请先出租或出售车位后再修改车辆");
        }

        //按 carId 查询车辆，确认车辆存在
        queryOwnerCar(reqJson.getString("carId"), reqJson.getString("communityId"));

        //原车位状态为出租 H 时 出租时间范围必填 且 结束时间晚于开始时间
        if (ParkingSpaceDto.STATE_HIRE.equals(parkingSpaceDtos.get(0).getState())) {
            Assert.hasKeyAndValue(reqJson, "startTime", "出租时 开始时间不能为空");
            Assert.hasKeyAndValue(reqJson, "endTime", "出租时 结束时间不能为空");
            Assert.isDate(reqJson.getString("startTime"), DateUtil.DATE_FORMATE_STRING_A, "开始时间不是有效时间");
            Assert.isDate(reqJson.getString("endTime"), DateUtil.DATE_FORMATE_STRING_A, "结束时间不是有效时间");
            if (DateUtil.getDateFromStringA(reqJson.getString("endTime")).before(DateUtil.getDateFromStringA(reqJson.getString("startTime")))) {
                throw new CmdException("结束时间必须晚于开始时间");
            }
        }
    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        String communityId = reqJson.getString("communityId");
        String psId = reqJson.getString("psId");
        String carId = reqJson.getString("carId");
        String ownerId = reqJson.getString("ownerId");

        //查询车位当前绑定车辆及状态
        ParkingSpaceDto parkingSpaceDto = new ParkingSpaceDto();
        parkingSpaceDto.setCommunityId(communityId);
        parkingSpaceDto.setPsId(psId);
        List<ParkingSpaceDto> parkingSpaceDtos = parkingSpaceV1InnerServiceSMOImpl.queryParkingSpaces(parkingSpaceDto);
        if (ListUtil.isNull(parkingSpaceDtos) || parkingSpaceDtos.size() != 1) {
            throw new CmdException("未查询到停车位信息");
        }
        ParkingSpaceDto tParkingSpace = parkingSpaceDtos.get(0);

        //按 carId 查询车辆信息，以车辆表为准取车牌号及 memberId
        OwnerCarDto tOwnerCar = queryOwnerCar(carId, communityId);

        ParkingSpacePo parkingSpacePo = new ParkingSpacePo();
        parkingSpacePo.setPsId(psId);
        parkingSpacePo.setCommunityId(communityId);
        parkingSpacePo.setOwnerId(ownerId);
        parkingSpacePo.setOwnerName(queryOwnerName(ownerId, communityId));
        parkingSpacePo.setCarId(carId);
        parkingSpacePo.setCarNum(tOwnerCar.getCarNum());
        if (ParkingSpaceDto.STATE_HIRE.equals(tParkingSpace.getState())) {
            parkingSpacePo.setStartTime(reqJson.getString("startTime"));
            parkingSpacePo.setEndTime(reqJson.getString("endTime"));
        } else {
            parkingSpacePo.setStartTime("");
            parkingSpacePo.setEndTime("");
        }
        int flag = parkingSpaceV1InnerServiceSMOImpl.updateParkingSpace(parkingSpacePo);
        if (flag < 1) {
            throw new CmdException("修改车位失败");
        }

        context.setResponseEntity(ResultVo.success());
    }

    /**
     * 按 carId 查询车辆信息
     *
     * @param carId       车辆ID
     * @param communityId 小区ID
     * @return 车辆信息
     */
    private OwnerCarDto queryOwnerCar(String carId, String communityId) {
        OwnerCarDto ownerCarDto = new OwnerCarDto();
        ownerCarDto.setCarId(carId);
        ownerCarDto.setCommunityId(communityId);
        List<OwnerCarDto> ownerCarDtos = ownerCarInnerServiceSMOImpl.queryOwnerCars(ownerCarDto);
        if (ListUtil.isNull(ownerCarDtos) || ownerCarDtos.size() != 1) {
            throw new CmdException("未查询到车辆信息");
        }
        return ownerCarDtos.get(0);
    }

    /**
     * 查询业主名称
     *
     * @param ownerId 业主ID
     * @return 业主名称
     */
    private String queryOwnerName(String ownerId, String communityId) {
        OwnerDto ownerDto = new OwnerDto();
        ownerDto.setOwnerId(ownerId);
        ownerDto.setCommunityId(communityId);
        List<OwnerDto> ownerDtos = ownerInnerServiceSMOImpl.queryOwners(ownerDto);
        if (ListUtil.isNull(ownerDtos)) {
            return "";
        }
        return ownerDtos.get(0).getName();
    }
}
