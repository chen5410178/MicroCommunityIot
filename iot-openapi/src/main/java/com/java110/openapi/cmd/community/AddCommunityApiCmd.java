package com.java110.openapi.cmd.community;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.bean.dto.org.OrgStaffRelDto;
import com.java110.bean.po.org.OrgPo;
import com.java110.bean.po.org.OrgStaffRelPo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cache.MappingCache;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.constant.MappingConstant;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.AuthenticationFactory;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.CmdContextUtils;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.dto.community.CommunityDto;
import com.java110.dto.communityMember.CommunityMemberDto;
import com.java110.dto.menuGroup.MenuGroupDto;
import com.java110.dto.store.StoreDto;
import com.java110.dto.storeStaff.StoreStaffDto;
import com.java110.dto.user.UserDto;
import com.java110.intf.community.ICommunityMemberV1InnerServiceSMO;
import com.java110.intf.community.ICommunityV1InnerServiceSMO;
import com.java110.intf.user.*;
import com.java110.po.community.CommunityPo;
import com.java110.po.communityMember.CommunityMemberPo;
import com.java110.po.menuGroupCommunity.MenuGroupCommunityPo;
import com.java110.po.privilegeUser.PrivilegeUserPo;
import com.java110.po.store.StorePo;
import com.java110.po.storeStaff.StoreStaffPo;
import com.java110.po.user.UserPo;
import org.springframework.aop.scope.ScopedObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * 同步小区接口
 * <p>
 * 协议 请查看 《三方系统对接HC物联网系统协议1.0》下的 2.1小区
 */
@Java110Cmd(serviceCode = "community.addCommunityApi")
public class AddCommunityApiCmd extends Cmd {

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Autowired
    private IStoreV1InnerServiceSMO storeV1InnerServiceSMOImpl;

    @Autowired
    private ICommunityV1InnerServiceSMO communityV1InnerServiceSMOImpl;

    @Autowired
    private ICommunityMemberV1InnerServiceSMO communityMemberV1InnerServiceSMOImpl;

    @Autowired
    private IStoreStaffV1InnerServiceSMO storeUserV1InnerServiceSMOImpl;

    @Autowired
    private IOrgV1InnerServiceSMO orgV1InnerServiceSMOImpl;

    @Autowired
    private IOrgStaffRelV1InnerServiceSMO orgStaffRelV1InnerServiceSMOImpl;


    @Autowired
    private IPrivilegeUserV1InnerServiceSMO privilegeUserV1InnerServiceSMOImpl;

    @Autowired
    private IMenuGroupV1InnerServiceSMO menuGroupV1InnerServiceSMOImpl;

    @Autowired
    private IMenuGroupCommunityV1InnerServiceSMO menuGroupCommunityV1InnerServiceSMOImpl;


    public static final String CODE_PREFIX_ID = "10";

    /**
     * communityId	string	30	是	小区ID
     * name	string	64	是	小区名称
     * address	String	200	是	小区地址
     * cityCode	String	12	是	城市编码请查看city_area表
     * tel	String	11	是	手机号
     * storeId	string	30	是	物业公司ID
     * storeName	String	64	是	物业公司名称
     */
    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {


        Assert.hasKeyAndValue(reqJson, "communityId", "未包含communityId");
        Assert.hasKeyAndValue(reqJson, "name", "未包含name");
        Assert.hasKeyAndValue(reqJson, "address", "未包含address");
        Assert.hasKeyAndValue(reqJson, "cityCode", "未包含cityCode");
        Assert.hasKeyAndValue(reqJson, "tel", "未包含tel");
        Assert.hasKeyAndValue(reqJson, "storeId", "未包含storeId");
        Assert.hasKeyAndValue(reqJson, "storeName", "未包含storeName");

        // todo 只有运营团队的账号才能操作接口

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


        //todo 小区数据处理
        doCommunity(reqJson, context);


        //todo 物业数据处理
        doProperty(reqJson, context);

    }

    private void doProperty(JSONObject reqJson, ICmdDataFlowContext context) {


        StoreDto storeDto = new StoreDto();
        storeDto.setStoreId(reqJson.getString("storeId"));

        List<StoreDto> storeDtos = storeV1InnerServiceSMOImpl.queryStores(storeDto);

        StorePo storePo = new StorePo();
        storePo.setCreateUserId(CmdContextUtils.getUserId(context));
        storePo.setAddress(reqJson.getString("address"));
        storePo.setStoreTypeCd(StoreDto.STORE_TYPE_PROPERTY);
        storePo.setTel(reqJson.getString("tel"));
        storePo.setName(reqJson.getString("storeName"));
        storePo.setState(StoreDto.STATE_NORMAL);
        storePo.setStoreId(reqJson.getString("storeId"));

        if (!ListUtil.isNull(storeDtos)) {
            storeV1InnerServiceSMOImpl.updateStore(storePo);
            return;
        }

        storeV1InnerServiceSMOImpl.saveStore(storePo);

        //添加用户
        UserPo userPo = new UserPo();
        userPo.setTel(reqJson.getString("tel"));
        userPo.setName(reqJson.getString("storeName"));
        userPo.setAddress(reqJson.getString("address"));
        userPo.setUserId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_userId));
        userPo.setEmail("无");
        userPo.setLevelCd(UserDto.LEVEL_CD_ADMIN);
        userPo.setSex("1");
        userPo.setPassword(AuthenticationFactory.passwdMd5(reqJson.getString("tel")));
        int flag = userV1InnerServiceSMOImpl.saveUser(userPo);
        if (flag < 1) {
            throw new CmdException("注册失败");
        }

        //保存 商户和用户的关系
        StoreStaffPo storeUserPo = new StoreStaffPo();
        storeUserPo.setStoreStaffId(GenerateCodeFactory.getGeneratorId(CODE_PREFIX_ID));
        storeUserPo.setAdminFlag(StoreStaffDto.ADMIN_FLAG_Y);

        storeUserPo.setStoreId(storePo.getStoreId());
        storeUserPo.setStaffId(userPo.getUserId());
        storeUserPo.setTel(reqJson.getString("tel"));
        storeUserPo.setStaffName(reqJson.getString("name"));
        flag = storeUserV1InnerServiceSMOImpl.saveStoreStaff(storeUserPo);
        if (flag < 1) {
            throw new CmdException("注册失败");
        }

        //保存公司级组织
        OrgPo orgPo = new OrgPo();
        orgPo.setOrgName(storePo.getName());
        orgPo.setOrgLevel("1");
        orgPo.setOrgId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_orgId));
        orgPo.setAllowOperation("F");
        orgPo.setBelongCommunityId("9999");
        orgPo.setParentOrgId("-1");
        orgPo.setStoreId(storePo.getStoreId());

        flag = orgV1InnerServiceSMOImpl.saveOrg(orgPo);
        if (flag < 1) {
            throw new CmdException("注册失败");
        }


        //添加组织 员工关系
        OrgStaffRelPo orgStaffRelPo = new OrgStaffRelPo();
        orgStaffRelPo.setOrgId(orgPo.getOrgId());
        orgStaffRelPo.setStaffId(userPo.getUserId());
        orgStaffRelPo.setRelId(GenerateCodeFactory.getGeneratorId(CODE_PREFIX_ID));
        orgStaffRelPo.setRelCd(OrgStaffRelDto.REL_CD_ADMIN);
        orgStaffRelPo.setStoreId(storePo.getStoreId());
        flag = orgStaffRelV1InnerServiceSMOImpl.saveOrgStaffRel(orgStaffRelPo);
        if (flag < 1) {
            throw new CmdException("注册失败");
        }


        String defaultPrivilege = MappingCache.getValue(MappingConstant.DOMAIN_DEFAULT_PRIVILEGE_ADMIN, StoreDto.STORE_TYPE_PROPERTY);

        Assert.hasLength(defaultPrivilege, "未配置物业默认权限");
        PrivilegeUserPo privilegeUserPo = new PrivilegeUserPo();
        privilegeUserPo.setPrivilegeFlag("1");
        privilegeUserPo.setStoreId(storePo.getStoreId());
        privilegeUserPo.setUserId(userPo.getUserId());
        privilegeUserPo.setpId(defaultPrivilege);
        privilegeUserPo.setPuId(GenerateCodeFactory.getGeneratorId(CODE_PREFIX_ID));

        flag = privilegeUserV1InnerServiceSMOImpl.savePrivilegeUser(privilegeUserPo);
        if (flag < 1) {
            throw new CmdException("注册失败");
        }

    }

    /**
     * 添加小区
     *
     * @param reqJson
     * @param context
     */
    private void doCommunity(JSONObject reqJson, ICmdDataFlowContext context) {

        //todo 查询小区是否存在
        CommunityDto communityDto = new CommunityDto();
        communityDto.setCommunityId(reqJson.getString("communityId"));
        List<CommunityDto> communityDtos = communityV1InnerServiceSMOImpl.queryCommunitys(communityDto);

        CommunityPo communityPo = new CommunityPo();
        communityPo.setCommunityId(reqJson.getString("communityId"));
        communityPo.setExtCommunityId(reqJson.getString("communityId"));
        communityPo.setName(reqJson.getString("name"));
        communityPo.setAddress(reqJson.getString("address"));
        communityPo.setCityCode(reqJson.getString("cityCode"));
        communityPo.setTel(reqJson.getString("tel"));
        if (ListUtil.isNull(communityDtos)) {
            communityV1InnerServiceSMOImpl.saveCommunity(communityPo);
            saveMenuGroupCommunity(communityPo);
        } else {
            communityV1InnerServiceSMOImpl.updateCommunity(communityPo);
        }

        //todo 查询小区成员是否存在
        CommunityMemberDto communityMemberDto = new CommunityMemberDto();
        communityMemberDto.setCommunityId(reqJson.getString("communityId"));
        communityMemberDto.setMemberId(reqJson.getString("storeId"));
        List<CommunityMemberDto> communityMemberDtos = communityMemberV1InnerServiceSMOImpl.queryCommunityMembers(communityMemberDto);

        CommunityMemberPo communityMemberPo = new CommunityMemberPo();
        communityMemberPo.setCommunityId(reqJson.getString("communityId"));
        communityMemberPo.setMemberId(reqJson.getString("storeId"));
        communityMemberPo.setEndTime(DateUtil.getLastTime());
        communityMemberPo.setStartTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        communityMemberPo.setMemberTypeCd(CommunityMemberDto.MEMBER_TYPE_PROPERTY);
        communityMemberPo.setMemberName(reqJson.getString("storeName"));

        if (ListUtil.isNull(communityMemberDtos)) {
            communityMemberPo.setCommunityMemberId(GenerateCodeFactory.getGeneratorId("11"));
            communityMemberV1InnerServiceSMOImpl.saveCommunityMember(communityMemberPo);
        } else {
            communityMemberPo.setCommunityMemberId(communityMemberDtos.get(0).getCommunityMemberId());
            communityMemberV1InnerServiceSMOImpl.updateCommunityMember(communityMemberPo);
        }
    }


    /**
     * 分配小区 权限功能
     *
     * @param communityPo
     */
    private void saveMenuGroupCommunity(CommunityPo communityPo) {
        List<MenuGroupDto> menuGroupDtos = null;
        MenuGroupDto menuGroupDto = null;
        menuGroupDto = new MenuGroupDto();
        menuGroupDto.setStoreType(StoreDto.STORE_TYPE_PROPERTY);
        menuGroupDtos = menuGroupV1InnerServiceSMOImpl.queryMenuGroups(menuGroupDto);


        List<MenuGroupCommunityPo> menuGroupCommunityPos = new ArrayList<>();
        MenuGroupCommunityPo tmpMenuGroupCommunityPo = null;
        for (MenuGroupDto tmpMenuGroupDto : menuGroupDtos) {
            tmpMenuGroupCommunityPo = new MenuGroupCommunityPo();
            tmpMenuGroupCommunityPo.setCommunityId(communityPo.getCommunityId());
            tmpMenuGroupCommunityPo.setCommunityName(communityPo.getName());
            tmpMenuGroupCommunityPo.setGcId(GenerateCodeFactory.getGeneratorId(CODE_PREFIX_ID));
            tmpMenuGroupCommunityPo.setgId(tmpMenuGroupDto.getgId());
            tmpMenuGroupCommunityPo.setName(tmpMenuGroupDto.getName());
            menuGroupCommunityPos.add(tmpMenuGroupCommunityPo);
        }
        int flag = menuGroupCommunityV1InnerServiceSMOImpl.saveMenuGroupCommunitys(menuGroupCommunityPos);
        if (flag < 1) {
            throw new CmdException("注册失败");
        }
    }
}
