package com.java110.core.utils;

import com.java110.core.constant.CommonConstant;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * 获取请求中的token
 */
public class SessionTokenUtil {

    /**
     * 获取TOKEN
     *
     * @param request
     * @return
     */
    public static String getToken(HttpServletRequest request) throws IllegalArgumentException {
        String token = "";
        if (request.getCookies() != null && request.getCookies().length > 0) {
            for (Cookie cookie : request.getCookies()) {
                if (CommonConstant.COOKIE_AUTH_TOKEN.equals(cookie.getName())) {
                    token = cookie.getValue();
                }
            }
        }

        String authorization = request.getHeader("Authorization");
//StringUtil.isEmpty(token) &&
        if (!StringUtil.isEmpty(authorization)) {
            token = authorization.substring("Bearer ".length());
        }

        if (StringUtil.isNullOrNone(token)) {
            throw new IllegalArgumentException("您还没有登录，请先登录");
        }
        return token;
    }
}
