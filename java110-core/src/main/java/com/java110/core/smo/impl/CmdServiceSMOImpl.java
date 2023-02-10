package com.java110.core.smo.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.core.cache.MappingCache;
import com.java110.core.client.RestTemplate;
import com.java110.core.cmd.ServiceCmdEventPublishing;
import com.java110.core.constant.MappingConstant;
import com.java110.core.context.CmdDataFlow;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.factory.DataFlowFactory;
import com.java110.core.factory.KafkaFactory;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.smo.ICmdServiceSMO;
import com.java110.core.smo.ISaveTransactionLogSMO;
import com.java110.core.trace.Java110TraceLog;
import com.java110.core.utils.DateUtil;
import com.java110.vo.ResultVo;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * cmd服务处理类
 * Created by wuxw on 2018/4/13.
 */
@Service("cmdServiceSMOImpl")
public class CmdServiceSMOImpl implements ICmdServiceSMO {

    private static Logger logger = LoggerFactory.getLogger(CmdServiceSMOImpl.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RestTemplate outRestTemplate;

    @Autowired
    private ISaveTransactionLogSMO saveTransactionLogSMOImpl;


    /**
     * 服务调度
     *
     * @param reqJson 请求报文json
     * @param headers
     * @return
     * @throws
     */
    @Override
    @Java110TraceLog
    public ResponseEntity<String> cmd(String reqJson, Map<String, String> headers) throws Exception {

        ICmdDataFlowContext cmdDataFlowContext = null;

        Date startDate = DateUtil.getCurrentDate();

        ResponseEntity<String> responseEntity = null;

        //1.0 创建数据流 appId serviceCode
        cmdDataFlowContext = DataFlowFactory.newInstance(CmdDataFlow.class).builder(reqJson, headers);


        //6.0 调用下游系统
        invokeBusinessSystem(cmdDataFlowContext);

        responseEntity = cmdDataFlowContext.getResponseEntity();

        Date endDate = DateUtil.getCurrentDate();

        if (responseEntity == null) {
            responseEntity = ResultVo.success();
        }
        return responseEntity;
    }


    /**
     * 6.0 调用下游系统
     *
     * @param cmdDataFlowContext
     * @throws Exception
     */
    private void invokeBusinessSystem(ICmdDataFlowContext cmdDataFlowContext) throws Exception {
        ServiceCmdEventPublishing.multicastEvent(cmdDataFlowContext);
    }




    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

}
