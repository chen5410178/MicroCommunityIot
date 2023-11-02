package com.java110.lift.factory.common;

import com.java110.bean.ResultVo;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.lift.LiftMachineDto;
import com.java110.dto.lift.LiftMachineLogDto;
import com.java110.dto.lift.LiftMachineReservationDto;
import com.java110.intf.lift.ILiftMachineLogV1InnerServiceSMO;
import com.java110.intf.lift.ILiftMachineReservationV1InnerServiceSMO;
import com.java110.lift.factory.ILiftFactoryAdapt;
import com.java110.po.lift.LiftMachineLogPo;
import com.java110.po.lift.LiftMachineReservationPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("commonLiftFactoryAdaptImpl")
public class CommonLiftFactoryAdaptImpl implements ILiftFactoryAdapt {

    @Autowired
    private ILiftMachineLogV1InnerServiceSMO liftMachineLogV1InnerServiceSMOImpl;

    @Autowired
    private ILiftMachineReservationV1InnerServiceSMO liftMachineReservationV1InnerServiceSMO;

    @Override
    public LiftMachineDto getLiftCurState(LiftMachineDto liftMachineDto) {
        return null;
    }

    @Override
    public ResultVo startLift(LiftMachineDto liftMachineDto) {
        String logId = GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_logId);

        //todo: 组装请求报文
        String reqParam = "";
        //todo: 发送请求接收同步返回   假如是同步成功要修改日志记录的状态,异步在接收回调通知后修改日志记录的状态

        //成功 记录日志
        LiftMachineLogPo liftOperateLog = createLiftOperateLog(liftMachineDto, logId, reqParam, LiftMachineLogDto.REQUEST_SUCCESS);
        liftMachineLogV1InnerServiceSMOImpl.saveLiftMachineLog(liftOperateLog);

        return new ResultVo(ResultVo.CODE_OK, "请求已发送，等待电梯反馈数据");
    }

    @Override
    public ResultVo stopLift(LiftMachineDto liftMachineDto) {
        return null;
    }

    @Override
    public ResultVo unknownLiftOperate(LiftMachineDto liftMachineDto) {
        LiftMachineLogPo liftOperateLog = createLiftOperateLog(liftMachineDto, GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_logId), "", LiftMachineLogDto.OPERATE_FAILED);
        liftMachineLogV1InnerServiceSMOImpl.saveLiftMachineLog(liftOperateLog);

        return new ResultVo(ResultVo.CODE_ERROR, "未知操作");
    }

    private LiftMachineLogPo createLiftOperateLog(LiftMachineDto liftMachineDto, String logId, String reqParam, String state) {
        LiftMachineLogPo liftMachineLogPo = new LiftMachineLogPo();
        liftMachineLogPo.setLogId(logId);
        liftMachineLogPo.setMachineId(liftMachineDto.getMachineId());
        liftMachineLogPo.setCommunityId(liftMachineDto.getCommunityId());
        liftMachineLogPo.setLogAction(liftMachineDto.getLogAction());
        liftMachineLogPo.setPersonId(liftMachineDto.getUserId());
        liftMachineLogPo.setPersonName(liftMachineDto.getUserName());
        liftMachineLogPo.setReqParam(reqParam);
        liftMachineLogPo.setState(state);
        return liftMachineLogPo;
    }

    @Override
    public ResultVo reservationLiftMachine(LiftMachineReservationDto liftMachineReservationDto) {
        String lmrId = GenerateCodeFactory.getGeneratorId("10");

        //todo: 组装请求报文
        String reqParam = "";
        //todo: 发送请求接收同步返回

        //预约记录
        LiftMachineReservationPo liftMachineReservationPo = BeanConvertUtil.covertBean(liftMachineReservationDto, LiftMachineReservationPo.class);
        liftMachineReservationPo.setLmrId(lmrId);
        liftMachineReservationV1InnerServiceSMO.saveLiftMachineReservation(liftMachineReservationPo);

        // todo:假如是同步.成功后修改当前电梯楼层; 异步在接收异步通知后修改当前楼层

        return new ResultVo(ResultVo.CODE_OK, "请求已发送，等待电梯反馈数据");
    }

    @Override
    public void queryLiftMachineState(LiftMachineDto liftMachineDto) {
        liftMachineDto.setIsOnlineState(LiftMachineDto.STATE_ONLINE);
        liftMachineDto.setIsOnlineStateName("在线");
    }
}
