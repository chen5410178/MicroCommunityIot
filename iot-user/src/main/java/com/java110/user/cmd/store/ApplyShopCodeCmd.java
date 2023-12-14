package com.java110.user.cmd.store;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cache.CommonCache;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.Assert;
import com.java110.dto.shop.ShopDto;
import com.java110.dto.shop.StoreShopDto;
import com.java110.intf.user.IStoreShopV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.List;

/**
 * 申请商户的临时票据
 */
@Java110Cmd(serviceCode = "store.applyShopCode")
public class ApplyShopCodeCmd extends Cmd {

    @Autowired
    private IStoreShopV1InnerServiceSMO storeShopV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {

        Assert.hasKeyAndValue(reqJson, "shopId", "未包含商铺信息");

        String storeId = context.getReqHeaders().get("store-id");

        StoreShopDto storeShopDto = new StoreShopDto();
        storeShopDto.setShopId(reqJson.getString("shopId"));
        storeShopDto.setStoreId(storeId);
        List<ShopDto> storeShopDtos = storeShopV1InnerServiceSMOImpl.queryStoreShops(storeShopDto);

        Assert.listOnlyOne(storeShopDtos, "您没有权限操作");

    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        String code = GenerateCodeFactory.getUUID();

        CommonCache.setValue(reqJson.getString("shopId") + code, code, CommonCache.PAY_DEFAULT_EXPIRE_TIME);

        context.setResponseEntity(ResultVo.createResponseEntity(code));

    }
}
