package com.java110.user.cmd.owner;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.dto.file.FileDto;
import com.java110.bean.dto.file.FileRelDto;
import com.java110.bean.dto.owner.OwnerAppUserDto;
import com.java110.bean.dto.owner.OwnerDto;
import com.java110.bean.po.file.FileRelPo;
import com.java110.bean.po.owner.OwnerPo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.annotation.Java110Transactional;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.ListUtil;
import com.java110.doc.annotation.*;
import com.java110.intf.system.IFileInnerServiceSMO;
import com.java110.intf.system.IFileRelInnerServiceSMO;
import com.java110.intf.user.IOwnerInnerServiceSMO;
import com.java110.intf.user.IOwnerV1InnerServiceSMO;
import com.java110.user.bmo.ISyncAccessControlBMO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.List;


@Java110CmdDoc(title = "修改业主或者家庭成员人脸",
        description = "主要用于第三方系统 调用此接口修改人脸信息，同步门禁，进出小区或者楼栋",
        httpMethod = "post",
        url = "http://{ip}:{port}/app/owner.uploadOwnerPhoto",
        resource = "userDoc",
        author = "吴学文",
        serviceCode = "owner.uploadOwnerPhoto",
        seq = 13
)

@Java110ParamsDoc(params = {
        @Java110ParamDoc(name = "communityId", length = 30, remark = "小区ID"),
        @Java110ParamDoc(name = "memberId", length = 30, remark = "人员编号，可以通过 查询业主 接口获取到"),
        @Java110ParamDoc(name = "photo", length = -1, remark = "业主人脸 用于同步门禁 人脸开门 base64"),
})

@Java110ResponseDoc(
        params = {
                @Java110ParamDoc(name = "code", type = "int", length = 11, defaultValue = "0", remark = "返回编号，0 成功 其他失败"),
                @Java110ParamDoc(name = "msg", type = "String", length = 250, defaultValue = "成功", remark = "描述"),
        }
)

@Java110ExampleDoc(
        reqBody = "{\n" +
                "\t\"memberId\": 123123123,\n" +
                "\t\"ownerPhoto\": \"\",\n" +
                "\t\"communityId\": \"2022121921870161\"\n" +
                "}",
        resBody = "{\"code\":0,\"msg\":\"成功\"}"
)
@Java110Cmd(serviceCode = "owner.uploadOwnerPhoto")
public class UploadOwnerPhotoCmd extends Cmd {


    @Autowired
    private IFileInnerServiceSMO fileInnerServiceSMOImpl;

    @Autowired
    private IOwnerInnerServiceSMO ownerInnerServiceSMOImpl;

    @Autowired
    private IFileRelInnerServiceSMO fileRelInnerServiceSMOImpl;

    @Autowired
    private IOwnerV1InnerServiceSMO ownerV1InnerServiceSMOImpl;

    @Autowired
    private ISyncAccessControlBMO syncAccessControlBMOImpl;


    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        Assert.jsonObjectHaveKey(reqJson, "memberId", "请求报文中未包含ownerId");
        Assert.jsonObjectHaveKey(reqJson, "photo", "请求报文中未包含photo");
        Assert.jsonObjectHaveKey(reqJson, "communityId", "请求报文中未包含communityId");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        FileRelDto fileRelDto = new FileRelDto();
        fileRelDto.setRelTypeCd("10000");
        fileRelDto.setObjId(reqJson.getString("memberId"));
        List<FileRelDto> fileRelDtos = fileRelInnerServiceSMOImpl.queryFileRels(fileRelDto);
        if (ListUtil.isNull(fileRelDtos)) {
            JSONObject businessUnit = new JSONObject();
            businessUnit.put("fileRelId", GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_fileRelId));
            businessUnit.put("relTypeCd", "10000");
            businessUnit.put("saveWay", "table");
            businessUnit.put("objId", reqJson.getString("memberId"));
            businessUnit.put("fileRealName", reqJson.getString("photo"));
            businessUnit.put("fileSaveName", reqJson.getString("photo"));
            FileRelPo fileRelPo = BeanConvertUtil.covertBean(businessUnit, FileRelPo.class);
            fileRelInnerServiceSMOImpl.saveFileRel(fileRelPo);
        }else{
            JSONObject businessUnit = new JSONObject();
            businessUnit.putAll(BeanConvertUtil.beanCovertMap(fileRelDtos.get(0)));
            businessUnit.put("fileRealName", reqJson.getString("photo"));
            businessUnit.put("fileSaveName", reqJson.getString("photo"));
            FileRelPo fileRelPo = BeanConvertUtil.covertBean(businessUnit, FileRelPo.class);
            fileRelInnerServiceSMOImpl.updateFileRel(fileRelPo);
        }



        //todo 人员同步门禁
        syncAccessControlBMOImpl.syncAccessControl(reqJson.getString("memberId"));
    }

}
