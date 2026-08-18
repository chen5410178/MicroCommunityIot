package com.java110.community.cmd.shop;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.annotation.Java110Transactional;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.shop.BuildingShopDto;
import com.java110.intf.community.IBuildingShopV1InnerServiceSMO;
import com.java110.po.shop.BuildingShopPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

@Java110Cmd(serviceCode = "shop.saveBuildingShop")
public class SaveBuildingShopCmd extends Cmd {

    @Autowired
    private IBuildingShopV1InnerServiceSMO buildingShopV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        Assert.jsonObjectHaveKey(reqJson, "shopNum", "请求报文中未包含shopNum节点");
        Assert.jsonObjectHaveKey(reqJson, "locationId", "请求报文中未包含locationId节点");
        Assert.jsonObjectHaveKey(reqJson, "locationName", "请求报文中未包含locationName节点");
        Assert.jsonObjectHaveKey(reqJson, "builtUpArea", "请求报文中未包含builtUpArea节点");
        Assert.jsonObjectHaveKey(reqJson, "communityId", "请求报文中未包含communityId节点");
    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        reqJson.put("shopId", GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_shopId));
        if (!reqJson.containsKey("state")) {
            reqJson.put("state", BuildingShopDto.STATE_FREE);
        }
        if (!reqJson.containsKey("shopRent")) {
            reqJson.put("shopRent", "0.00");
        }
        BuildingShopPo buildingShopPo = BeanConvertUtil.covertBean(reqJson, BuildingShopPo.class);
        int flag = buildingShopV1InnerServiceSMOImpl.saveBuildingShop(buildingShopPo);
        if (flag < 1) {
            throw new CmdException("保存商铺失败");
        }
        ResponseEntity<String> responseEntity = ResultVo.success();
        context.setResponseEntity(responseEntity);
    }
}
