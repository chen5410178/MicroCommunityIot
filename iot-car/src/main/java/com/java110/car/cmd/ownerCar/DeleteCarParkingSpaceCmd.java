package com.java110.car.cmd.ownerCar;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.dto.owner.OwnerCarDto;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
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

import java.util.List;

@Java110Cmd(serviceCode = "ownerCar.deleteCarParkingSpace")
public class DeleteCarParkingSpaceCmd extends Cmd {

    @Autowired
    private IOwnerCarInnerServiceSMO ownerCarInnerServiceSMOImpl;



    @Autowired
    private IParkingSpaceV1InnerServiceSMO parkingSpaceInnerServiceSMOImpl;

    @Autowired
    private IParkingSpaceV1InnerServiceSMO parkingSpaceV1InnerServiceSMOImpl;

    @Autowired
    private IOwnerCarV1InnerServiceSMO ownerCarV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        Assert.jsonObjectHaveKey(reqJson, "communityId", "未包含小区ID");
//        Assert.jsonObjectHaveKey(reqJson, "carId", "请求报文中未包含carId");
        Assert.hasLength(reqJson.getString("communityId"), "小区ID不能为空");

        OwnerCarDto ownerCarDto = new OwnerCarDto();
        ownerCarDto.setCarId(reqJson.getString("carId"));
        ownerCarDto.setCommunityId(reqJson.getString("communityId"));
        ownerCarDto.setStatusCd("0");
        List<OwnerCarDto> ownerCarDtos = ownerCarInnerServiceSMOImpl.queryOwnerCars(ownerCarDto);
        if (ownerCarDtos != null && ownerCarDtos.size() > 1) {
            throw new IllegalArgumentException("有多个车辆绑定此车位，请先删除车辆！");
        }

        String state = ownerCarDtos.get(0).getState();

        if (StringUtil.isEmpty(state) || state.equals(OwnerCarDto.STATE_FINISH)) {
            throw new IllegalArgumentException("车位已经释放无需释放");
        }

//        if (ownerCarDtos.get(0).getEndTime().getTime() > DateUtil.getCurrentDate().getTime()) {
//            throw new IllegalArgumentException("车位租用还未结束不能释放");
//        }
        reqJson.put("ownerCarDto", ownerCarDtos.get(0));
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {

        OwnerCarDto ownerCarDto = (OwnerCarDto) reqJson.get("ownerCarDto");
        boolean oweFee = false;


        if (OwnerCarDto.STATE_FINISH.equals(ownerCarDto.getState())) {
            oweFee = false;
        }


        OwnerCarPo ownerCarPo = new OwnerCarPo();
//        ownerCarPo.setPsId("-1");
        ownerCarPo.setCarId(reqJson.getString("carId"));
        ownerCarPo.setMemberId(reqJson.getString("memberId"));
        ownerCarPo.setCommunityId(reqJson.getString("communityId"));
        if (oweFee) {
            ownerCarPo.setState(OwnerCarDto.STATE_FINISH);
        }
        int flag = ownerCarV1InnerServiceSMOImpl.updateOwnerCar(ownerCarPo);
        if (flag < 1) {
            throw new IllegalArgumentException("修改车辆出错");
        }
        reqJson.put("carNumType", ParkingSpaceDto.STATE_FREE);
        reqJson.put("psId", ownerCarDto.getPsId());
        ParkingSpaceDto parkingSpaceDto = new ParkingSpaceDto();
        parkingSpaceDto.setCommunityId(reqJson.getString("communityId"));
        parkingSpaceDto.setPsId(reqJson.getString("psId"));
        List<ParkingSpaceDto> parkingSpaceDtos = parkingSpaceInnerServiceSMOImpl.queryParkingSpaces(parkingSpaceDto);

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
