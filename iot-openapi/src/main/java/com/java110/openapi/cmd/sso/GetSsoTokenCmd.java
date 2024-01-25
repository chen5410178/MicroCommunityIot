package com.java110.openapi.cmd.sso;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cache.CommonCache;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.CmdContextUtils;
import com.java110.core.utils.ListUtil;
import com.java110.dto.store.StoreDto;
import com.java110.dto.storeStaff.StoreStaffDto;
import com.java110.dto.user.UserDto;
import com.java110.intf.user.IStoreStaffV1InnerServiceSMO;
import com.java110.intf.user.IStoreV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.List;

/**
 * 二、获取临时票据（getSsoToken）
 * 物联网系统中必须存在手机号，根据手机号单点登录，您可以通过如下方式添加物联网员工信息：
 * 方式一：可以通过《三方系统对接HC物联网系统协议1.0》协议中的二、小区的方式同步物业公司和小区 再通过 七、同步员工的方式同步员工信息，然后登陆物联网系统分配权限。
 * 方式二：可以登陆物联网系统 添加员工手机号和业务系统的手机号一一对应即可，分配权限
 */
@Java110Cmd(serviceCode = "sso.getSsoToken")
public class GetSsoTokenCmd extends Cmd {

    @Autowired
    private IStoreV1InnerServiceSMO storeV1InnerServiceSMOImpl;

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;


    @Autowired
    private IStoreStaffV1InnerServiceSMO storeStaffV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        Assert.hasKeyAndValue(reqJson, "tel", "未包含手机号");
        Assert.hasKeyAndValue(reqJson, "storeName", "未包含物业公司名称");

        String storeId = CmdContextUtils.getStoreId(context);

        StoreDto storeDto = new StoreDto();
        storeDto.setStoreId(storeId);
        storeDto.setStoreTypeCd(StoreDto.STORE_TYPE_ADMIN);
        List<StoreDto> storeDtos = storeV1InnerServiceSMOImpl.queryStores(storeDto);

        if (ListUtil.isNull(storeDtos)) {
            throw new CmdException("登陆账户没有权限");
        }


    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        StoreStaffDto storeStaffDto = new StoreStaffDto();
        storeStaffDto.setTel(reqJson.getString("tel"));
        List<StoreStaffDto> storeStaffDtos = storeStaffV1InnerServiceSMOImpl.queryStoreStaffs(storeStaffDto);

        if (ListUtil.isNull(storeStaffDtos)) {
            throw new CmdException("未包含员工");
        }
        StoreStaffDto loginStoreStaffDto = null;
        for (StoreStaffDto tmpStoreStaffDto : storeStaffDtos) {
            if (reqJson.getString("storeName").equals(tmpStoreStaffDto.getName())) {
                loginStoreStaffDto = tmpStoreStaffDto;
                break;
            }
        }

        if (loginStoreStaffDto == null) {
            throw new CmdException("未包含员工");
        }

        String token = GenerateCodeFactory.getUUID();

        CommonCache.setValue("sso_" + token, JSONObject.toJSONString(loginStoreStaffDto), CommonCache.SSO_TOKEN_EXPIRE_TIME);
    }
}
