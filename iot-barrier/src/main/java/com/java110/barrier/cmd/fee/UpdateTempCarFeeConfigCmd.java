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
import com.java110.core.utils.StringUtil;
import com.java110.dto.fee.TempCarFeeConfigDto;
import com.java110.intf.barrier.ITempCarFeeConfigAttrV1InnerServiceSMO;
import com.java110.intf.barrier.ITempCarFeeConfigInnerServiceSMO;
import com.java110.intf.barrier.ITempCarFeeConfigV1InnerServiceSMO;
import com.java110.po.fee.TempCarFeeConfigAttrPo;
import com.java110.po.fee.TempCarFeeConfigPo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Java110Cmd(serviceCode = "fee.updateTempCarFeeConfig")
public class UpdateTempCarFeeConfigCmd extends Cmd {

    @Autowired
    private ITempCarFeeConfigInnerServiceSMO tempCarFeeConfigInnerServiceSMOImpl;

    @Autowired
    private ITempCarFeeConfigV1InnerServiceSMO tempCarFeeConfigV1InnerServiceSMOImpl;


    @Autowired
    private ITempCarFeeConfigAttrV1InnerServiceSMO tempCarFeeConfigAttrV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        Assert.hasKeyAndValue(reqJson, "configId", "configId不能为空");
        Assert.hasKeyAndValue(reqJson, "feeName", "请求报文中未包含feeName");
        Assert.hasKeyAndValue(reqJson, "paId", "请求报文中未包含paId");
        Assert.hasKeyAndValue(reqJson, "carType", "请求报文中未包含carType");
        Assert.hasKeyAndValue(reqJson, "ruleId", "请求报文中未包含ruleId");
        Assert.hasKeyAndValue(reqJson, "communityId", "请求报文中未包含communityId");
        Assert.hasKeyAndValue(reqJson, "startTime", "请求报文中未包含startTime");
        Assert.hasKeyAndValue(reqJson, "endTime", "请求报文中未包含endTime");
    }

    @Override
    //@Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {

        TempCarFeeConfigDto tempCarFeeConfigDto = new TempCarFeeConfigDto();
        tempCarFeeConfigDto.setConfigId(reqJson.getString("configId"));
        tempCarFeeConfigDto.setCommunityId(reqJson.getString("communityId"));
        List<TempCarFeeConfigDto> tempCarFeeConfigDtos = tempCarFeeConfigInnerServiceSMOImpl.queryTempCarFeeConfigs(tempCarFeeConfigDto);
        Assert.listOnlyOne(tempCarFeeConfigDtos, "临时车收费标准不存在");
        TempCarFeeConfigPo tempCarFeeConfigPo = BeanConvertUtil.covertBean(reqJson, TempCarFeeConfigPo.class);
        updateAttr(reqJson);
        int flag = tempCarFeeConfigV1InnerServiceSMOImpl.updateTempCarFeeConfig(tempCarFeeConfigPo);
        if (flag < 1) {
            throw new CmdException("修改临时车费用配置失败");
        }

    }

    private void updateAttr(JSONObject reqJson) {
        int flag;
        JSONArray attrs = reqJson.getJSONArray("attrs");
        if (attrs == null || attrs.size() < 1) {
            return;
        }
        JSONObject attr = null;
        for (int attrIndex = 0; attrIndex < attrs.size(); attrIndex++) {
            attr = attrs.getJSONObject(attrIndex);
            attr.put("ruleId", reqJson.getString("ruleId"));
            attr.put("communityId", reqJson.getString("communityId"));
            if (!attr.containsKey("attrId") || attr.getString("attrId").startsWith("-") || StringUtil.isEmpty(attr.getString("attrId"))) {
                attr.put("attrId", GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_attrId));
                TempCarFeeConfigAttrPo tempCarFeeConfigAttrPo = BeanConvertUtil.covertBean(attr, TempCarFeeConfigAttrPo.class);
                flag = tempCarFeeConfigAttrV1InnerServiceSMOImpl.saveTempCarFeeConfigAttr(tempCarFeeConfigAttrPo);
                if (flag < 1) {
                    throw new CmdException("修改属性失败");
                }
                continue;
            }
            TempCarFeeConfigAttrPo tempCarFeeConfigAttrPo = BeanConvertUtil.covertBean(attr, TempCarFeeConfigAttrPo.class);
            flag = tempCarFeeConfigAttrV1InnerServiceSMOImpl.updateTempCarFeeConfigAttr(tempCarFeeConfigAttrPo);
            if (flag < 1) {
                throw new CmdException("修改属性失败");
            }
        }
    }


}
