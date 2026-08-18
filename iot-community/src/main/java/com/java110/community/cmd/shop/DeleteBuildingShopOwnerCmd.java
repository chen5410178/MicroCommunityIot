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
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.shop.BuildingShopDto;
import com.java110.dto.shop.BuildingShopOwnerDto;
import com.java110.intf.community.IBuildingShopOwnerV1InnerServiceSMO;
import com.java110.intf.community.IBuildingShopV1InnerServiceSMO;
import com.java110.po.shop.BuildingShopOwnerPo;
import com.java110.po.shop.BuildingShopPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Java110Cmd(serviceCode = "shop.deleteBuildingShopOwner")
public class DeleteBuildingShopOwnerCmd extends Cmd {

    @Autowired
    private IBuildingShopOwnerV1InnerServiceSMO buildingShopOwnerV1InnerServiceSMOImpl;

    @Autowired
    private IBuildingShopV1InnerServiceSMO buildingShopV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        Assert.jsonObjectHaveKey(reqJson, "communityId", "请求报文中未包含communityId节点");
        if (!reqJson.containsKey("shopOwnerId")) {
            Assert.jsonObjectHaveKey(reqJson, "shopId", "请求报文中未包含shopId节点");
            Assert.jsonObjectHaveKey(reqJson, "ownerId", "请求报文中未包含ownerId节点");
        }
    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        String shopOwnerId = reqJson.getString("shopOwnerId");
        String shopId = reqJson.getString("shopId");
        if (shopOwnerId == null || shopOwnerId.isEmpty()) {
            BuildingShopOwnerDto queryDto = new BuildingShopOwnerDto();
            queryDto.setShopId(shopId);
            queryDto.setOwnerId(reqJson.getString("ownerId"));
            queryDto.setStatusCd("0");
            queryDto.setPage(1);
            queryDto.setRow(1);
            List<BuildingShopOwnerDto> shopOwners = buildingShopOwnerV1InnerServiceSMOImpl.queryBuildingShopOwners(queryDto);
            if (shopOwners != null && !shopOwners.isEmpty()) {
                shopOwnerId = shopOwners.get(0).getShopOwnerId();
            }
        }

        if (shopOwnerId == null || shopOwnerId.isEmpty()) {
            throw new CmdException("未找到商铺业主记录");
        }

        BuildingShopOwnerPo buildingShopOwnerPo = new BuildingShopOwnerPo();
        buildingShopOwnerPo.setShopOwnerId(shopOwnerId);
        int flag = buildingShopOwnerV1InnerServiceSMOImpl.deleteBuildingShopOwner(buildingShopOwnerPo);
        if (flag < 1) {
            throw new CmdException("删除商铺租赁失败");
        }

        if (shopId != null && !shopId.isEmpty()) {
            BuildingShopPo buildingShopPo = new BuildingShopPo();
            buildingShopPo.setShopId(shopId);
            buildingShopPo.setState(BuildingShopDto.STATE_FREE);
            int updateFlag = buildingShopV1InnerServiceSMOImpl.updateBuildingShop(buildingShopPo);
            if (updateFlag < 1) {
                throw new CmdException("更新商铺状态失败");
            }
        }

        ResponseEntity<String> responseEntity = ResultVo.success();
        context.setResponseEntity(responseEntity);
    }
}
