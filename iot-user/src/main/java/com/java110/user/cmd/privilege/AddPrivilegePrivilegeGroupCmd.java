package com.java110.user.cmd.privilege;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.privilegeRel.PrivilegeRelDto;
import com.java110.dto.store.StoreDto;
import com.java110.intf.user.IPrivilegeRelV1InnerServiceSMO;
import com.java110.intf.user.IPrivilegeV1InnerServiceSMO;
import com.java110.intf.user.IStoreV1InnerServiceSMO;
import com.java110.po.privilegeRel.PrivilegeRelPo;
import com.java110.user.bmo.IPrivilegeAssignBMO;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Java110Cmd(serviceCode = "add.privilege.PrivilegeGroup")
public class AddPrivilegePrivilegeGroupCmd extends Cmd {

    @Autowired
    private IStoreV1InnerServiceSMO storeV1InnerServiceSMOImpl;

    @Autowired
    private IPrivilegeV1InnerServiceSMO privilegeInnerServiceSMOImpl;

    @Autowired
    private IPrivilegeRelV1InnerServiceSMO privilegeRelV1InnerServiceSMOImpl;

    @Autowired
    private IPrivilegeAssignBMO privilegeAssignBMOImpl;


    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {

        Assert.hasKeyAndValue(reqJson, "pgId", "请求报文中未包含角色");

        if (!reqJson.containsKey("pIds")) {
            throw new CmdException("未包含权限");
        }

        JSONArray pIds = reqJson.getJSONArray("pIds");

        if (pIds.size() < 1) {
            throw new CmdException("未包含权限");
        }


        String storeId = context.getReqHeaders().get("store-id");

        StoreDto storeDto = new StoreDto();
        storeDto.setStoreId(storeId);
        storeDto.setPage(1);
        storeDto.setRow(1);
        List<StoreDto> storeDtos = storeV1InnerServiceSMOImpl.queryStores(storeDto);

        Assert.listOnlyOne(storeDtos, "商户不存在");

        reqJson.put("storeId", storeDtos.get(0).getStoreId());
        reqJson.put("storeTypeCd", storeDtos.get(0).getStoreTypeCd());

        String userId = context.getReqHeaders().get("user-id");
        List<String> pIdList = new ArrayList<>();
        for (int pIdIndex = 0; pIdIndex < pIds.size(); pIdIndex++) {
            pIdList.add(pIds.getJSONObject(pIdIndex).getString("pId"));
        }
        privilegeAssignBMOImpl.validateAssignable(storeId, userId, reqJson.getString("storeTypeCd"), pIdList);

    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {


        JSONArray pIds = reqJson.getJSONArray("pIds");
        int errorCount = 0;
        JSONObject tmpPId = null;
        PrivilegeRelPo tmpPrivilegePo = null;
        int flag = 0;
        for (int pIdIndex = 0; pIdIndex < pIds.size(); pIdIndex++) {
            tmpPId = pIds.getJSONObject(pIdIndex);
            reqJson.put("pId", tmpPId.getString("pId"));
            tmpPrivilegePo = BeanConvertUtil.covertBean(reqJson, PrivilegeRelPo.class);
            tmpPrivilegePo.setRelId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_relId));
            tmpPrivilegePo.setLevelCd("2002");//用户级
            flag = privilegeRelV1InnerServiceSMOImpl.savePrivilegeRel(tmpPrivilegePo);
            if (flag < 1) {
                errorCount++;
            }
        }

        JSONObject paramOut = new JSONObject();
        paramOut.put("success", pIds.size() - errorCount);
        paramOut.put("error", errorCount);


        context.setResponseEntity(ResultVo.createResponseEntity(paramOut.toJSONString()));
    }
}
