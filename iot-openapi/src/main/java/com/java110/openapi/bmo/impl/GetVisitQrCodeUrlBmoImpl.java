package com.java110.openapi.bmo.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.cache.UrlCache;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.utils.Assert;
import com.java110.core.utils.CmdContextUtils;
import com.java110.openapi.bmo.IIotCommonApiBmo;
import org.springframework.stereotype.Service;

@Service("getVisitQrCodeUrlBmoImpl")
public class GetVisitQrCodeUrlBmoImpl implements IIotCommonApiBmo {
    @Override
    public void validate(ICmdDataFlowContext context, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "communityId", "请求报文中未包含小区信息");
    }

    @Override
    public void doCmd(ICmdDataFlowContext context, JSONObject reqJson) {
        String userId = CmdContextUtils.getUserId(context);
        String ownerUrl = UrlCache.getOwnerUrl();
        ownerUrl += ("/#/packageVisit/pages/visit/addVisit?createUserId=" + userId +"&communityId="+reqJson.getString("communityId"));
        reqJson.put("url", ownerUrl);
        context.setResponseEntity(ResultVo.createResponseEntity(reqJson));
    }
}
