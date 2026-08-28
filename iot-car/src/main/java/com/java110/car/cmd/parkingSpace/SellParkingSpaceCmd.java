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
import com.java110.core.utils.StringUtil;
import com.java110.dto.parking.ParkingSpaceDto;
import com.java110.intf.car.IOwnerCarInnerServiceSMO;
import com.java110.intf.car.IOwnerCarV1InnerServiceSMO;
import com.java110.intf.car.IParkingSpaceV1InnerServiceSMO;
import com.java110.intf.user.IOwnerInnerServiceSMO;
import com.java110.po.ownerCar.OwnerCarPo;
import com.java110.po.parking.ParkingSpacePo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.util.List;

@Java110Cmd(serviceCode = "parkingSpace.sellParkingSpace")
public class SellParkingSpaceCmd extends Cmd {

    @Autowired
    private IParkingSpaceV1InnerServiceSMO parkingSpaceV1InnerServiceSMOImpl;

    @Autowired
    private IOwnerCarInnerServiceSMO ownerCarInnerServiceSMOImpl;

    @Autowired
    private IOwnerCarV1InnerServiceSMO ownerCarV1InnerServiceSMOImpl;

    @Autowired
    private IOwnerInnerServiceSMO ownerInnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        Assert.hasKeyAndValue(reqJson, "communityId", "未包含小区ID");
        Assert.hasKeyAndValue(reqJson, "psId", "未包含psId");
        Assert.hasKeyAndValue(reqJson, "ownerId", "未包含ownerId");
        Assert.hasKeyAndValue(reqJson, "sellOrHire", "未包含sellOrHire");

        String sellOrHire = reqJson.getString("sellOrHire");
        if (!"H".equals(sellOrHire) && !"S".equals(sellOrHire)) {
            throw new CmdException("sellOrHire值错误，出售为S 出租为H");
        }

        //校验车位当前状态必须为空闲 F 才可出租/出售
        ParkingSpaceDto parkingSpaceDto = new ParkingSpaceDto();
        parkingSpaceDto.setCommunityId(reqJson.getString("communityId"));
        parkingSpaceDto.setPsId(reqJson.getString("psId"));
        List<ParkingSpaceDto> parkingSpaceDtos = parkingSpaceV1InnerServiceSMOImpl.queryParkingSpaces(parkingSpaceDto);
        if (ListUtil.isNull(parkingSpaceDtos) || parkingSpaceDtos.size() != 1) {
            throw new CmdException("未查询到停车位信息");
        }
        if (!ParkingSpaceDto.STATE_FREE.equals(parkingSpaceDtos.get(0).getState())) {
            throw new CmdException("当前车位状态不允许出租或出售");
        }

        //出租时 出租时间范围必填 且 结束时间晚于开始时间
        if ("H".equals(sellOrHire)) {
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
        String sellOrHire = reqJson.getString("sellOrHire");

        //carId 选填：为空时车位仅绑业主，不绑车辆
        if (StringUtil.isNullOrNone(carId)) {
            carId = "";
        }

        //查询车位信息
        ParkingSpaceDto parkingSpaceDto = new ParkingSpaceDto();
        parkingSpaceDto.setCommunityId(communityId);
        parkingSpaceDto.setPsId(psId);
        List<ParkingSpaceDto> parkingSpaceDtos = parkingSpaceV1InnerServiceSMOImpl.queryParkingSpaces(parkingSpaceDto);
        if (ListUtil.isNull(parkingSpaceDtos) || parkingSpaceDtos.size() != 1) {
            throw new CmdException("未查询到停车位信息");
        }

        //按 carId 查询车辆信息，以车辆表为准取车牌号及 memberId（carId 选填，为空时仅绑业主）
        OwnerCarDto tOwnerCar = null;
        if (!StringUtil.isNullOrNone(carId)) {
            tOwnerCar = queryOwnerCar(carId, communityId);
            if (!reqJson.getString("ownerId").equals(tOwnerCar.getOwnerId())) {
                throw new CmdException("所选车辆不属于该业主");
            }
            //校验车辆未绑定其他车位
            ParkingSpaceDto checkPsDto = new ParkingSpaceDto();
            checkPsDto.setCommunityId(communityId);
            checkPsDto.setCarId(carId);
            List<ParkingSpaceDto> checkPsDtos = parkingSpaceV1InnerServiceSMOImpl.queryParkingSpaces(checkPsDto);
            if (!ListUtil.isNull(checkPsDtos)) {
                throw new CmdException("所选车辆已绑定车位");
            }
        }

        String startTime = "";
        String endTime = "";
        if ("H".equals(sellOrHire)) {
            startTime = reqJson.getString("startTime");
            endTime = reqJson.getString("endTime");
        } else {
            startTime = DateUtil.getFormatTimeString(new java.util.Date(), DateUtil.DATE_FORMATE_STRING_A);
            endTime = "2050-01-01 00:00:00";
        }

        ParkingSpacePo parkingSpacePo = new ParkingSpacePo();
        parkingSpacePo.setPsId(psId);
        parkingSpacePo.setCommunityId(communityId);
        parkingSpacePo.setState(sellOrHire);
        parkingSpacePo.setOwnerId(reqJson.getString("ownerId"));
        parkingSpacePo.setOwnerName(queryOwnerName(reqJson.getString("ownerId"), communityId));
        parkingSpacePo.setCarId(carId);
        parkingSpacePo.setCarNum(tOwnerCar == null ? "" : tOwnerCar.getCarNum());
        parkingSpacePo.setStartTime(startTime);
        parkingSpacePo.setEndTime(endTime);
        int flag = parkingSpaceV1InnerServiceSMOImpl.updateParkingSpace(parkingSpacePo);
        if (flag < 1) {
            throw new CmdException("修改车位失败");
        }

        //选择了车辆时，同步车辆租期与租赁类型（未选车辆跳过）
        if (tOwnerCar != null) {
            OwnerCarPo ownerCarPo = new OwnerCarPo();
            ownerCarPo.setMemberId(tOwnerCar.getMemberId());
            ownerCarPo.setOwnerId(tOwnerCar.getOwnerId());
            ownerCarPo.setCommunityId(communityId);
            ownerCarPo.setStartTime(startTime);
            ownerCarPo.setEndTime(endTime);
            ownerCarPo.setLeaseType(sellOrHire);
            ownerCarV1InnerServiceSMOImpl.updateOwnerCar(ownerCarPo);
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
