package com.java110.car.manufactor.songbing;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.bean.dto.file.FileDto;
import com.java110.car.manufactor.IParkingSpaceMachineManufactor;
import com.java110.core.cache.MappingCache;
import com.java110.core.constant.MappingConstant;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.parkingSpaceMachine.ParkingSpaceMachineDto;
import com.java110.dto.parkingSpaceMachineRel.ParkingSpaceMachineRelDto;
import com.java110.intf.car.IParkingSpaceMachineRelV1InnerServiceSMO;
import com.java110.intf.car.IParkingSpaceMachineV1InnerServiceSMO;
import com.java110.intf.system.IFileInnerServiceSMO;
import com.java110.po.parkingSpaceMachine.ParkingSpaceMachinePo;
import com.java110.po.parkingSpaceMachineRel.ParkingSpaceMachineRelPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 设备请到这里购买
 * http://shop.songbingcloud.com/
 */
@Service("songBingParkingSpaceMachineAdapt")
public class SongBingParkingSpaceMachineAdapt implements IParkingSpaceMachineManufactor {

    @Autowired
    private IParkingSpaceMachineV1InnerServiceSMO parkingSpaceMachineV1InnerServiceSMOImpl;

    @Autowired
    private IParkingSpaceMachineRelV1InnerServiceSMO parkingSpaceMachineRelV1InnerServiceSMOImpl;

    @Autowired
    private IFileInnerServiceSMO fileInnerServiceSMOImpl;

    /**
     * 心跳
     * 心跳地址：http://iot.homecommunity.cn/iot/hal/parkingSpaceMachine/heartbeat/123
     * 123 为设备编号
     *
     * @param parkingSpaceMachineDto
     * @param reqParam
     * {
     * 	"type": "http_reg",
     * 	"body": {
     * 		"dev_name": "F4M",
     * 		"ipaddr": "192.168.1.100",
     * 		"serialno": "ebe9001e-501c119c",
     * 		"all_parking_info": [{
     * 			"error_code": 0,
     * 			"frame_name": "车位 1",
     * 			"lock_battery": 0,
     * 			"lock_status": 0,
     * 			"parking_status": 1
     * 		        }, {
     * 			"error_code": 0,
     * 			"frame_name": "车位 2",
     * 			"lock_battery": 64,
     * 			"lock_status": 3,
     * 			"parking_status": 0
     *        }, {
     * 			"error_code": 0,
     * 			"frame_name": "车位 3",
     * 			"lock_battery": 0,
     * 			"lock_status": 0,
     * 			"parking_status": 0
     *        }]    * 	}
     * }
     * @return
     */
    @Override
    public String heartbeat(ParkingSpaceMachineDto parkingSpaceMachineDto, String reqParam) {


        //todo 地磁心跳
        ParkingSpaceMachinePo parkingSpaceMachinePo = new ParkingSpaceMachinePo();
        parkingSpaceMachinePo.setMachineId(parkingSpaceMachineDto.getMachineId());
        parkingSpaceMachinePo.setHeartbeatTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        parkingSpaceMachineV1InnerServiceSMOImpl.updateParkingSpaceMachine(parkingSpaceMachinePo);

        JSONObject reqJson = JSONObject.parseObject(reqParam);
        //todo 刷新车位状态
        JSONArray all_parking_info = reqJson.getJSONObject("body").getJSONArray("all_parking_info");
        if(ListUtil.isNull(all_parking_info)){
            return getHeartbeatResult(parkingSpaceMachineDto);
        }

        /**
         * {
         * 			"error_code": 0,
         * 			"frame_name": "车位 1",
         * 			"lock_battery": 0,
         * 			"lock_status": 0,
         * 			"parking_status": 1
         * 		        }
         */
        JSONObject parkingSpace = null;
        ParkingSpaceMachineRelDto parkingSpaceMachineRelDto = null;
        ParkingSpaceMachineRelPo parkingSpaceMachineRelPo = null;
        for(int pIndex = 0; pIndex < all_parking_info.size();pIndex++){
            parkingSpace = all_parking_info.getJSONObject(pIndex);

            parkingSpaceMachineRelDto = new ParkingSpaceMachineRelDto();
            parkingSpaceMachineRelDto.setPsName(parkingSpace.getString("frame_name"));
            parkingSpaceMachineRelDto.setMachineId(parkingSpaceMachineDto.getMachineId());
            List<ParkingSpaceMachineRelDto> parkingSpaceMachineRelDtos = parkingSpaceMachineRelV1InnerServiceSMOImpl.queryParkingSpaceMachineRels(parkingSpaceMachineRelDto);

            if(ListUtil.isNull(parkingSpaceMachineRelDtos)){
                continue;
            }
            parkingSpaceMachineRelPo = new ParkingSpaceMachineRelPo();
            parkingSpaceMachineRelPo.setLockBattery(parkingSpace.getString("lock_battery"));
            parkingSpaceMachineRelPo.setRelId(parkingSpaceMachineRelDtos.get(0).getRelId());
            parkingSpaceMachineRelPo.setPsState(
                    "0".equals(parkingSpace.getString("parking_status"))
                    ?ParkingSpaceMachineRelDto.PS_STATE_FREE:ParkingSpaceMachineRelDto.PS_STATE_OCCUPY
            );

            parkingSpaceMachineRelPo.setUpdateTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));

            /**
             * enum VZ_E_LOCK_STATE {
             * GRDLOCK_STATUS_ERROR = -1,
             * GRDLOCK_STATUS_UNKNOW = 0 // 未知状态,
             * GRDLOCK_STATUS_DOWN = 5, // 下降
             * GRDLOCK_STATUS_UP = 6, // 上升
             * GRDLOCK_STATUS_DOWN_FOREVER = 7, // 常降锁
             * GRDLOCK_STATUS_UP_FOREVER
             * } ;
             */
            if("5".equals(parkingSpace.getString("lock_status"))){
                parkingSpaceMachineRelPo.setLockState(ParkingSpaceMachineRelDto.LOCK_STATE_OFF);
            }else if("6".equals(parkingSpace.getString("lock_status"))){
                parkingSpaceMachineRelPo.setLockState(ParkingSpaceMachineRelDto.LOCK_STATE_ON);
            }

            parkingSpaceMachineRelV1InnerServiceSMOImpl.updateParkingSpaceMachineRel(parkingSpaceMachineRelPo);


        }

        return getHeartbeatResult(parkingSpaceMachineDto);
    }

    /**
     * 心跳
     * 心跳地址：http://iot.homecommunity.cn/iot/hal/parkingSpaceMachine/plateResult/123
     * 123 为设备编号
     *
     * @param parkingSpaceMachineDto
     * @param reqParam
     * {
     * 	"BDTime": {
     * 		"BDTDay": 29,
     * 		"BDTHour": 19,
     * 		"BDTMin": 17,
     * 		"BDTMon": 8,
     * 		"BDTSec": 6,
     * 		"BDTYear": 2022
     * 	    },
     * 	"Base64ImageLength": 193208,
     * 	"Base64SmallImageLength": 0,
     * 	"Confidence": 0,
     * 	"FrameIndex": 1,
     * 	"FrameName": "车位 1",
     * 	"ImageData": "",
     * 	"ImageDataLength": 144905,
     * 	"License": "",
     * 	"LicenseType": 0,
     * 	"MIX": [{
     * 		'License': '粤 ZH696 澳',
     * 		'LicenseColor': 4,
     * 		'LicenseType': 14
     *    }, {
     * 		'License': 'ZM3810',
     * 		'LicenseColor': 3,
     * 		'LicenseType': 26
     *    }, {
     * 		'License': 'MR8328',
     * 		'LicenseColor': 4,
     * 		'LicenseType': 26
     *    }]
     * 	"vehicle_type": 0,
     * 	"OccupyLine": 0,
     * 	"ParkingStatus": 0,
     * 	"SmallImageData": "",
     * 	"SmallImageLength": 0,
     * 	"TriggerType": 2,
     * 	"Type": "PKResult",
     * 	"frame_status": 0,
     * 	"ibeacon": "",
     * 	"led_color": 0,
     * 	"lock": 0,
     * 	"s_addr": 342403264,
     * 	"sn": "ebe9001e-501c119c",
     * 	"vacant": 1,
     * 	"wl": 0,
     * 	"new_energy_park_space ": 0
     * }
     * @return
     */
    @Override
    public String plateResult(ParkingSpaceMachineDto parkingSpaceMachineDto, String reqParam) {

        JSONObject parkingSpace = JSONObject.parseObject(reqParam);



        ParkingSpaceMachineRelDto parkingSpaceMachineRelDto = new ParkingSpaceMachineRelDto();
        parkingSpaceMachineRelDto.setPsName(parkingSpace.getString("frame_name"));
        parkingSpaceMachineRelDto.setMachineId(parkingSpaceMachineDto.getMachineId());
        List<ParkingSpaceMachineRelDto> parkingSpaceMachineRelDtos = parkingSpaceMachineRelV1InnerServiceSMOImpl.queryParkingSpaceMachineRels(parkingSpaceMachineRelDto);

        if(ListUtil.isNull(parkingSpaceMachineRelDtos)){
            return getPlateReulst(parkingSpaceMachineDto);
        }

        String imageData = parkingSpace.getString("ImageData");

        if (!StringUtil.isEmpty(imageData)) { //说明是图片
            FileDto fileDto = new FileDto();
            fileDto.setFileId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_file_id));
            fileDto.setFileName(fileDto.getFileId());
            fileDto.setContext(imageData);
            fileDto.setSuffix("jpeg");
            fileDto.setCommunityId(parkingSpaceMachineDto.getCommunityId());
            String fileName = fileInnerServiceSMOImpl.saveFile(fileDto);
            String imgUrl = MappingCache.getValue(MappingConstant.FILE_DOMAIN, "IMG_PATH");

            imageData = imgUrl + fileName;
        }

        ParkingSpaceMachineRelPo parkingSpaceMachineRelPo = new ParkingSpaceMachineRelPo();
        parkingSpaceMachineRelPo.setImagePhoto(imageData);
        parkingSpaceMachineRelPo.setCarNum(parkingSpace.getString("License"));
        parkingSpaceMachineRelPo.setRelId(parkingSpaceMachineRelDtos.get(0).getRelId());
        parkingSpaceMachineRelPo.setPsState(
                "0".equals(parkingSpace.getString("ParkingStatus"))
                        ?ParkingSpaceMachineRelDto.PS_STATE_FREE:ParkingSpaceMachineRelDto.PS_STATE_OCCUPY
        );

        parkingSpaceMachineRelPo.setUpdateTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));

        /**
         * enum VZ_E_LOCK_STATE {
         * GRDLOCK_STATUS_ERROR = -1,
         * GRDLOCK_STATUS_UNKNOW = 0 // 未知状态,
         * GRDLOCK_STATUS_DOWN = 5, // 下降
         * GRDLOCK_STATUS_UP = 6, // 上升
         * GRDLOCK_STATUS_DOWN_FOREVER = 7, // 常降锁
         * GRDLOCK_STATUS_UP_FOREVER
         * } ;
         */
        if("5".equals(parkingSpace.getString("lock"))){
            parkingSpaceMachineRelPo.setLockState(ParkingSpaceMachineRelDto.LOCK_STATE_OFF);
        }else if("6".equals(parkingSpace.getString("lock"))){
            parkingSpaceMachineRelPo.setLockState(ParkingSpaceMachineRelDto.LOCK_STATE_ON);
        }

        parkingSpaceMachineRelV1InnerServiceSMOImpl.updateParkingSpaceMachineRel(parkingSpaceMachineRelPo);
        return getPlateReulst(parkingSpaceMachineDto);
    }

    @Override
    public String eventPush(ParkingSpaceMachineDto parkingSpaceMachineDto, String reqParam) {


        return getPlateReulst(parkingSpaceMachineDto);
    }


    @Override
    public String getEmptyFrame(ParkingSpaceMachineDto parkingSpaceMachineDto, String reqParam) {


        return getPlateReulst(parkingSpaceMachineDto);
    }

    private String getPlateReulst(ParkingSpaceMachineDto parkingSpaceMachineDto) {

        /**
         * { "type" : "PKResult", "state" : 200, "error_msg" : "All done"}
         * }
         */
        JSONObject result = new JSONObject();
        result.put("msg","All done");
        result.put("state",200);
        result.put("type","PKResult");
        return result.toJSONString();
    }

    /**
     * 心跳恢复
     * @param parkingSpaceMachineDto
     * @return
     *
     * { "msg": "ok", "state": 200, "type": "http_reg"}
     */
    public String getHeartbeatResult(ParkingSpaceMachineDto parkingSpaceMachineDto){

        JSONObject result = new JSONObject();
        result.put("msg","ok");
        result.put("state",200);
        result.put("type","http_reg");

        //todo 检查队里中是否有执行回复的动作


        return result.toJSONString();
    }
}
