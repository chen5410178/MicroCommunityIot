package com.java110.system.cmd.log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.log.AssetImportLogDetailDto;
import com.java110.intf.system.IAssetImportLogDetailInnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.util.List;

@Java110Cmd(serviceCode = "log.queryAssetImportLogDetail")
public class QueryAssetImportLogDetailCmd extends Cmd {

    @Autowired
    IAssetImportLogDetailInnerServiceSMO assetImportLogDetailInnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson, "communityId", "未包含小区ID");
        super.validatePageInfo(reqJson);
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        AssetImportLogDetailDto assetImportLogDetailDto = BeanConvertUtil.covertBean(reqJson,AssetImportLogDetailDto.class);

        int count = assetImportLogDetailInnerServiceSMOImpl.queryAssetImportLogDetailsCount(assetImportLogDetailDto);

        List<AssetImportLogDetailDto> assetImportLogDetailDtos = null;

        JSONArray datas = null;
        if (count > 0) {
            assetImportLogDetailDtos = assetImportLogDetailInnerServiceSMOImpl.queryAssetImportLogDetails(assetImportLogDetailDto);
            // todo 转换为jsonArray
            datas = covertToData(assetImportLogDetailDtos);
        } else {
            datas = new JSONArray();
        }

        ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) assetImportLogDetailDto.getRow()), count, datas);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        context.setResponseEntity(responseEntity);
    }

    private JSONArray covertToData(List<AssetImportLogDetailDto> assetImportLogDetailDtos) {
        JSONArray datas = new JSONArray();

        if (assetImportLogDetailDtos == null || assetImportLogDetailDtos.size() < 1) {
            return datas;
        }

        JSONObject data = null;

        for (AssetImportLogDetailDto assetImportLogDetailDto : assetImportLogDetailDtos) {
            data = BeanConvertUtil.beanCovertJson(assetImportLogDetailDto);
            if (!StringUtil.isEmpty(assetImportLogDetailDto.getContent())) {
                data.putAll(JSONObject.parseObject(assetImportLogDetailDto.getContent()));
            }
            datas.add(data);
        }

        return datas;
    }
}
