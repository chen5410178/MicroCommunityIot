package com.java110.accessControl.cmd.accessControlShop;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.accessControl.bmo.ISaveAccessControlFaceBMO;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.annotation.Java110Transactional;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.ListUtil;
import com.java110.doc.annotation.*;
import com.java110.dto.shop.ShopLocationDto;
import com.java110.intf.accessControl.IAccessControlShopV1InnerServiceSMO;
import com.java110.intf.community.IShopLocationV1InnerServiceSMO;
import com.java110.po.accessControlShop.AccessControlShopPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 类表述：保存商铺授权门禁
 * 服务编码：accessControlShop.saveAccessControlShop
 * 请求路劲：/iot/api/accessControlShop.saveAccessControlShop
 */
@Java110CmdDoc(title = "商铺授权门禁",
        description = "按位置批量授权商铺业主门禁",
        httpMethod = "post",
        url = "http://{ip}:{port}/iot/api/accessControlShop.saveAccessControlShop",
        resource = "accessControlDoc",
        author = "吴学文",
        serviceCode = "accessControlShop.saveAccessControlShop",
        seq = 9
)

@Java110ParamsDoc(params = {
        @Java110ParamDoc(name = "communityId", length = 30, remark = "小区ID"),
        @Java110ParamDoc(name = "locationId", length = 64, remark = "位置ID（必填）"),
        @Java110ParamDoc(name = "machineIds", length = 64, type = "Array", remark = "设备编号"),
        @Java110ParamDoc(name = "startDate", length = 64, remark = "生效开始时间"),
        @Java110ParamDoc(name = "endDate", length = 64, remark = "生效结束时间"),
})

@Java110ResponseDoc(
        params = {
                @Java110ParamDoc(name = "code", type = "int", length = 11, defaultValue = "0", remark = "返回编号，0 成功 其他失败"),
                @Java110ParamDoc(name = "msg", type = "String", length = 250, defaultValue = "成功", remark = "描述"),
        }
)

@Java110ExampleDoc(
        reqBody = "{\"machineIds\":[123123,123123],\"locationId\":\"22\",\"startDate\":\"2026-08-18 00:00:00\",\"endDate\":\"2026-08-18 23:59:59\",\"communityId\":\"2022081539020475\"}",
        resBody = "{'code':0,'msg':'成功'}"
)
@Java110Cmd(serviceCode = "accessControlShop.saveAccessControlShop")
public class SaveAccessControlShopCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(SaveAccessControlShopCmd.class);

    public static final String CODE_PREFIX_ID = "10";

    @Autowired
    private IAccessControlShopV1InnerServiceSMO accessControlShopV1InnerServiceSMOImpl;

    @Autowired
    private IShopLocationV1InnerServiceSMO shopLocationV1InnerServiceSMOImpl;

    @Autowired
    private ISaveAccessControlFaceBMO saveAccessControlFaceBMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "communityId", "请求报文中未包含communityId");
        Assert.hasKeyAndValue(reqJson, "locationId", "请求报文中未包含locationId");
        Assert.hasKeyAndValue(reqJson, "startDate", "请求报文中未包含startDate");
        Assert.hasKeyAndValue(reqJson, "endDate", "请求报文中未包含endDate");

        JSONArray machineIds = reqJson.getJSONArray("machineIds");
        if (ListUtil.isNull(machineIds)) {
            throw new CmdException("未包含门禁");
        }

        String startDate = reqJson.getString("startDate");
        String endDate = reqJson.getString("endDate");

        if (!startDate.contains(":")) {
            startDate += " 00:00:00";
            reqJson.put("startDate", startDate);
        }
        if (!endDate.contains(":")) {
            endDate += " 23:59:59";
            reqJson.put("endDate", endDate);
        }
    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

        //todo 校验位置存在并补查位置名称
        ShopLocationDto shopLocationDto = new ShopLocationDto();
        shopLocationDto.setLocationId(reqJson.getString("locationId"));
        shopLocationDto.setCommunityId(reqJson.getString("communityId"));
        List<ShopLocationDto> shopLocationDtos = shopLocationV1InnerServiceSMOImpl.queryShopLocations(shopLocationDto);

        if (ListUtil.isNull(shopLocationDtos)) {
            throw new CmdException("位置不存在");
        }

        String locationName = shopLocationDtos.get(0).getLocationName();
        JSONArray machineIds = reqJson.getJSONArray("machineIds");

        //todo 保存 商铺授权
        for (int machineIndex = 0; machineIndex < machineIds.size(); machineIndex++) {
            doComputeAccessControl(locationName, machineIds.getString(machineIndex), reqJson);
        }

        cmdDataFlowContext.setResponseEntity(ResultVo.success());
    }

    private void doComputeAccessControl(String locationName, String machineId, JSONObject reqJson) {

        AccessControlShopPo accessControlShopPo = new AccessControlShopPo();
        accessControlShopPo.setAcsId(GenerateCodeFactory.getGeneratorId(CODE_PREFIX_ID));
        accessControlShopPo.setMachineId(machineId);
        accessControlShopPo.setCommunityId(reqJson.getString("communityId"));
        accessControlShopPo.setLocationId(reqJson.getString("locationId"));
        accessControlShopPo.setLocationName(locationName);
        int flag = accessControlShopV1InnerServiceSMOImpl.saveAccessControlShop(accessControlShopPo);

        if (flag < 1) {
            throw new CmdException("保存数据失败");
        }

        //todo  查询位置下商铺业主 异步关联门禁人员
        saveAccessControlFaceBMOImpl.saveShopOwner(accessControlShopPo, machineId, reqJson.getString("startDate"), reqJson.getString("endDate"));
    }
}
