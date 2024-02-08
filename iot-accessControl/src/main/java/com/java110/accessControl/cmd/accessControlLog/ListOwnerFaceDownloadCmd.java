package com.java110.accessControl.cmd.accessControlLog;

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
import com.java110.dto.accessControlLog.AccessControlLogDto;
import com.java110.dto.appUser.AppUserDto;
import com.java110.intf.accessControl.IAccessControlLogV1InnerServiceSMO;
import com.java110.intf.user.IAppUserV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * 查询业主人脸下发日志
 */
@Java110Cmd(serviceCode = "accessControlLog.listOwnerFaceDownload")
public class ListOwnerFaceDownloadCmd extends Cmd {

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Autowired
    private IAppUserV1InnerServiceSMO appUserV1InnerServiceSMOImpl;

    @Autowired
    private IAccessControlLogV1InnerServiceSMO accessControlLogV1InnerServiceSMOImpl;

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
        AccessControlLogDto accessControlLogDto = BeanConvertUtil.covertBean(reqJson, AccessControlLogDto.class);

        accessControlLogDto.setUserId(reqJson.getString("memberId"));
        accessControlLogDto.setLogCmds(new String[]{AccessControlLogDto.CMD_ADD_FACE,AccessControlLogDto.CMD_UPDATE_FACE,AccessControlLogDto.CMD_DELETE_FACE});

        int count = accessControlLogV1InnerServiceSMOImpl.queryAccessControlLogsCount(accessControlLogDto);

        List<AccessControlLogDto> accessControlLogDtos = null;

        if (count > 0) {
            accessControlLogDtos = accessControlLogV1InnerServiceSMOImpl.queryAccessControlLogs(accessControlLogDto);
        } else {
            accessControlLogDtos = new ArrayList<>();
        }

        ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) reqJson.getInteger("row")), count, accessControlLogDtos);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        context.setResponseEntity(responseEntity);
    }
}
