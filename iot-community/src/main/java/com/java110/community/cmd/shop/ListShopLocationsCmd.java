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
import com.java110.dto.shop.ShopLocationDto;
import com.java110.intf.community.IShopLocationV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Java110Cmd(serviceCode = "shop.listShopLocations")
public class ListShopLocationsCmd extends Cmd {

    @Autowired
    private IShopLocationV1InnerServiceSMO shopLocationV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        Assert.jsonObjectHaveKey(reqJson, "communityId", "请求报文中未包含communityId节点");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        ShopLocationDto shopLocationDto = BeanConvertUtil.covertBean(reqJson, ShopLocationDto.class);
        shopLocationDto.setPage(-1);
        List<ShopLocationDto> allLocations = shopLocationV1InnerServiceSMOImpl.queryShopLocations(shopLocationDto);
        List<ShopLocationDto> treeData = buildTree(allLocations);
        int count = treeData.size();
        ResponseEntity<String> responseEntity = ResultVo.createResponseEntity(1, count, treeData);
        context.setResponseEntity(responseEntity);
    }

    private List<ShopLocationDto> buildTree(List<ShopLocationDto> allLocations) {
        if (allLocations == null || allLocations.isEmpty()) {
            return new ArrayList<>();
        }
        Map<String, ShopLocationDto> locationMap = new HashMap<>();
        List<ShopLocationDto> roots = new ArrayList<>();

        for (ShopLocationDto location : allLocations) {
            location.setChildren(new ArrayList<>());
            locationMap.put(location.getLocationId(), location);
        }

        for (ShopLocationDto location : allLocations) {
            if (location.getParentId() == null || "-1".equals(location.getParentId()) || !locationMap.containsKey(location.getParentId())) {
                roots.add(location);
            } else {
                ShopLocationDto parent = locationMap.get(location.getParentId());
                parent.getChildren().add(location);
            }
        }
        return roots;
    }
}
