package com.java110.user.cmd.owner;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.file.FileRelDto;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cache.MappingCache;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.constant.MappingConstant;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.CmdContextUtils;
import com.java110.core.utils.ListUtil;
import com.java110.dto.appUser.AppUserDto;
import com.java110.intf.system.IFileRelInnerServiceSMO;
import com.java110.intf.user.IAppUserV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.List;

@Java110Cmd(serviceCode = "owner.listOwnerFace")
public class ListOwnerFaceCmd extends Cmd {

    @Autowired
    private IAppUserV1InnerServiceSMO appUserV1InnerServiceSMOImpl;

    @Autowired
    private IFileRelInnerServiceSMO fileRelInnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        String userId = CmdContextUtils.getUserId(context);


        AppUserDto appUserDto = new AppUserDto();
        appUserDto.setUserId(userId);
        List<AppUserDto> appUserDtos = appUserV1InnerServiceSMOImpl.queryAppUsers(appUserDto);

        if(ListUtil.isNull(appUserDtos)){
            throw new CmdException("业主未认证");
        }

        reqJson.put("memberId",appUserDtos.get(0).getMemberId());

    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        String ownerUrl = "";
        FileRelDto fileRelDto = new FileRelDto();
        fileRelDto.setObjId(reqJson.getString("memberId"));
        fileRelDto.setRelTypeCd("10000"); //人员照片
        List<FileRelDto> fileRelDtos = fileRelInnerServiceSMOImpl.queryFileRels(fileRelDto);
        String imgUrl = MappingCache.getValue(MappingConstant.FILE_DOMAIN, "IMG_PATH");

        if (!ListUtil.isNull(fileRelDtos)) {
            if (fileRelDtos.get(0).getFileSaveName().startsWith("http")) {
                ownerUrl = fileRelDtos.get(0).getFileRealName();
            } else {
                ownerUrl = imgUrl + fileRelDtos.get(0).getFileRealName();
            }
        }
        context.setResponseEntity(ResultVo.createResponseEntity(ownerUrl));
    }
}
