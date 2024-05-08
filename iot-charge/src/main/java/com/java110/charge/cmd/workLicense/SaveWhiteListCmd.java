package com.java110.charge.cmd.workLicense;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.charge.workLicense.IWorkLicenseAdapt;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.CmdContextUtils;
import com.java110.core.utils.ListUtil;
import com.java110.dto.workLicenseFactory.WorkLicenseFactoryDto;
import com.java110.dto.workLicenseMachine.WorkLicenseMachineDto;
import com.java110.intf.charge.IWorkLicenseFactoryV1InnerServiceSMO;
import com.java110.intf.charge.IWorkLicenseMachineV1InnerServiceSMO;
import com.java110.po.whiteList.WhiteListPo;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.List;


@Java110Cmd(serviceCode = "workLicense.saveWhiteList")
public class SaveWhiteListCmd extends Cmd {

    @Autowired
    private IWorkLicenseMachineV1InnerServiceSMO workLicenseMachineV1InnerServiceSMOImpl;

    @Autowired
    private IWorkLicenseFactoryV1InnerServiceSMO workLicenseFactoryV1InnerServiceSMOImpl;


    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson, "machineId", "未包含设备");
        Assert.hasKeyAndValue(reqJson, "tel1", "未包含电话");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        String storeId = CmdContextUtils.getStoreId(context);
        WorkLicenseMachineDto workLicenseMachineDto = new WorkLicenseMachineDto();
        workLicenseMachineDto.setMachineId(reqJson.getString("machineId"));
        workLicenseMachineDto.setStoreId(storeId);
        List<WorkLicenseMachineDto> machineDtos = workLicenseMachineV1InnerServiceSMOImpl.queryWorkLicenseMachines(workLicenseMachineDto);
        if (ListUtil.isNull(machineDtos)) {
            throw new CmdException("工牌不存在");
        }

        WorkLicenseFactoryDto workLicenseFactoryDto = null;

        workLicenseFactoryDto = new WorkLicenseFactoryDto();
        workLicenseFactoryDto.setFactoryId(machineDtos.get(0).getImplBean());
        List<WorkLicenseFactoryDto> workLicenseFactoryDtos = workLicenseFactoryV1InnerServiceSMOImpl.queryWorkLicenseFactorys(workLicenseFactoryDto);

        Assert.listOnlyOne(workLicenseFactoryDtos, "工牌厂家不存在");

        IWorkLicenseAdapt workLicenseAdapt = ApplicationContextFactory.getBean(workLicenseFactoryDtos.get(0).getBeanImpl(), IWorkLicenseAdapt.class);
        if (workLicenseAdapt == null) {
            throw new CmdException("厂家接口未实现");
        }
        WhiteListPo whiteListPo = BeanConvertUtil.covertBean(reqJson, WhiteListPo.class);
        ResultVo resultVo = workLicenseAdapt.saveWhiteList(machineDtos.get(0), whiteListPo);

        context.setResponseEntity(ResultVo.createResponseEntity(resultVo));


    }
}
