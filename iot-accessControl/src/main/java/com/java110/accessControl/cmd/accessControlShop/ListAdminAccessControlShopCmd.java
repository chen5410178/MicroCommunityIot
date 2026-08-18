package com.java110.accessControl.cmd.accessControlShop;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.ListUtil;
import com.java110.dto.accessControlFace.AccessControlFaceDto;
import com.java110.dto.accessControlShop.AccessControlShopDto;
import com.java110.intf.accessControl.IAccessControlFaceV1InnerServiceSMO;
import com.java110.intf.accessControl.IAccessControlShopV1InnerServiceSMO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 类表述：运营侧查询商铺授权门禁
 * 服务编码：accessControlShop.listAdminAccessControlShop
 * 请求路劲：/iot/api/accessControlShop.listAdminAccessControlShop
 */
@Java110Cmd(serviceCode = "accessControlShop.listAdminAccessControlShop")
public class ListAdminAccessControlShopCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(ListAdminAccessControlShopCmd.class);

    @Autowired
    private IAccessControlShopV1InnerServiceSMO accessControlShopV1InnerServiceSMOImpl;

    @Autowired
    private IAccessControlFaceV1InnerServiceSMO accessControlFaceV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        super.validatePageInfo(reqJson);
        super.assertAdmin(cmdDataFlowContext);
        Assert.hasKeyAndValue(reqJson, "communityId", "未包含小区");
        Assert.hasKeyAndValue(reqJson, "locationId", "未包含位置");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

        AccessControlShopDto accessControlShopDto = BeanConvertUtil.covertBean(reqJson, AccessControlShopDto.class);

        int count = accessControlShopV1InnerServiceSMOImpl.queryAccessControlShopsCount(accessControlShopDto);

        List<AccessControlShopDto> accessControlShopDtos = null;

        if (count > 0) {
            accessControlShopDtos = accessControlShopV1InnerServiceSMOImpl.queryAccessControlShops(accessControlShopDto);

            //todo 查询授权人员
            queryShopAuthOwner(accessControlShopDtos);
        } else {
            accessControlShopDtos = new ArrayList<>();
        }

        ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) reqJson.getInteger("row")), count, accessControlShopDtos);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        cmdDataFlowContext.setResponseEntity(responseEntity);
    }

    /**
     * 计算位置授权人员
     *
     * @param accessControlShopDtos
     */
    private void queryShopAuthOwner(List<AccessControlShopDto> accessControlShopDtos) {

        if (ListUtil.isNull(accessControlShopDtos)) {
            return;
        }
        List<String> acsIds = new ArrayList<>();
        for (AccessControlShopDto accessControlShopDto : accessControlShopDtos) {
            acsIds.add(accessControlShopDto.getAcsId());
        }

        AccessControlFaceDto accessControlFaceDto = new AccessControlFaceDto();
        accessControlFaceDto.setComeIds(acsIds.toArray(new String[acsIds.size()]));
        accessControlFaceDto.setCommunityId(accessControlShopDtos.get(0).getCommunityId());
        List<AccessControlFaceDto> accessControlFaceDtos = accessControlFaceV1InnerServiceSMOImpl.queryAccessControlFacesByComeId(accessControlFaceDto);

        if (ListUtil.isNull(accessControlFaceDtos)) {
            return;
        }

        for (AccessControlShopDto accessControlShopDto : accessControlShopDtos) {
            for (AccessControlFaceDto tAccessControlFaceDto : accessControlFaceDtos) {
                if (!tAccessControlFaceDto.getComeId().equals(accessControlShopDto.getAcsId())) {
                    continue;
                }
                accessControlShopDto.setPersonCount(tAccessControlFaceDto.getPersonCount());
            }
        }
    }
}
