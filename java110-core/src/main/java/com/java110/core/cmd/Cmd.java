package com.java110.core.cmd;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.CmdContextUtils;
import com.java110.dto.store.StoreDto;
import com.java110.dto.user.UserDto;
import com.java110.intf.user.IStoreV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import org.apache.catalina.User;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


public abstract class Cmd implements ServiceCmdListener {

    private static Logger logger = LoggerFactory.getLogger(Cmd.class);

    protected static final int MAX_ROW = 10000;

    @Autowired(required = false)
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;


    @Autowired(required = false)
    private IStoreV1InnerServiceSMO storeV1InnerServiceSMOImpl;

    /**
     * 分页信息校验
     *
     * @param reqJson
     */
    protected void validatePageInfo(JSONObject reqJson) {
        Assert.jsonObjectHaveKey(reqJson, "page", "请求中未包含page信息");
        Assert.jsonObjectHaveKey(reqJson, "row", "请求中未包含row信息");
    }

    /**
     * 查询用户信息
     *
     * @param context
     * @return
     */
    protected UserDto getUserDto(ICmdDataFlowContext context) {
        String userId = CmdContextUtils.getLoginUserId(context);
        Assert.hasLength(userId, "用户未登录");
        if (userId.startsWith("-")) {
            throw new IllegalArgumentException("用户未登录");
        }
        UserDto userDto = new UserDto();
        userDto.setUserId(userId);
        List<UserDto> userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);

        Assert.listOnlyOne(userDtos, "未包含用户");
        return userDtos.get(0);
    }


    /**
     * 查询商户信息
     *
     * @param context
     * @return
     */
    protected StoreDto getStoreDto(ICmdDataFlowContext context) {
        String storeId = CmdContextUtils.getStoreId(context);
        Assert.hasLength(storeId, "未查询到商户");
        if (storeId.startsWith("-")) {
            throw new IllegalArgumentException("未查询到商户");
        }
        StoreDto storeDto = new StoreDto();
        storeDto.setStoreId(storeId);
        List<StoreDto> storeDtos = storeV1InnerServiceSMOImpl.queryStores(storeDto);

        Assert.listOnlyOne(storeDtos, "未包含商户");
        return storeDtos.get(0);
    }


    @Override
    public int getOrder() {
        return 0;
    }

}
