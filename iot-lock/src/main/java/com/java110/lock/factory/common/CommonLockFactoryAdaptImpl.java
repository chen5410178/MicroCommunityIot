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
import org.springframework.web.client.RestClientException;
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

    @Autowired
    private RestTemplate outRestTemplate;

    @Autowired
    private ILockLogV1InnerServiceSMO lockLogV1InnerServiceSMOImpl;

    @Autowired
    private ILockInoutV1InnerServiceSMO lockInoutV1InnerServiceSMOImpl;

    @Override
    public ResultVo unlock(JSONObject reqJson) {
        String logId = GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_logId);

        //todo: 组装请求报文
        String reqParam = "{msg:\"这是一条请求报文,上线请替换\"}";
        //todo: 发送请求接收同步返回   如果同步成功要修改日志记录的状态,异步在接收回调通知后修改日志记录的状态,

        //请求报文发送成功 记录日志
        LockLogPo lockLogPo = BeanConvertUtil.covertBean(reqJson, LockLogPo.class);
        lockLogPo.setLogId(logId);
        lockLogPo.setReqParam(reqParam);
        lockLogPo.setState(LockLogDto.REQUEST_SUCCESS);
        lockLogV1InnerServiceSMOImpl.saveLockLog(lockLogPo);

        //模拟请求报文同步回调成功,记录操作日志 todo: 异步回调成功要记录操作日志
        LockInoutPo lockInoutPo = BeanConvertUtil.covertBean(reqJson, LockInoutPo.class);
        lockInoutPo.setInoutId(GenerateCodeFactory.getGeneratorId("10"));
        lockInoutPo.setState(LockInoutDto.UNLOCK_SUCCESS);
        lockInoutV1InnerServiceSMOImpl.saveLockInout(lockInoutPo);

        return new ResultVo(ResultVo.CODE_OK, "请求已发送，等待电梯反馈数据");
    }

    @Override
    public void queryLockMachineState(LockMachineDto lockMachineDto) {
        lockMachineDto.setState(LockMachineDto.STATE_ONLINE);
        lockMachineDto.setStateName("在线");
    }

    @Override
    public void addPasswordToCloud(LockPersonPo lockPersonPo, List<LockMachineParamDto> lockMachineParamDtos) {
        //获取令牌,如果令牌失效,刷新令牌
        String accessToken = getAccessToken(lockPersonPo, lockMachineParamDtos);
        //添加自定义密码
        StringBuilder params = new StringBuilder();
        List<LockMachineParamDto> client = lockMachineParamDtos.stream().filter(lockMachineParam -> "clientId".equals(lockMachineParam.getSpecName())).collect(Collectors.toList());
        params.append("clientId=" + client.get(0).getValue());
        params.append("&accessToken=" + accessToken);
        params.append("&lockId=" + lockPersonPo.getMachineId()); // todo: 初始化锁的id
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
        Integer keyboardPwdId = res.getInteger("keyboardPwdId");
        if (keyboardPwdId != null) {
            lockLogPo.setState(LockLogDto.RETURN_SUCCESS);
            lockLogPo.setResParam(responseEntity.getBody());
            lockLogV1InnerServiceSMOImpl.updateLockLog(lockLogPo);
            logger.info("添加自定义密码成功,返回密码ID:" + keyboardPwdId);
            return ;
        }
        logger.error("添加自定义密码失败，错误码：" + res.getInteger("errcode") + "，错误信息：" + res.getString("errmsg") + "，错误描述：" + res.getString("description"));
    }

    private String getAccessToken(LockPersonPo lockPersonPo, List<LockMachineParamDto> lockMachineParamDtos) {
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

        /*params.append(lockMachineParamDtos.get(0).getSpecName() + "=" + lockMachineParamDtos.get(0).getValue());
        for (int i = 1; i < lockMachineParamDtos.size(); i++) {
            params.append("&" + lockMachineParamDtos.get(i).getSpecName() + "=" + lockMachineParamDtos.get(i).getValue());
        }*/

        LockLogPo lockLogPo = new LockLogPo();
        lockLogPo.setLogId(GenerateCodeFactory.getGeneratorId("10"));
        lockLogPo.setMachineId(lockMachineParamDtos.get(0).getMachineId());
        lockLogPo.setCommunityId(lockMachineParamDtos.get(0).getCommunityId());
        lockLogPo.setLogAction(LockLogDto.LOG_ACTION_GET_ACCESS_TOKEN);
        lockLogPo.setUserId(lockPersonPo.getOprUserId());
        lockLogPo.setUserName(lockPersonPo.getOprUserName());
        String url = ACCESS_TOKEN_URL + "?" + params;
        lockLogPo.setReqParam(url);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/x-www-form-urlencoded");
        HttpEntity httpEntity = new HttpEntity("", httpHeaders);
        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = outRestTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        } catch (HttpStatusCodeException e) {
            logger.error("请求异常", e.getResponseBodyAsString());
        }

        lockLogPo.setState(LockLogDto.REQUEST_SUCCESS);
        lockLogV1InnerServiceSMOImpl.saveLockLog(lockLogPo);
        JSONObject res = JSONObject.parseObject(responseEntity.getBody());

        String accessToken = res.getString("access_token");
        if (accessToken != null) {
            lockLogPo.setState(LockLogDto.RETURN_SUCCESS);
            lockLogPo.setResParam(responseEntity.getBody());
            lockLogV1InnerServiceSMOImpl.updateLockLog(lockLogPo);
            logger.info("获取访问令牌为:" + accessToken);
            return accessToken;
        }
        if (res.getInteger("errcode") == CommonLockFactoryAdaptUtil.ERRCODE_INVALID_ACCESS_TOKEN) {
            return refreshAccessToken(lockPersonPo, lockMachineParamDtos);
        }
        logger.error("令牌获取失败，错误码：" + res.getInteger("errcode") + "，错误信息：" + res.getString("errmsg") + "，错误描述：" + res.getString("description"));
        throw new CmdException("授权失败");
    }

    private String refreshAccessToken(LockPersonPo lockPersonPo, List<LockMachineParamDto> lockMachineParamDtos) {
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
            }
        }
        params.append("grant_type=refresh_token");
        for (int i = 1; i < paramList.size(); i++) {
            params.append("&" + paramList.get(i));
        }

        LockLogPo lockLogPo = new LockLogPo();
        lockLogPo.setLogId(GenerateCodeFactory.getGeneratorId("10"));
        lockLogPo.setMachineId(lockMachineParamDtos.get(0).getMachineId());
        lockLogPo.setCommunityId(lockMachineParamDtos.get(0).getCommunityId());
        lockLogPo.setLogAction(LockLogDto.LOG_ACTION_REFRESH_ACCESS_TOKEN);
        lockLogPo.setUserId(lockPersonPo.getOprUserId());
        lockLogPo.setUserName(lockPersonPo.getOprUserName());
        String url = ACCESS_TOKEN_URL + "?" + params;
        lockLogPo.setReqParam(url);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/x-www-form-urlencoded");
        HttpEntity httpEntity = new HttpEntity("", httpHeaders);
        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = outRestTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        } catch (HttpStatusCodeException e) {
            logger.error("请求异常", e.getResponseBodyAsString());
        }
        lockLogPo.setState(LockLogDto.REQUEST_SUCCESS);
        lockLogV1InnerServiceSMOImpl.saveLockLog(lockLogPo);
        JSONObject res = JSONObject.parseObject(responseEntity.getBody());
        String accessToken = res.getString("access_token");
        if (accessToken != null) {
            lockLogPo.setState(LockLogDto.RETURN_SUCCESS);
            lockLogPo.setResParam(responseEntity.getBody());
            lockLogV1InnerServiceSMOImpl.updateLockLog(lockLogPo);
            return accessToken;
        }
        logger.error("令牌刷新失败，错误码：" + res.getInteger("errcode") + "，错误信息：" + res.getString("errmsg") + "，错误描述：" + res.getString("description"));
        throw new CmdException("授权失败");
    }
}
