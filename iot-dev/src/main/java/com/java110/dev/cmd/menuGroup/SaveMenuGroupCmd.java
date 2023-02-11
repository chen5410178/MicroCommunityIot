package com.java110.dev.cmd.menuGroup;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.menuGroup.MenuGroupDto;
import com.java110.intf.user.IMenuGroupV1InnerServiceSMO;
import com.java110.po.menuGroup.MenuGroupPo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Java110Cmd(serviceCode = "menuGroup.saveMenuGroup")
public class SaveMenuGroupCmd extends Cmd {

    @Autowired
    private IMenuGroupV1InnerServiceSMO menuInnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        Assert.hasKeyAndValue(reqJson, "name", "必填，请填写组名称");
        Assert.hasKeyAndValue(reqJson, "icon", "必填，请填写icon");
        Assert.hasKeyAndValue(reqJson, "seq", "必填，请填写序列");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        ResponseEntity<String> responseEntity = null;

        MenuGroupPo menuGroupDto = BeanConvertUtil.covertBean(reqJson, MenuGroupPo.class);

        freshGId(menuGroupDto);


        int saveFlag = menuInnerServiceSMOImpl.saveMenuGroup(menuGroupDto);

        responseEntity = new ResponseEntity<String>(saveFlag > 0 ? "成功" : "失败", saveFlag > 0 ? HttpStatus.OK : HttpStatus.BAD_REQUEST);

        context.setResponseEntity(responseEntity);
    }

    /**
     * 刷新 菜单组ID
     * @param menuGroupDto
     */
    private void freshGId(MenuGroupPo menuGroupDto) {

        if(!StringUtils.isEmpty(menuGroupDto.getgId())){
            return ;
        }
        //生成流水
        menuGroupDto.setgId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.MENU_GROUP));
    }
}
