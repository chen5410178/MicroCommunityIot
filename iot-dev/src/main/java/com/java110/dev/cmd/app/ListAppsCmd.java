package com.java110.dev.cmd.app;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.app.AppDto;
import com.java110.intf.dev.IAppV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;


@Java110Cmd(serviceCode = "app.listApps")
public class ListAppsCmd extends Cmd {


    @Autowired
    private IAppV1InnerServiceSMO appInnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {
        super.validatePageInfo(reqJson);
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

        AppDto appDto = BeanConvertUtil.covertBean(reqJson, AppDto.class);

        int count = appInnerServiceSMOImpl.queryAppsCount(appDto);

        List<AppDto> apps = null;

        if (count > 0) {
            apps = appInnerServiceSMOImpl.queryApps(appDto);
        } else {
            apps = new ArrayList<>();
        }


        ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) reqJson.getInteger("row")), count, apps);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        cmdDataFlowContext.setResponseEntity(responseEntity);
    }
}
