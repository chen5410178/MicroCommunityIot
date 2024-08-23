package com.java110.openapi.cmd.car;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.dto.car.OwnerCarDto;
import com.java110.bean.dto.owner.OwnerDto;
import com.java110.bean.po.owner.OwnerPo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.CmdContextUtils;
import com.java110.core.utils.ListUtil;
import com.java110.dto.parking.ParkingAreaDto;
import com.java110.dto.parking.ParkingSpaceDto;
import com.java110.dto.store.StoreDto;
import com.java110.intf.car.IOwnerCarV1InnerServiceSMO;
import com.java110.intf.car.IParkingAreaV1InnerServiceSMO;
import com.java110.intf.car.IParkingSpaceV1InnerServiceSMO;
import com.java110.intf.user.IOwnerV1InnerServiceSMO;
import com.java110.intf.user.IStoreV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import com.java110.po.ownerCar.OwnerCarPo;
import com.java110.po.parking.ParkingAreaPo;
import com.java110.po.parking.ParkingSpacePo;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.List;

/**
 * 五、同步车辆
 * 同步车辆相关信息，这里主要包括业主 业主下车辆信息，如果车辆存在则修改，如果不存在则添加；
 */
@Java110Cmd(serviceCode = "car.addCarApi")
public class AddCarApiCmd extends Cmd {

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Autowired
    private IStoreV1InnerServiceSMO storeV1InnerServiceSMOImpl;

    @Autowired
    private IOwnerV1InnerServiceSMO ownerV1InnerServiceSMOImpl;

    @Autowired
    private IOwnerCarV1InnerServiceSMO ownerCarV1InnerServiceSMOImpl;

    @Autowired
    private IParkingAreaV1InnerServiceSMO parkingAreaV1InnerServiceSMOImpl;

    @Autowired
    private IParkingSpaceV1InnerServiceSMO parkingSpaceV1InnerServiceSMOImpl;


    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson, "communityId", "未包含communityId");
        Assert.hasKeyAndValue(reqJson, "ownerId", "未包含ownerId");
        Assert.hasKeyAndValue(reqJson, "name", "未包含name");
        Assert.hasKeyAndValue(reqJson, "link", "未包含link");

        Assert.hasKeyAndValue(reqJson, "carMemberId", "未包含carMemberId");
        Assert.hasKeyAndValue(reqJson, "carId", "未包含carId");
        Assert.hasKeyAndValue(reqJson, "carNum", "未包含carNum");
        Assert.hasKeyAndValue(reqJson, "paId", "未包含paId");
        Assert.hasKeyAndValue(reqJson, "psId", "未包含psId");
        Assert.hasKeyAndValue(reqJson, "paNum", "未包含paNum");
        Assert.hasKeyAndValue(reqJson, "psNum", "未包含psNum");
        Assert.hasKeyAndValue(reqJson, "carTypeCd", "未包含carTypeCd");
        Assert.hasKeyAndValue(reqJson, "startTime", "未包含startTime");
        Assert.hasKeyAndValue(reqJson, "endTime", "未包含endTime");
        Assert.hasKeyAndValue(reqJson, "leaseType", "未包含leaseType");

        // todo 只有运营团队的账号才能操作接口

        String storeId = CmdContextUtils.getStoreId(context);

        StoreDto storeDto = new StoreDto();
        storeDto.setStoreId(storeId);
        storeDto.setStoreTypeCd(StoreDto.STORE_TYPE_ADMIN);
        List<StoreDto> storeDtos = storeV1InnerServiceSMOImpl.queryStores(storeDto);

        if (ListUtil.isNull(storeDtos)) {
            throw new CmdException("登陆账户没有权限");
        }
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {


        //todo 处理业主数据
        doOwner(reqJson, context);

        //todo 保存停车场
        toSaveParkingArea(reqJson, context, reqJson.getString("communityId"));

        //todo 保存车位
        toSaveParkingSpace(reqJson, context, reqJson.getString("communityId"));

        //todo 处理 车辆信息
        doOneCar(reqJson, context);

    }

    private void doOneCar(JSONObject car, ICmdDataFlowContext context) {

        //todo 处理车位

        OwnerCarDto ownerCarDto = new OwnerCarDto();
        ownerCarDto.setMemberId(car.getString("carMemberId"));
        ownerCarDto.setStatusCd("");
        List<OwnerCarDto> ownerCarDtos = ownerCarV1InnerServiceSMOImpl.queryOwnerCars(ownerCarDto);


        OwnerCarPo ownerCarPo = new OwnerCarPo();
        ownerCarPo.setStartTime(car.getString("startTime"));
        ownerCarPo.setEndTime(car.getString("endTime"));
        //获取房屋名称
        ownerCarPo.setCarTypeCd(car.getString("carTypeCd")); //主车辆
        ownerCarPo.setPsId(car.getString("psId"));
        ownerCarPo.setOwnerId(car.getString("ownerId"));
        ownerCarPo.setCommunityId(car.getString("communityId"));
        ownerCarPo.setCarId(car.getString("carId"));
        ownerCarPo.setMemberId(car.getString("carMemberId"));
        ownerCarPo.setState("1001"); //1001 正常状态，2002 车位释放欠费状态，3003 车位释放
        ownerCarPo.setLeaseType(car.getString("leaseType"));
        ownerCarPo.setCarNum(car.getString("carNum"));
        ownerCarPo.setCarType("9901");

        if (ListUtil.isNull(ownerCarDtos)) {
            ownerCarV1InnerServiceSMOImpl.saveOwnerCar(ownerCarPo);
        } else {
            ownerCarV1InnerServiceSMOImpl.updateOwnerCar(ownerCarPo);
        }


    }


    private void toSaveParkingArea(JSONObject car, ICmdDataFlowContext context, String communityId) {

        ParkingAreaDto parkingAreaDto = new ParkingAreaDto();
        parkingAreaDto.setPaId(car.getString("paId"));
        parkingAreaDto.setCommunityId(communityId);
        parkingAreaDto.setStatusCd("");
        //查询停车场
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaV1InnerServiceSMOImpl.queryParkingAreas(parkingAreaDto);

        if (ListUtil.isNull(parkingAreaDtos)) {
            parkingAreaDto = new ParkingAreaDto();
            parkingAreaDto.setNum(car.getString("paNum"));
            parkingAreaDto.setCommunityId(communityId);
            //查询停车场
            parkingAreaDtos = parkingAreaV1InnerServiceSMOImpl.queryParkingAreas(parkingAreaDto);
            if(!ListUtil.isNull(parkingAreaDtos)) {
                car.put("paId", parkingAreaDtos.get(0).getPaId());
            }
        }
        ParkingAreaPo parkingAreaPo = new ParkingAreaPo();
        parkingAreaPo.setCommunityId(communityId);
        parkingAreaPo.setNum(car.getString("paNum"));
        parkingAreaPo.setPaId(car.getString("paId"));
        parkingAreaPo.setTypeCd("1001");
        parkingAreaPo.setRemark("导入数据");
        if (!ListUtil.isNull(parkingAreaDtos)) {
            parkingAreaPo.setPaId(parkingAreaDtos.get(0).getPaId());
            car.put("paId", parkingAreaDtos.get(0).getPaId());
            parkingAreaV1InnerServiceSMOImpl.updateParkingArea(parkingAreaPo);
            return;
        }
        parkingAreaV1InnerServiceSMOImpl.saveParkingArea(parkingAreaPo);

    }

    /**
     * 保存停车位
     *
     * @param car
     * @param context
     * @param communityId
     */
    private void toSaveParkingSpace(JSONObject car, ICmdDataFlowContext context, String communityId) {
        ParkingSpaceDto parkingSpaceDto = new ParkingSpaceDto();
        parkingSpaceDto.setPsId(car.getString("psId"));
        parkingSpaceDto.setStatusCd("");
        //查询停车位
        List<ParkingSpaceDto> parkingSpaceDtos = parkingSpaceV1InnerServiceSMOImpl.queryParkingSpaces(parkingSpaceDto);
        ParkingSpacePo parkingSpacePo = new ParkingSpacePo();
        parkingSpacePo.setCommunityId(communityId);
        parkingSpacePo.setNum(car.getString("psNum"));
        parkingSpacePo.setPaId(car.getString("paId"));
        parkingSpacePo.setParkingType(ParkingSpaceDto.TYPE_CD_COMMON);
        parkingSpacePo.setState(ParkingSpaceDto.STATE_HIRE);
        parkingSpacePo.setPsId(car.getString("psId"));
        parkingSpacePo.setRemark("导入数据");
        if (!ListUtil.isNull(parkingSpaceDtos)) {
            parkingSpaceV1InnerServiceSMOImpl.updateParkingSpace(parkingSpacePo);
            return;
        }
        parkingSpaceV1InnerServiceSMOImpl.saveParkingSpace(parkingSpacePo);

    }


    private void doOwner(JSONObject reqJson, ICmdDataFlowContext context) {

        OwnerDto ownerDto = new OwnerDto();
        ownerDto.setMemberId(reqJson.getString("memberId"));
        ownerDto.setStatusCd("");
        List<OwnerDto> ownerDtos = ownerV1InnerServiceSMOImpl.queryOwners(ownerDto);
        OwnerPo ownerPo = new OwnerPo();
        ownerPo.setUserId("-1");
        ownerPo.setAge("1");
        ownerPo.setCommunityId(reqJson.getString("communityId"));
        ownerPo.setIdCard("");
        ownerPo.setLink(reqJson.getString("link"));
        ownerPo.setSex("1");
        ownerPo.setMemberId(reqJson.getString("ownerId"));
        ownerPo.setName(reqJson.getString("name"));
        ownerPo.setOwnerId(reqJson.getString("ownerId")); //业主 所以和成员ID需要一样

        if (!ListUtil.isNull(ownerDtos)) {
            ownerV1InnerServiceSMOImpl.updateOwner(ownerPo);
            return;
        }

        ownerPo.setOwnerTypeCd(OwnerDto.OWNER_TYPE_CD_OWNER);
        ownerPo.setRemark("第三方导入系统同步");
        ownerPo.setState("2000");
        ownerPo.setAddress("无");
        ownerPo.setOwnerFlag(OwnerDto.OWNER_FLAG_TRUE);

        /**
         *   Assert.hasKeyAndValue(reqJson, "communityId", "未包含communityId");
         *         Assert.hasKeyAndValue(reqJson, "ownerId", "未包含ownerId");
         *         Assert.hasKeyAndValue(reqJson, "name", "未包含name");
         *         Assert.hasKeyAndValue(reqJson, "link", "未包含link");
         */
        ownerV1InnerServiceSMOImpl.saveOwner(ownerPo);

    }
}
