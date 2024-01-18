package com.java110.openapi.cmd.staff;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.po.org.OrgStaffRelPo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.intf.user.IOrgStaffRelV1InnerServiceSMO;
import com.java110.intf.user.IStoreStaffV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import com.java110.po.storeStaff.StoreStaffPo;
import com.java110.po.user.UserPo;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;

/**
 * 接口删除员工
 */
@Java110Cmd(serviceCode = "staff.deleteStaffApi")
public class DeleteStaffApiCmd extends Cmd {

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Autowired
    private IStoreStaffV1InnerServiceSMO storeStaffV1InnerServiceSMOImpl;

    @Autowired
    private IOrgStaffRelV1InnerServiceSMO orgStaffRelV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson, "propertyId", "未包含propertyId");
        Assert.hasKeyAndValue(reqJson, "staffId", "未包含staffId");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {


        UserPo userPo = new UserPo();
        userPo.setUserId(reqJson.getString("staffId"));
        userV1InnerServiceSMOImpl.deleteUser(userPo);


        StoreStaffPo storeStaffPo = new StoreStaffPo();
        storeStaffPo.setStaffId(reqJson.getString("staffId"));
        storeStaffV1InnerServiceSMOImpl.deleteStoreStaff(storeStaffPo);


        OrgStaffRelPo orgStaffRelPo = new OrgStaffRelPo();
        orgStaffRelPo.setStaffId(reqJson.getString("staffId"));
        orgStaffRelV1InnerServiceSMOImpl.deleteOrgStaffRel(orgStaffRelPo);

    }
}
