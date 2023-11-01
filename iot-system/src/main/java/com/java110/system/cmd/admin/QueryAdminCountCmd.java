package com.java110.system.cmd.admin;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.car.OwnerCarDto;
import com.java110.bean.dto.owner.OwnerDto;
import com.java110.bean.dto.room.RoomDto;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.CmdContextUtils;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.dto.barrier.BarrierDto;
import com.java110.dto.chargeMachine.ChargeMachineDto;
import com.java110.dto.community.CommunityDto;
import com.java110.dto.lift.LiftMachineDto;
import com.java110.dto.lock.LockMachineDto;
import com.java110.dto.meter.MeterMachineDto;
import com.java110.dto.monitor.MonitorMachineDto;
import com.java110.dto.shop.ShopDto;
import com.java110.dto.store.StoreDto;
import com.java110.intf.accessControl.IAccessControlV1InnerServiceSMO;
import com.java110.intf.barrier.IBarrierV1InnerServiceSMO;
import com.java110.intf.car.IOwnerCarV1InnerServiceSMO;
import com.java110.intf.charge.IChargeMachineV1InnerServiceSMO;
import com.java110.intf.community.ICommunityInnerServiceSMO;
import com.java110.intf.community.IRoomV1InnerServiceSMO;
import com.java110.intf.lift.ILiftMachineV1InnerServiceSMO;
import com.java110.intf.lock.ILockMachineFactorySpecV1InnerServiceSMO;
import com.java110.intf.lock.ILockMachineV1InnerServiceSMO;
import com.java110.intf.meter.IMeterMachineV1InnerServiceSMO;
import com.java110.intf.monitor.IMonitorAreaV1InnerServiceSMO;
import com.java110.intf.monitor.IMonitorMachineV1InnerServiceSMO;
import com.java110.intf.user.IOwnerV1InnerServiceSMO;
import com.java110.intf.user.IStoreV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Java110Cmd(serviceCode = "admin.queryAdminCount")
public class QueryAdminCountCmd extends Cmd {


    @Autowired
    private IStoreV1InnerServiceSMO storeInnerServiceSMOImpl;

    @Autowired
    private ICommunityInnerServiceSMO communityInnerServiceSMOImpl;


    @Autowired
    private IRoomV1InnerServiceSMO roomV1InnerServiceSMOImpl;

    @Autowired
    private IOwnerV1InnerServiceSMO ownerV1InnerServiceSMOImpl;

    @Autowired
    private IOwnerCarV1InnerServiceSMO ownerCarV1InnerServiceSMOImpl;

    @Autowired
    private IAccessControlV1InnerServiceSMO accessControlV1InnerServiceSMOImpl;

    @Autowired
    private IBarrierV1InnerServiceSMO barrierV1InnerServiceSMOImpl;

    @Autowired
    private IChargeMachineV1InnerServiceSMO chargeMachineV1InnerServiceSMOImpl;

    @Autowired
    private IMeterMachineV1InnerServiceSMO meterMachineV1InnerServiceSMOImpl;

    @Autowired
    private ILiftMachineV1InnerServiceSMO liftMachineV1InnerServiceSMOImpl;

    @Autowired
    private IMonitorMachineV1InnerServiceSMO monitorMachineV1InnerServiceSMOImpl;

    @Autowired
    private ILockMachineV1InnerServiceSMO lockMachineV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        String storeId = CmdContextUtils.getStoreId(context);

        StoreDto storeDto = new StoreDto();
        storeDto.setStoreId(storeId);
        storeDto.setStoreTypeCd(StoreDto.STORE_TYPE_ADMIN);
        int count = storeInnerServiceSMOImpl.queryStoresCount(storeDto);
        if (count < 1) {
            throw new CmdException("非法操作，请用系统管理员账户操作");
        }
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        List<Map> datas = new ArrayList<>();
        //todo 小区个数

        CommunityDto communityDto = new CommunityDto();
        int communityCount = communityInnerServiceSMOImpl.queryCommunitysCount(communityDto);
        setDatas(datas, "小区数", communityCount);

        //todo 物业个数
        StoreDto storeDto = new StoreDto();
        storeDto.setStoreTypeCd(StoreDto.STORE_TYPE_PROPERTY);
        int storeCount = storeInnerServiceSMOImpl.queryStoresCount(storeDto);
        setDatas(datas, "物业数", storeCount);


        //todo 房屋数
        RoomDto roomDto = new RoomDto();
        roomDto.setRoomType(RoomDto.ROOM_TYPE_ROOM);
        int roomCount = roomV1InnerServiceSMOImpl.queryRoomsCount(roomDto);
        setDatas(datas, "房屋数", roomCount);


        //todo 业主数
        OwnerDto ownerDto = new OwnerDto();
        int ownerCount = ownerV1InnerServiceSMOImpl.queryOwnersCount(ownerDto);
        setDatas(datas, "业主数", ownerCount);

        //todo 车辆数

        OwnerCarDto ownerCarDto = new OwnerCarDto();
        ownerCarDto.setLeaseTypes(new String[]{OwnerCarDto.LEASE_TYPE_INNER,
                OwnerCarDto.LEASE_TYPE_MONTH,
                OwnerCarDto.LEASE_TYPE_SALE,
                OwnerCarDto.LEASE_TYPE_NO_MONEY,
                OwnerCarDto.LEASE_TYPE_RESERVE
        });
        int carCount = ownerCarV1InnerServiceSMOImpl.queryOwnerCarsCount(ownerCarDto);
        setDatas(datas, "车辆数", carCount);


        //todo 门禁数量
        AccessControlDto accessControlDto = new AccessControlDto();
        int accessControlCount = accessControlV1InnerServiceSMOImpl.queryAccessControlsCount(accessControlDto);
        setDatas(datas, "门禁数", accessControlCount);

        //todo 道闸摄像头
        BarrierDto barrierDto = new BarrierDto();
        int barrierCount = barrierV1InnerServiceSMOImpl.queryBarriersCount(barrierDto);
        setDatas(datas, "道闸摄像头", barrierCount);


        //todo 充电桩数
        ChargeMachineDto chargeMachineDto = new ChargeMachineDto();
        int chargeCount = chargeMachineV1InnerServiceSMOImpl.queryChargeMachinesCount(chargeMachineDto);
        setDatas(datas, "充电桩数", chargeCount);

        //todo 智能表数
        MeterMachineDto meterMachineDto = new MeterMachineDto();
        int meterCount = meterMachineV1InnerServiceSMOImpl.queryMeterMachinesCount(meterMachineDto);
        setDatas(datas, "智能表数", meterCount);

        //todo 电梯
        LiftMachineDto liftMachineDto = new LiftMachineDto();
        int liftCount = liftMachineV1InnerServiceSMOImpl.queryLiftMachinesCount(liftMachineDto);
        setDatas(datas, "电梯数", liftCount);

        //todo 监控数量
        MonitorMachineDto monitorMachineDto = new MonitorMachineDto();
        int monitorCount = monitorMachineV1InnerServiceSMOImpl.queryMonitorMachinesCount(monitorMachineDto);
        setDatas(datas, "监控数", monitorCount);
        //todo 监控数量
        LockMachineDto lockMachineDto = new LockMachineDto();
        int lockCount = lockMachineV1InnerServiceSMOImpl.queryLockMachinesCount(lockMachineDto);
        setDatas(datas, "门锁数", lockCount);


        context.setResponseEntity(ResultVo.createResponseEntity(datas));

    }

    private void setDatas(List<Map> datas, String name, int value) {
        Map info = new HashMap();
        info.put("name", name);
        info.put("value", value);
        datas.add(info);
    }
}
