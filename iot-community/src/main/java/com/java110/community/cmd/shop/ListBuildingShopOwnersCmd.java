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
import com.java110.dto.shop.BuildingShopOwnerDto;
import com.java110.intf.community.IBuildingShopOwnerV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

@Java110Cmd(serviceCode = "shop.listBuildingShopOwners")
public class ListBuildingShopOwnersCmd extends Cmd {

    @Autowired
    private IBuildingShopOwnerV1InnerServiceSMO buildingShopOwnerV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        super.validatePageInfo(reqJson);
        Assert.jsonObjectHaveKey(reqJson, "communityId", "请求报文中未包含communityId节点");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        BuildingShopOwnerDto buildingShopOwnerDto = BeanConvertUtil.covertBean(reqJson, BuildingShopOwnerDto.class);
        int count = buildingShopOwnerV1InnerServiceSMOImpl.queryBuildingShopOwnersCount(buildingShopOwnerDto);
        List<BuildingShopOwnerDto> buildingShopOwners;
        if (count > 0) {
            buildingShopOwners = buildingShopOwnerV1InnerServiceSMOImpl.queryBuildingShopOwners(buildingShopOwnerDto);
        } else {
            buildingShopOwners = new ArrayList<>();
        }
        int records = (int) Math.ceil((double) count / buildingShopOwnerDto.getRow());
        ResponseEntity<String> responseEntity = ResultVo.createResponseEntity(records, count, buildingShopOwners);
        context.setResponseEntity(responseEntity);
    }
}
