package com.java110.lock.factory.hc;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.factory.MqttFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.lock.LockInoutDto;
import com.java110.dto.lock.LockLogDto;
import com.java110.dto.lock.LockMachineDto;
import com.java110.dto.lock.LockMachineParamDto;
import com.java110.intf.lock.ILockInoutV1InnerServiceSMO;
import com.java110.intf.lock.ILockLogV1InnerServiceSMO;
import com.java110.intf.lock.ILockMachineV1InnerServiceSMO;
import com.java110.lock.factory.ILockFactoryAdapt;
import com.java110.po.lock.LockInoutPo;
import com.java110.po.lock.LockLogPo;
import com.java110.po.lock.LockMachinePo;
import com.java110.po.lock.LockPersonPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * HC 官方门锁协议类
 */
@Component("hcLockFactoryAdaptImpl")
public class HcLockFactoryAdaptImpl implements ILockFactoryAdapt {

    private static Logger logger = LoggerFactory.getLogger(HcLockFactoryAdaptImpl.class);


    // todo 请求topic
    public static final String TOPIC_REQ = "hc/lock/request/{sn}";

    //todo 返回 topic
    public static final String TOPIC_RES = "hc/lock/response";

    public static final String CMD_HEARTBEAT = "heartbeat"; //todo 心跳

    public static final String CMD_OPEN_LOCK = "openLock"; //todo 远程开锁
    public static final String CMD_ADD_PWD = "addPwd"; //todo 添加密码
    public static final String CMD_UPDATE_PWD = "updatePwd"; //todo 修改密码
    public static final String CMD_DELETE_PWD = "deletePwd"; // 删除密码


    public static final String SN = "{sn}";
    public static final int KEYBOARD_PWD_TYPE_TERM = 3;//默认

    @Autowired
    private ILockMachineV1InnerServiceSMO lockMachineV1InnerServiceSMOImpl;

    @Autowired
    private ILockLogV1InnerServiceSMO lockLogV1InnerServiceSMOImpl;

    @Autowired
    private ILockInoutV1InnerServiceSMO lockInoutV1InnerServiceSMOImpl;

    @Override
    public void unlock(LockMachinePo lockMachinePo, List<LockMachineParamDto> lockMachineParamDtos) {

        String taskId = GenerateCodeFactory.getGeneratorId("11");
        JSONObject param = new JSONObject();
        param.put("cmd", CMD_OPEN_LOCK);
        param.put("taskId", taskId);
        param.put("machineCode", lockMachinePo.getMachineCode());
        param.put("openTime", DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));

        MqttFactory.publish(TOPIC_REQ.replace(SN, lockMachinePo.getMachineCode()), param.toJSONString());


        LockLogPo lockLogPo = new LockLogPo();
        lockLogPo.setLogId(taskId);
        lockLogPo.setMachineId(lockMachineParamDtos.get(0).getMachineId());
        lockLogPo.setCommunityId(lockMachineParamDtos.get(0).getCommunityId());
        lockLogPo.setLogAction(LockLogDto.LOG_ACTION_UNLOCK);
        lockLogPo.setUserId(lockMachinePo.getOprUserId());
        lockLogPo.setUserName(lockMachinePo.getOprUserName());
        lockLogPo.setReqParam(param.toJSONString());
        lockLogPo.setState(LockLogDto.REQUEST_SUCCESS);
        lockLogV1InnerServiceSMOImpl.saveLockLog(lockLogPo);

        LockInoutPo lockInoutPo = new LockInoutPo();
        lockInoutPo.setInoutId(GenerateCodeFactory.getGeneratorId("10"));
        lockInoutPo.setMachineId(lockMachinePo.getMachineId());
        lockInoutPo.setMachineCode(lockMachinePo.getMachineCode());
        lockInoutPo.setUserId(lockMachinePo.getOprUserId());
        lockInoutPo.setUserName(lockMachinePo.getOprUserName());
        lockInoutPo.setOpenTypeCd(LockInoutDto.OPEN_TYPE_REMOTE);
        lockInoutPo.setCommunityId(lockMachinePo.getCommunityId());
        lockInoutPo.setTel(lockMachinePo.getTel());

        lockInoutPo.setState(LockInoutDto.UNLOCK_SUCCESS);
        lockInoutV1InnerServiceSMOImpl.saveLockInout(lockInoutPo);
        logger.info("开锁成功");
    }

    @Override
    public void queryLockMachineState(LockMachineDto lockMachineDto, List<LockMachineParamDto> lockMachineParamDtos) {
        Date heartbeatTime = lockMachineDto.getHeartbeatTime();

        if (heartbeatTime == null) {
            lockMachineDto.setStateName("设备离线");
            lockMachineDto.setState("OFFLINE");
            return;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(heartbeatTime);
        calendar.add(Calendar.MINUTE, 2);
        if (calendar.getTime().getTime() <= DateUtil.getCurrentDate().getTime()) {
            lockMachineDto.setStateName("设备离线");
            lockMachineDto.setState("OFFLINE");
            return;
        }
        lockMachineDto.setStateName("设备在线");
        lockMachineDto.setState("ONLINE");

    }

    @Override
    public String addPasswordToCloud(LockPersonPo lockPersonPo, List<LockMachineParamDto> lockMachineParamDtos) {

        LockMachineDto lockMachineDto = new LockMachineDto();
        lockMachineDto.setMachineId(lockPersonPo.getMachineId());

        List<LockMachineDto> lockMachineDtos = lockMachineV1InnerServiceSMOImpl.queryLockMachines(lockMachineDto);

        Assert.listOnlyOne(lockMachineDtos, "未包含门锁设备");

        String taskId = GenerateCodeFactory.getGeneratorId("11");
        JSONObject param = new JSONObject();
        param.put("cmd", CMD_ADD_PWD);
        param.put("taskId", taskId);
        param.put("machineCode", lockMachineDtos.get(0).getMachineCode());
        param.put("keyboardPwd", lockPersonPo.getCardNumber());
        param.put("keyboardPwdType", KEYBOARD_PWD_TYPE_TERM);
        param.put("startDate", DateUtil.getDateFromStringB(lockPersonPo.getStartTime()).getTime());
        param.put("endDate", DateUtil.getDateFromStringB(lockPersonPo.getEndTime()).getTime());
        MqttFactory.publish(TOPIC_REQ.replace(SN, lockMachineDtos.get(0).getMachineCode()), param.toJSONString());

        LockLogPo lockLogPo = new LockLogPo();
        lockLogPo.setLogId(taskId);
        lockLogPo.setMachineId(lockMachineParamDtos.get(0).getMachineId());
        lockLogPo.setCommunityId(lockMachineParamDtos.get(0).getCommunityId());
        lockLogPo.setLogAction(LockLogDto.LOG_ACTION_ADD_PWD_TO_CLOUD);
        lockLogPo.setUserId(lockPersonPo.getOprUserId());
        lockLogPo.setUserName(lockPersonPo.getOprUserName());

        lockLogPo.setReqParam(param.toJSONString());

        lockLogPo.setState(LockLogDto.REQUEST_SUCCESS);
        lockLogV1InnerServiceSMOImpl.saveLockLog(lockLogPo);
        return lockPersonPo.getCardNumber();
    }

    @Override
    public void updatePasswordToCloud(LockPersonPo lockPersonPo, List<LockMachineParamDto> lockMachineParamDtos) {
        LockMachineDto lockMachineDto = new LockMachineDto();
        lockMachineDto.setMachineId(lockPersonPo.getMachineId());

        List<LockMachineDto> lockMachineDtos = lockMachineV1InnerServiceSMOImpl.queryLockMachines(lockMachineDto);

        Assert.listOnlyOne(lockMachineDtos, "未包含门锁设备");

        String taskId = GenerateCodeFactory.getGeneratorId("11");
        JSONObject param = new JSONObject();
        param.put("cmd", CMD_UPDATE_PWD);
        param.put("taskId", taskId);
        param.put("machineCode", lockMachineDtos.get(0).getMachineCode());
        param.put("keyboardPwd", lockPersonPo.getCardNumber());
        param.put("keyboardPwdType", KEYBOARD_PWD_TYPE_TERM);
        param.put("startDate", DateUtil.getDateFromStringB(lockPersonPo.getStartTime()).getTime());
        param.put("endDate", DateUtil.getDateFromStringB(lockPersonPo.getEndTime()).getTime());
        MqttFactory.publish(TOPIC_REQ.replace(SN, lockMachineDtos.get(0).getMachineCode()), param.toJSONString());

        LockLogPo lockLogPo = new LockLogPo();
        lockLogPo.setLogId(taskId);
        lockLogPo.setMachineId(lockMachineParamDtos.get(0).getMachineId());
        lockLogPo.setCommunityId(lockMachineParamDtos.get(0).getCommunityId());
        lockLogPo.setLogAction(LockLogDto.LOG_ACTION_ADD_PWD_TO_CLOUD);
        lockLogPo.setUserId(lockPersonPo.getOprUserId());
        lockLogPo.setUserName(lockPersonPo.getOprUserName());

        lockLogPo.setReqParam(param.toJSONString());

        lockLogPo.setState(LockLogDto.REQUEST_SUCCESS);
        lockLogV1InnerServiceSMOImpl.saveLockLog(lockLogPo);
    }


    @Override
    public void deletePassword(LockPersonPo lockPersonPo, List<LockMachineParamDto> lockMachineParamDtos) {
        LockMachineDto lockMachineDto = new LockMachineDto();
        lockMachineDto.setMachineId(lockPersonPo.getMachineId());

        List<LockMachineDto> lockMachineDtos = lockMachineV1InnerServiceSMOImpl.queryLockMachines(lockMachineDto);

        Assert.listOnlyOne(lockMachineDtos, "未包含门锁设备");

        String taskId = GenerateCodeFactory.getGeneratorId("13");
        JSONObject param = new JSONObject();
        param.put("cmd", CMD_DELETE_PWD);
        param.put("taskId", taskId);
        param.put("lockId", lockPersonPo.getCardNumber());//锁ID
        param.put("keyboardPwdId", KEYBOARD_PWD_TYPE_TERM);//	密码ID
        MqttFactory.publish(TOPIC_REQ.replace(SN, lockMachineDtos.get(0).getMachineCode()), param.toJSONString());

        LockLogPo lockLogPo = new LockLogPo();
        lockLogPo.setLogId(taskId);
        lockLogPo.setMachineId(lockMachineParamDtos.get(0).getMachineId());
        lockLogPo.setCommunityId(lockMachineParamDtos.get(0).getCommunityId());
        lockLogPo.setLogAction(LockLogDto.LOG_ACTION_PWD_DELETE);
        lockLogPo.setUserId(lockPersonPo.getOprUserId());
        lockLogPo.setUserName(lockPersonPo.getOprUserName());

        lockLogPo.setReqParam(param.toJSONString());

        lockLogPo.setState(LockLogDto.REQUEST_SUCCESS);
        lockLogV1InnerServiceSMOImpl.saveLockLog(lockLogPo);
    }

    @Override
    public Integer queryElectricQuantity(LockMachineDto lockMachineDto, List<LockMachineParamDto> lockMachineParamDtos) {

        if (StringUtil.isNumber(lockMachineDto.getCharge())) {
            return 0;
        }
        return Integer.parseInt(lockMachineDto.getCharge());
    }

    @Override
    public void lockResult(String topic, String data) {

        //todo 解析指令
        JSONObject paramIn = JSONObject.parseObject(data);
        String cmd = paramIn.getString("cmd");
        String taskId = paramIn.getString("taskId");
        String machineCode = paramIn.getString("machineCode");

        Assert.hasLength(cmd, "未包含cmd");
        Assert.hasLength(taskId, "未包含taskId");
        Assert.hasLength(machineCode, "未包含machineCode");


        LockMachineDto lockMachineDto = new LockMachineDto();
        lockMachineDto.setMachineCode(machineCode);

        List<LockMachineDto> lockMachineDtos = lockMachineV1InnerServiceSMOImpl.queryLockMachines(lockMachineDto);

        Assert.listOnlyOne(lockMachineDtos, "未包含门锁设备");

        logger.debug("设备 {} ，解析到 指令为 ：{}", machineCode, cmd);

        switch (cmd) {
            case CMD_HEARTBEAT: // todo 心跳
                machineHeartbeat(cmd, taskId, machineCode, lockMachineDtos.get(0), paramIn);
                break;
            case CMD_OPEN_LOCK: // todo 远程开锁返回
                reponseOpenLock(cmd, taskId, machineCode, lockMachineDtos.get(0), paramIn);
                break;
            case CMD_ADD_PWD: // todo 添加密码返回
                reponse(cmd, taskId, machineCode, lockMachineDtos.get(0), paramIn);
                break;
            case CMD_UPDATE_PWD:// todo 修改密码返回
                reponse(cmd, taskId, machineCode, lockMachineDtos.get(0), paramIn);
                break;
//            case LvCCUtil.CMD_CHANGE_PORT_STATE:// todo 充电过程中上报 功率
//                machineChangePortState(chargeMachineDto, data);
//                break;


        }
    }

    private void reponse(String cmd, String taskId, String machineCode, LockMachineDto lockMachineDto, JSONObject paramIn) {
        LockLogPo lockLogPo = new LockLogPo();
        lockLogPo.setState(LockLogDto.RETURN_SUCCESS);
        lockLogPo.setResParam(paramIn.toJSONString());
        lockLogPo.setLogId(taskId);
        lockLogV1InnerServiceSMOImpl.updateLockLog(lockLogPo);
    }

    /**
     * 开锁返回
     *
     * @param cmd
     * @param taskId
     * @param machineCode
     * @param lockMachineDto
     * @param paramIn
     */
    private void reponseOpenLock(String cmd, String taskId, String machineCode, LockMachineDto lockMachineDto, JSONObject paramIn) {

        LockLogPo lockLogPo = new LockLogPo();
        lockLogPo.setState(LockLogDto.RETURN_SUCCESS);
        lockLogPo.setResParam(paramIn.toJSONString());
        lockLogPo.setLogId(taskId);
        lockLogV1InnerServiceSMOImpl.updateLockLog(lockLogPo);

    }

    /**
     * 门锁心跳
     *
     * @param cmd
     * @param taskId
     * @param machineCode
     * @param lockMachineDto
     */
    private void machineHeartbeat(String cmd, String taskId, String machineCode, LockMachineDto lockMachineDto, JSONObject paramIn) {

        LockMachinePo lockMachinePo = new LockMachinePo();
        lockMachinePo.setHeartbeatTime(DateUtil.getCurrentDate());
        lockMachinePo.setMachineId(lockMachineDto.getMachineId());
        lockMachinePo.setMachineCode(lockMachineDto.getMachineCode());
        lockMachinePo.setCharge(paramIn.getString("electricQuantity"));
        lockMachineV1InnerServiceSMOImpl.updateLockMachine(lockMachinePo);

        JSONObject paramOut = new JSONObject();
        paramOut.put("cmd", cmd);
        paramOut.put("taskId", taskId);
        paramOut.put("code", 0);
        paramOut.put("msg", "处理成功");
        paramOut.put("machineCode", machineCode);
        MqttFactory.publish(TOPIC_REQ.replace(SN, machineCode), paramOut.toJSONString());
    }
}
