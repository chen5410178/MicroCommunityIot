package com.java110.dev.cmd.menuGroup;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.menuGroup.MenuGroupDto;
import com.java110.intf.user.IMenuGroupV1InnerServiceSMO;
import com.java110.intf.user.IMenuV1InnerServiceSMO;
import com.java110.po.menuGroup.MenuGroupPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Java110Cmd(serviceCode = "menuGroup.deleteMenuGroup")
public class DeleteMenuGroupCmd extends Cmd {
    @Autowired
    private IMenuGroupV1InnerServiceSMO menuInnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {

        Assert.hasKeyAndValue(reqJson, "gId", "组Id不能为空");

    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {

        ResponseEntity<String> responseEntity = null;

        MenuGroupPo menuGroupDto = BeanConvertUtil.covertBean(reqJson, MenuGroupPo.class);


        int saveFlag = menuInnerServiceSMOImpl.deleteMenuGroup(menuGroupDto);

        responseEntity = new ResponseEntity<String>(saveFlag > 0 ? "成功" : "失败", saveFlag > 0 ? HttpStatus.OK : HttpStatus.BAD_REQUEST);

        context.setResponseEntity(responseEntity);
    }
}
