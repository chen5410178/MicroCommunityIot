package com.java110.job.importData.adapt;

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
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.importData.ImportOwnerRoomDto;
import com.java110.dto.log.AssetImportLogDetailDto;
import com.java110.dto.parking.ParkingAreaDto;
import com.java110.dto.parking.ParkingSpaceDto;
import com.java110.intf.car.IOwnerCarV1InnerServiceSMO;
import com.java110.intf.car.IParkingAreaV1InnerServiceSMO;
import com.java110.intf.car.IParkingSpaceV1InnerServiceSMO;
import com.java110.intf.community.IFloorV1InnerServiceSMO;
import com.java110.intf.community.IRoomV1InnerServiceSMO;
import com.java110.intf.community.IUnitV1InnerServiceSMO;
import com.java110.intf.user.IOwnerRoomRelV1InnerServiceSMO;
import com.java110.intf.user.IOwnerV1InnerServiceSMO;
import com.java110.job.importData.DefaultImportData;
import com.java110.job.importData.IImportDataAdapt;
import com.java110.po.ownerCar.OwnerCarPo;
import com.java110.po.parking.ParkingAreaPo;
import com.java110.po.parking.ParkingSpacePo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 车辆导入 适配器
 * 前端请求 时 必须传入
 * param.append('importAdapt', "importRoomOwner");
 */
@Service("importOwnerCarQueueData")
public class ImportOwnerCarQueueDataAdapt extends DefaultImportData implements IImportDataAdapt {


    @Autowired
    private IOwnerCarV1InnerServiceSMO ownerCarV1InnerServiceSMOImpl;


    @Autowired
    private IParkingSpaceV1InnerServiceSMO parkingSpaceV1InnerServiceSMOImpl;

    @Autowired
    private IParkingAreaV1InnerServiceSMO parkingAreaV1InnerServiceSMOImpl;

    @Override
    public void importData(List<AssetImportLogDetailDto> assetImportLogDetailDtos) {
        importDatas(assetImportLogDetailDtos);
    }

    private void importDatas(List<AssetImportLogDetailDto> infos) {
        String state = "";
        String msg = "";
        for (AssetImportLogDetailDto assetImportLogDetailDto : infos) {

            try {
                doImportData(assetImportLogDetailDto);
                updateImportLogDetailState(assetImportLogDetailDto.getDetailId());
            } catch (Exception e) {
                e.printStackTrace();
                updateImportLogDetailState(assetImportLogDetailDto.getDetailId(), e);
            }
        }

    }

    /**
     * 导入数据
     *
     * @param assetImportLogDetailDto
     */
    private void doImportData(AssetImportLogDetailDto assetImportLogDetailDto) {
        JSONObject data = JSONObject.parseObject(assetImportLogDetailDto.getContent());
        OwnerCarDto ownerCarDto = BeanConvertUtil.covertBean(data, OwnerCarDto.class);

        OwnerCarPo ownerCarPo = BeanConvertUtil.covertBean(ownerCarDto, OwnerCarPo.class);
        ownerCarPo.setStartTime(ownerCarDto.getStartTimeStr());
        ownerCarPo.setEndTime(ownerCarDto.getEndTimeStr());
        //获取房屋名称
        ownerCarPo.setCarTypeCd("1001"); //主车辆
        ParkingAreaDto parkingAreaDto = new ParkingAreaDto();
        parkingAreaDto.setNum(ownerCarDto.getAreaNum());
        parkingAreaDto.setTypeCd(ownerCarDto.getTypeCd());
        //查询停车场
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaV1InnerServiceSMOImpl.queryParkingAreas(parkingAreaDto);
        //Assert.listOnlyOne(parkingAreaDtos, "查询停车场错误！");
        String paId = "";
        if (parkingAreaDtos == null || parkingAreaDtos.size() < 1) {
            paId = GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_paId);
            ParkingAreaPo parkingAreaPo = new ParkingAreaPo();
            parkingAreaPo.setCommunityId(ownerCarDto.getCommunityId());
            parkingAreaPo.setNum(ownerCarDto.getAreaNum());
            parkingAreaPo.setPaId(paId);
            parkingAreaPo.setTypeCd(ownerCarDto.getTypeCd());
            parkingAreaPo.setRemark("导入数据");
            parkingAreaV1InnerServiceSMOImpl.saveParkingArea(parkingAreaPo);
        } else {
            paId = parkingAreaDtos.get(0).getPaId();
        }
        ParkingSpaceDto parkingSpaceDto = new ParkingSpaceDto();
        parkingSpaceDto.setNum(ownerCarDto.getNum());
        parkingSpaceDto.setPaId(paId);
        //查询停车位
        List<ParkingSpaceDto> parkingSpaceDtos = parkingSpaceV1InnerServiceSMOImpl.queryParkingSpaces(parkingSpaceDto);
        String state = "";
        String psId = "";
        if (parkingSpaceDtos == null || parkingSpaceDtos.size() < 1) {
            psId = GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_psId);
            ParkingSpacePo parkingSpacePo = new ParkingSpacePo();
            parkingSpacePo.setCommunityId(ownerCarDto.getCommunityId());
            parkingSpacePo.setNum(ownerCarDto.getNum());
            parkingSpacePo.setPaId(paId);
            parkingSpacePo.setParkingType(ParkingSpaceDto.TYPE_CD_COMMON);
            parkingSpacePo.setState(ParkingSpaceDto.STATE_FREE);
            parkingSpacePo.setPsId(psId);
            parkingSpacePo.setRemark("导入数据");
            parkingSpaceV1InnerServiceSMOImpl.saveParkingSpace(parkingSpacePo);
            state = ParkingSpaceDto.STATE_FREE;
        } else {
            psId = parkingSpaceDtos.get(0).getPsId();
            //获取停车位状态(出售 S，出租 H ，空闲 F)
            state = parkingSpaceDtos.get(0).getState();
        }

        if (!StringUtil.isEmpty(state) && !state.equals("F")) {
            throw new IllegalArgumentException(ownerCarDto.getAreaNum() + "停车场-" + ownerCarDto.getNum() + "停车位不是空闲状态！");
        }
        ownerCarPo.setPsId(psId);
        ownerCarPo.setOwnerId(ownerCarDto.getOwnerId());
        ownerCarPo.setCommunityId(ownerCarDto.getCommunityId());
        ownerCarPo.setCarId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_carId));
        ownerCarPo.setMemberId(ownerCarPo.getCarId());
        ownerCarPo.setState("1001"); //1001 正常状态，2002 车位释放欠费状态，3003 车位释放
        ownerCarPo.setLeaseType(ownerCarDto.getSpaceSate());
        ownerCarV1InnerServiceSMOImpl.saveOwnerCar(ownerCarPo);
        ParkingSpacePo parkingSpacePo = new ParkingSpacePo();
        parkingSpacePo.setPsId(psId); //车位id
        parkingSpacePo.setState(ownerCarDto.getSpaceSate());
        parkingSpaceV1InnerServiceSMOImpl.updateParkingSpace(parkingSpacePo);
    }


}


