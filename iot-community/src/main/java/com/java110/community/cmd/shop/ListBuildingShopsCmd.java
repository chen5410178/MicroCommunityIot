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
import com.java110.dto.shop.BuildingShopDto;
import com.java110.intf.community.IBuildingShopV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

@Java110Cmd(serviceCode = "shop.listBuildingShops")
public class ListBuildingShopsCmd extends Cmd {

    @Autowired
    private IBuildingShopV1InnerServiceSMO buildingShopV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        super.validatePageInfo(reqJson);
        Assert.jsonObjectHaveKey(reqJson, "communityId", "请求报文中未包含communityId节点");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        BuildingShopDto buildingShopDto = BeanConvertUtil.covertBean(reqJson, BuildingShopDto.class);
        int count = buildingShopV1InnerServiceSMOImpl.queryBuildingShopsCount(buildingShopDto);
        List<BuildingShopDto> buildingShops;
        if (count > 0) {
            buildingShops = buildingShopV1InnerServiceSMOImpl.queryBuildingShops(buildingShopDto);
        } else {
            buildingShops = new ArrayList<>();
        }
        int records = (int) Math.ceil((double) count / buildingShopDto.getRow());
        ResponseEntity<String> responseEntity = ResultVo.createResponseEntity(records, count, buildingShops);
        context.setResponseEntity(responseEntity);
    }
}
