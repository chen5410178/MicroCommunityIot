package com.java110.community.cmd.shop;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.annotation.Java110Transactional;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.dto.shop.BuildingShopOwnerDto;
import com.java110.intf.community.IBuildingShopOwnerV1InnerServiceSMO;
import com.java110.intf.community.IBuildingShopV1InnerServiceSMO;
import com.java110.po.shop.BuildingShopOwnerPo;
import com.java110.po.shop.BuildingShopPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

@Java110Cmd(serviceCode = "shop.deleteBuildingShop")
public class DeleteBuildingShopCmd extends Cmd {

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
        String shopId = reqJson.getString("shopId");
        String communityId = reqJson.getString("communityId");

        // 删除商铺业主关联
        BuildingShopOwnerDto buildingShopOwnerDto = new BuildingShopOwnerDto();
        buildingShopOwnerDto.setShopId(shopId);
        buildingShopOwnerDto.setCommunityId(communityId);
        int count = buildingShopOwnerV1InnerServiceSMOImpl.queryBuildingShopOwnersCount(buildingShopOwnerDto);
        if (count > 0) {
            BuildingShopOwnerPo buildingShopOwnerPo = new BuildingShopOwnerPo();
            buildingShopOwnerPo.setShopId(shopId);
            buildingShopOwnerPo.setCommunityId(communityId);
            buildingShopOwnerV1InnerServiceSMOImpl.deleteBuildingShopOwner(buildingShopOwnerPo);
        }

        // 删除商铺
        BuildingShopPo buildingShopPo = new BuildingShopPo();
        buildingShopPo.setShopId(shopId);
        buildingShopPo.setCommunityId(communityId);
        buildingShopPo.setStatusCd("1");
        int flag = buildingShopV1InnerServiceSMOImpl.deleteBuildingShop(buildingShopPo);
        if (flag < 1) {
            throw new CmdException("删除商铺失败");
        }
        ResponseEntity<String> responseEntity = ResultVo.success();
        context.setResponseEntity(responseEntity);
    }
}
