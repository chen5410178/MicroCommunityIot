package com.java110.job.importData.adapt;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.dto.room.RoomDto;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.importData.ImportMeterMachineDto;
import com.java110.dto.log.AssetImportLogDetailDto;
import com.java110.dto.meter.MeterTypeDto;
import com.java110.intf.community.IRoomV1InnerServiceSMO;
import com.java110.intf.meter.IMeterMachineSpecV1InnerServiceSMO;
import com.java110.intf.meter.IMeterMachineV1InnerServiceSMO;
import com.java110.intf.meter.IMeterTypeV1InnerServiceSMO;
import com.java110.job.importData.DefaultImportData;
import com.java110.job.importData.IImportDataAdapt;
import com.java110.po.meter.MeterMachinePo;
import com.java110.po.meter.MeterMachineSpecPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("importMeterMachineQueueData")
public class ImportMeterMachineQueueDataAdapt extends DefaultImportData implements IImportDataAdapt {

    public static final String CODE_PREFIX_ID = "10";

    @Autowired
    private IMeterMachineV1InnerServiceSMO meterMachineV1InnerServiceSMOImpl;


    @Autowired
    private IMeterMachineSpecV1InnerServiceSMO meterMachineSpecV1InnerServiceSMOImpl;

    @Autowired
    private IRoomV1InnerServiceSMO roomV1InnerServiceSMOImpl;

    @Autowired
    private IMeterTypeV1InnerServiceSMO meterTypeV1InnerServiceSMOImpl;

    @Override
    public void importData(List<AssetImportLogDetailDto> assetImportLogDetailDtos) {
        for (AssetImportLogDetailDto assetImportLogDetailDto : assetImportLogDetailDtos) {

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
        ImportMeterMachineDto importMeterMachineDto = BeanConvertUtil.covertBean(data, ImportMeterMachineDto.class);


        String[] roomItems = importMeterMachineDto.getRoomName().split("-", 3);
        //查询房屋
        RoomDto roomDto = new RoomDto();
        roomDto.setFloorNum(roomItems[0]);
        roomDto.setUnitNum(roomItems[1]);
        roomDto.setRoomNum(roomItems[2]);
        roomDto.setCommunityId(importMeterMachineDto.getCommunityId());
        List<RoomDto> roomDtos = roomV1InnerServiceSMOImpl.queryRooms(roomDto);
        Assert.listOnlyOne(roomDtos, "房屋不存在");

        MeterTypeDto meterTypeDto = new MeterTypeDto();
        meterTypeDto.setCommunityId(importMeterMachineDto.getCommunityId());
        meterTypeDto.setTypeName(importMeterMachineDto.getMeterType());
        List<MeterTypeDto> meterTypeDtos = meterTypeV1InnerServiceSMOImpl.queryMeterTypes(meterTypeDto);

        Assert.listOnlyOne(meterTypeDtos, "电表类型不存在");


        MeterMachinePo meterMachinePo = BeanConvertUtil.covertBean(importMeterMachineDto, MeterMachinePo.class);
        meterMachinePo.setMachineId(GenerateCodeFactory.getGeneratorId(CODE_PREFIX_ID));
        meterMachinePo.setCurDegrees("0");
        meterMachinePo.setCurReadingTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        meterMachinePo.setPrestoreDegrees("0");
        meterMachinePo.setHeartbeatTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        meterMachinePo.setReadDay(1);
        meterMachinePo.setReadHours(1);
        meterMachinePo.setRoomId(roomDtos.get(0).getRoomId());
        meterMachinePo.setMeterType(meterTypeDtos.get(0).getTypeId());

        int flag = meterMachineV1InnerServiceSMOImpl.saveMeterMachine(meterMachinePo);

        if (flag < 1) {
            throw new CmdException("保存数据失败");
        }
        if (StringUtil.isEmpty(importMeterMachineDto.getValue1())) {
            return;
        }

        String specId = "120101";

        //拓强电表-单相单费率电表
        if("1".equals(meterMachinePo.getImplBean())){
            specId = "120101";
        }else if("2".equals(meterMachinePo.getImplBean())){ //拓强电表-单相远程单费率预付费表
            specId = "120102";
        }else if("3".equals(meterMachinePo.getImplBean())){ // 拓强水表-Lora无预付费水表
            specId = "120103";
        }else if("4".equals(meterMachinePo.getImplBean())){ //拓强水表-Lora普通预付费水表
            specId = "120104";
        }

        MeterMachineSpecPo meterMachineSpecPo = null;
        meterMachineSpecPo = new MeterMachineSpecPo();
        meterMachineSpecPo.setMachineId(meterMachinePo.getMachineId());
        meterMachineSpecPo.setSpecId(specId);
        meterMachineSpecPo.setSpecName("采集器ID");
        meterMachineSpecPo.setSpecValue(importMeterMachineDto.getValue1());
        meterMachineSpecPo.setMmsId(GenerateCodeFactory.getGeneratorId(CODE_PREFIX_ID));
        meterMachineSpecPo.setCommunityId(meterMachinePo.getCommunityId());
        flag = meterMachineSpecV1InnerServiceSMOImpl.saveMeterMachineSpec(meterMachineSpecPo);

        if (flag < 1) {
            throw new CmdException("保存数据失败");
        }


    }
}
