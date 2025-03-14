package com.java110.openapi.cmd.common;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.CmdContextUtils;
import com.java110.core.utils.ListUtil;
import com.java110.dto.store.StoreDto;
import com.java110.intf.user.IStoreV1InnerServiceSMO;
import com.java110.openapi.bmo.IIotAdminApiBmo;
import com.java110.openapi.bmo.IIotCommonApiBmo;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.List;

/**
 * 专属于HC小区管理系统调用
 */
@Java110Cmd(serviceCode = "common.openAdminApi")
public class OpenAdminApiCmd extends Cmd {

    private IIotAdminApiBmo iotCommonApiBmoImpl;

    @Autowired
    private IStoreV1InnerServiceSMO storeV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        Assert.hasKeyAndValue(reqJson, "iotApiCode", "未包含IOT接口编码");
        Assert.hasKeyAndValue(reqJson, "adminUserTel", "未包含IOT接口adminUserTel");
        Assert.hasKeyAndValue(reqJson, "adminStoreId", "未包含IOT接口adminStoreId");

        String storeId = CmdContextUtils.getStoreId(context);

        StoreDto storeDto = new StoreDto();
        storeDto.setStoreId(storeId);
        storeDto.setStoreTypeCd(StoreDto.STORE_TYPE_ADMIN);
        List<StoreDto> storeDtos = storeV1InnerServiceSMOImpl.queryStores(storeDto);

        if (ListUtil.isNull(storeDtos)) {
            throw new CmdException("登陆账户没有权限");
        }

        iotCommonApiBmoImpl = ApplicationContextFactory.getBean(reqJson.getString("iotApiCode"), IIotAdminApiBmo.class);
        if (iotCommonApiBmoImpl == null) {
            throw new CmdException("未实现该能力");
        }

        iotCommonApiBmoImpl.validate(context, reqJson);
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        iotCommonApiBmoImpl = ApplicationContextFactory.getBean(reqJson.getString("iotApiCode"), IIotAdminApiBmo.class);
        if (iotCommonApiBmoImpl == null) {
            throw new CmdException("未实现该能力");
        }
        iotCommonApiBmoImpl.doCmd(context, reqJson);
    }
}
