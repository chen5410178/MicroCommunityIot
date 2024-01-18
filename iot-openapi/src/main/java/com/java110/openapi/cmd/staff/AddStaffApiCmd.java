package com.java110.openapi.cmd.staff;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.dto.org.OrgDto;
import com.java110.bean.dto.org.OrgStaffRelDto;
import com.java110.bean.po.org.OrgStaffRelPo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.AuthenticationFactory;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.ListUtil;
import com.java110.dto.storeStaff.StoreStaffDto;
import com.java110.dto.user.UserDto;
import com.java110.intf.user.*;
import com.java110.po.storeStaff.StoreStaffPo;
import com.java110.po.user.UserPo;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.List;

/**
 * 接口添加员工
 * <p>
 * 员工同步只同步员工，不同步组织架构和小区授权，小区功能授权需要 在物联网系统上手工授权；
 */
@Java110Cmd(serviceCode = "staff.addStaffApi")
public class AddStaffApiCmd extends Cmd {
    private static final String STAFF_DEFAULT_PASSWORD = "123456";
    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Autowired
    private IStoreStaffV1InnerServiceSMO storeUserV1InnerServiceSMOImpl;

    @Autowired
    private IOrgV1InnerServiceSMO orgV1InnerServiceSMOImpl;

    @Autowired
    private IOrgStaffRelV1InnerServiceSMO orgStaffRelV1InnerServiceSMOImpl;


    @Autowired
    private IPrivilegeUserV1InnerServiceSMO privilegeUserV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson, "propertyId", "未包含propertyId");
        Assert.hasKeyAndValue(reqJson, "staffId", "未包含staffId");
        Assert.hasKeyAndValue(reqJson, "name", "未包含name");
        Assert.hasKeyAndValue(reqJson, "tel", "未包含link");
        Assert.hasKeyAndValue(reqJson, "relCd", "未包含link");


        //判断员工手机号是否重复(员工可根据手机号登录平台)
        UserDto userDto = new UserDto();
        userDto.setTel(reqJson.getString("tel"));
        userDto.setUserFlag("1");
        userDto.setLevelCd("01"); //员工
        List<UserDto> users = userV1InnerServiceSMOImpl.queryUsers(userDto);
        Assert.listIsNull(users, "员工手机号不能重复，请重新输入");

    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        //todo 添加用户
        addUser(reqJson);

        //todo 添加商户员工关系
        addStaff(reqJson);

        //todo 添加员工组织关系
        addStaffOrg(reqJson);

        //todo 同步人脸至 门禁
        //synchronousAccessControl(reqJson);

    }

    private void addStaffOrg(JSONObject reqJson) {

        OrgDto orgDto = new OrgDto();
        orgDto.setStoreId(reqJson.getString("storeId"));
        orgDto.setOrgLevel("1");
        List<OrgDto> orgDtos = orgV1InnerServiceSMOImpl.queryOrgs(orgDto);

        if (ListUtil.isNull(orgDtos)) {
            return;
        }


        //添加组织 员工关系
        OrgStaffRelPo orgStaffRelPo = new OrgStaffRelPo();
        orgStaffRelPo.setOrgId(orgDtos.get(0).getOrgId());
        orgStaffRelPo.setStaffId(reqJson.getString("staffId"));
        orgStaffRelPo.setRelId(GenerateCodeFactory.getGeneratorId("11"));
        orgStaffRelPo.setRelCd(OrgStaffRelDto.REL_CD_ADMIN);
        orgStaffRelPo.setStoreId(reqJson.getString("storeId"));
        int flag = orgStaffRelV1InnerServiceSMOImpl.saveOrgStaffRel(orgStaffRelPo);
        if (flag < 1) {
            throw new CmdException("注册失败");
        }
    }

    private void addStaff(JSONObject reqJson) {

        //保存 商户和用户的关系
        StoreStaffPo storeUserPo = new StoreStaffPo();
        storeUserPo.setStoreStaffId(GenerateCodeFactory.getGeneratorId("11"));
        storeUserPo.setAdminFlag(StoreStaffDto.ADMIN_FLAG_N);

        storeUserPo.setStoreId(reqJson.getString("storeId"));
        storeUserPo.setStaffId(reqJson.getString("staffId"));
        storeUserPo.setTel(reqJson.getString("tel"));
        storeUserPo.setStaffName(reqJson.getString("name"));
        int flag = storeUserV1InnerServiceSMOImpl.saveStoreStaff(storeUserPo);
        if (flag < 1) {
            throw new CmdException("注册失败");
        }

    }

    /**
     * Assert.hasKeyAndValue(reqJson, "propertyId", "未包含communityId");
     * Assert.hasKeyAndValue(reqJson, "staffId", "未包含ownerId");
     * Assert.hasKeyAndValue(reqJson, "name", "未包含name");
     * Assert.hasKeyAndValue(reqJson, "tel", "未包含link");
     * Assert.hasKeyAndValue(reqJson, "relCd", "未包含link");
     *
     * @param reqJson
     */
    private void addUser(JSONObject reqJson) {

        UserPo userPo = new UserPo();
        userPo.setTel(reqJson.getString("tel"));
        userPo.setName(reqJson.getString("name"));
        userPo.setAddress("无");
        userPo.setUserId(reqJson.getString("staffId"));
        userPo.setEmail("无");
        userPo.setLevelCd(UserDto.LEVEL_CD_STAFF);
        userPo.setSex("1");
        //设置默认密码
        String staffDefaultPassword = STAFF_DEFAULT_PASSWORD;
        staffDefaultPassword = AuthenticationFactory.passwdMd5(staffDefaultPassword);
        userPo.setPassword(staffDefaultPassword);
        userV1InnerServiceSMOImpl.saveUser(userPo);
    }
}
