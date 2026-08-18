package com.java110.community.cmd.shop;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.intf.community.IBuildingShopOwnerV1InnerServiceSMO;
import com.java110.po.shop.BuildingShopOwnerPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

@Java110Cmd(serviceCode = "shop.updateBuildingShopOwner")
public class UpdateBuildingShopOwnerCmd extends Cmd {

    @Autowired
    private IBuildingShopOwnerV1InnerServiceSMO buildingShopOwnerV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        Assert.jsonObjectHaveKey(reqJson, "shopOwnerId", "请求报文中未包含shopOwnerId节点");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        BuildingShopOwnerPo buildingShopOwnerPo = BeanConvertUtil.covertBean(reqJson, BuildingShopOwnerPo.class);
        int flag = buildingShopOwnerV1InnerServiceSMOImpl.updateBuildingShopOwner(buildingShopOwnerPo);
        if (flag < 1) {
            throw new CmdException("修改商铺租赁失败");
        }
        ResponseEntity<String> responseEntity = ResultVo.success();
        context.setResponseEntity(responseEntity);
    }
}
