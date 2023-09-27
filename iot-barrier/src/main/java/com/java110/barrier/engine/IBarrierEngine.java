package com.java110.barrier.engine;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.dto.fee.TempCarPayOrderDto;
import org.springframework.http.ResponseEntity;

public interface IBarrierEngine {
    ResultVo getTempCarFeeOrder(TempCarPayOrderDto tempCarPayOrderDto);

    ResultVo notifyTempCarFeeOrder(TempCarPayOrderDto tempCarPayOrderDto);

    ResponseEntity<String> customCarInOut(JSONObject reqJson);
}
