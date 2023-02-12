package com.java110.dev.cmd.basePrivilege;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.basePrivilege.BasePrivilegeDto;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.intf.user.IMenuV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

@Java110Cmd(serviceCode = "basePrivilege.listBasePrivileges")
public class ListBasePrivilegesCmd extends Cmd {

    @Autowired
    private IMenuV1InnerServiceSMO menuInnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        super.validatePageInfo(reqJson);
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {


        BasePrivilegeDto basePrivilegeDto = BeanConvertUtil.covertBean(reqJson, BasePrivilegeDto.class);

        int count = menuInnerServiceSMOImpl.queryBasePrivilegesCount(basePrivilegeDto);

        List<BasePrivilegeDto> basePrivileges = null;

        if (count > 0) {
            basePrivileges = menuInnerServiceSMOImpl.queryBasePrivileges(basePrivilegeDto);
        } else {
            basePrivileges = new ArrayList<>();
        }


        ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) reqJson.getInteger("row")), count, basePrivileges);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        context.setResponseEntity(responseEntity);
    }
}
