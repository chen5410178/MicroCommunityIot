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
import com.java110.core.utils.StringUtil;
import com.java110.dto.store.StoreDto;
import com.java110.dto.user.UserDto;
import com.java110.intf.user.IStoreV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * 查询登录用户信息
 */
@Java110Cmd(serviceCode = "user.getUserInfo")
public class GetUserInfoCmd extends Cmd {


    @Autowired
    private IStoreV1InnerServiceSMO storeV1InnerServiceSMOImpl;

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {

    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        String userId = context.getReqHeaders().get("user-id");
        String storeId = context.getReqHeaders().get("store-id");

        UserDto userDto = new UserDto();
        userDto.setUserId(userId);
        List<UserDto> userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);

        Assert.listOnlyOne(userDtos, "用户不存在");
        JSONObject tmpUserInfo = JSONObject.parseObject(JSONObject.toJSONString(userDtos.get(0)));
        JSONObject resultUserInfo = new JSONObject();


        if (!StringUtil.isEmpty(storeId)) {
            StoreDto storeDto = new StoreDto();
            storeDto.setStoreId(storeId);
            storeDto.setPage(1);
            storeDto.setRow(1);
            List<StoreDto> storeDtos = storeV1InnerServiceSMOImpl.queryStores(storeDto);
            if (storeDtos != null && storeDtos.size() > 0) {
                resultUserInfo.put("storeTypeCd", storeDtos.get(0).getStoreTypeCd());
            }
        }

        resultUserInfo.put("name", tmpUserInfo.getString("name"));
        resultUserInfo.put("address", tmpUserInfo.getString("address"));
        resultUserInfo.put("sex", tmpUserInfo.getString("sex"));
        resultUserInfo.put("localtionCd", tmpUserInfo.getString("localtionCd"));
        resultUserInfo.put("levelCd", tmpUserInfo.getString("levelCd"));
        //resultUserInfo.put("tel", CommonUtil.mobileEncrypt(tmpUserInfo.getString("tel")));
        resultUserInfo.put("tel", tmpUserInfo.getString("tel")); // 这里不加密了 因为前台很多地方直接 关联出 用户的手机号 所以 加密了 没法处理 modify by wuxw 2022-07-04
        resultUserInfo.put("email", tmpUserInfo.getString("email"));
        resultUserInfo.put("userId", tmpUserInfo.getString("userId"));
        String watermark = MappingCache.getValue(MappingConstant.DOMAIN_SYSTEM_SWITCH, "watermark");
        resultUserInfo.put("watermark", watermark);

        ResponseEntity responseEntity = new ResponseEntity<String>(resultUserInfo.toJSONString(), HttpStatus.OK);

        context.setResponseEntity(responseEntity);
    }
}
