package com.java110.barrier.cmd.fee;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.annotation.Java110Transactional;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.fee.FeeDto;
import com.java110.dto.parking.ParkingAreaDto;
import com.java110.intf.barrier.ITempCarFeeConfigAttrV1InnerServiceSMO;
import com.java110.intf.barrier.ITempCarFeeConfigV1InnerServiceSMO;
import com.java110.intf.car.IParkingAreaV1InnerServiceSMO;
import com.java110.po.fee.TempCarFeeConfigAttrPo;
import com.java110.po.fee.TempCarFeeConfigPo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Java110Cmd(serviceCode = "fee.saveTempCarFeeConfig")
public class SaveTempCarFeeConfigCmd extends Cmd {

    @Autowired
    private IParkingAreaV1InnerServiceSMO parkingAreaInnerServiceSMOImpl;

    @Autowired
    private ITempCarFeeConfigV1InnerServiceSMO tempCarFeeConfigV1InnerServiceSMOImpl;

    @Autowired
    private ITempCarFeeConfigAttrV1InnerServiceSMO tempCarFeeConfigAttrV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        Assert.hasKeyAndValue(reqJson, "feeName", "请求报文中未包含feeName");
        Assert.hasKeyAndValue(reqJson, "paId", "请求报文中未包含paId");
        Assert.hasKeyAndValue(reqJson, "carType", "请求报文中未包含carType");
        Assert.hasKeyAndValue(reqJson, "ruleId", "请求报文中未包含ruleId");
        Assert.hasKeyAndValue(reqJson, "communityId", "请求报文中未包含communityId");
        Assert.hasKeyAndValue(reqJson, "startTime", "请求报文中未包含startTime");
        Assert.hasKeyAndValue(reqJson, "endTime", "请求报文中未包含endTime");
        //查询停车场编号
        ParkingAreaDto parkingAreaDto = new ParkingAreaDto();
        parkingAreaDto.setPaId(reqJson.getString("paId"));
        parkingAreaDto.setCommunityId(reqJson.getString("communityId"));
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaInnerServiceSMOImpl.queryParkingAreas(parkingAreaDto);
        Assert.listOnlyOne(parkingAreaDtos, "停车场不存在");
        reqJson.put("areaNum", parkingAreaDtos.get(0).getNum());
    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        reqJson.put("feeConfigId", GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_configId));
        reqJson.put("configId", GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_configId));
        TempCarFeeConfigPo tempCarFeeConfigPo = BeanConvertUtil.covertBean(reqJson, TempCarFeeConfigPo.class);
        //处理房屋属性
        dealAttr(reqJson, context);
        int flag = tempCarFeeConfigV1InnerServiceSMOImpl.saveTempCarFeeConfig(tempCarFeeConfigPo);
        if (flag < 1) {
            throw new CmdException("保存临时收费失败");
        }
        //补费用项数据

    }

    private void dealAttr(JSONObject reqJson, ICmdDataFlowContext context) {
        if (!reqJson.containsKey("attrs")) {
            return;
        }
        JSONArray attrs = reqJson.getJSONArray("attrs");
        if (attrs == null || attrs.size() < 1) {
            return;
        }
        JSONObject attr = null;
        int flag = 0;
        for (int attrIndex = 0; attrIndex < attrs.size(); attrIndex++) {
            attr = attrs.getJSONObject(attrIndex);
            attr.put("configId", reqJson.getString("configId"));
            attr.put("communityId", reqJson.getString("communityId"));
            attr.put("attrId", GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_attrId));
            TempCarFeeConfigAttrPo tempCarFeeConfigAttrPo = BeanConvertUtil.covertBean(attr, TempCarFeeConfigAttrPo.class);
            flag = tempCarFeeConfigAttrV1InnerServiceSMOImpl.saveTempCarFeeConfigAttr(tempCarFeeConfigAttrPo);
            if (flag < 1) {
                throw new CmdException("保存临时收费失败");
            }
        }
    }
}
