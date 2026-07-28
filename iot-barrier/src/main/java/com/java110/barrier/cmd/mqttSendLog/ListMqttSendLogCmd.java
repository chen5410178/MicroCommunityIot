
package com.java110.barrier.cmd.mqttSendLog;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.barrier.dao.IMqttSendLogV1ServiceDao;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Java110Cmd(serviceCode = "barrier.listMqttSendLog")
public class ListMqttSendLogCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(ListMqttSendLogCmd.class);

    @Autowired
    private IMqttSendLogV1ServiceDao mqttSendLogV1ServiceDaoImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        super.validatePageInfo(reqJson);
        Assert.hasKeyAndValue(reqJson, "machineCode", "请求报文中未包含设备编码");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {
        Map info = new HashMap();
        info.put("machineCode", reqJson.getString("machineCode"));
        if (reqJson.containsKey("cmdType")) {
            info.put("cmdType", reqJson.getString("cmdType"));
        }
        int page = reqJson.getInteger("page");
        int row = reqJson.getInteger("row");
        info.put("page", (page - 1) * row);
        info.put("row", row);

        int count = mqttSendLogV1ServiceDaoImpl.queryMqttSendLogCount(info);
        List<Map> list;
        if (count > 0) {
            list = mqttSendLogV1ServiceDaoImpl.queryMqttSendLogs(info);
        } else {
            list = new ArrayList<>();
        }

        ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) row), count, list);
        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);
        cmdDataFlowContext.setResponseEntity(responseEntity);
    }
}
