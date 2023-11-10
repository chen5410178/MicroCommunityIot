package com.java110.user.cmd.user;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.dto.file.FileRelDto;
import com.java110.bean.dto.owner.OwnerDto;
import com.java110.bean.dto.room.RoomDto;
import com.java110.bean.po.org.OrgStaffRelPo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.annotation.Java110Transactional;
import com.java110.core.cache.MappingCache;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.constant.MappingConstant;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.AuthenticationFactory;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.*;
import com.java110.doc.annotation.*;
import com.java110.dto.accessControlFace.AccessControlFaceDto;
import com.java110.dto.accessControlFloor.AccessControlFloorDto;
import com.java110.dto.accessControlOrg.AccessControlOrgDto;
import com.java110.dto.storeStaff.StoreStaffDto;
import com.java110.dto.user.UserDto;
import com.java110.intf.accessControl.IAccessControlFaceV1InnerServiceSMO;
import com.java110.intf.accessControl.IAccessControlOrgV1InnerServiceSMO;
import com.java110.intf.user.IOrgStaffRelV1InnerServiceSMO;
import com.java110.intf.user.IStoreStaffV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import com.java110.po.accessControlFace.AccessControlFacePo;
import com.java110.po.storeStaff.StoreStaffPo;
import com.java110.po.user.UserPo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;


@Java110CmdDoc(title = "添加员工",
        description = "外部系统通过添加员工接口 添加员工，注意需要物业管理员账号登录，因为不需要传storeId 是根据管理员登录信息获取的",
        httpMethod = "post",
        url = "http://{ip}:{port}/app/user.staff.add",
        resource = "userDoc",
        author = "吴学文",
        serviceCode = "user.staff.add"
)

@Java110ParamsDoc(params = {
        @Java110ParamDoc(name = "orgId", length = 30, remark = "组织ID"),
        @Java110ParamDoc(name = "orgName", length = 64, remark = "组织名称"),
        @Java110ParamDoc(name = "name", length = 64, remark = "名称"),
        @Java110ParamDoc(name = "sex", length = 64, remark = "性别 0女 1男"),
        @Java110ParamDoc(name = "email", length = 64, remark = "邮箱"),
        @Java110ParamDoc(name = "tel", length = 11, remark = "手机号"),
        @Java110ParamDoc(name = "address", length = 64, remark = "地址"),
        @Java110ParamDoc(name = "relCd", length = 64, remark = "岗位,普通员工 1000 部门经理 2000 部门副经理 3000 部门组长 4000 分公司总经理 5000 分公司副总经理 6000 总经理助理 7000 总公司总经理 8000 总公司副总经理 9000"),
})

@Java110ResponseDoc(
        params = {
                @Java110ParamDoc(name = "code", type = "int", length = 11, defaultValue = "0", remark = "返回编号，0 成功 其他失败"),
                @Java110ParamDoc(name = "msg", type = "String", length = 250, defaultValue = "成功", remark = "描述"),
        }
)

@Java110ExampleDoc(
        reqBody = "{\"orgId\":\"102022091988250052\",\"orgName\":\"演示物业 / 件部\",\"username\":\"张三\",\"sex\":\"0\",\"email\":\"231@qq.com\",\"tel\":\"123\",\"address\":\"123\",\"relCd\":\"1000\",\"photo\":\"\",\"name\":\"张三\"}",
        resBody = "{'code':0,'msg':'成功'}"
)

@Java110Cmd(serviceCode = "user.staff.add")
public class UserStaffAddCmd extends Cmd {

    private static final String STAFF_DEFAULT_PASSWORD = "123456";

    @Autowired
    private IUserV1InnerServiceSMO userInnerServiceSMOImpl;

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Autowired
    private IStoreStaffV1InnerServiceSMO storeUserV1InnerServiceSMOImpl;

    @Autowired
    private IOrgStaffRelV1InnerServiceSMO orgStaffRelV1InnerServiceSMOImpl;

    @Autowired
    private IAccessControlOrgV1InnerServiceSMO accessControlOrgV1InnerServiceSMOImpl;

    @Autowired
    private IAccessControlFaceV1InnerServiceSMO accessControlFaceV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        if (!reqJson.containsKey("storeId")) {
            String storeId = context.getReqHeaders().get("store-id");
            reqJson.put("storeId", storeId);
        }
        //获取数据上下文对象
        Assert.jsonObjectHaveKey(reqJson, "storeId", "请求参数中未包含storeId 节点，请确认");
        //判断员工手机号是否重复(员工可根据手机号登录平台)
        UserDto userDto = new UserDto();
        userDto.setTel(reqJson.getString("tel"));
        userDto.setUserFlag("1");
        userDto.setLevelCd("01"); //员工
        List<UserDto> users = userInnerServiceSMOImpl.queryUsers(userDto);
        Assert.listIsNull(users, "员工手机号不能重复，请重新输入");
    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        String userId = "";

        //String relCd = reqJson.getString("relCd");//员工 组织 岗位

        userId = GenerateCodeFactory.getUserId();
        reqJson.put("userId", userId);
        //todo 添加用户
        addUser(reqJson);
        reqJson.put("userId", userId);

        //todo 添加商户员工关系
        addStaff(reqJson);
        //todo 添加员工组织关系
        //reqJson.put("relCd", relCd);
        addStaffOrg(reqJson);

        //todo 同步人脸至 门禁
        synchronousAccessControl(reqJson);

    }

    /**
     * 添加用户
     *
     * @param paramObj
     */
    public void addUser(JSONObject paramObj) {

        //校验json 格式中是否包含 name,email,levelCd,tel
        Assert.jsonObjectHaveKey(paramObj, "name", "请求参数中未包含name 节点，请确认");
        //Assert.jsonObjectHaveKey(paramObj,"email","请求参数中未包含email 节点，请确认");
        Assert.jsonObjectHaveKey(paramObj, "tel", "请求参数中未包含tel 节点，请确认");
        Assert.jsonObjectHaveKey(paramObj, "address", "请求报文格式错误或未包含地址信息");
        Assert.jsonObjectHaveKey(paramObj, "sex", "请求报文格式错误或未包含性别信息");


        if (paramObj.containsKey("email") && !StringUtil.isEmpty(paramObj.getString("email"))) {
            Assert.isEmail(paramObj, "email", "不是有效的邮箱格式");
        }


        UserPo userPo = BeanConvertUtil.covertBean(refreshParamIn(paramObj), UserPo.class);
        userPo.setFaceUrl(paramObj.getString("photo"));
        int flag = userV1InnerServiceSMOImpl.saveUser(userPo);

        if (flag < 1) {
            throw new CmdException("保存用户异常");
        }
    }

    /**
     * 对请求报文处理
     *
     * @param paramObj
     * @return
     */
    private JSONObject refreshParamIn(JSONObject paramObj) {
        //paramObj.put("userId","-1");
        paramObj.put("levelCd", UserDto.LEVEL_CD_STAFF);
        //设置默认密码
        String staffDefaultPassword = STAFF_DEFAULT_PASSWORD;
        staffDefaultPassword = AuthenticationFactory.passwdMd5(staffDefaultPassword);
        paramObj.put("password", staffDefaultPassword);
        return paramObj;
    }

    /**
     * 添加员工
     *
     * @param paramInJson
     * @return
     */
    public void addStaff(JSONObject paramInJson) {

        JSONObject businessStoreUser = new JSONObject();
        businessStoreUser.put("storeId", paramInJson.getString("storeId"));
        businessStoreUser.put("storeStaffId", GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_storeUserId));
        businessStoreUser.put("staffId", paramInJson.getString("userId"));
        businessStoreUser.put("staffName", paramInJson.getString("name"));
        businessStoreUser.put("tel", paramInJson.getString("tel"));
        businessStoreUser.put("adminFlag", paramInJson.getString("adminFlag"));

        StoreStaffPo storeUserPo = BeanConvertUtil.covertBean(businessStoreUser, StoreStaffPo.class);
        int flag = storeUserV1InnerServiceSMOImpl.saveStoreStaff(storeUserPo);

        if (flag < 1) {
            throw new CmdException("保存员工 失败");
        }
    }

    public void addStaffOrg(JSONObject paramInJson) {

        if (!paramInJson.containsKey("orgId") || StringUtil.isEmpty(paramInJson.getString("orgId"))) {
            return;
        }

        JSONObject businessOrgStaffRel = new JSONObject();
        businessOrgStaffRel.put("relId", GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_relId));
        businessOrgStaffRel.put("storeId", paramInJson.getString("storeId"));
        businessOrgStaffRel.put("staffId", paramInJson.getString("userId"));
        businessOrgStaffRel.put("orgId", paramInJson.getString("orgId"));
        businessOrgStaffRel.put("relCd", paramInJson.getString("relCd"));
        OrgStaffRelPo orgStaffRelPo = BeanConvertUtil.covertBean(businessOrgStaffRel, OrgStaffRelPo.class);
        int flag = orgStaffRelV1InnerServiceSMOImpl.saveOrgStaffRel(orgStaffRelPo);
        if (flag < 1) {
            throw new CmdException("保存员工 失败");
        }
    }

    private void synchronousAccessControl(JSONObject reqJson) {

        if (!reqJson.containsKey("orgId") || StringUtil.isEmpty(reqJson.getString("orgId"))) {
            return;
        }


        AccessControlOrgDto accessControlOrgDto = new AccessControlOrgDto();
        accessControlOrgDto.setOrgId(reqJson.getString("orgId"));
        List<AccessControlOrgDto> accessControlOrgDtos = accessControlOrgV1InnerServiceSMOImpl.queryAccessControlOrgs(accessControlOrgDto);
        if (ListUtil.isNull(accessControlOrgDtos)) {
            return;
        }

        //todo 查询人员信息
        UserDto userDto = new UserDto();
        userDto.setUserId(reqJson.getString("userId"));
        List<UserDto> userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);
        if (ListUtil.isNull(userDtos)) {
            return;
        }
        userDto = userDtos.get(0);

        //todo 查询业主照片
        String imgUrl = MappingCache.getValue(MappingConstant.FILE_DOMAIN, "IMG_PATH");


        AccessControlFacePo accessControlFacePo = null;
        for (AccessControlOrgDto tmpAccessControlOrgDto : accessControlOrgDtos) {
            accessControlFacePo = new AccessControlFacePo();
            accessControlFacePo.setFacePath(imgUrl + userDto.getFaceUrl());
            accessControlFacePo.setCommunityId(tmpAccessControlOrgDto.getCommunityId());
            accessControlFacePo.setIdNumber("");
            accessControlFacePo.setComeId(tmpAccessControlOrgDto.getAcoId());
            accessControlFacePo.setComeType(AccessControlFaceDto.COME_TYPE_STAFF);
            accessControlFacePo.setEndTime(DateUtil.getLastTime());
            accessControlFacePo.setStartTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_B));
            accessControlFacePo.setCardNumber("");
            accessControlFacePo.setMachineId(tmpAccessControlOrgDto.getMachineId());
            accessControlFacePo.setMessage("待下发到门禁");
            accessControlFacePo.setMfId(GenerateCodeFactory.getGeneratorId("11"));
            accessControlFacePo.setName(userDto.getName());
            accessControlFacePo.setPersonId(userDto.getUserId());
            accessControlFacePo.setPersonType(AccessControlFaceDto.PERSON_TYPE_STAFF);
            accessControlFacePo.setState(AccessControlFaceDto.STATE_WAIT);
            accessControlFacePo.setRoomName("无");
            accessControlFaceV1InnerServiceSMOImpl.saveAccessControlFace(accessControlFacePo);
        }


    }
}
