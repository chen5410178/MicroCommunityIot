package com.java110.lock.factory.common;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.lock.LockInoutDto;
import com.java110.dto.lock.LockLogDto;
import com.java110.dto.lock.LockMachineDto;
import com.java110.intf.lock.ILockInoutV1InnerServiceSMO;
import com.java110.intf.lock.ILockLogV1InnerServiceSMO;
import com.java110.lock.factory.ILockFactoryAdapt;
import com.java110.po.lock.LockInoutPo;
import com.java110.po.lock.LockLogPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("commonLockFactoryAdaptImpl")
public class CommonLockFactoryAdaptImpl implements ILockFactoryAdapt {

    @Autowired
    private ILockLogV1InnerServiceSMO lockLogV1InnerServiceSMOImpl;

    @Autowired
    private ILockInoutV1InnerServiceSMO lockInoutV1InnerServiceSMOImpl;

    @Override
    public ResultVo unlock(JSONObject reqJson) {
        String logId = GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_logId);

        //todo: 组装请求报文
        String reqParam = "{msg:\"这是一条请求报文,上线请替换\"}";
        //todo: 发送请求接收同步返回   如果同步成功要修改日志记录的状态,异步在接收回调通知后修改日志记录的状态,

        //请求报文发送成功 记录日志
        LockLogPo lockLogPo = BeanConvertUtil.covertBean(reqJson, LockLogPo.class);
        lockLogPo.setLogId(logId);
        lockLogPo.setReqParam(reqParam);
        lockLogPo.setState(LockLogDto.REQUEST_SUCCESS);
        lockLogV1InnerServiceSMOImpl.saveLockLog(lockLogPo);

        //模拟请求报文同步回调成功,记录操作日志 todo: 异步回调成功要记录操作日志
        LockInoutPo lockInoutPo = BeanConvertUtil.covertBean(reqJson, LockInoutPo.class);
        lockInoutPo.setInoutId(GenerateCodeFactory.getGeneratorId("10"));
        lockInoutPo.setState(LockInoutDto.UNLOCK_SUCCESS);
        lockInoutV1InnerServiceSMOImpl.saveLockInout(lockInoutPo);

        return new ResultVo(ResultVo.CODE_OK, "请求已发送，等待电梯反馈数据");
    }

    @Override
    public void queryLockMachineState(LockMachineDto lockMachineDto) {
        lockMachineDto.setState(LockMachineDto.STATE_ONLINE);
        lockMachineDto.setStateName("在线");
    }
}
