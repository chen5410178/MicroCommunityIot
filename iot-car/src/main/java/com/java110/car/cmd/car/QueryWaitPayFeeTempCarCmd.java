package com.java110.car.cmd.car;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.car.OwnerCarDto;
import com.java110.bean.dto.car.UnlicensedCarPlateDto;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.carInout.CarInoutDto;
import com.java110.dto.ownerCarOpenUser.OwnerCarOpenUserDto;
import com.java110.intf.barrier.ICarInoutV1InnerServiceSMO;
import com.java110.intf.car.IOwnerCarOpenUserV1InnerServiceSMO;
import com.java110.intf.car.IUnlicensedCarPlateV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 查询待缴费临时车
 */
@Java110Cmd(serviceCode = "car.queryWaitPayFeeTempCar")
public class QueryWaitPayFeeTempCarCmd extends Cmd {

    @Autowired
    private IOwnerCarOpenUserV1InnerServiceSMO ownerCarOpenUserV1InnerServiceSMOImpl;

    @Autowired
    private ICarInoutV1InnerServiceSMO carInoutV1InnerServiceSMOImpl;

    @Autowired
    private IUnlicensedCarPlateV1InnerServiceSMO unlicensedCarPlateV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        Assert.hasKeyAndValue(reqJson, "openId", "未包含开放用户");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        List<OwnerCarDto> ownerCarDtos = new ArrayList<>();
        Set<String> carNumSet = new HashSet<>();
        OwnerCarDto ownerCarDto = null;
        if (!StringUtil.jsonHasKayAndValue(reqJson, "machineId")) {
            OwnerCarOpenUserDto ownerCarOpenUserDto = new OwnerCarOpenUserDto();
            ownerCarOpenUserDto.setOpenId(reqJson.getString("openId"));
            List<OwnerCarOpenUserDto> ownerCarOpenUserDtos = ownerCarOpenUserV1InnerServiceSMOImpl.queryOwnerCarOpenUsers(ownerCarOpenUserDto);
            for (OwnerCarOpenUserDto tmpOwnerCarOpenUserDto : ownerCarOpenUserDtos) {
                if (carNumSet.add(tmpOwnerCarOpenUserDto.getCarNum())) {
                    ownerCarDto = new OwnerCarDto();
                    ownerCarDto.setCarNum(tmpOwnerCarOpenUserDto.getCarNum());
                    ownerCarDtos.add(ownerCarDto);
                }
            }
        } else {

            //查询是否有车牌号

            CarInoutDto carInoutDto = new CarInoutDto();
            carInoutDto.setMachineId(reqJson.getString("machineId"));
            carInoutDto.setState(CarInoutDto.STATE_IN_FAIL);
            carInoutDto.setCarInout(CarInoutDto.CAR_INOUT_OUT);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, -5);
            carInoutDto.setStartTime(DateUtil.getFormatTimeStringA(calendar.getTime()));
            carInoutDto.setPage(1);
            carInoutDto.setRow(1);
            List<CarInoutDto> carInoutDetailDtos = carInoutV1InnerServiceSMOImpl.queryCarInoutDetails(carInoutDto);

            if (carInoutDetailDtos != null) {
                for (CarInoutDto tmpCarInoutDetailDto : carInoutDetailDtos) {
                    if (carNumSet.add(tmpCarInoutDetailDto.getCarNum())) {
                        ownerCarDto = new OwnerCarDto();
                        ownerCarDto.setCarNum(tmpCarInoutDetailDto.getCarNum());
                        ownerCarDtos.add(ownerCarDto);
                    }
                }
            }
        }

        //查询无牌车在场记录
        UnlicensedCarPlateDto unlicensedCarPlateDto = new UnlicensedCarPlateDto();
        unlicensedCarPlateDto.setOpenId(reqJson.getString("openId"));
        if (StringUtil.jsonHasKayAndValue(reqJson, "communityId")) {
            unlicensedCarPlateDto.setCommunityId(reqJson.getString("communityId"));
        }
        unlicensedCarPlateDto.setStatusCd("0");
        List<UnlicensedCarPlateDto> unlicensedCarPlateDtos = unlicensedCarPlateV1InnerServiceSMOImpl.queryUnlicensedCarPlates(unlicensedCarPlateDto);
        if (unlicensedCarPlateDtos != null && unlicensedCarPlateDtos.size() > 0) {
            CarInoutDto queryCarInoutDto = new CarInoutDto();
            queryCarInoutDto.setCarInout(CarInoutDto.CAR_INOUT_IN);
            queryCarInoutDto.setStates(new String[]{CarInoutDto.STATE_IN, CarInoutDto.STATE_PAY, CarInoutDto.STATE_REPAY});
            for (UnlicensedCarPlateDto tmpUnlicensedCarPlateDto : unlicensedCarPlateDtos) {
                queryCarInoutDto.setCarNum(tmpUnlicensedCarPlateDto.getCarNum());
                List<CarInoutDto> carInoutDtos = carInoutV1InnerServiceSMOImpl.queryCarInouts(queryCarInoutDto);
                if (carInoutDtos != null && carInoutDtos.size() > 0
                        && carNumSet.add(tmpUnlicensedCarPlateDto.getCarNum())) {
                    ownerCarDto = new OwnerCarDto();
                    ownerCarDto.setCarNum(tmpUnlicensedCarPlateDto.getCarNum());
                    ownerCarDtos.add(ownerCarDto);
                }
            }
        }

        context.setResponseEntity(ResultVo.createResponseEntity(ownerCarDtos));

    }
}
