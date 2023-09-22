package com.java110.user.cmd.user;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.roleCommunity.RoleCommunityDto;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.community.CommunityDto;
import com.java110.dto.user.UserDto;
import com.java110.intf.user.IOrgV1InnerServiceSMO;
import com.java110.intf.user.IRoleCommunityV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Java110Cmd(serviceCode = "user.listCommunityStaffsByCommunityId")
public class ListCommunityStaffsByCommunityIdCmd extends Cmd {

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        Assert.hasKeyAndValue(reqJson, "communityId", "未包含 小区");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        Map map = new HashMap();
        map.put("communityId", reqJson.getString("communityId"));

        List<UserDto> userDtos = userV1InnerServiceSMOImpl.queryCommunityStaffsByCommunityId(map);
        if (CollectionUtils.isEmpty(userDtos)) {
            userDtos = new ArrayList<>();
        }

        ResultVo resultVo = new ResultVo(1, userDtos.size(), userDtos);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        context.setResponseEntity(responseEntity);

    }
}
