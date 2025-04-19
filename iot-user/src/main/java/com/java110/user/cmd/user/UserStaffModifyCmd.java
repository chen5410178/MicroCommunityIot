package com.java110.user.cmd.user;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.dto.org.OrgStaffRelDto;
import com.java110.bean.po.org.OrgStaffRelPo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.annotation.Java110Transactional;
import com.java110.core.cache.MappingCache;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.constant.MappingConstant;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.*;
import com.java110.doc.annotation.*;
import com.java110.dto.accessControlFace.AccessControlFaceDto;
import com.java110.dto.accessControlOrg.AccessControlOrgDto;
import com.java110.dto.storeStaff.StoreStaffDto;
import com.java110.dto.user.UserDto;
import com.java110.intf.accessControl.IAccessControlFaceV1InnerServiceSMO;
import com.java110.intf.accessControl.IAccessControlOrgV1InnerServiceSMO;
import com.java110.intf.user.IOrgStaffRelInnerServiceSMO;
import com.java110.intf.user.IOrgStaffRelV1InnerServiceSMO;
import com.java110.intf.user.IStoreStaffV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import com.java110.po.accessControlFace.AccessControlFacePo;
import com.java110.po.storeStaff.StoreStaffPo;
import com.java110.po.user.UserPo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@Java110CmdDoc(title = "修改员工",
        description = "外部系统通过修改员工接口 修改员工，注意需要物业管理员账号登录，因为不需要传storeId 是根据管理员登录信息获取的",
        httpMethod = "post",
        url = "http://{ip}:{port}/app/user.staff.modify",
        resource = "userDoc",
        author = "吴学文",
        serviceCode = "user.staff.modify"
)

@Java110ParamsDoc(params = {
        @Java110ParamDoc(name = "userId", length = 30, remark = "员工ID"),
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
        reqBody="{\"userId\":\"123123\",\"orgId\":\"102022091988250052\",\"orgName\":\"演示物业 / 件部\",\"username\":\"张三\",\"sex\":\"0\",\"email\":\"231@qq.com\",\"tel\":\"123\",\"address\":\"123\",\"relCd\":\"1000\",\"photo\":\"\",\"name\":\"张三\"}",
        resBody="{'code':0,'msg':'成功'"
)

@Java110Cmd(serviceCode = "user.staff.modify")
public class UserStaffModifyCmd extends Cmd {

//    @Autowired
//    private IFileInnerServiceSMO fileInnerServiceSMOImpl;
//
//    @Autowired
//    private IFileRelInnerServiceSMO fileRelInnerServiceSMOImpl;

    @Autowired
    private IUserV1InnerServiceSMO userInnerServiceSMOImpl;

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Autowired
    private IOrgStaffRelInnerServiceSMO orgStaffRelInnerServiceSMOImpl;

    @Autowired
    private IOrgStaffRelV1InnerServiceSMO orgStaffRelV1InnerServiceSMOImpl;

    @Autowired
    private IStoreStaffV1InnerServiceSMO storeUserV1InnerServiceSMOImpl;

    @Autowired
    private IAccessControlFaceV1InnerServiceSMO accessControlFaceV1InnerServiceSMOImpl;

    @Autowired
    private IAccessControlOrgV1InnerServiceSMO accessControlOrgV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        Assert.jsonObjectHaveKey(reqJson, "staffId", "请求参数中未包含员工 节点，请确认");
        //校验json 格式中是否包含 name,email,levelCd,tel
        Assert.jsonObjectHaveKey(reqJson, "name", "请求参数中未包含name 节点，请确认");
        Assert.jsonObjectHaveKey(reqJson, "tel", "请求参数中未包含tel 节点，请确认");
    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject paramObj) throws CmdException {
        UserPo userPo = BeanConvertUtil.covertBean(paramObj, UserPo.class);
        //根据手机号查询用户
        UserDto userDto = new UserDto();
        userDto.setTel(userPo.getTel());
        userDto.setUserFlag("1");
        userDto.setLevelCd("01"); //员工
        List<UserDto> users = userInnerServiceSMOImpl.queryUsers(userDto);
        if (!ListUtil.isNull(users)) {
            for (UserDto user : users) {
                if (!user.getUserId().equals(userPo.getUserId())) {
                    throw new IllegalArgumentException("员工手机号不能重复，请重新输入");
                }
            }
        }
        userPo.setFaceUrl(paramObj.getString("photo"));
        int flag = userV1InnerServiceSMOImpl.updateUser(userPo);

        //todo 同步门禁
        synchronousAccessControl(paramObj);

        if (flag < 1) {
            throw new CmdException("保存用户异常");
        }
        OrgStaffRelDto orgStaffRelDto = new OrgStaffRelDto();
        orgStaffRelDto.setStaffId(userPo.getUserId());
        List<OrgStaffRelDto> orgStaffRelDtoList = orgStaffRelInnerServiceSMOImpl.queryOrgInfoByStaffIds(orgStaffRelDto);

        if (orgStaffRelDtoList == null || orgStaffRelDtoList.size() < 1) {
            return;
        }
        OrgStaffRelPo orgStaffRelPo = new OrgStaffRelPo();
        orgStaffRelPo.setRelCd(paramObj.getString("relCd"));
        orgStaffRelPo.setRelId(orgStaffRelDtoList.get(0).getRelId());
        orgStaffRelPo.setOrgId(paramObj.getString("orgId"));

        flag = orgStaffRelV1InnerServiceSMOImpl.updateOrgStaffRel(orgStaffRelPo);
        if (flag < 1) {
            throw new CmdException("保存员工 失败");
        }

        StoreStaffDto storeUserDto = new StoreStaffDto();
        storeUserDto.setUserId(userPo.getUserId());
        List<StoreStaffDto> storeUserDtos = storeUserV1InnerServiceSMOImpl.queryStoreStaffs(storeUserDto);

        if (storeUserDtos == null || storeUserDtos.size() < 1) {
            return;
        }
        StoreStaffPo storeUserPo = new StoreStaffPo();
        storeUserPo.setStaffName(userPo.getName());
        storeUserPo.setTel(userPo.getTel());
        storeUserPo.setStoreStaffId(storeUserDtos.get(0).getStoreStaffId());

        flag = storeUserV1InnerServiceSMOImpl.updateStoreStaff(storeUserPo);
        if (flag < 1) {
            throw new CmdException("保存员工 失败");
        }
    }

    private void synchronousAccessControl(JSONObject reqJson) {


        AccessControlFaceDto accessControlFaceDto = new AccessControlFaceDto();
        accessControlFaceDto.setPersonId(reqJson.getString("userId"));
        List<AccessControlFaceDto> accessControlFaceDtos = accessControlFaceV1InnerServiceSMOImpl.queryAccessControlFaces(accessControlFaceDto);
        AccessControlFacePo accessControlFacePo = null;
        String photo  = "";
        if (!ListUtil.isNull(accessControlFaceDtos)) {
            for (AccessControlFaceDto tmpAccessControlFaceDto : accessControlFaceDtos) {
                accessControlFacePo = BeanConvertUtil.covertBean(tmpAccessControlFaceDto, AccessControlFacePo.class);
                accessControlFacePo.setState(AccessControlFaceDto.STATE_WAIT);
                accessControlFacePo.setMessage("待下发到门禁");
                String imgUrl = MappingCache.getValue(MappingConstant.FILE_DOMAIN,"IMG_PATH");
                photo = reqJson.getString("photo");
                if(StringUtil.isEmpty(photo)){
                    continue;
                }
                if(photo.startsWith("http")){
                    accessControlFacePo.setFacePath(reqJson.getString("photo"));
                    continue;
                }
                accessControlFacePo.setFacePath(imgUrl+reqJson.getString("photo"));
            }
            accessControlFaceV1InnerServiceSMOImpl.updateAccessControlFace(accessControlFacePo);
            return;
        }

        OrgStaffRelDto orgStaffRelDto = new OrgStaffRelDto();
        orgStaffRelDto.setStaffId(reqJson.getString("userId"));
        List<OrgStaffRelDto> orgStaffRelDtoList = orgStaffRelInnerServiceSMOImpl.queryOrgInfoByStaffIdsNew(orgStaffRelDto);

        if (ListUtil.isNull(orgStaffRelDtoList)) {
            return;
        }

        AccessControlOrgDto accessControlOrgDto = new AccessControlOrgDto();
        accessControlOrgDto.setOrgId(orgStaffRelDtoList.get(0).getOrgId());
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
