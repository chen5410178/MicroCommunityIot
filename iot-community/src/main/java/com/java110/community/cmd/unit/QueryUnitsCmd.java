package com.java110.community.cmd.unit;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.unit.UnitDto;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.doc.annotation.*;
import com.java110.intf.community.IFloorInnerServiceSMO;
import com.java110.intf.community.IUnitInnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@Java110CmdDoc(title = "查询单元",
        description = "用于外系统查询单元信息功能",
        httpMethod = "get",
        url = "http://{ip}:{port}/iot/api/unit.queryUnits",
        resource = "communityDoc",
        author = "吴学文",
        serviceCode = "unit.queryUnits"
)

@Java110ParamsDoc(params = {
        @Java110ParamDoc(name = "communityId", length = 30, remark = "小区ID"),
        @Java110ParamDoc(name = "floorId", length = 30, remark = "楼栋ID"),
        @Java110ParamDoc(name = "page", type = "int",length = 11, remark = "页数"),
        @Java110ParamDoc(name = "row", type = "int",length = 11, remark = "行数"),
})

@Java110ResponseDoc(
        params = {
                @Java110ParamDoc(name = "code", type = "int", length = 11, defaultValue = "0", remark = "返回编号，0 成功 其他失败"),
                @Java110ParamDoc(name = "msg", type = "String", length = 250, defaultValue = "成功", remark = "描述"),
                @Java110ParamDoc(name = "records", type = "int", length = 11, defaultValue = "1", remark = "总页数"),
                @Java110ParamDoc(name = "total", type = "int", length = 11, defaultValue = "1", remark = "总记录数"),
                @Java110ParamDoc(name = "data", type = "Array", remark = "有效数据"),
                @Java110ParamDoc(parentNodeName = "data", name = "unitId", type = "String", length = 30,  remark = "单元ID"),
                @Java110ParamDoc(parentNodeName = "data", name = "unitNum",  type = "String", length = 250,  remark = "单元编号"),
                @Java110ParamDoc(parentNodeName = "data", name = "seq",  type = "int", length = 11,  remark = "排序"),
        }
)

@Java110ExampleDoc(
        reqBody="http://{ip}:{port}/app/unit.queryUnits?page=1&row=10&communityId=123123&floorId=123",
        resBody="{'code':0,'msg':'成功','records':1,'total':1,'data':[{'unitId':'123123','unitNum':'123123','seq':1}]}"
)

@Java110Cmd(serviceCode = "unit.queryUnits")
public class QueryUnitsCmd extends Cmd {
    @Autowired
    private IUnitInnerServiceSMO unitInnerServiceSMOImpl;

    @Autowired
    private IFloorInnerServiceSMO floorInnerServiceSMOImpl;



    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        Assert.jsonObjectHaveKey(reqJson, "communityId", "请求中未包含communityId信息");
        //Assert.jsonObjectHaveKey(reqJson, "floorId", "请求中未包含floorId信息");
        //校验小区楼ID和小区是否有对应关系
//        int total = floorInnerServiceSMOImpl.queryFloorsCount(BeanConvertUtil.covertBean(reqJson, FloorDto.class));
//
//        if (total < 1) {
//            throw new IllegalArgumentException("传入小区楼ID不是该小区的楼");
//        }
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {
        UnitDto unitDto = BeanConvertUtil.covertBean(reqJson, UnitDto.class);
        unitDto.setUserId("");

        List<UnitDto> unitDtoList = unitInnerServiceSMOImpl.queryUnits(unitDto);

        int total = unitInnerServiceSMOImpl.queryUnitsCount(unitDto);
        int row = reqJson.containsKey("row") ? reqJson.getIntValue("row") : (total > 0 ? total : 1);
        int records = (int) Math.ceil((double) total / (double) row);

        ResultVo resultVo = new ResultVo(records, total, unitDtoList);
        cmdDataFlowContext.setResponseEntity(ResultVo.createResponseEntity(resultVo));
    }
}
