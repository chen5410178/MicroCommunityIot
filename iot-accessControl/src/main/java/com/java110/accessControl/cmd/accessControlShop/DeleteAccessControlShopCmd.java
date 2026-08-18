package com.java110.accessControl.cmd.accessControlShop;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.annotation.Java110Transactional;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.ListUtil;
import com.java110.doc.annotation.*;
import com.java110.dto.accessControlFace.AccessControlFaceDto;
import com.java110.dto.accessControlShop.AccessControlShopDto;
import com.java110.intf.accessControl.IAccessControlFaceV1InnerServiceSMO;
import com.java110.intf.accessControl.IAccessControlShopV1InnerServiceSMO;
import com.java110.po.accessControlFace.AccessControlFacePo;
import com.java110.po.accessControlShop.AccessControlShopPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 类表述：删除商铺授权门禁
 * 服务编码：accessControlShop.deleteAccessControlShop
 * 请求路劲：/iot/api/accessControlShop.deleteAccessControlShop
 */
@Java110CmdDoc(title = "删除商铺授权",
        description = "用于删除商铺授权并清理门禁人员功能",
        httpMethod = "post",
        url = "http://{ip}:{port}/iot/api/accessControlShop.deleteAccessControlShop",
        resource = "accessControlDoc",
        author = "吴学文",
        serviceCode = "accessControlShop.deleteAccessControlShop",
        seq = 10
)

@Java110ParamsDoc(params = {
        @Java110ParamDoc(name = "communityId", length = 30, remark = "小区ID"),
        @Java110ParamDoc(name = "acsId", length = 64, remark = "编号"),

})

@Java110ResponseDoc(
        params = {
                @Java110ParamDoc(name = "code", type = "int", length = 11, defaultValue = "0", remark = "返回编号，0 成功 其他失败"),
                @Java110ParamDoc(name = "msg", type = "String", length = 250, defaultValue = "成功", remark = "描述"),
        }
)

@Java110ExampleDoc(
        reqBody = "{\"acsId\":\"22\",\"communityId\":\"2022081539020475\"}",
        resBody = "{'code':0,'msg':'成功'}"
)
@Java110Cmd(serviceCode = "accessControlShop.deleteAccessControlShop")
public class DeleteAccessControlShopCmd extends Cmd {
    private static Logger logger = LoggerFactory.getLogger(DeleteAccessControlShopCmd.class);

    @Autowired
    private IAccessControlShopV1InnerServiceSMO accessControlShopV1InnerServiceSMOImpl;

    @Autowired
    private IAccessControlFaceV1InnerServiceSMO accessControlFaceV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "acsId", "acsId不能为空");
        Assert.hasKeyAndValue(reqJson, "communityId", "communityId不能为空");
    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

        // todo 查询 商铺授权是否存在
        AccessControlShopDto accessControlShopDto = new AccessControlShopDto();
        accessControlShopDto.setAcsId(reqJson.getString("acsId"));
        accessControlShopDto.setCommunityId(reqJson.getString("communityId"));
        List<AccessControlShopDto> accessControlShopDtos = accessControlShopV1InnerServiceSMOImpl.queryAccessControlShops(accessControlShopDto);
        Assert.listOnlyOne(accessControlShopDtos, "商铺授权不存在");

        AccessControlShopPo accessControlShopPo = BeanConvertUtil.covertBean(reqJson, AccessControlShopPo.class);
        int flag = accessControlShopV1InnerServiceSMOImpl.deleteAccessControlShop(accessControlShopPo);

        if (flag < 1) {
            throw new CmdException("删除数据失败");
        }

        //todo 删除该授权下的门禁人员
        AccessControlFaceDto accessControlFaceDto = new AccessControlFaceDto();
        accessControlFaceDto.setComeId(reqJson.getString("acsId"));
        accessControlFaceDto.setComeType(AccessControlFaceDto.COME_TYPE_SHOP);
        accessControlFaceDto.setCommunityId(reqJson.getString("communityId"));
        List<AccessControlFaceDto> accessControlFaceDtos = accessControlFaceV1InnerServiceSMOImpl.queryAccessControlFaces(accessControlFaceDto);

        if (!ListUtil.isNull(accessControlFaceDtos)) {
            for (AccessControlFaceDto tmpAccessControlFaceDto : accessControlFaceDtos) {
                AccessControlFacePo accessControlFacePo = new AccessControlFacePo();
                accessControlFacePo.setComeId(tmpAccessControlFaceDto.getComeId());
                accessControlFacePo.setCommunityId(tmpAccessControlFaceDto.getCommunityId());
                accessControlFacePo.setMfId(tmpAccessControlFaceDto.getMfId());
                accessControlFacePo.setPersonId(tmpAccessControlFaceDto.getPersonId());
                accessControlFacePo.setName(tmpAccessControlFaceDto.getName());
                accessControlFacePo.setMachineId(tmpAccessControlFaceDto.getMachineId());
                accessControlFaceV1InnerServiceSMOImpl.deleteAccessControlFace(accessControlFacePo);
            }
        }

        cmdDataFlowContext.setResponseEntity(ResultVo.success());
    }
}
