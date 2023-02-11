package com.java110.dev.cmd.menu;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.constant.ResponseConstant;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.menu.MenuDto;
import com.java110.intf.user.IMenuV1InnerServiceSMO;
import com.java110.po.menu.MenuPo;
import org.springframework.beans.factory.annotation.Autowired;

@Java110Cmd(serviceCode = "menu.updateMenu")
public class UpdateMenuCmd extends Cmd {

    @Autowired
    private IMenuV1InnerServiceSMO menuInnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        Assert.hasKeyAndValue(reqJson, "mId", "菜单ID不能为空");
        Assert.hasKeyAndValue(reqJson, "name", "必填，请填写菜单名称");
        Assert.hasKeyAndValue(reqJson, "url", "必填，请菜单菜单地址");
        Assert.hasKeyAndValue(reqJson, "seq", "必填，请填写序列");
        Assert.hasKeyAndValue(reqJson, "isShow", "必填，请选择是否显示菜单");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        MenuPo menuDto = BeanConvertUtil.covertBean(reqJson, MenuPo.class);


        int count = menuInnerServiceSMOImpl.updateMenu(menuDto);

        if (count < 1) {
            throw new CmdException(ResponseConstant.RESULT_CODE_ERROR, "修改数据失败");
        }


        context.setResponseEntity(ResultVo.success());
    }
}
