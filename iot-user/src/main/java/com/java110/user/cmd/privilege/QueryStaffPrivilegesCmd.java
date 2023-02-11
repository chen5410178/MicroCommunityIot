package com.java110.user.cmd.privilege;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.StringUtil;
import com.java110.dto.privilegeUser.PrivilegeUserDto;
import com.java110.dto.store.StoreDto;
import com.java110.intf.user.IPrivilegeUserV1InnerServiceSMO;
import com.java110.intf.user.IStoreV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * 查询员工权限
 */
@Java110Cmd(serviceCode = "privilege.queryStaffPrivileges")
public class QueryStaffPrivilegesCmd extends Cmd {

    @Autowired
    private IStoreV1InnerServiceSMO storeV1InnerServiceSMOImpl;

    @Autowired
    private IPrivilegeUserV1InnerServiceSMO privilegeUserV1InnerServiceSMOImpl;


    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        String userId = context.getReqHeaders().get("user-id");
        //String storeId = context.getReqHeaders().get("store-id");

        if (StringUtil.isEmpty(userId)) {
            userId = reqJson.getString("userId");
        }

        Assert.hasLength(userId, "未包含用户");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {

        String userId = context.getReqHeaders().get("user-id");
        String storeId = context.getReqHeaders().get("store-id");

        if (StringUtil.isEmpty(userId)) {
            userId = reqJson.getString("userId");
        }

        if (reqJson.containsKey("staffId") && !StringUtil.isEmpty(reqJson.getString("staffId"))) {
            userId = reqJson.getString("staffId");
        }

        String domain = "";
        if (!reqJson.containsKey("domain") || StringUtil.isEmpty(reqJson.getString("domain"))) {

            StoreDto storeDto = new StoreDto();
            storeDto.setStoreId(storeId);
            storeDto.setPage(1);
            storeDto.setRow(1);
            List<StoreDto> storeDtos = storeV1InnerServiceSMOImpl.queryStores(storeDto);

            Assert.listOnlyOne(storeDtos, "商户不存在");
            domain = storeDtos.get(0).getStoreTypeCd();
        } else {
            domain = reqJson.getString("domain");
        }

        PrivilegeUserDto privilegeUserDto = new PrivilegeUserDto();
        privilegeUserDto.setDomain(domain);
        privilegeUserDto.setUserId(userId);
        List<PrivilegeUserDto> privilegeUserDtos = privilegeUserV1InnerServiceSMOImpl.getUserPrivileges(privilegeUserDto);

        JSONArray privileges = JSONArray.parseArray(JSONArray.toJSONString(privilegeUserDtos));

        JSONArray tmpPrivilegeArrays = new JSONArray();

        JSONObject privilegeObj = null;
        for (int privilegeIndex = 0; privilegeIndex < privileges.size(); privilegeIndex++) {
            privilegeObj = privileges.getJSONObject(privilegeIndex);
            hasSameData(privilegeObj, tmpPrivilegeArrays);
        }

        JSONObject resObj = new JSONObject();
        resObj.put("datas", privileges);
        context.setResponseEntity(new ResponseEntity<String>(resObj.toJSONString(), HttpStatus.OK));
    }

    private void hasSameData(JSONObject privilegeObj, JSONArray tmpPrivilegeArrays) {
        JSONObject tmpPrivilegeObj = null;
        for (int tmpPrivilegeIndex = 0; tmpPrivilegeIndex < tmpPrivilegeArrays.size(); tmpPrivilegeIndex++) {
            tmpPrivilegeObj = tmpPrivilegeArrays.getJSONObject(tmpPrivilegeIndex);
            if (privilegeObj.getString("pId").equals(tmpPrivilegeObj.getString("pId"))) {
                if (!StringUtil.isEmpty(privilegeObj.getString("pgId"))) {
                    tmpPrivilegeArrays.remove(tmpPrivilegeIndex);
                    tmpPrivilegeArrays.add(privilegeObj);
                }
                return;
            }
        }
        tmpPrivilegeArrays.add(privilegeObj);
    }
}
