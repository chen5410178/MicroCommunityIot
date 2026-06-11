package com.java110.hal.http.yiteSmart.bmo.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.cache.CommonCache;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.StringUtil;
import com.java110.hal.http.yiteSmart.bmo.IManagerSignVerifyBmo;
import com.java110.hal.http.yiteSmart.util.YiteSignUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
public class ManagerSignVerifyBmoImpl implements IManagerSignVerifyBmo {

    @Override
    public ResponseEntity<String> verify(HttpServletRequest request) {
        String uuid = request.getParameter("uuid");
        if (StringUtil.isEmpty(uuid)) {
            return buildErrorResponse("缺少设备ID", 202);
        }

        String token = CommonCache.getValue("YITESMART_MANAGER_TOKEN_" + uuid);
        if (StringUtil.isEmpty(token)) {
            return buildErrorResponse("登录token失效", 1002);
        }

        String expire = request.getParameter("expire");
        if (!StringUtil.isEmpty(expire)) {
            long expireTime = Long.parseLong(expire);
            long now = System.currentTimeMillis() / 1000;
            if (now > expireTime) {
                return buildErrorResponse("登录token失效", 1002);
            }
        }

        String sign = request.getParameter("sign");
        if (StringUtil.isEmpty(sign)) {
            return buildErrorResponse("缺少签名参数", 1001);
        }

        TreeMap<String, String> params = new TreeMap<>();
        Enumeration<String> pns = request.getParameterNames();
        while (pns.hasMoreElements()) {
            String k = pns.nextElement();
            if (!"sign".equals(k)) {
                params.put(k, request.getParameter(k));
            }
        }

        String paramJsonStr = YiteSignUtil.linkMapKeyValues(params, ":");
        String expectedSign = YiteSignUtil.genHMAC(paramJsonStr, token);

        if (expectedSign.equals(sign)) {
            String signUsedKey = "YITESMART_SIGN_USED_" + sign;
            String used = CommonCache.getValue(signUsedKey);
            if (!StringUtil.isEmpty(used)) {
                return buildErrorResponse("请求已过期", 1002);
            }
            int expireSec = 5 * 60;
            if (!StringUtil.isEmpty(expire)) {
                long expireTime = Long.parseLong(expire);
                long now = System.currentTimeMillis() / 1000;
                expireSec = (int) (expireTime - now);
                if (expireSec < 60) expireSec = 60;
            }
            CommonCache.setValue(signUsedKey, "1", expireSec);
            return null;
        }

        return buildErrorResponse("签名校验失败", 1002);
    }

    private ResponseEntity<String> buildErrorResponse(String msg, int code) {
        JSONObject result = new JSONObject();
        result.put("code", code);
        result.put("msg", msg);
        result.put("timestamp", DateUtil.getCurrentDate().getTime() / 1000);
        return new ResponseEntity<>(result.toJSONString(), HttpStatus.OK);
    }
}
