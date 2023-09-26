package com.java110.lamp.factory.common;

import com.java110.bean.ResultVo;
import com.java110.core.factory.GenerateCodeFactory;

import com.java110.dto.lamp.LampMachineDto;
import com.java110.dto.lamp.LampMachineLogDto;
import com.java110.intf.lamp.ILampMachineLogV1InnerServiceSMO;
import com.java110.intf.lamp.ILampMachineOperationV1InnerServiceSMO;
import com.java110.lamp.factory.ILampFactoryAdapt;
import com.java110.po.lamp.LampMachineLogPo;
import com.java110.po.lamp.LampMachineOperationPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("commonLampFactoryAdaptImpl")
public class CommonLampFactoryAdaptImpl implements ILampFactoryAdapt {

    @Autowired
    private ILampMachineLogV1InnerServiceSMO lampMachineLogV1InnerServiceSMOImpl;

    @Autowired
    private ILampMachineOperationV1InnerServiceSMO lampMachineOperationV1InnerServiceSMOImpl;

    @Override
    public LampMachineDto getLampCurState(LampMachineDto lampMachineDto) {
        return null;
    }

    @Override
    public ResultVo startLamp(LampMachineDto lampMachineDto) {
        String logId = GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_logId);

        //todo: 组装请求报文
        String reqParam = "";
        //todo: 发送请求接收同步返回   假如是同步成功要修改日志记录的状态,异步在接收回调通知后修改日志记录的状态,

        //请求报文发送成功 记录日志
        LampMachineLogPo lampOperateLog = createLampMachineLog(lampMachineDto, logId, reqParam, LampMachineLogDto.REQUEST_SUCCESS);
        lampMachineLogV1InnerServiceSMOImpl.saveLampMachineLog(lampOperateLog);

        //模拟请求报文同步回调成功,记录操作日志 todo: 异步回调成功要记录操作日志
        LampMachineOperationPo lampMachineOperationPo = createLampMachineOperation(lampMachineDto);
        lampMachineOperationV1InnerServiceSMOImpl.saveLampMachineOperation(lampMachineOperationPo);

        return new ResultVo(ResultVo.CODE_OK, "请求已发送，等待电梯反馈数据");
    }

    @Override
    public ResultVo stopLamp(LampMachineDto lampMachineDto) {
        return null;
    }

    @Override
    public ResultVo unknownLampOperate(LampMachineDto lampMachineDto) {
        LampMachineLogPo lampOperateLog = createLampMachineLog(lampMachineDto, GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_logId), "", LampMachineLogDto.OPERATE_FAILED);
        lampMachineLogV1InnerServiceSMOImpl.saveLampMachineLog(lampOperateLog);

        return new ResultVo(ResultVo.CODE_ERROR, "未知操作");
    }

    private LampMachineLogPo createLampMachineLog(LampMachineDto lampMachineDto, String logId, String reqParam, String state) {
        LampMachineLogPo lampMachineLogPo = new LampMachineLogPo();
        lampMachineLogPo.setLogId(logId);
        lampMachineLogPo.setMachineId(lampMachineDto.getMachineId());
        lampMachineLogPo.setCommunityId(lampMachineDto.getCommunityId());
        lampMachineLogPo.setLogAction(lampMachineDto.getLogAction());
        lampMachineLogPo.setPersonId(lampMachineDto.getUserId());
        lampMachineLogPo.setPersonName(lampMachineDto.getUserName());
        lampMachineLogPo.setReqParam(reqParam);
        lampMachineLogPo.setState(state);
        return lampMachineLogPo;
    }

    private LampMachineOperationPo createLampMachineOperation(LampMachineDto lampMachineDto) {
        LampMachineOperationPo lampMachineOperationPo = new LampMachineOperationPo();
        lampMachineOperationPo.setLmoId(GenerateCodeFactory.getGeneratorId("10"));
        lampMachineOperationPo.setMachineId(lampMachineDto.getMachineId());
        lampMachineOperationPo.setPersonId(lampMachineDto.getUserId());
        lampMachineOperationPo.setPersonName(lampMachineDto.getUserName());
        lampMachineOperationPo.setType(lampMachineDto.getLogAction());
        lampMachineOperationPo.setOperationTime(lampMachineDto.getOperationTime());
        return lampMachineOperationPo;
    }
}
