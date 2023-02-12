package com.java110.dev.cmd.basePrivilege;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.dto.basePrivilege.BasePrivilegeDto;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.intf.user.IMenuV1InnerServiceSMO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Java110Cmd(serviceCode = "basePrivilege.saveBasePrivilege")
public class SaveBasePrivilegCmd extends Cmd {

    @Autowired
    private IMenuV1InnerServiceSMO menuInnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        Assert.hasKeyAndValue(reqJson, "name", "必填，请填写权限名称");
        Assert.hasKeyAndValue(reqJson, "domain", "必填，请选择商户类型");
        Assert.hasKeyAndValue(reqJson, "resource", "必填，请选择资源路径");
        Assert.hasKeyAndValue(reqJson, "mId", "必填，菜单为空");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        ResponseEntity<String> responseEntity = null;

        BasePrivilegeDto basePrivilegeDto = BeanConvertUtil.covertBean(reqJson, BasePrivilegeDto.class);

        freshPId(basePrivilegeDto);


        int saveFlag = menuInnerServiceSMOImpl.saveBasePrivilege(basePrivilegeDto);

        responseEntity = new ResponseEntity<String>(saveFlag > 0 ? "成功" : "失败", saveFlag > 0 ? HttpStatus.OK : HttpStatus.BAD_REQUEST);

        context.setResponseEntity(responseEntity);
    }

    /**
     * 刷新 菜单组ID
     * @param basePrivilegeDto
     */
    private void freshPId(BasePrivilegeDto basePrivilegeDto) {

        if(!StringUtils.isEmpty(basePrivilegeDto.getpId())){
            return ;
        }
        //生成流水
        basePrivilegeDto.setpId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.BASE_PRIVILEGE));
    }
}
