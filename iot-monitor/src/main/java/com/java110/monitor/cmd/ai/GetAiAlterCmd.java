package com.java110.monitor.cmd.ai;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.client.RestTemplate;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.AuthenticationFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.dto.monitorManufactor.MonitorManufactorDto;
import com.java110.intf.monitor.IMonitorManufactorV1InnerServiceSMO;
import com.java110.monitor.factory.mvideo.MVideoAdaptImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;

import java.text.ParseException;
import java.util.List;

/**
 * 获取AI告警
 */
@Java110Cmd(serviceCode = "ai.getAiAlter")
public class GetAiAlterCmd extends Cmd {

    public static final String PATH = "/oapp/ext/common";
    public static final String mvApiCode = "getTaskAlterService";

    private static Logger logger = LoggerFactory.getLogger(GetAiAlterCmd.class);

    @Autowired
    private RestTemplate outRestTemplate;
    @Autowired
    private IMonitorManufactorV1InnerServiceSMO monitorManufactorV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson, "communityId", "未包含小区ID");
        super.validatePageInfo(reqJson);
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        MonitorManufactorDto monitorManufactorDto = new MonitorManufactorDto();
        monitorManufactorDto.setCommunityId(reqJson.getString("communityId"));
        List<MonitorManufactorDto> monitorManufactorDtos = monitorManufactorV1InnerServiceSMOImpl.queryMonitorManufactors(monitorManufactorDto);

        if (ListUtil.isNull(monitorManufactorDtos)) {
            return;
        }


        String url = monitorManufactorDtos.get(0).getMmUrl();
        url = url.endsWith("/") ? (url + PATH) : (url + "/" + PATH);
        String appId = monitorManufactorDtos.get(0).getAppId();
        String appSecure = monitorManufactorDtos.get(0).getAppSecure();
        long timestamp = DateUtil.getCurrentDate().getTime();
        String sign = AuthenticationFactory.md5(appId + mvApiCode + timestamp + appSecure);
        JSONObject paramIn = new JSONObject();
        paramIn.put("appId", appId);
        paramIn.put("timestamp", timestamp+"");
        paramIn.put("sign", sign);
        paramIn.put("mvApiCode", mvApiCode);
        paramIn.put("page", reqJson.getIntValue("page"));
        paramIn.put("row", reqJson.getIntValue("row"));

        HttpHeaders headers = new HttpHeaders();
        HttpEntity httpEntity = new HttpEntity(paramIn.toJSONString(), headers);

        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = outRestTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        } catch (HttpStatusCodeException e) { //这里spring 框架 在4XX 或 5XX 时抛出 HttpServerErrorException 异常，需要重新封装一下
            responseEntity = ResultVo.error(e.getMessage());
            logger.error("调用接口" + url + "异常：", e);
            throw e;
        } catch (Exception e) {
            logger.error("调用接口" + url + "异常：", e);
            throw e;
        } finally {
            logger.debug("请求地址为,{} 请求中心服务信息，{},中心服务返回信息，{}", url, httpEntity, responseEntity);
        }
        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());
        if (paramOut.getIntValue("code") != 0) {
            throw new IllegalArgumentException(paramOut.getString("msg"));
        }

        context.setResponseEntity(ResultVo.createResponseEntity(paramOut.getJSONArray("data")));
    }
}
