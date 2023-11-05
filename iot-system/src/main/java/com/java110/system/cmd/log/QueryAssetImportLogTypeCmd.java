package com.java110.system.cmd.log;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.log.AssetImportLogTypeDto;
import com.java110.intf.system.IAssetImportLogInnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.List;

/**
 * 查询导入类型 字段
 */
@Java110Cmd(serviceCode = "log.queryAssetImportLogType")
public class QueryAssetImportLogTypeCmd extends Cmd {

    @Autowired
    private IAssetImportLogInnerServiceSMO assetImportLogInnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson, "logType", "未包含类型");

    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        AssetImportLogTypeDto assetImportLogTypeDto = BeanConvertUtil.covertBean(reqJson, AssetImportLogTypeDto.class);
        List<AssetImportLogTypeDto> assetImportLogTypes = assetImportLogInnerServiceSMOImpl.queryAssetImportLogType(assetImportLogTypeDto);

        context.setResponseEntity(ResultVo.createResponseEntity(assetImportLogTypes));
    }
}
