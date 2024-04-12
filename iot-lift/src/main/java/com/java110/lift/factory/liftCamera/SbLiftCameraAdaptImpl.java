package com.java110.lift.factory.liftCamera;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.dto.file.FileDto;
import com.java110.core.cache.MappingCache;
import com.java110.core.constant.MappingConstant;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.lift.LiftCameraDto;
import com.java110.intf.lift.ILiftCameraV1InnerServiceSMO;
import com.java110.intf.lift.ILiftMachineV1InnerServiceSMO;
import com.java110.intf.lift.ILiftWarnV1InnerServiceSMO;
import com.java110.intf.system.IFileInnerServiceSMO;
import com.java110.lift.factory.ILiftCameraAdapt;
import com.java110.po.liftCamera.LiftCameraPo;
import com.java110.po.liftWarn.LiftWarnPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 松饼 电梯相机 上报信息
 */
@Service("sbLiftCameraAdaptImpl")
public class SbLiftCameraAdaptImpl implements ILiftCameraAdapt {

    @Autowired
    private ILiftCameraV1InnerServiceSMO liftCameraV1InnerServiceSMOImpl;

    @Autowired
    private ILiftMachineV1InnerServiceSMO liftMachineV1InnerServiceSMOImpl;

    @Autowired
    private ILiftWarnV1InnerServiceSMO liftWarnV1InnerServiceSMOImpl;

    @Autowired
    private IFileInnerServiceSMO fileInnerServiceSMOImpl;


    public static final String CMD_LIFT_DETECT_EVENT = "lift_detect_event";// 电梯报警

    /**
     * @param liftCameraDto
     * @param reqBody
     * @return
     */
    @Override
    public String heartbeat(LiftCameraDto liftCameraDto, String reqBody) {

        LiftCameraPo liftCameraPo = new LiftCameraPo();
        liftCameraPo.setLcId(liftCameraDto.getLcId());
        liftCameraPo.setHeartbeatTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        liftCameraV1InnerServiceSMOImpl.updateLiftCamera(liftCameraPo);

        JSONObject paramOut = new JSONObject();
        paramOut.put("code", 0);
        paramOut.put("msg", "成功");
        return paramOut.toJSONString();
    }

    @Override
    public String liftWarnResult(LiftCameraDto liftCameraDto, String reqBody) {
        JSONObject reqJson = JSONObject.parseObject(reqBody);
        String cmd = reqJson.getString("cmd");


        switch (cmd) {
            case CMD_LIFT_DETECT_EVENT:
                leftDetectEvent(liftCameraDto, reqJson);
                break;
        }


        JSONObject paramOut = new JSONObject();
        paramOut.put("code", 0);
        paramOut.put("msg", "成功");
        return paramOut.toJSONString();
    }

    /**
     * 电梯报警
     *
     * @param liftCameraDto
     * @param reqJson
     */
    private void leftDetectEvent(LiftCameraDto liftCameraDto, JSONObject reqJson) {

        JSONObject body = reqJson.getJSONObject("body");

        JSONObject values = body.getJSONObject("values");

        JSONObject errors = reqJson.getJSONArray("errors").getJSONObject(0);

        String content = body.getString("content");
        String facePath = "";
        if (!StringUtil.isEmpty(content) && content.length() > 512) { //说明是图片
            FileDto fileDto = new FileDto();
            fileDto.setFileId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_file_id));
            fileDto.setFileName(fileDto.getFileId());
            fileDto.setContext(content);
            fileDto.setSuffix("jpeg");
            fileDto.setCommunityId(liftCameraDto.getCommunityId());
            String fileName = fileInnerServiceSMOImpl.saveFile(fileDto);
            String imgUrl = MappingCache.getValue(MappingConstant.FILE_DOMAIN, "IMG_PATH");

            facePath = imgUrl + fileName;
        }

        LiftWarnPo liftWarnPo = new LiftWarnPo();
        liftWarnPo.setLcId(liftCameraDto.getLcId());
        liftWarnPo.setCurrentPeopleNum(values.getString("current_people_num"));
        liftWarnPo.setLeavePeopleNum(values.getString("leave_people_num"));
        if("0".equals(values.getString("have_bicycle"))) {
            liftWarnPo.setHaveBicycle("N");
        }else{
            liftWarnPo.setHaveBicycle("Y");

        }
        liftWarnPo.setLiftDoorState(values.getString("lift_door_state"));
        liftWarnPo.setWarnType(errors.getString("code"));
        liftWarnPo.setLiftOperateState(values.getString("lift_operate_state"));
        liftWarnPo.setCurrentSpeed(values.getString("current_speed"));
        liftWarnPo.setAlarmInPolarity("1");
        liftWarnPo.setLwId(GenerateCodeFactory.getGeneratorId("11"));
        liftWarnPo.setLiftName(liftCameraDto.getLiftName());
        liftWarnPo.setWarnTime(errors.getString("time"));
        liftWarnPo.setEnterPeopleNum(values.getString("enter_people_num"));
        liftWarnPo.setWarnImg(facePath);
        liftWarnPo.setLiftMachineId(liftCameraDto.getLiftMachineId());
        liftWarnPo.setCameraCode(liftCameraDto.getCameraCode());
        liftWarnPo.setCommunityId(liftCameraDto.getCommunityId());
        liftWarnPo.setCameraName(liftCameraDto.getCameraName());

        liftWarnV1InnerServiceSMOImpl.saveLiftWarn(liftWarnPo);
    }
}
