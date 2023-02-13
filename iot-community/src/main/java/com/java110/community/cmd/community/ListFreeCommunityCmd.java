package com.java110.community.cmd.community;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.community.CommunityDto;
import com.java110.intf.community.ICommunityV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Java110Cmd(serviceCode = "community.listFreeCommunity")
public class ListFreeCommunityCmd extends Cmd {

    @Autowired
    private ICommunityV1InnerServiceSMO communityV1InnerServiceSMOImpl;
    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        super.validatePageInfo(reqJson);

    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        CommunityDto communityDto = BeanConvertUtil.covertBean(reqJson, CommunityDto.class);

        int count = communityV1InnerServiceSMOImpl.queryFreeCommunitysCount(communityDto);

        List<CommunityDto> communityDtos = null;
        if (count > 0) {
            communityDtos = communityV1InnerServiceSMOImpl.queryFreeCommunitys(communityDto);

        } else {
            communityDtos = new ArrayList<>();
        }
        ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) reqJson.getInteger("row")), count, communityDtos);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        context.setResponseEntity(responseEntity);
    }
}
