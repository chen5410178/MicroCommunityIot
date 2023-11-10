package com.java110.user.cmd.user;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cache.MappingCache;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.constant.MappingConstant;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.CmdContextUtils;
import com.java110.core.utils.ListUtil;
import com.java110.doc.annotation.*;
import com.java110.dto.accessControlFace.AccessControlFaceDto;
import com.java110.dto.privilegeUser.PrivilegeUserDto;
import com.java110.intf.accessControl.IAccessControlFaceV1InnerServiceSMO;
import com.java110.intf.user.IPrivilegeUserV1InnerServiceSMO;
import com.java110.intf.user.IStoreStaffV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import com.java110.po.accessControlFace.AccessControlFacePo;
import com.java110.po.privilegeUser.PrivilegeUserPo;
import com.java110.po.storeStaff.StoreStaffPo;
import com.java110.po.user.UserPo;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.List;


@Java110CmdDoc(title = "删除员工",
        description = "外部系统通过删除员工接口 删除员工 注意需要物业管理员账号登录，因为不需要传storeId 是根据管理员登录信息获取的",
        httpMethod = "post",
        url = "http://{ip}:{port}/app/user.staff.delete",
        resource = "userDoc",
        author = "吴学文",
        serviceCode = "user.staff.delete"
)

@Java110ParamsDoc(params = {
        @Java110ParamDoc(name = "userId", length = 30, remark = "员工ID"),
})

@Java110ResponseDoc(
        params = {
                @Java110ParamDoc(name = "code", type = "int", length = 11, defaultValue = "0", remark = "返回编号，0 成功 其他失败"),
                @Java110ParamDoc(name = "msg", type = "String", length = 250, defaultValue = "成功", remark = "描述"),
        }
)

@Java110ExampleDoc(
        reqBody = "{\"userId\":\"123123\"}",
        resBody = "{'code':0,'msg':'成功'"
)

@Java110Cmd(serviceCode = "user.staff.delete")
public class UserStaffDeleteCmd extends Cmd {


    @Autowired
    private IStoreStaffV1InnerServiceSMO storeUserV1InnerServiceSMOImpl;

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Autowired
    private IPrivilegeUserV1InnerServiceSMO privilegeUserV1InnerServiceSMOImpl;

    @Autowired
    private IAccessControlFaceV1InnerServiceSMO accessControlFaceV1InnerServiceSMOImpl;


    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {

        if (!reqJson.containsKey("storeId")) {
            reqJson.put("storeId", CmdContextUtils.getStoreId(context));
        }
        Assert.jsonObjectHaveKey(reqJson, "storeId", "请求参数中未包含storeId 节点，请确认");

        Assert.jsonObjectHaveKey(reqJson, "userId", "请求参数中未包含userId 节点，请确认");


    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        //todo 同步门禁
        synchronousAccessControl(reqJson);

        //todo 删除员工
        deleteStaff(reqJson);

        //todo 删除用户
        deleteUser(reqJson);

        //todo 赋权
        deleteUserPrivilege(reqJson);
    }

    /**
     * 删除用户权限
     *
     * @param paramInJson
     */
    private void deleteUserPrivilege(JSONObject paramInJson) {

        PrivilegeUserDto privilegeUserDto = new PrivilegeUserDto();
        privilegeUserDto.setUserId(paramInJson.getString("userId"));
        List<PrivilegeUserDto> privilegeUserDtos = privilegeUserV1InnerServiceSMOImpl.queryPrivilegeUsers(privilegeUserDto);

        if (privilegeUserDtos == null || privilegeUserDtos.size() < 1) {
            return;
        }

        for (PrivilegeUserDto tmpPrivilegeUserDto : privilegeUserDtos) {
            PrivilegeUserPo privilegeUserPo = new PrivilegeUserPo();
            privilegeUserPo.setPuId(tmpPrivilegeUserDto.getPuId());
            int flag = privilegeUserV1InnerServiceSMOImpl.deletePrivilegeUser(privilegeUserPo);
            if (flag < 1) {
                throw new CmdException("删除员工失败");
            }
        }
    }

    /**
     * 删除商户
     *
     * @param paramInJson
     * @return
     */
    public void deleteStaff(JSONObject paramInJson) {

        JSONObject businessStoreUser = new JSONObject();
        businessStoreUser.put("storeId", paramInJson.getString("storeId"));
        businessStoreUser.put("staffId", paramInJson.getString("userId"));


        StoreStaffPo storeUserPo = BeanConvertUtil.covertBean(businessStoreUser, StoreStaffPo.class);

        int flag = storeUserV1InnerServiceSMOImpl.deleteStoreStaff(storeUserPo);

        if (flag < 1) {
            throw new CmdException("删除员工失败");
        }
    }

    /**
     * 删除商户
     *
     * @param paramInJson
     * @return
     */
    public void deleteUser(JSONObject paramInJson) {
        //校验json 格式中是否包含 name,email,levelCd,tel
        JSONObject businessStoreUser = new JSONObject();
        businessStoreUser.put("userId", paramInJson.getString("userId"));

        UserPo userPo = BeanConvertUtil.covertBean(businessStoreUser, UserPo.class);
        int flag = userV1InnerServiceSMOImpl.deleteUser(userPo);

        if (flag < 1) {
            throw new CmdException("删除员工失败");
        }
    }


    private void synchronousAccessControl(JSONObject reqJson) {
        AccessControlFaceDto accessControlFaceDto = new AccessControlFaceDto();
        accessControlFaceDto.setPersonId(reqJson.getString("userId"));
        List<AccessControlFaceDto> accessControlFaceDtos = accessControlFaceV1InnerServiceSMOImpl.queryAccessControlFaces(accessControlFaceDto);
        AccessControlFacePo accessControlFacePo = null;
        if (ListUtil.isNull(accessControlFaceDtos)) {
            return;
        }
        for (AccessControlFaceDto tmpAccessControlFaceDto : accessControlFaceDtos) {
            accessControlFacePo = BeanConvertUtil.covertBean(tmpAccessControlFaceDto, AccessControlFacePo.class);
            accessControlFacePo.setState(AccessControlFaceDto.STATE_WAIT);
            accessControlFacePo.setMessage("待下发到门禁");
        }
        accessControlFaceV1InnerServiceSMOImpl.deleteAccessControlFace(accessControlFacePo);
    }

}
