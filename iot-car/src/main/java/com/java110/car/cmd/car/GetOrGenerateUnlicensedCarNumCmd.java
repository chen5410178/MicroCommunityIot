package com.java110.car.cmd.car;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.car.UnlicensedCarPlateDto;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.Assert;
import com.java110.intf.car.IUnlicensedCarPlateV1InnerServiceSMO;
import com.java110.po.car.UnlicensedCarPlatePo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Java110Cmd(serviceCode = "car.getOrGenerateUnlicensedCarNum")
public class GetOrGenerateUnlicensedCarNumCmd extends Cmd {

    @Autowired
    private IUnlicensedCarPlateV1InnerServiceSMO unlicensedCarPlateV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        Assert.hasKeyAndValue(reqJson, "openId", "未包含openId");
        Assert.hasKeyAndValue(reqJson, "openType", "未包含openType");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        String openId = reqJson.getString("openId");
        String communityId = reqJson.getString("communityId");
        String openType = reqJson.getString("openType");

        UnlicensedCarPlateDto queryDto = new UnlicensedCarPlateDto();
        queryDto.setOpenId(openId);
        queryDto.setStatusCd("0");
        List<UnlicensedCarPlateDto> existList = unlicensedCarPlateV1InnerServiceSMOImpl.queryUnlicensedCarPlates(queryDto);

        String carNum;
        if (existList != null && existList.size() > 0) {
            carNum = existList.get(0).getCarNum();
        } else {
            carNum = generateNewCarNum();
            UnlicensedCarPlatePo po = new UnlicensedCarPlatePo();
            po.setPlateId(GenerateCodeFactory.getGeneratorId("11"));
            po.setOpenId(openId);
            po.setCarNum(carNum);
            po.setCommunityId(communityId);
            po.setOpenType(openType);
            unlicensedCarPlateV1InnerServiceSMOImpl.saveUnlicensedCarPlate(po);
        }

        JSONObject result = new JSONObject();
        result.put("carNum", carNum);
        context.setResponseEntity(ResultVo.createResponseEntity(result));
    }

    private String generateNewCarNum() {
        UnlicensedCarPlateDto queryDto = new UnlicensedCarPlateDto();
        int count = unlicensedCarPlateV1InnerServiceSMOImpl.getUnlicensedCarPlateCount(queryDto);
        return "无A" + String.format("%05d", count + 1);
    }
}
