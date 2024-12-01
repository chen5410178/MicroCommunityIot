package com.java110.accessControl.manufactor.adapt.accessControl.mvideo;

import com.alibaba.fastjson.JSONObject;
import com.java110.accessControl.manufactor.AbstractAccessControlManufactorAdapt;
import com.java110.bean.dto.owner.OwnerDto;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.MVideoHttpUtil;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.dto.accessControlFace.AccessControlFaceDto;
import com.java110.dto.monitorManufactor.MonitorManufactorDto;
import com.java110.dto.user.UserDto;
import com.java110.intf.monitor.IMonitorManufactorV1InnerServiceSMO;
import com.java110.intf.user.IOwnerV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import com.java110.po.accessControlFace.AccessControlFacePo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 同步数据到MVideo 监控平台
 */
@Service("mvideoAssessControlProcessAdapt")
public class MvideoAssessControlProcessAdapt extends AbstractAccessControlManufactorAdapt {

    public static final String CMD_ADD_USER = "addPersonFaceService";// todo 添加用户；
    public static final String ACTION = "MVIDEO";

    public static final String CMD_UPDATE_USER = "updateUser";// todo 修改用户；
    public static final String CMD_DELETE_USER = "deleteUser";// todo 删除用户；

    public static final String MVIDEO_BEAN = "mvideoAdaptImpl";

    public static final String faceUrl = "/oapp/ext/common";

    @Autowired
    private IMonitorManufactorV1InnerServiceSMO monitorManufactorV1InnerServiceSMOImpl;

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Autowired
    private IOwnerV1InnerServiceSMO ownerV1InnerServiceSMOImpl;


    /**
     * 添加用户到MVideo监控平台
     *
     * @param accessControlDto    门禁信息
     * @param accessControlFacePo 人员信息
     * @return
     */
    @Override
    public boolean addUser(AccessControlDto accessControlDto, AccessControlFacePo accessControlFacePo) {

        String taskId = GenerateCodeFactory.getGeneratorId("11");
        JSONObject param = new JSONObject();
        param.put("mvApiCode", CMD_ADD_USER);
        param.put("action", ACTION);
        param.put("sn", accessControlDto.getMachineCode());

        String tel = "";
        if(AccessControlFaceDto.PERSON_TYPE_STAFF.equals(accessControlFacePo.getPersonType())){
            UserDto userDto = new UserDto();
            userDto.setUserId(accessControlFacePo.getPersonId());
            List<UserDto> userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);
            if(!ListUtil.isNull(userDtos)){
                tel = userDtos.get(0).getTel();
            }
        }else {
            OwnerDto ownerDto = new OwnerDto();
            ownerDto.setMemberId(accessControlFacePo.getPersonId());
            List<OwnerDto> ownerDtos = ownerV1InnerServiceSMOImpl.queryOwners(ownerDto);
            if(!ListUtil.isNull(ownerDtos)){
                tel = ownerDtos.get(0).getLink();
            }
        }

        param.put("taskId", taskId);
        param.put("personId", accessControlFacePo.getPersonId());
        param.put("personName", accessControlFacePo.getName());
        param.put("personLink", tel);
        param.put("pass", accessControlDto.getMachineMac());
        param.put("idcard", accessControlFacePo.getIdNumber());
        param.put("faceUrl", accessControlFacePo.getFacePath());
        param.put("idNumber", accessControlFacePo.getCardNumber());
        param.put("userType", accessControlFacePo.getPersonType());
        param.put("startTime", accessControlFacePo.getStartTime());
        param.put("endTime", accessControlFacePo.getEndTime());

        MonitorManufactorDto monitorManufactorDto = new MonitorManufactorDto();
        monitorManufactorDto.setCommunityId(accessControlDto.getCommunityId());
        monitorManufactorDto.setMmBean(MVIDEO_BEAN);
        List<MonitorManufactorDto> monitorManufactorDtos = monitorManufactorV1InnerServiceSMOImpl.queryMonitorManufactors(monitorManufactorDto);
        if (ListUtil.isNull(monitorManufactorDtos)) {
            throw new CmdException("未配置监控平台");
        }
        saveAddFaceLog(taskId, accessControlDto.getMachineId(), CMD_ADD_USER,
                accessControlDto.getCommunityId(), param.toJSONString(), accessControlFacePo.getPersonId(), accessControlFacePo.getName());
        JSONObject paramOut = MVideoHttpUtil.post(faceUrl,param,monitorManufactorDtos.get(0));

        int code = -1;
        if (!paramOut.containsKey("code")) {
            code = -1;
        } else {
            code = paramOut.getIntValue("code");
        }
        String msg = paramOut.getString("msg") + "说明：" + paramOut.get(code + "") + "," + paramOut.getString("desc");
        cmdResult(code, msg, CMD_ADD_USER, accessControlDto.getMachineCode(), accessControlFacePo.getPersonId());

        return true;
    }

    @Override
    public boolean updateUser(AccessControlDto accessControlDto, AccessControlFacePo accessControlFacePo) {
        deleteUser(accessControlDto, accessControlFacePo);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return addUser(accessControlDto, accessControlFacePo);
    }

    @Override
    public boolean deleteUser(AccessControlDto accessControlDto, AccessControlFacePo accessControlFacePo) {
        String taskId = GenerateCodeFactory.getGeneratorId("11");
        JSONObject param = new JSONObject();
        param.put("mvApiCode", CMD_DELETE_USER);
        param.put("action", ACTION);
        param.put("sn", accessControlDto.getMachineCode());

        param.put("taskId", taskId);
        param.put("userId", accessControlFacePo.getPersonId());

        MonitorManufactorDto monitorManufactorDto = new MonitorManufactorDto();
        monitorManufactorDto.setCommunityId(accessControlDto.getCommunityId());
        monitorManufactorDto.setMmBean(MVIDEO_BEAN);
        List<MonitorManufactorDto> monitorManufactorDtos = monitorManufactorV1InnerServiceSMOImpl.queryMonitorManufactors(monitorManufactorDto);
        if (ListUtil.isNull(monitorManufactorDtos)) {
            throw new CmdException("未配置监控平台");
        }
        saveAddFaceLog(taskId, accessControlDto.getMachineId(), CMD_ADD_USER,
                accessControlDto.getCommunityId(), param.toJSONString(), accessControlFacePo.getPersonId(), accessControlFacePo.getName());
        JSONObject paramOut = MVideoHttpUtil.post(faceUrl,param,monitorManufactorDtos.get(0));

        int code = -1;
        if (!paramOut.containsKey("code")) {
            code = -1;
        } else {
            code = paramOut.getIntValue("code");
        }
        String msg = paramOut.getString("msg") + "说明：" + paramOut.get(code + "") + "," + paramOut.getString("desc");
        cmdResult(code, msg, CMD_ADD_USER, accessControlDto.getMachineCode(), accessControlFacePo.getPersonId());

        return true;
    }

    @Override
    public boolean openDoor(AccessControlDto accessControlDto) {
        throw new CmdException("不支持远程开门");
    }

    @Override
    public boolean restartMachine(AccessControlDto accessControlDto) {
        throw new CmdException("不支持重启");

    }

    @Override
    public String accessControlResult(String topic, String param) {
        return null;
    }
}
