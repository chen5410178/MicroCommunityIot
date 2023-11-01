package com.java110.system.cmd.admin;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.CmdContextUtils;
import com.java110.dto.app.AppDto;
import com.java110.dto.store.StoreDto;
import com.java110.intf.dev.IAppV1InnerServiceSMO;
import com.java110.intf.user.IStoreV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.List;

@Java110Cmd(serviceCode = "admin.queryAppServiceData")
public class QueryAppServiceDataCmd extends Cmd {

    @Autowired
    private IStoreV1InnerServiceSMO storeInnerServiceSMOImpl;

    @Autowired
    private IAppV1InnerServiceSMO appInnerServiceSMOImpl;


    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        String storeId = CmdContextUtils.getStoreId(context);

        StoreDto storeDto = new StoreDto();
        storeDto.setStoreId(storeId);
        storeDto.setStoreTypeCd(StoreDto.STORE_TYPE_DEV);
        int count = storeInnerServiceSMOImpl.queryStoresCount(storeDto);
        if (count < 1) {
            throw new CmdException("非法操作，请用系统管理员账户操作");
        }

    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        AppDto appDto = new AppDto();
        List<AppDto> appDtos = appInnerServiceSMOImpl.queryAppsServiceData(appDto);

        context.setResponseEntity(ResultVo.createResponseEntity(appDtos));
    }
}
