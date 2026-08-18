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
import com.java110.intf.community.IShopLocationV1InnerServiceSMO;
import com.java110.po.shop.ShopLocationPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

@Java110Cmd(serviceCode = "shop.updateShopLocation")
public class UpdateShopLocationCmd extends Cmd {

    @Autowired
    private IShopLocationV1InnerServiceSMO shopLocationV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        Assert.jsonObjectHaveKey(reqJson, "locationId", "请求报文中未包含locationId节点");
        Assert.jsonObjectHaveKey(reqJson, "communityId", "请求报文中未包含communityId节点");
    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        ShopLocationPo shopLocationPo = BeanConvertUtil.covertBean(reqJson, ShopLocationPo.class);
        int flag = shopLocationV1InnerServiceSMOImpl.updateShopLocation(shopLocationPo);
        if (flag < 1) {
            throw new CmdException("修改商铺位置失败");
        }
        ResponseEntity<String> responseEntity = ResultVo.success();
        context.setResponseEntity(responseEntity);
    }
}
