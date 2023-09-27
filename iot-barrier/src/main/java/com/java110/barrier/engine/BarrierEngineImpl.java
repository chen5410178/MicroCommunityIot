package com.java110.barrier.engine;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.dto.fee.TempCarPayOrderDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class BarrierEngineImpl implements IBarrierEngine {
    @Override
    public ResultVo getTempCarFeeOrder(TempCarPayOrderDto tempCarPayOrderDto) {
        return null;
    }

    @Override
    public ResultVo notifyTempCarFeeOrder(TempCarPayOrderDto tempCarPayOrderDto) {
        return null;
    }

    @Override
    public ResponseEntity<String> customCarInOut(JSONObject reqJson) {
        return null;
    }
}
