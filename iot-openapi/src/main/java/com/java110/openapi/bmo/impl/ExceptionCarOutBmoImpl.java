package com.java110.openapi.bmo.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.ListUtil;
import com.java110.dto.carInout.CarInoutDto;
import com.java110.dto.parking.ParkingAreaDto;
import com.java110.intf.barrier.ICarInoutV1InnerServiceSMO;
import com.java110.intf.car.IParkingAreaV1InnerServiceSMO;
import com.java110.openapi.bmo.IIotCommonApiBmo;
import com.java110.po.carInout.CarInoutPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("exceptionCarOutBmoImpl")
public class ExceptionCarOutBmoImpl implements IIotCommonApiBmo {

    @Autowired
    private ICarInoutV1InnerServiceSMO carInoutV1InnerServiceSMOImpl;

    @Autowired
    private IParkingAreaV1InnerServiceSMO parkingAreaV1InnerServiceSMOImpl;

    @Override
    public void validate(ICmdDataFlowContext context, JSONObject reqJson) {
        // 验证必要参数
        Assert.hasKeyAndValue(reqJson, "communityId", "小区ID不能为空");
        Assert.hasKeyAndValue(reqJson, "carNum", "车牌号不能为空");
        Assert.hasKeyAndValue(reqJson, "paNum", "停车场编号不能为空");
        
        // 可选参数：如果没有传入inoutId，则根据车牌号查询在场记录
        if (!reqJson.containsKey("inoutId") || reqJson.getString("inoutId").isEmpty()) {
            Assert.hasKeyAndValue(reqJson, "carNum", "车牌号不能为空");
        }
    }

    @Override
    public void doCmd(ICmdDataFlowContext context, JSONObject reqJson) {
        String communityId = reqJson.getString("communityId");
        String carNum = reqJson.getString("carNum");
        String paNum = reqJson.getString("paNum");
        
        // 1. 根据paNum和communityId查询停车场信息，获取paId
        ParkingAreaDto parkingAreaDto = new ParkingAreaDto();
        parkingAreaDto.setNum(paNum);
        parkingAreaDto.setCommunityId(communityId);
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaV1InnerServiceSMOImpl.queryParkingAreas(parkingAreaDto);
        
        if (ListUtil.isNull(parkingAreaDtos)) {
            throw new CmdException("停车场不存在");
        }
        
        String paId = parkingAreaDtos.get(0).getPaId();
        String inoutId = reqJson.getString("inoutId");
        
        // 2. 查找要出场车辆的记录
        CarInoutDto carInoutDto = new CarInoutDto();
        carInoutDto.setCommunityId(communityId);
        carInoutDto.setCarNum(carNum);
        carInoutDto.setPaId(paId);
        
        // 只查询在场车辆（状态为在停、已支付、重新支付）
        carInoutDto.setStates(new String[]{CarInoutDto.STATE_IN, CarInoutDto.STATE_PAY, CarInoutDto.STATE_REPAY});
        
        List<CarInoutDto> carInoutDtos = carInoutV1InnerServiceSMOImpl.queryCarInouts(carInoutDto);
        
        if (ListUtil.isNull(carInoutDtos)) {
            throw new CmdException("未找到该车辆的在场记录");
        }
        
        // 如果传入了inoutId，则精确匹配
        CarInoutDto targetCarInout = null;
        if (inoutId != null && !inoutId.isEmpty()) {
            for (CarInoutDto dto : carInoutDtos) {
                if (inoutId.equals(dto.getInoutId())) {
                    targetCarInout = dto;
                    break;
                }
            }
            if (targetCarInout == null) {
                throw new CmdException("未找到指定的车辆进出记录");
            }
        } else {
            // 如果没有传入inoutId，取最近的一条在场记录
            if (carInoutDtos.size() > 1) {
                throw new CmdException("同一车辆存在多条在场记录，请指定inoutId");
            }
            targetCarInout = carInoutDtos.get(0);
        }
        
        // 3. 更新车辆状态为已出场
        CarInoutPo carInoutPo = new CarInoutPo();
        carInoutPo.setInoutId(targetCarInout.getInoutId());
        carInoutPo.setCommunityId(communityId);
        carInoutPo.setState(CarInoutDto.STATE_OUT);
        
        // 可选：设置出场时间
        // carInoutPo.setOutTime(DateUtil.getCurrentDate());
        
        int result = carInoutV1InnerServiceSMOImpl.updateCarInout(carInoutPo);
        
        if (result > 0) {
            // 4. 构建成功返回结果
            ResultVo resultVo = new ResultVo(0, "车辆异常出场成功", null);
            ResponseEntity<String> responseEntity = new ResponseEntity<>(resultVo.toString(), HttpStatus.OK);
            context.setResponseEntity(responseEntity);
        } else {
            throw new CmdException("车辆出场失败，请重试");
        }
    }
}