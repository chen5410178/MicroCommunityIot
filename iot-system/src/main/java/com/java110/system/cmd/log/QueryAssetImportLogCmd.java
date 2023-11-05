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
import com.java110.dto.log.AssetImportLogDto;
import com.java110.intf.system.IAssetImportLogInnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Java110Cmd(serviceCode = "log.queryAssetImportLog")
public class QueryAssetImportLogCmd extends Cmd {

    @Autowired
    private IAssetImportLogInnerServiceSMO assetImportLogInnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        Assert.hasKeyAndValue(reqJson, "communityId", "未包含小区ID");
        super.validatePageInfo(reqJson);
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        AssetImportLogDto assetImportLogDto = BeanConvertUtil.covertBean(reqJson, AssetImportLogDto.class);
        int count = assetImportLogInnerServiceSMOImpl.queryAssetImportLogsCount(assetImportLogDto);

        List<AssetImportLogDto> assetImportLogDtos = null;
        if (count > 0) {
            assetImportLogDtos = assetImportLogInnerServiceSMOImpl.queryAssetImportLogs(assetImportLogDto);
        } else {
            assetImportLogDtos = new ArrayList<>();
        }

        ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) assetImportLogDto.getRow()), count, assetImportLogDtos);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        context.setResponseEntity(responseEntity);
    }
}
