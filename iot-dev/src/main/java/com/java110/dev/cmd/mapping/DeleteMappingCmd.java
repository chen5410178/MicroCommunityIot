package com.java110.dev.cmd.mapping;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.dto.mapping.MappingDto;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.constant.ResponseConstant;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.intf.dev.IMappingV1InnerServiceSMO;
import com.java110.po.mapping.MappingPo;
import org.springframework.beans.factory.annotation.Autowired;
import com.java110.bean.ResultVo;

@Java110Cmd(serviceCode = "mapping.deleteMapping")
public class DeleteMappingCmd extends Cmd {
    @Autowired
    private IMappingV1InnerServiceSMO mappingInnerServiceSMOImpl;
    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        Assert.hasKeyAndValue(reqJson, "id", "编码ID不能为空");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        MappingPo mappingDto = BeanConvertUtil.covertBean(reqJson, MappingPo.class);

        int count = mappingInnerServiceSMOImpl.deleteMapping(mappingDto);

        if (count < 1) {
            throw new CmdException(ResponseConstant.RESULT_CODE_ERROR, "编辑数据失败");
        }

        context.setResponseEntity(ResultVo.success());
    }
}
