package com.java110.gateway.service.impl;

import com.java110.bean.dto.app.AppRoute;
import com.java110.bean.dto.app.AppService;
import com.java110.core.cache.AppRouteCache;
import com.java110.core.cache.MappingCache;
import com.java110.core.constant.CommonConstant;
import com.java110.core.constant.MappingConstant;
import com.java110.core.constant.StatusConstant;
import com.java110.core.exception.DecryptException;
import com.java110.core.factory.AuthenticationFactory;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.StringUtil;
import com.java110.gateway.service.IAppServiceCheck;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

@Service
public class AppServiceCheckImpl implements IAppServiceCheck {
    public static final String RESULT_CODE_NO_AUTHORITY_ERROR = "1996";
    private static final Logger logger = LoggerFactory.getLogger(AppServiceCheckImpl.class);

    @Override
    public AppService checkAndGetAppService(String reqJson, Map<String, String> headers) {
        if ("POST".equals(headers.get(CommonConstant.HTTP_METHOD))) {
            reqJson = decrypt(reqJson, headers);
        }

        String appId = headers.get(CommonConstant.APP_ID);
        String transactionId = headers.get(CommonConstant.HTTP_TRANSACTION_ID);
        String requestTime = headers.get(CommonConstant.REQUEST_TIME);
        String sign = headers.get(CommonConstant.HTTP_SIGN);
        String userId = headers.get(CommonConstant.USER_ID);
        String ip = headers.get(CommonConstant.HTTP_SRC_IP);
        String serviceCode = headers.get(CommonConstant.HTTP_SERVICE);

        AppService appService = null;

        if (StringUtil.isNullOrNone(appId)) {
            throw new IllegalArgumentException("未包含APP-ID");
        }

        List<AppRoute> appRoutes = AppRouteCache.getAppRoute(headers.get("app-id"));

        if (appRoutes == null || appRoutes.size() < 1) {
            //添加耗时
            throw new IllegalArgumentException("当前没有获取到AppId对应的信息，appId = " + headers.get("app-id"));
        }

        if (StringUtil.isNullOrNone(transactionId)) {
            //添加耗时
            throw new IllegalArgumentException("TRANSACTION-ID 不能为空");
        }

        if (!StringUtil.isNullOrNone(appRoutes.get(0).getSecurityCode())) {
            String nweSign = AuthenticationFactory.apiDataFlowMd5(transactionId, requestTime, appId, appRoutes.get(0).getSecurityCode(), reqJson);
            if (!nweSign.equals(sign.toLowerCase())) {
                throw new IllegalArgumentException("签名失败");
            }
        }

        if (StringUtil.isNullOrNone(requestTime) || !DateUtil.judgeDate(requestTime, DateUtil.DATE_FORMATE_STRING_DEFAULT)) {
            //添加耗时
            throw new IllegalArgumentException("requestTime 格式不对，遵循yyyyMMddHHmmss格式");
        }
        //用户ID校验
        if (StringUtil.isNullOrNone(userId)) {
            throw new IllegalArgumentException("USER-ID 不能为空");
        }
        for (AppRoute appRoute : appRoutes) {
            if (StatusConstant.STATUS_CD_VALID.equals(appRoute.getStatusCd())
                    && appRoute.getAppService().getServiceCode().equals(serviceCode)) {
                appService = appRoute.getAppService();
                break;
            }
        }
        //这里调用缓存 查询缓存信息
        if (appService == null) {
            //添加耗时
            throw new IllegalArgumentException("AppId 没有权限访问 serviceCode = " + serviceCode);
        }


        //检验白名单
        List<String> whileListIp = appRoutes.get(0).getWhileListIp();
        if (whileListIp != null && whileListIp.size() > 0 && !whileListIp.contains(ip)) {
            //添加耗时
            throw new IllegalArgumentException("当前IP被限制不能访问服务");
        }

        //检查黑名单
        List<String> backListIp = appRoutes.get(0).getBackListIp();
        if (backListIp != null && backListIp.size() > 0 && backListIp.contains(ip)) {
            //添加耗时
            throw new IllegalArgumentException("当前IP被限制不能访问服务");
        }
        return appService;
    }


    /**
     * 解密
     *
     * @param reqJson
     * @return
     */
    private String decrypt(String reqJson, Map<String, String> headers) {
        try {
            if (MappingConstant.VALUE_ON.equals(headers.get(CommonConstant.ENCRYPT))) {
                logger.debug("解密前字符：{}", reqJson);
                reqJson = new String(AuthenticationFactory.decrypt(reqJson.getBytes("UTF-8"),
                        AuthenticationFactory.loadPrivateKey(MappingConstant.KEY_PRIVATE_STRING)
                        , NumberUtils.isNumber(headers.get(CommonConstant.ENCRYPT_KEY_SIZE)) ?
                                Integer.parseInt(headers.get(CommonConstant.ENCRYPT_KEY_SIZE)) :
                                Integer.parseInt(MappingCache.getValue(MappingConstant.KEY_DEFAULT_DECRYPT_KEY_SIZE))), "UTF-8");
                logger.debug("解密后字符：" + reqJson);
            }
        } catch (Exception e) {
            throw new DecryptException(RESULT_CODE_NO_AUTHORITY_ERROR, "解密失败");
        }

        return reqJson;
    }
}
