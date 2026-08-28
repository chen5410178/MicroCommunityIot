package com.java110.car.cmd.parkingSpace;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.annotation.Java110Transactional;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.ListUtil;
import com.java110.dto.parking.ParkingSpaceDto;
import com.java110.intf.car.IParkingSpaceV1InnerServiceSMO;
import com.java110.po.parking.ParkingSpacePo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.util.List;

@Java110Cmd(serviceCode = "parkingSpace.releaseParkingSpace")
public class ReleaseParkingSpaceCmd extends Cmd {

    @Autowired
    private IParkingSpaceV1InnerServiceSMO parkingSpaceV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        Assert.hasKeyAndValue(reqJson, "communityId", "未包含小区ID");
        Assert.hasKeyAndValue(reqJson, "psId", "未包含psId");

        //校验车位当前状态必须为出租 H 或出售 S 才可释放
        ParkingSpaceDto parkingSpaceDto = new ParkingSpaceDto();
        parkingSpaceDto.setCommunityId(reqJson.getString("communityId"));
        parkingSpaceDto.setPsId(reqJson.getString("psId"));
        List<ParkingSpaceDto> parkingSpaceDtos = parkingSpaceV1InnerServiceSMOImpl.queryParkingSpaces(parkingSpaceDto);
        if (ListUtil.isNull(parkingSpaceDtos) || parkingSpaceDtos.size() != 1) {
            throw new CmdException("未查询到停车位信息");
        }
        if (ParkingSpaceDto.STATE_FREE.equals(parkingSpaceDtos.get(0).getState())) {
            throw new CmdException("当前车位已是空闲状态，无需释放");
        }
    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        String communityId = reqJson.getString("communityId");
        String psId = reqJson.getString("psId");

        //查询车位信息
        ParkingSpaceDto parkingSpaceDto = new ParkingSpaceDto();
        parkingSpaceDto.setCommunityId(communityId);
        parkingSpaceDto.setPsId(psId);
        List<ParkingSpaceDto> parkingSpaceDtos = parkingSpaceV1InnerServiceSMOImpl.queryParkingSpaces(parkingSpaceDto);
        if (ListUtil.isNull(parkingSpaceDtos) || parkingSpaceDtos.size() != 1) {
            throw new CmdException("未查询到停车位信息");
        }

        //释放车位：状态置为空闲 F，清空业主、车辆、租期字段
        ParkingSpacePo parkingSpacePo = new ParkingSpacePo();
        parkingSpacePo.setPsId(psId);
        parkingSpacePo.setCommunityId(communityId);
        parkingSpacePo.setState(ParkingSpaceDto.STATE_FREE);
        parkingSpacePo.setOwnerId("");
        parkingSpacePo.setOwnerName("");
        parkingSpacePo.setCarId("");
        parkingSpacePo.setCarNum("");
        parkingSpacePo.setStartTime("");
        parkingSpacePo.setEndTime("");
        int flag = parkingSpaceV1InnerServiceSMOImpl.updateParkingSpace(parkingSpacePo);
        if (flag < 1) {
            throw new CmdException("修改车位失败");
        }

        context.setResponseEntity(ResultVo.success());
    }
}
