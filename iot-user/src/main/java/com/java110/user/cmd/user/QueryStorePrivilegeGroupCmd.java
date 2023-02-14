package com.java110.user.cmd.user;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.StringUtil;
import com.java110.dto.privilegeGroup.PrivilegeGroupDto;
import com.java110.dto.store.StoreDto;
import com.java110.dto.user.UserDto;
import com.java110.intf.user.IPrivilegeGroupV1InnerServiceSMO;
import com.java110.intf.user.IStoreV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Java110Cmd(serviceCode = "query.store.privilegeGroup")
public class QueryStorePrivilegeGroupCmd extends Cmd {

    @Autowired
    private IStoreV1InnerServiceSMO storeV1InnerServiceSMOImpl;

//    @Autowired
//    private IQueryServiceSMO queryServiceSMOImpl;

    @Autowired
    private IPrivilegeGroupV1InnerServiceSMO privilegeGroupV1InnerServiceSMOImpl;

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;



    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {

    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        String userId = context.getReqHeaders().get("user-id");
        String storeId = context.getReqHeaders().get("store-id");
        String communityId = context.getReqHeaders().get("communityId");

        if(StringUtil.isEmpty(userId)){
            userId = reqJson.getString("userId");
        }
        // 判断是不是管理员，管理员反馈 物业 的所角色
        UserDto userDto = new UserDto();
        userDto.setUserId(userId);
        userDto.setPage(1);
        userDto.setRow(1);
        List<UserDto> userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);
        Assert.listOnlyOne(userDtos, "用户不存在");
        String storeTypeCd = "";
        if(!reqJson.containsKey("storeTypeCd") || StringUtil.isEmpty(reqJson.getString("storeTypeCd"))) {
            StoreDto storeDto = new StoreDto();
            storeDto.setStoreId(storeId);
            storeDto.setPage(1);
            storeDto.setRow(1);
            List<StoreDto> storeDtos = storeV1InnerServiceSMOImpl.queryStores(storeDto);
            Assert.listOnlyOne(storeDtos, "商户不存在");
            storeTypeCd = storeDtos.get(0).getStoreTypeCd();
        }else{
            storeTypeCd = reqJson.getString("storeTypeCd");
        }

        PrivilegeGroupDto privilegeGroupDto = new PrivilegeGroupDto();
        privilegeGroupDto.setStoreId(storeId);
        privilegeGroupDto.setStoreTypeCd(storeTypeCd);
        privilegeGroupDto.setDomain(storeTypeCd);
       List<PrivilegeGroupDto> privilegeGroupDtos =  privilegeGroupV1InnerServiceSMOImpl.queryPrivilegeGroups(privilegeGroupDto);


        JSONArray privilegeGroups = JSONArray.parseArray(JSONArray.toJSONString(privilegeGroupDtos));

        context.setResponseEntity(new ResponseEntity<String>(privilegeGroups.toJSONString(), HttpStatus.OK));
    }
}
