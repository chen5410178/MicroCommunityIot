package com.java110.accessControl.cmd.visit;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.CmdContextUtils;
import com.java110.dto.appUser.AppUserDto;
import com.java110.dto.visit.VisitDto;
import com.java110.intf.accessControl.IVisitV1InnerServiceSMO;
import com.java110.intf.user.IAppUserV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Java110Cmd(serviceCode = "visit.listOwnerVisit")
public class ListOwnerVisitCmd extends Cmd {

    @Autowired
    private IAppUserV1InnerServiceSMO appUserV1InnerServiceSMOImpl;
    @Autowired
    private IVisitV1InnerServiceSMO visitV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        super.validatePageInfo(reqJson);
        Assert.hasKeyAndValue(reqJson, "communityId", "communityId不能为空");

        String userId = CmdContextUtils.getUserId(context);

        AppUserDto appUserDto = new AppUserDto();
        appUserDto.setUserId(userId);
        List<AppUserDto> appUserDtos = appUserV1InnerServiceSMOImpl.queryAppUsers(appUserDto);

        Assert.listOnlyOne(appUserDtos,"未认证房产");

        reqJson.put("memberId",appUserDtos.get(0).getMemberId());
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        VisitDto visitDto = BeanConvertUtil.covertBean(reqJson, VisitDto.class);
        visitDto.setOwnerId(reqJson.getString("memberId"));
        int count = visitV1InnerServiceSMOImpl.queryVisitsCount(visitDto);

        List<VisitDto> visitDtos = null;

        if (count > 0) {
            visitDtos = visitV1InnerServiceSMOImpl.queryVisits(visitDto);
        } else {
            visitDtos = new ArrayList<>();
        }

        ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) reqJson.getInteger("row")), count, visitDtos);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        context.setResponseEntity(responseEntity);
    }
}
