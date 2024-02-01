package com.java110.openapi.bmo.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.fee.TempCarPayOrderDto;
import com.java110.dto.parking.ParkingBoxAreaDto;
import com.java110.intf.barrier.ICarInoutPaymentV1InnerServiceSMO;
import com.java110.intf.barrier.IParkingBoxAreaV1InnerServiceSMO;
import com.java110.openapi.bmo.IIotCommonApiBmo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("getTempCarFeeOrderBmoImpl")
public class GetTempCarFeeOrderBmoImpl implements IIotCommonApiBmo {

    @Autowired
    private IParkingBoxAreaV1InnerServiceSMO parkingBoxAreaV1InnerServiceSMOImpl;

    @Autowired
    private ICarInoutPaymentV1InnerServiceSMO carInoutPaymentV1InnerServiceSMOImpl;

    @Override
    public void validate(ICmdDataFlowContext context, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "carNum", "carNum不能为空");
        if (StringUtil.isEmpty(reqJson.getString("paId"))) {
            Assert.hasKeyAndValue(reqJson, "boxId", "boxId不能为空");

            ParkingBoxAreaDto parkingBoxAreaDto = new ParkingBoxAreaDto();
            parkingBoxAreaDto.setDefaultArea(ParkingBoxAreaDto.DEFAULT_AREA_TRUE);
            parkingBoxAreaDto.setBoxId(reqJson.getString("boxId"));
            List<ParkingBoxAreaDto> parkingBoxAreaDtos = parkingBoxAreaV1InnerServiceSMOImpl.queryParkingBoxAreas(parkingBoxAreaDto);
            if (ListUtil.isNull(parkingBoxAreaDtos)) {
                throw new CmdException("未找到停车场");
            }
            reqJson.put("paId", parkingBoxAreaDtos.get(0).getPaId());
        }
        Assert.hasKeyAndValue(reqJson, "paId", "paId不能为空");
    }

    @Override
    public void doCmd(ICmdDataFlowContext context, JSONObject reqJson) {
        TempCarPayOrderDto tempCarPayOrderDto = new TempCarPayOrderDto();
        tempCarPayOrderDto.setPaId(reqJson.getString("paId"));
        tempCarPayOrderDto.setCarNum(reqJson.getString("carNum"));
        if (reqJson.containsKey("pccIds") && !StringUtil.isEmpty(reqJson.getString("pccIds"))) {
            tempCarPayOrderDto.setPccIds(reqJson.getString("pccIds").split(","));
        }
        ResultVo resultVo = carInoutPaymentV1InnerServiceSMOImpl.getTempCarFeeOrder(tempCarPayOrderDto);
        context.setResponseEntity(ResultVo.createResponseEntity(resultVo));
    }
}
