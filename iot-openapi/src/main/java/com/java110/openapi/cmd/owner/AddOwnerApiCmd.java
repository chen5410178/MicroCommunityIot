package com.java110.openapi.cmd.owner;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.bean.dto.car.OwnerCarDto;
import com.java110.bean.dto.floor.FloorDto;
import com.java110.bean.dto.owner.OwnerDto;
import com.java110.bean.dto.owner.OwnerRoomRelDto;
import com.java110.bean.dto.room.RoomDto;
import com.java110.bean.dto.unit.UnitDto;
import com.java110.bean.po.floor.FloorPo;
import com.java110.bean.po.owner.OwnerPo;
import com.java110.bean.po.owner.OwnerRoomRelPo;
import com.java110.bean.po.room.RoomPo;
import com.java110.bean.po.unit.UnitPo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.smo.IPhotoSMO;
import com.java110.core.utils.*;
import com.java110.dto.parking.ParkingAreaDto;
import com.java110.dto.parking.ParkingSpaceDto;
import com.java110.dto.store.StoreDto;
import com.java110.intf.car.IOwnerCarV1InnerServiceSMO;
import com.java110.intf.car.IParkingAreaV1InnerServiceSMO;
import com.java110.intf.car.IParkingSpaceV1InnerServiceSMO;
import com.java110.intf.community.IFloorV1InnerServiceSMO;
import com.java110.intf.community.IRoomV1InnerServiceSMO;
import com.java110.intf.community.IUnitV1InnerServiceSMO;
import com.java110.intf.user.IOwnerRoomRelV1InnerServiceSMO;
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
 * 接口添加业主
 * <p>
 * 协议 请查看 《三方系统对接HC物联网系统协议1.0》下的 三、同步业主
 */
@Java110Cmd(serviceCode = "owner.addOwnerApi")
public class AddOwnerApiCmd extends Cmd {

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Autowired
    private IStoreV1InnerServiceSMO storeV1InnerServiceSMOImpl;

    @Autowired
    private IOwnerV1InnerServiceSMO ownerV1InnerServiceSMOImpl;

    @Autowired
    private IFloorV1InnerServiceSMO floorV1InnerServiceSMOImpl;

    @Autowired
    private IUnitV1InnerServiceSMO unitV1InnerServiceSMOImpl;

    @Autowired
    private IRoomV1InnerServiceSMO roomV1InnerServiceSMOImpl;

    @Autowired
    private IOwnerRoomRelV1InnerServiceSMO ownerRoomRelV1InnerServiceSMOImpl;

    @Autowired
    private IOwnerCarV1InnerServiceSMO ownerCarV1InnerServiceSMOImpl;

    @Autowired
    private IParkingAreaV1InnerServiceSMO parkingAreaV1InnerServiceSMOImpl;

    @Autowired
    private IParkingSpaceV1InnerServiceSMO parkingSpaceV1InnerServiceSMOImpl;

    @Autowired
    private IPhotoSMO photoSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        Assert.hasKeyAndValue(reqJson, "communityId", "未包含communityId");
        Assert.hasKeyAndValue(reqJson, "memberId", "未包含memberId");
        Assert.hasKeyAndValue(reqJson, "ownerId", "未包含ownerId");
        Assert.hasKeyAndValue(reqJson, "name", "未包含name");
        Assert.hasKeyAndValue(reqJson, "link", "未包含link");
        Assert.hasKeyAndValue(reqJson, "ownerTypeCd", "未包含ownerTypeCd");

        //todo 如果包含房屋信息
        JSONArray rooms = reqJson.getJSONArray("rooms");
        JSONObject room = null;
        if (!ListUtil.isNull(rooms)) {
            for (int roomIndex = 0; roomIndex < rooms.size(); roomIndex++) {
                room = rooms.getJSONObject(roomIndex);
                Assert.hasKeyAndValue(room, "roomId", "未包含roomId");
                Assert.hasKeyAndValue(room, "floorId", "未包含floorId");
                Assert.hasKeyAndValue(room, "unitId", "未包含unitId");
                Assert.hasKeyAndValue(room, "floorNum", "未包含floorNum");
                Assert.hasKeyAndValue(room, "unitNum", "未包含unitNum");
                Assert.hasKeyAndValue(room, "roomNum", "未包含roomNum");
            }

        }

        //todo 如果包含车辆信息
        JSONArray cars = reqJson.getJSONArray("cars");
        JSONObject car = null;

        if (!ListUtil.isNull(cars)) {
            for (int carIndex = 0; carIndex < cars.size(); carIndex++) {
                car = cars.getJSONObject(carIndex);
                Assert.hasKeyAndValue(car, "carMemberId", "未包含carMemberId");
                Assert.hasKeyAndValue(car, "carId", "未包含carId");
                Assert.hasKeyAndValue(car, "carNum", "未包含carNum");
                Assert.hasKeyAndValue(car, "paId", "未包含paId");
                Assert.hasKeyAndValue(car, "psId", "未包含psId");
                Assert.hasKeyAndValue(car, "paNum", "未包含paNum");
                Assert.hasKeyAndValue(car, "psNum", "未包含psNum");
                Assert.hasKeyAndValue(car, "carTypeCd", "未包含carTypeCd");
                Assert.hasKeyAndValue(car, "startTime", "未包含startTime");
                Assert.hasKeyAndValue(car, "endTime", "未包含endTime");
                Assert.hasKeyAndValue(car, "leaseType", "未包含leaseType");
            }

        }

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


        //todo 处理 房屋信息
        doRooms(reqJson, context);

        //todo 处理 车辆信息
        doCars(reqJson, context);

    }

    /**
     * Assert.hasKeyAndValue(car, "carMemberId", "未包含carMemberId");
     * Assert.hasKeyAndValue(car, "carId", "未包含carId");
     * Assert.hasKeyAndValue(car, "carNum", "未包含carNum");
     * Assert.hasKeyAndValue(car, "paId", "未包含paId");
     * Assert.hasKeyAndValue(car, "psId", "未包含psId");
     * Assert.hasKeyAndValue(car, "paNum", "未包含paNum");
     * Assert.hasKeyAndValue(car, "psNum", "未包含psNum");
     * Assert.hasKeyAndValue(car, "carTypeCd", "未包含carTypeCd");
     * Assert.hasKeyAndValue(car, "startTime", "未包含startTime");
     * Assert.hasKeyAndValue(car, "endTime", "未包含endTime");
     * Assert.hasKeyAndValue(car, "leaseType", "未包含leaseType");
     *
     * @param reqJson
     * @param context
     */
    private void doCars(JSONObject reqJson, ICmdDataFlowContext context) {

        JSONArray cars = reqJson.getJSONArray("cars");

        if (ListUtil.isNull(cars)) {
            return;
        }

        JSONObject car = null;
        OwnerCarDto ownerCarDto = null;
        List<OwnerCarDto> ownerCarDtos = null;
        for (int carIndex = 0; carIndex < cars.size(); carIndex++) {
            car = cars.getJSONObject(carIndex);

            doOneCar(reqJson, car, context);


        }


    }

    private void doOneCar(JSONObject reqJson, JSONObject car, ICmdDataFlowContext context) {

        //todo 保存停车场
        toSaveParkingArea(car, context, reqJson.getString("communityId"));

        //todo 保存车位
        toSaveParkingSpace(car, context, reqJson.getString("communityId"));

        //todo 处理车位

        OwnerCarDto ownerCarDto = new OwnerCarDto();
        ownerCarDto.setMemberId(car.getString("carMemberId"));
        List<OwnerCarDto> ownerCarDtos = ownerCarV1InnerServiceSMOImpl.queryOwnerCars(ownerCarDto);


        OwnerCarPo ownerCarPo = new OwnerCarPo();
        ownerCarPo.setStartTime(car.getString("startTime"));
        ownerCarPo.setEndTime(car.getString("endTime"));
        //获取房屋名称
        ownerCarPo.setCarTypeCd(car.getString("carTypeCd")); //主车辆
        ownerCarPo.setPsId(car.getString("psId"));
        ownerCarPo.setOwnerId(reqJson.getString("memberId"));
        ownerCarPo.setCommunityId(reqJson.getString("communityId"));
        ownerCarPo.setCarId(car.getString("carId"));
        ownerCarPo.setMemberId(car.getString("carMemberId"));
        ownerCarPo.setState("1001"); //1001 正常状态，2002 车位释放欠费状态，3003 车位释放
        ownerCarPo.setLeaseType(car.getString("leaseType"));
        ownerCarPo.setCarNum(car.getString("carNum"));

        if (ListUtil.isNull(ownerCarDtos)) {
            ownerCarV1InnerServiceSMOImpl.saveOwnerCar(ownerCarPo);
        } else {
            ownerCarV1InnerServiceSMOImpl.updateOwnerCar(ownerCarPo);
        }


    }


    private void toSaveParkingArea(JSONObject car, ICmdDataFlowContext context, String communityId) {

        ParkingAreaDto parkingAreaDto = new ParkingAreaDto();
        parkingAreaDto.setPaId(car.getString("carId"));
        //查询停车场
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaV1InnerServiceSMOImpl.queryParkingAreas(parkingAreaDto);
        if (ListUtil.isNull(parkingAreaDtos)) {

            ParkingAreaPo parkingAreaPo = new ParkingAreaPo();
            parkingAreaPo.setCommunityId(communityId);
            parkingAreaPo.setNum(car.getString("paNum"));
            parkingAreaPo.setPaId(car.getString("paId"));
            parkingAreaPo.setTypeCd("1001");
            parkingAreaPo.setRemark("导入数据");
            parkingAreaV1InnerServiceSMOImpl.saveParkingArea(parkingAreaPo);
        }
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
        //查询停车位
        List<ParkingSpaceDto> parkingSpaceDtos = parkingSpaceV1InnerServiceSMOImpl.queryParkingSpaces(parkingSpaceDto);

        if (ListUtil.isNull(parkingSpaceDtos)) {
            ParkingSpacePo parkingSpacePo = new ParkingSpacePo();
            parkingSpacePo.setCommunityId(communityId);
            parkingSpacePo.setNum(car.getString("psNum"));
            parkingSpacePo.setPaId(car.getString("paId"));
            parkingSpacePo.setParkingType(ParkingSpaceDto.TYPE_CD_COMMON);
            parkingSpacePo.setState(ParkingSpaceDto.STATE_HIRE);
            parkingSpacePo.setPsId(car.getString("psId"));
            parkingSpacePo.setRemark("导入数据");
            parkingSpaceV1InnerServiceSMOImpl.saveParkingSpace(parkingSpacePo);
        }
    }

    /**
     * 处理房屋信息
     *
     * @param reqJson
     * @param context
     */
    private void doRooms(JSONObject reqJson, ICmdDataFlowContext context) {

        JSONArray rooms = reqJson.getJSONArray("rooms");
        if (ListUtil.isNull(rooms)) {
            return;
        }

        JSONObject room = null;

        //todo 解绑业主下所有房屋

        OwnerRoomRelDto ownerRoomRelDto = new OwnerRoomRelDto();
        ownerRoomRelDto.setOwnerId(reqJson.getString("memberId"));
        List<OwnerRoomRelDto> ownerRoomRelDtos = ownerRoomRelV1InnerServiceSMOImpl.queryOwnerRoomRels(ownerRoomRelDto);

        if (!ListUtil.isNull(ownerRoomRelDtos)) {
            OwnerRoomRelPo ownerRoomRelPo = new OwnerRoomRelPo();
            ownerRoomRelPo.setOwnerId(reqJson.getString("memberId"));
            ownerRoomRelV1InnerServiceSMOImpl.deleteOwnerRoomRel(ownerRoomRelPo);
        }


        for (int roomIndex = 0; roomIndex < rooms.size(); roomIndex++) {
            room = rooms.getJSONObject(roomIndex);
            doOneRoom(reqJson, room, context);

        }
    }

    /**
     * Assert.hasKeyAndValue(room, "roomId", "未包含roomId");
     * Assert.hasKeyAndValue(room, "floorId", "未包含floorId");
     * Assert.hasKeyAndValue(room, "unitId", "未包含unitId");
     * Assert.hasKeyAndValue(room, "floorNum", "未包含floorNum");
     * Assert.hasKeyAndValue(room, "unitNum", "未包含unitNum");
     * Assert.hasKeyAndValue(room, "roomNum", "未包含roomNum");
     *
     * @param reqJson
     * @param room
     * @param context
     */
    private void doOneRoom(JSONObject reqJson, JSONObject room, ICmdDataFlowContext context) {


        //todo 保存楼栋
        toSaveFloor(room, context, reqJson.getString("communityId"));


        //todo 保存单元
        toSaveUnit(room, context, reqJson.getString("communityId"));


        RoomDto roomDto = null;
        RoomPo roomPo = null;
        List<RoomDto> roomDtos = null;

        roomDto = new RoomDto();
        roomDto.setRoomId(room.getString("roomId"));
        roomDtos = roomV1InnerServiceSMOImpl.queryRooms(roomDto);

        if (ListUtil.isNull(roomDtos)) {
            roomPo = new RoomPo();
            roomPo.setState(RoomDto.STATE_SELL);
            roomPo.setRoomId(room.getString("roomId"));
            roomPo.setApartment("10102");
            roomPo.setSection("1");
            roomPo.setCommunityId(reqJson.getString("communityId"));
            roomPo.setBuiltUpArea("1");
            roomPo.setFeeCoefficient("1.00");
            roomPo.setLayer("1");
            roomPo.setRoomArea("1");
            roomPo.setRoomNum(room.getString("roomNum"));
            roomPo.setRoomRent("0");
            roomPo.setRoomSubType(RoomDto.ROOM_SUB_TYPE_PERSON);
            roomPo.setRoomType(RoomDto.ROOM_TYPE_ROOM);
            roomPo.setUnitId(room.getString("unitId"));
            roomPo.setRemark("房产导入");
            roomPo.setUserId(CmdContextUtils.getUserId(context));
            roomV1InnerServiceSMOImpl.saveRoom(roomPo);
        }

        //todo 添加业主房屋关系

        OwnerRoomRelPo ownerRoomRelPo = new OwnerRoomRelPo();
        ownerRoomRelPo.setUserId(CmdContextUtils.getUserId(context));
        ownerRoomRelPo.setbId("-1");
        ownerRoomRelPo.setRelId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_relId));
        ownerRoomRelPo.setState("2001");
        ownerRoomRelPo.setRoomId(room.getString("roomId"));
        ownerRoomRelPo.setOwnerId(reqJson.getString("ownerId"));
        ownerRoomRelPo.setStartTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_B));
        ownerRoomRelPo.setEndTime(DateUtil.LAST_TIME);

        int flag = ownerRoomRelV1InnerServiceSMOImpl.saveOwnerRoomRel(ownerRoomRelPo);
        if (flag < 1) {
            throw new IllegalArgumentException("业主房屋关系失败");
        }


    }

    private void toSaveUnit(JSONObject room, ICmdDataFlowContext context, String communityId) {

        UnitDto unitDto = new UnitDto();
        unitDto.setCommunityId(communityId);
        unitDto.setUnitId(room.getString("unitId"));
        List<UnitDto> unitDtos = unitV1InnerServiceSMOImpl.queryUnits(unitDto);
        UnitPo unitPo = null;

        int flag = 0;
        if (!ListUtil.isNull(unitDtos)) {
            return;
        }
        unitPo = new UnitPo();
        unitPo.setFloorId(room.getString("floorId"));
        unitPo.setLayerCount("33");
        unitPo.setLift("1010");
        unitPo.setUnitId(room.getString("unitId"));
        unitPo.setUnitArea("1");
        unitPo.setUnitNum(room.getString("unitNum"));
        unitPo.setRemark("第三方系统同步");
        unitPo.setUserId(CmdContextUtils.getUserId(context));
        flag = unitV1InnerServiceSMOImpl.saveUnit(unitPo);

        if (flag < 1) {
            throw new IllegalArgumentException("导入单元失败");
        }
    }

    private void toSaveFloor(JSONObject room, ICmdDataFlowContext context, String communityId) {

        FloorDto floorDto = new FloorDto();
        floorDto.setFloorId(room.getString("floorId"));
        List<FloorDto> floorDtos = floorV1InnerServiceSMOImpl.queryFloors(floorDto);


        if (!ListUtil.isNull(floorDtos)) {
            return;
        }
        int flag = 0;
        FloorPo floorPo = new FloorPo();
        floorPo.setbId("-1");
        floorPo.setCommunityId(communityId);
        floorPo.setFloorArea("1");
        floorPo.setFloorId(room.getString("floorId"));
        floorPo.setFloorNum(room.getString("floorNum"));
        floorPo.setName(room.getString("floorNum") + "栋");
        floorPo.setRemark("房产导入");
        floorPo.setUserId(CmdContextUtils.getUserId(context));
        floorPo.setSeq(1);
        flag = floorV1InnerServiceSMOImpl.saveFloor(floorPo);
        if (flag < 1) {
            throw new IllegalArgumentException("添加楼栋失败");
        }

    }

    /**
     * 处理业主信息
     * Assert.hasKeyAndValue(reqJson, "communityId", "未包含communityId");
     * Assert.hasKeyAndValue(reqJson, "memberId", "未包含memberId");
     * Assert.hasKeyAndValue(reqJson, "ownerId", "未包含ownerId");
     * Assert.hasKeyAndValue(reqJson, "name", "未包含name");
     * Assert.hasKeyAndValue(reqJson, "link", "未包含link");
     * Assert.hasKeyAndValue(reqJson, "ownerTypeCd", "未包含ownerTypeCd");
     *
     * @param reqJson
     * @param context
     */
    private void doOwner(JSONObject reqJson, ICmdDataFlowContext context) {

        OwnerDto ownerDto = new OwnerDto();
        ownerDto.setMemberId(reqJson.getString("memberId"));

        List<OwnerDto> ownerDtos = ownerV1InnerServiceSMOImpl.queryOwners(ownerDto);

        OwnerPo ownerPo = new OwnerPo();
        ownerPo.setUserId("-1");
        ownerPo.setAge("1");
        ownerPo.setCommunityId(reqJson.getString("communityId"));
        ownerPo.setIdCard(reqJson.getString("idCard"));
        ownerPo.setLink(reqJson.getString("link"));
        ownerPo.setSex("1");
        ownerPo.setMemberId(reqJson.getString("memberId"));
        ownerPo.setName(reqJson.getString("name"));
        ownerPo.setOwnerId(reqJson.getString("ownerId")); //业主 所以和成员ID需要一样
        ownerPo.setOwnerTypeCd(reqJson.getString("ownerTypeCd"));
        ownerPo.setRemark("第三方导入系统同步");
        ownerPo.setState("2000");
        ownerPo.setAddress("无");
        ownerPo.setOwnerFlag(OwnerDto.OWNER_FLAG_TRUE);

        if (ListUtil.isNull(ownerDtos)) {
            ownerV1InnerServiceSMOImpl.saveOwner(ownerPo);
        } else {
            ownerV1InnerServiceSMOImpl.updateOwner(ownerPo);
        }


        //todo 人脸图片
        photoSMOImpl.savePhoto(reqJson.getString("ownerPhoto"),
                reqJson.getString("memberId"),
                reqJson.getString("communityId"),
                "10000");
    }
}
