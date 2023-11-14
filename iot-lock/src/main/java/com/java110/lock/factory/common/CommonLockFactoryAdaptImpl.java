package com.java110.lock.factory.common;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.DateUtil;
import com.java110.dto.lock.LockInoutDto;
import com.java110.dto.lock.LockLogDto;
import com.java110.dto.lock.LockMachineDto;
import com.java110.dto.lock.LockMachineParamDto;
import com.java110.intf.lock.ILockInoutV1InnerServiceSMO;
import com.java110.intf.lock.ILockLogV1InnerServiceSMO;
import com.java110.lock.factory.ILockFactoryAdapt;
import com.java110.po.lock.LockInoutPo;
import com.java110.po.lock.LockLogPo;
import com.java110.po.lock.LockMachinePo;
import com.java110.po.lock.LockPersonPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Component("commonLockFactoryAdaptImpl")
public class CommonLockFactoryAdaptImpl implements ILockFactoryAdapt {

    private static Logger logger = LoggerFactory.getLogger(CommonLockFactoryAdaptImpl.class);

    public static final int KEYBOARD_PWD_TYPE_FOREVER = 2;
    public static final int KEYBOARD_PWD_TYPE_TERM = 3;//默认
    public static final int ADD_TYPE_BLUETOOTH = 1;//默认
    public static final int ADD_TYPE_WIFI = 2;
    public static final int ADD_TYPE_NB_IOT = 3;

    //获取或刷新访问令牌
    public static final String ACCESS_TOKEN_URL = "https://cnapi.sciener.com/oauth2/token";
    //添加自定义密码
    public static final String ADD_PASSWORD_URL = "https://cnapi.sciener.com/v3/keyboardPwd/add";
    //修改密码
    public static final String CHANGE_PASSWORD_URL = "https://cnapi.sciener.com/v3/keyboardPwd/change";
    //远程开锁
    public static final String UNLOCK_URL = "https://cnapi.sciener.com/v3/lock/unlock";
    //获取wifi锁信息
    public static final String GET_WIFI_LOCK_DETAIL_URL = "https://cnapi.sciener.com/v3/wifiLock/detail";
    //查询锁电量
    public static final String QUERY_ELECTRIC_QUANTITY_URL = "https://cnapi.sciener.com/v3/lock/queryElectricQuantity";

    @Autowired
    private RestTemplate outRestTemplate;

    @Autowired
    private ILockLogV1InnerServiceSMO lockLogV1InnerServiceSMOImpl;

    @Autowired
    private ILockInoutV1InnerServiceSMO lockInoutV1InnerServiceSMOImpl;

    @Override
    public void unlock(LockMachinePo lockMachinePo, List<LockMachineParamDto> lockMachineParamDtos) {
        String accessToken = getAccessToken(/*lockMachinePo.getOprUserId(), lockMachinePo.getOprUserName(), */lockMachineParamDtos);

        StringBuilder params = new StringBuilder();
        List<LockMachineParamDto> client = lockMachineParamDtos.stream().filter(lockMachineParam -> "clientId".equals(lockMachineParam.getSpecName())).collect(Collectors.toList());
        params.append("clientId=" + client.get(0).getValue());
        params.append("&accessToken=" + accessToken);
        params.append("&lockId=" + lockMachinePo.getMachineId());
        params.append("&date=" + new Date().getTime());

        LockLogPo lockLogPo = new LockLogPo();
        lockLogPo.setLogId(GenerateCodeFactory.getGeneratorId("10"));
        lockLogPo.setMachineId(lockMachineParamDtos.get(0).getMachineId());
        lockLogPo.setCommunityId(lockMachineParamDtos.get(0).getCommunityId());
        lockLogPo.setLogAction(LockLogDto.LOG_ACTION_UNLOCK);
        lockLogPo.setUserId(lockMachinePo.getOprUserId());
        lockLogPo.setUserName(lockMachinePo.getOprUserName());
        String url = UNLOCK_URL + "?" + params;
        lockLogPo.setReqParam(url);

        LockInoutPo lockInoutPo = new LockInoutPo();
        lockInoutPo.setInoutId(GenerateCodeFactory.getGeneratorId("10"));
        lockInoutPo.setMachineId(lockMachinePo.getMachineId());
        lockInoutPo.setMachineCode(lockMachinePo.getMachineCode());
        lockInoutPo.setUserId(lockMachinePo.getOprUserId());
        lockInoutPo.setUserName(lockMachinePo.getOprUserName());
        lockInoutPo.setOpenTypeCd(LockInoutDto.OPEN_TYPE_REMOTE);
        lockInoutPo.setCommunityId(lockMachinePo.getCommunityId());
        lockInoutPo.setTel(lockMachinePo.getTel());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/x-www-form-urlencoded");
        HttpEntity httpEntity = new HttpEntity("", httpHeaders);
        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = outRestTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        } catch (HttpStatusCodeException e) {
            logger.error("请求异常", e.getResponseBodyAsString());
            throw new CmdException("开锁失败," + e.getResponseBodyAsString());
        }
        lockLogPo.setState(LockLogDto.REQUEST_SUCCESS);
        lockLogV1InnerServiceSMOImpl.saveLockLog(lockLogPo);

        JSONObject res = JSONObject.parseObject(responseEntity.getBody());
        if (CommonLockFactoryAdaptUtil.errorMap.containsKey(res.getInteger("errcode"))) {
            logger.error("开锁失败，错误码：" + res.getInteger("errcode") + "，错误信息：" + res.getString("errmsg") + "，错误描述：" + res.getString("description"));
            throw new CmdException(CommonLockFactoryAdaptUtil.errorMap.get(res.getInteger("errcode")));
        }
        if (res.getInteger("errcode") == 0) {
            lockLogPo.setState(LockLogDto.RETURN_SUCCESS);
            lockLogPo.setResParam(responseEntity.getBody());
            lockLogV1InnerServiceSMOImpl.updateLockLog(lockLogPo);

            lockInoutPo.setState(LockInoutDto.UNLOCK_SUCCESS);
            lockInoutV1InnerServiceSMOImpl.saveLockInout(lockInoutPo);
            logger.info("开锁成功");
        }
    }

    @Override
    public void queryLockMachineState(LockMachineDto lockMachineDto, List<LockMachineParamDto> lockMachineParamDtos) {
        String accessToken = getAccessToken(lockMachineParamDtos);

        StringBuilder params = new StringBuilder();
        List<LockMachineParamDto> client = lockMachineParamDtos.stream().filter(lockMachineParam -> "clientId".equals(lockMachineParam.getSpecName())).collect(Collectors.toList());
        params.append("clientId=" + client.get(0).getValue());
        params.append("&accessToken=" + accessToken);
        params.append("&lockId=" + lockMachineDto.getMachineId());
        params.append("&date=" + new Date().getTime());
        String url = GET_WIFI_LOCK_DETAIL_URL + "?" + params;

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/x-www-form-urlencoded");
        HttpEntity httpEntity = new HttpEntity("", httpHeaders);
        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = outRestTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
        } catch (HttpStatusCodeException e) {
            logger.error("请求异常", e.getResponseBodyAsString());
            throw new CmdException("查询失败," + e.getResponseBodyAsString());
        }

        JSONObject res = JSONObject.parseObject(responseEntity.getBody());
        if (CommonLockFactoryAdaptUtil.errorMap.containsKey(res.getInteger("errcode"))) {
            logger.error("修改密码失败，错误码：" + res.getInteger("errcode") + "，错误信息：" + res.getString("errmsg") + "，错误描述：" + res.getString("description"));
            throw new CmdException(CommonLockFactoryAdaptUtil.errorMap.get(res.getInteger("errcode")));
        }

        if (1 == res.getInteger("isOnline")) {
            lockMachineDto.setState(LockMachineDto.STATE_ONLINE);
            lockMachineDto.setStateName("在线");
        } else {
            lockMachineDto.setState(LockMachineDto.STATE_OFFLINE);
            lockMachineDto.setStateName("离线");
        }
    }

    @Override
    public String addPasswordToCloud(LockPersonPo lockPersonPo, List<LockMachineParamDto> lockMachineParamDtos) {
        //获取令牌,如果令牌失效,刷新令牌
        String accessToken = getAccessToken(/*lockPersonPo.getOprUserId(), lockPersonPo.getOprUserName(), */lockMachineParamDtos);
        //添加自定义密码
        StringBuilder params = new StringBuilder();
        List<LockMachineParamDto> client = lockMachineParamDtos.stream().filter(lockMachineParam -> "clientId".equals(lockMachineParam.getSpecName())).collect(Collectors.toList());
        params.append("clientId=" + client.get(0).getValue());
        params.append("&accessToken=" + accessToken);
        params.append("&lockId=" + lockPersonPo.getMachineId());
        params.append("&keyboardPwd=" + lockPersonPo.getCardNumber());
        params.append("&keyboardPwdType=" + KEYBOARD_PWD_TYPE_TERM);
        params.append("&startDate=" + DateUtil.getDateFromStringB(lockPersonPo.getStartTime()).getTime());
        params.append("&endDate=" + DateUtil.getDateFromStringB(lockPersonPo.getEndTime()).getTime());
        params.append("&addType=" + ADD_TYPE_WIFI);
        params.append("&date=" + new Date().getTime());

        LockLogPo lockLogPo = new LockLogPo();
        lockLogPo.setLogId(GenerateCodeFactory.getGeneratorId("10"));
        lockLogPo.setMachineId(lockMachineParamDtos.get(0).getMachineId());
        lockLogPo.setCommunityId(lockMachineParamDtos.get(0).getCommunityId());
        lockLogPo.setLogAction(LockLogDto.LOG_ACTION_ADD_PWD_TO_CLOUD);
        lockLogPo.setUserId(lockPersonPo.getOprUserId());
        lockLogPo.setUserName(lockPersonPo.getOprUserName());
        String url = ADD_PASSWORD_URL + "?" + params;
        lockLogPo.setReqParam(url);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/x-www-form-urlencoded");
        HttpEntity httpEntity = new HttpEntity("", httpHeaders);
        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = outRestTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        } catch (HttpStatusCodeException e) {
            logger.error("请求异常", e.getResponseBodyAsString());
            throw new CmdException("授权失败," + e.getResponseBodyAsString());
        }
        lockLogPo.setState(LockLogDto.REQUEST_SUCCESS);
        lockLogV1InnerServiceSMOImpl.saveLockLog(lockLogPo);

        JSONObject res = JSONObject.parseObject(responseEntity.getBody());
        if (CommonLockFactoryAdaptUtil.errorMap.containsKey(res.getInteger("errcode"))) {
            logger.error("添加自定义密码失败，错误码：" + res.getInteger("errcode") + "，错误信息：" + res.getString("errmsg") + "，错误描述：" + res.getString("description"));
            throw new CmdException(CommonLockFactoryAdaptUtil.errorMap.get(res.getInteger("errcode")));
        }

        Integer keyboardPwdId = res.getInteger("keyboardPwdId");
        if (keyboardPwdId != null) {
            lockLogPo.setState(LockLogDto.RETURN_SUCCESS);
            lockLogPo.setResParam(responseEntity.getBody());
            lockLogV1InnerServiceSMOImpl.updateLockLog(lockLogPo);

            logger.info("添加自定义密码成功,返回密码ID:" + keyboardPwdId);
            return keyboardPwdId.toString();
        }
        return keyboardPwdId.toString();
    }

    @Override
    public void updatePasswordToCloud(LockPersonPo lockPersonPo, List<LockMachineParamDto> lockMachineParamDtos) {
        String accessToken = getAccessToken(/*lockPersonPo.getOprUserId(), lockPersonPo.getOprUserName(), */lockMachineParamDtos);

        StringBuilder params = new StringBuilder();
        List<LockMachineParamDto> client = lockMachineParamDtos.stream().filter(lockMachineParam -> "clientId".equals(lockMachineParam.getSpecName())).collect(Collectors.toList());
        params.append("clientId=" + client.get(0).getValue());
        params.append("&accessToken=" + accessToken);
        params.append("&lockId=" + lockPersonPo.getMachineId());
        params.append("&keyboardPwdId=" + lockPersonPo.getLpId());
        params.append("&newKeyboardPwd=" + lockPersonPo.getCardNumber());
        params.append("&startDate=" + DateUtil.getDateFromStringB(lockPersonPo.getStartTime()).getTime());
        params.append("&endDate=" + DateUtil.getDateFromStringB(lockPersonPo.getEndTime()).getTime());
        params.append("&changeType=" + ADD_TYPE_WIFI);
        params.append("&date=" + new Date().getTime());

        LockLogPo lockLogPo = new LockLogPo();
        lockLogPo.setLogId(GenerateCodeFactory.getGeneratorId("10"));
        lockLogPo.setMachineId(lockMachineParamDtos.get(0).getMachineId());
        lockLogPo.setCommunityId(lockMachineParamDtos.get(0).getCommunityId());
        lockLogPo.setLogAction(LockLogDto.LOG_ACTION_CHANGE_PWD_TO_CLOUD);
        lockLogPo.setUserId(lockPersonPo.getOprUserId());
        lockLogPo.setUserName(lockPersonPo.getOprUserName());
        String url = CHANGE_PASSWORD_URL + "?" + params;
        lockLogPo.setReqParam(url);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/x-www-form-urlencoded");
        HttpEntity httpEntity = new HttpEntity("", httpHeaders);
        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = outRestTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        } catch (HttpStatusCodeException e) {
            logger.error("请求异常", e.getResponseBodyAsString());
            throw new CmdException("操作失败," + e.getResponseBodyAsString());
        }
        lockLogPo.setState(LockLogDto.REQUEST_SUCCESS);
        lockLogV1InnerServiceSMOImpl.saveLockLog(lockLogPo);

        JSONObject res = JSONObject.parseObject(responseEntity.getBody());
        if (CommonLockFactoryAdaptUtil.errorMap.containsKey(res.getInteger("errcode"))) {
            logger.error("修改密码失败，错误码：" + res.getInteger("errcode") + "，错误信息：" + res.getString("errmsg") + "，错误描述：" + res.getString("description"));
            throw new CmdException(CommonLockFactoryAdaptUtil.errorMap.get(res.getInteger("errcode")));
        }

        if (res.getInteger("errcode") == 0) {
            lockLogPo.setState(LockLogDto.RETURN_SUCCESS);
            lockLogPo.setResParam(responseEntity.getBody());
            lockLogV1InnerServiceSMOImpl.updateLockLog(lockLogPo);
            logger.info("操作成功");
        }
    }

    @Override
    public Integer queryElectricQuantity(LockMachineDto lockMachineDto, List<LockMachineParamDto> lockMachineParamDtos) {
        String accessToken = getAccessToken(lockMachineParamDtos);

        StringBuilder params = new StringBuilder();
        List<LockMachineParamDto> client = lockMachineParamDtos.stream().filter(lockMachineParam -> "clientId".equals(lockMachineParam.getSpecName())).collect(Collectors.toList());
        params.append("clientId=" + client.get(0).getValue());
        params.append("&accessToken=" + accessToken);
        params.append("&lockId=" + lockMachineDto.getMachineId());
        params.append("&date=" + new Date().getTime());
        String url = QUERY_ELECTRIC_QUANTITY_URL + "?" + params;

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/x-www-form-urlencoded");
        HttpEntity httpEntity = new HttpEntity("", httpHeaders);
        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = outRestTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
        } catch (HttpStatusCodeException e) {
            logger.error("请求异常", e.getResponseBodyAsString());
            throw new CmdException("查询失败," + e.getResponseBodyAsString());
        }

        JSONObject res = JSONObject.parseObject(responseEntity.getBody());
        if (CommonLockFactoryAdaptUtil.errorMap.containsKey(res.getInteger("errcode"))) {
            logger.error("修改密码失败，错误码：" + res.getInteger("errcode") + "，错误信息：" + res.getString("errmsg") + "，错误描述：" + res.getString("description"));
            throw new CmdException(CommonLockFactoryAdaptUtil.errorMap.get(res.getInteger("errcode")));
        }

        return res.getInteger("electricQuantity");
    }

    private String getAccessToken(/*String oprUserId, String oprUserName, */List<LockMachineParamDto> lockMachineParamDtos) {
        StringBuilder params = new StringBuilder();
        List<String> paramList = new ArrayList<>();
        for (int i = 0; i < lockMachineParamDtos.size(); i++) {
            switch (lockMachineParamDtos.get(i).getSpecName()) {
                case "clientId":
                    paramList.add("clientId=" + lockMachineParamDtos.get(i).getValue());
                    break;
                case "clientSecret":
                    paramList.add("clientSecret=" + lockMachineParamDtos.get(i).getValue());
                    break;
                case "username":
                    paramList.add("username=" + lockMachineParamDtos.get(i).getValue());
                    break;
                case "password":
                    paramList.add("password=" + lockMachineParamDtos.get(i).getValue());
                    break;
            }
        }
        params.append(paramList.get(0));
        for (int i = 1; i < paramList.size(); i++) {
            params.append("&" + paramList.get(i));
        }

        /*LockLogPo lockLogPo = new LockLogPo();
        lockLogPo.setLogId(GenerateCodeFactory.getGeneratorId("10"));
        lockLogPo.setMachineId(lockMachineParamDtos.get(0).getMachineId());
        lockLogPo.setCommunityId(lockMachineParamDtos.get(0).getCommunityId());
        lockLogPo.setLogAction(LockLogDto.LOG_ACTION_GET_ACCESS_TOKEN);
        lockLogPo.setUserId(oprUserId);
        lockLogPo.setUserName(oprUserName);*/
        String url = ACCESS_TOKEN_URL + "?" + params;
        /*lockLogPo.setReqParam(url);*/

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/x-www-form-urlencoded");
        HttpEntity httpEntity = new HttpEntity("", httpHeaders);
        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = outRestTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        } catch (HttpStatusCodeException e) {
            logger.error("请求异常", e.getResponseBodyAsString());
        }

        /*lockLogPo.setState(LockLogDto.REQUEST_SUCCESS);
        lockLogV1InnerServiceSMOImpl.saveLockLog(lockLogPo);*/
        JSONObject res = JSONObject.parseObject(responseEntity.getBody());

        String accessToken = res.getString("access_token");
        if (accessToken != null) {
            /*lockLogPo.setState(LockLogDto.RETURN_SUCCESS);
            lockLogPo.setResParam(responseEntity.getBody());
            lockLogV1InnerServiceSMOImpl.updateLockLog(lockLogPo);*/
            logger.info("获取访问令牌为:" + accessToken);
            return accessToken;
        }
        if (res.getInteger("errcode") == CommonLockFactoryAdaptUtil.ERRCODE_INVALID_ACCESS_TOKEN) {
            return refreshAccessToken(/*oprUserId, oprUserName, */lockMachineParamDtos);
        }
        logger.error("令牌获取失败，错误码：" + res.getInteger("errcode") + "，错误信息：" + res.getString("errmsg") + "，错误描述：" + res.getString("description"));
        if (CommonLockFactoryAdaptUtil.errorMap.containsKey(res.getInteger("errcode"))) {
            throw new CmdException(CommonLockFactoryAdaptUtil.errorMap.get(res.getInteger("errcode")));
        }
        throw new CmdException("令牌获取失败");
    }

    private String refreshAccessToken(/*String oprUserId, String oprUserName, */List<LockMachineParamDto> lockMachineParamDtos) {
        StringBuilder params = new StringBuilder();
        List<String> paramList = new ArrayList<>();
        for (int i = 0; i < lockMachineParamDtos.size(); i++) {
            switch (lockMachineParamDtos.get(i).getSpecName()) {
                case "clientId":
                    paramList.add("clientId=" + lockMachineParamDtos.get(i).getValue());
                    break;
                case "clientSecret":
                    paramList.add("clientSecret=" + lockMachineParamDtos.get(i).getValue());
                    break;
                case "refreshToken":
                    paramList.add("refresh_token=" + lockMachineParamDtos.get(i).getValue());
                    break;
            }
        }
        params.append("grant_type=refresh_token");
        for (int i = 1; i < paramList.size(); i++) {
            params.append("&" + paramList.get(i));
        }

        /*LockLogPo lockLogPo = new LockLogPo();
        lockLogPo.setLogId(GenerateCodeFactory.getGeneratorId("10"));
        lockLogPo.setMachineId(lockMachineParamDtos.get(0).getMachineId());
        lockLogPo.setCommunityId(lockMachineParamDtos.get(0).getCommunityId());
        lockLogPo.setLogAction(LockLogDto.LOG_ACTION_REFRESH_ACCESS_TOKEN);
        lockLogPo.setUserId(oprUserId);
        lockLogPo.setUserName(oprUserName);*/
        String url = ACCESS_TOKEN_URL + "?" + params;
        /*lockLogPo.setReqParam(url);*/

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/x-www-form-urlencoded");
        HttpEntity httpEntity = new HttpEntity("", httpHeaders);
        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = outRestTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        } catch (HttpStatusCodeException e) {
            logger.error("请求异常", e.getResponseBodyAsString());
        }
        /*lockLogPo.setState(LockLogDto.REQUEST_SUCCESS);
        lockLogV1InnerServiceSMOImpl.saveLockLog(lockLogPo);*/
        JSONObject res = JSONObject.parseObject(responseEntity.getBody());
        if (CommonLockFactoryAdaptUtil.errorMap.containsKey(res.getInteger("errcode"))) {
            throw new CmdException(CommonLockFactoryAdaptUtil.errorMap.get(res.getInteger("errcode")));
        }

        String accessToken = res.getString("access_token");
        if (accessToken != null) {
            /*lockLogPo.setState(LockLogDto.RETURN_SUCCESS);
            lockLogPo.setResParam(responseEntity.getBody());
            lockLogV1InnerServiceSMOImpl.updateLockLog(lockLogPo);*/
            return accessToken;
        }
        logger.error("令牌刷新失败，错误码：" + res.getInteger("errcode") + "，错误信息：" + res.getString("errmsg") + "，错误描述：" + res.getString("description"));
        throw new CmdException("令牌刷新失败");
    }
}
