package com.java110.user.cmd.passcode;

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
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.dto.passQrcode.PassQrcodeDto;
import com.java110.intf.user.IPassQrcodeV1InnerServiceSMO;
import com.java110.po.passQrcode.PassQrcodePo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Java110Cmd(serviceCode = "passcode.refreshPassQrcode")
public class RefreshPassQrcodeCmd extends Cmd {

    @Autowired
    private IPassQrcodeV1InnerServiceSMO passQrcodeV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson, "communityId", "communityId不能为空");
        Assert.hasKeyAndValue(reqJson, "openId", "openId不能为空");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        PassQrcodeDto passQrcodeDto = new PassQrcodeDto();
        passQrcodeDto.setOpenId(reqJson.getString("openId"));
        passQrcodeDto.setCommunityId(reqJson.getString("communityId"));
        passQrcodeDto.setPage(1);
        passQrcodeDto.setRow(1);
        passQrcodeDto.setStates(new String[]{"W","C"});

        int count = passQrcodeV1InnerServiceSMOImpl.queryPassQrcodesCount(passQrcodeDto);

        List<PassQrcodeDto> passQrcodeDtos = null;

        if (count > 0) {
            passQrcodeDtos = passQrcodeV1InnerServiceSMOImpl.queryPassQrcodes(passQrcodeDto);
            refreshQrcode(passQrcodeDtos);
        } else {
            passQrcodeDtos = new ArrayList<>();
        }

        ResultVo resultVo = new ResultVo(1, count, passQrcodeDtos);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        context.setResponseEntity(responseEntity);
    }

    private void refreshQrcode(List<PassQrcodeDto> passQrcodeDtos) {

        if(ListUtil.isNull(passQrcodeDtos)){
            return;
        }

        PassQrcodePo passQrcodePo = new PassQrcodePo();
        passQrcodePo.setPqId(passQrcodeDtos.get(0).getPqId());
        passQrcodePo.setQrcodeTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        passQrcodePo.setQrcode(GenerateCodeFactory.getUUID());
        passQrcodeV1InnerServiceSMOImpl.updatePassQrcode(passQrcodePo);

        passQrcodeDtos.get(0).setQrcode(passQrcodePo.getQrcode());
        passQrcodeDtos.get(0).setQrcodeTime(passQrcodePo.getQrcodeTime());


    }
}
