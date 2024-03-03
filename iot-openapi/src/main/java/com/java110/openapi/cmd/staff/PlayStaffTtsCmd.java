package com.java110.openapi.cmd.staff;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.ListUtil;
import com.java110.dto.storeStaff.StoreStaffDto;
import com.java110.dto.workLicenseFactory.WorkLicenseFactoryDto;
import com.java110.dto.workLicenseMachine.WorkLicenseMachineDto;
import com.java110.dto.workLicenseMachine.WorkLicenseTtsTextDto;
import com.java110.intf.charge.IWorkLicenseFactoryV1InnerServiceSMO;
import com.java110.intf.charge.IWorkLicenseMachineV1InnerServiceSMO;
import com.java110.intf.user.IStoreStaffV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.List;

/**
 * 播放员工tts
 */
@Java110Cmd(serviceCode = "staff.playStaffTts")
public class PlayStaffTtsCmd extends Cmd {
    @Autowired
    private IWorkLicenseMachineV1InnerServiceSMO workLicenseMachineV1InnerServiceSMOImpl;

    @Autowired
    private IWorkLicenseFactoryV1InnerServiceSMO workLicenseFactoryV1InnerServiceSMOImpl;

    @Autowired
    private IStoreStaffV1InnerServiceSMO storeStaffV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        Assert.hasKeyAndValue(reqJson, "staffTel", "用工手机号");
        Assert.hasKeyAndValue(reqJson, "ttsText", "播放内容");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        //todo 查询员工是否存在
        StoreStaffDto storeStaffDto = new StoreStaffDto();
        storeStaffDto.setTel(reqJson.getString("staffTel"));
        List<StoreStaffDto> storeStaffDtos = storeStaffV1InnerServiceSMOImpl.queryStoreStaffs(storeStaffDto);
        if (ListUtil.isNull(storeStaffDtos)) {
            throw new CmdException("员工不存在");
        }
        ResultVo resultVo = null;
        for (StoreStaffDto tmpStoreStaffDto : storeStaffDtos) {
            WorkLicenseMachineDto workLicenseMachineDto = new WorkLicenseMachineDto();
            workLicenseMachineDto.setStaffId(tmpStoreStaffDto.getStaffId());
            List<WorkLicenseMachineDto> workLicenseMachineDtos = workLicenseMachineV1InnerServiceSMOImpl.queryWorkLicenseMachines(workLicenseMachineDto);
            if (ListUtil.isNull(workLicenseMachineDtos)) {
                continue;
            }

            //todo 播放tts
            resultVo = playTts(workLicenseMachineDtos.get(0), reqJson.getString("ttsText"));
            if (resultVo.getCode() != ResultVo.CODE_OK) {
                throw new CmdException(resultVo.getMsg());
            }
        }


    }

    private ResultVo playTts(WorkLicenseMachineDto workLicenseMachineDto, String ttsText) {
        WorkLicenseFactoryDto workLicenseFactoryDto = new WorkLicenseFactoryDto();
        workLicenseFactoryDto.setFactoryId(workLicenseMachineDto.getImplBean());
        List<WorkLicenseFactoryDto> workLicenseFactoryDtos = workLicenseFactoryV1InnerServiceSMOImpl.queryWorkLicenseFactorys(workLicenseFactoryDto);

        Assert.listOnlyOne(workLicenseFactoryDtos, "工牌厂家不存在");
        workLicenseMachineDto.setTtsText(ttsText);

        ResultVo resultVo = workLicenseMachineV1InnerServiceSMOImpl.playTts(new WorkLicenseTtsTextDto(workLicenseMachineDto,workLicenseFactoryDtos.get(0),ttsText));

        return resultVo;

    }
}
