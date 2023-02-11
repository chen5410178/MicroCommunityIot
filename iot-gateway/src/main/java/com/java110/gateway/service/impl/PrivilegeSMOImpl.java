package com.java110.gateway.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.bean.dto.basePrivilege.BasePrivilegeDto;
import com.java110.core.cache.PrivilegeCache;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.utils.RequestUtils;
import com.java110.core.utils.StringUtil;
import com.java110.gateway.configuration.ServiceConfiguration;
import com.java110.gateway.service.IPrivilegeSMO;
import com.java110.intf.user.IMenuV1InnerServiceSMO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * 用户权限处理类
 * Created by Administrator on 2019/4/1.
 */

@Service("privilegeSMOImpl")
public class PrivilegeSMOImpl implements IPrivilegeSMO {

    private final static Logger logger = LoggerFactory.getLogger(PrivilegeSMOImpl.class);

    @Autowired
    private IMenuV1InnerServiceSMO menuV1InnerServiceSMOImpl;


    public void hasPrivilege(String resource, String userId) {

        //如果不需要登录的 跳过
        String[] ignorePath = ServiceConfiguration.exclusions.toString().split(",");
        if (RequestUtils.isExcludedUri(ignorePath, resource)) {
            return;
        }

        //没有用户的情况下不做权限判断
        if (StringUtil.isEmpty(userId)) {
            return;
        }
        JSONObject paramIn = new JSONObject();
        //paramIn.put("resource", resource);
        paramIn.put("userId", userId);

        //校验资源路劲是否定义权限
        List<BasePrivilegeDto> basePrivilegeDtos = PrivilegeCache.getPrivileges();
        if (basePrivilegeDtos == null || basePrivilegeDtos.size() < 1) {
            return;
        }
        boolean hasPrivilege = false;
        for (BasePrivilegeDto privilegeDto : basePrivilegeDtos) {
            if (resource.equals(privilegeDto.getResource())) {
                hasPrivilege = true;
            }
        }
        if (!hasPrivilege) { //权限没有配置，直接跳过
            return;
        }

        BasePrivilegeDto basePrivilegeDto = new BasePrivilegeDto();
        basePrivilegeDto.setResource(resource);
        basePrivilegeDto.setUserId(userId);
        List<Map> privileges = menuV1InnerServiceSMOImpl.checkUserHasResource(basePrivilegeDto);

        if (privileges == null || privileges.size() < 1) {
            throw new UnsupportedOperationException("用户没有权限操作");
        }

        hasPrivilege = false;
        for (int privilegeIndex = 0; privilegeIndex < privileges.size(); privilegeIndex++) {
            if (resource.equals(privileges.get(privilegeIndex).get("resource"))) {
                hasPrivilege = true;
                break;
            }
        }
        if (!hasPrivilege) {
            throw new UnsupportedOperationException("用户没有权限操作");
        }

    }


}
