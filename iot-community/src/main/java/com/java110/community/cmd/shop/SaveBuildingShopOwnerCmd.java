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
import com.java110.intf.community.IBuildingShopOwnerV1InnerServiceSMO;
import com.java110.intf.community.IBuildingShopV1InnerServiceSMO;
import com.java110.po.shop.BuildingShopOwnerPo;
import com.java110.po.shop.BuildingShopPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

@Java110Cmd(serviceCode = "shop.saveBuildingShopOwner")
public class SaveBuildingShopOwnerCmd extends Cmd {

    @Autowired
    private IBuildingShopOwnerV1InnerServiceSMO buildingShopOwnerV1InnerServiceSMOImpl;

    @Autowired
    private IBuildingShopV1InnerServiceSMO buildingShopV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        Assert.jsonObjectHaveKey(reqJson, "shopId", "请求报文中未包含shopId节点");
        Assert.jsonObjectHaveKey(reqJson, "communityId", "请求报文中未包含communityId节点");
    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        reqJson.put("shopOwnerId", GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_shopId));
        BuildingShopOwnerPo buildingShopOwnerPo = BeanConvertUtil.covertBean(reqJson, BuildingShopOwnerPo.class);
        int flag = buildingShopOwnerV1InnerServiceSMOImpl.saveBuildingShopOwner(buildingShopOwnerPo);
        if (flag < 1) {
            throw new CmdException("保存商铺租赁失败");
        }

        BuildingShopPo buildingShopPo = new BuildingShopPo();
        buildingShopPo.setShopId(reqJson.getString("shopId"));
        buildingShopPo.setState(reqJson.getString("state"));
        int updateFlag = buildingShopV1InnerServiceSMOImpl.updateBuildingShop(buildingShopPo);
        if (updateFlag < 1) {
            throw new CmdException("更新商铺状态失败");
        }

        ResponseEntity<String> responseEntity = ResultVo.success();
        context.setResponseEntity(responseEntity);
    }
}
