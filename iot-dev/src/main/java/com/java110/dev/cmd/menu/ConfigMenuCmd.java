package com.java110.dev.cmd.menu;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.basePrivilege.BasePrivilegeDto;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.constant.ResponseConstant;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.menu.MenuDto;
import com.java110.dto.menuGroup.MenuGroupDto;
import com.java110.intf.user.IMenuGroupV1InnerServiceSMO;
import com.java110.intf.user.IMenuV1InnerServiceSMO;
import com.java110.intf.user.IPrivilegeV1InnerServiceSMO;
import com.java110.po.menu.MenuPo;
import com.java110.po.menuGroup.MenuGroupPo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@Java110Cmd(serviceCode = "menu.configMenu")
public class ConfigMenuCmd extends Cmd {
    @Autowired
    private IMenuV1InnerServiceSMO menuInnerServiceSMOImpl;

    @Autowired
    private IMenuGroupV1InnerServiceSMO menuGroupV1InnerServiceSMOImpl;

    @Autowired
    private IPrivilegeV1InnerServiceSMO privilegeV1InnerServiceSMOImpl;


    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        JSONArray infos = reqJson.getJSONArray("data");

        Assert.hasKeyByFlowData(infos, "addMenuView", "name", "必填，请填写菜单名称");
        Assert.hasKeyByFlowData(infos, "addMenuView", "url", "必填，请菜单菜单地址");
        Assert.hasKeyByFlowData(infos, "addMenuView", "seq", "必填，请填写序列");
        Assert.hasKeyByFlowData(infos, "addMenuView", "isShow", "必填，请选择是否显示菜单");
        Assert.hasKeyByFlowData(infos, "addPrivilegeView", "name", "必填，请填写权限名称");
        Assert.hasKeyByFlowData(infos, "addPrivilegeView", "domain", "必填，请选择商户类型");
        Assert.hasKeyByFlowData(infos, "addPrivilegeView", "resource", "必填，请填写资源路径");

    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        String userId = context.getReqHeaders().get("user-id");
        JSONArray infos = reqJson.getJSONArray("data");


        JSONObject viewMenuGroupInfo = getObj(infos, "viewMenuGroupInfo");
        JSONObject addMenuView = getObj(infos, "addMenuView");
        JSONObject addPrivilegeView = getObj(infos, "addPrivilegeView");
        if (!hasKey(addMenuView, "mId")) {
            addPrivilegeView.put("mId", GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.MENU));
        } else {
            addPrivilegeView.put("mId", addMenuView.getString("mId"));
        }

        if (!hasKey(viewMenuGroupInfo, "gId")) {
            saveMenuGroup(viewMenuGroupInfo, userId);
        }

        if (!hasKey(addPrivilegeView, "pId")) {
            saveMenuPrivilege(addPrivilegeView, userId);
        }
        if (!hasKey(addMenuView, "mId")) {
            addMenuView.put("mId", addPrivilegeView.getString("mId"));
            addMenuView.put("gId", viewMenuGroupInfo.getString("gId"));
            addMenuView.put("pId", addPrivilegeView.getString("pId"));
            saveMenu(addMenuView, userId);
        }

        JSONObject outParam = new JSONObject();
        outParam.put("gId", viewMenuGroupInfo.getString("gId"));
        outParam.put("pId", addPrivilegeView.getString("pId"));
        outParam.put("mId", addMenuView.getString("mId"));

        context.setResponseEntity(ResultVo.createResponseEntity(outParam));

    }

    private void saveMenuGroup(Map info, String userId) {
        info.put("gId", GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.MENU_GROUP));
        info.put("userId", userId);
        MenuGroupPo menuGroupDto = BeanConvertUtil.covertBean(info, MenuGroupPo.class);
        if (menuGroupV1InnerServiceSMOImpl.saveMenuGroup(menuGroupDto) < 1) {
            throw new CmdException(ResponseConstant.RESULT_CODE_ERROR, "参数异常，保存菜单组失败");
        }
    }

    private void saveMenu(Map info, String userId) {
        //info.put("mId", GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.MENU));
        info.put("userId", userId);
        MenuPo menuDto = BeanConvertUtil.covertBean(info, MenuPo.class);
        if (menuInnerServiceSMOImpl.saveMenu(menuDto) < 1) {
            throw new CmdException(ResponseConstant.RESULT_CODE_ERROR, "参数异常，保存菜单失败");
        }
    }

    private void saveMenuPrivilege(Map info, String userId) {
        info.put("pId", GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.BASE_PRIVILEGE));
        info.put("userId", userId);
        BasePrivilegeDto basePrivilegeDto = BeanConvertUtil.covertBean(info, BasePrivilegeDto.class);
        if (menuInnerServiceSMOImpl.saveBasePrivilege(basePrivilegeDto) < 1) {
            throw new CmdException(ResponseConstant.RESULT_CODE_ERROR, "参数异常，保存菜单权限失败");
        }
    }

    private boolean hasKey(JSONObject info, String key) {
        if (!info.containsKey(key)
                || StringUtil.isEmpty(info.getString(key))
                || info.getString(key).startsWith("-")) {
            return false;
        }
        return true;

    }

    private JSONObject getObj(JSONArray infos, String flowComponent) {

        JSONObject serviceInfo = null;

        for (int infoIndex = 0; infoIndex < infos.size(); infoIndex++) {

            Assert.hasKeyAndValue(infos.getJSONObject(infoIndex), "flowComponent", "未包含服务流程组件名称");

            if (flowComponent.equals(infos.getJSONObject(infoIndex).getString("flowComponent"))) {
                serviceInfo = infos.getJSONObject(infoIndex);
                Assert.notNull(serviceInfo, "未包含服务信息");
                return serviceInfo;
            }
        }

        throw new IllegalArgumentException("未找到组件编码为【" + flowComponent + "】数据");
    }
}
