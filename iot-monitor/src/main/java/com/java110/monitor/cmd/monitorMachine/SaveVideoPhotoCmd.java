package com.java110.monitor.cmd.monitorMachine;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.dto.file.FileDto;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.Assert;
import com.java110.dto.monitor.MonitorMachineDto;
import com.java110.intf.monitor.IMonitorMachineV1InnerServiceSMO;
import com.java110.intf.system.IFileInnerServiceSMO;
import com.java110.po.monitor.MonitorMachinePo;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;

@Java110Cmd(serviceCode = "monitorMachine.saveVideoPhoto")
public class SaveVideoPhotoCmd extends Cmd {

    @Autowired
    private IFileInnerServiceSMO fileInnerServiceSMOImpl;

    @Autowired
    private IMonitorMachineV1InnerServiceSMO monitorMachineV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson,"communityId","未包含小区");
        Assert.hasKeyAndValue(reqJson,"machineId","未包含设备ID");
        Assert.hasKeyAndValue(reqJson,"photo","未包含图片");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {


        FileDto fileDto = new FileDto();
        fileDto.setFileId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_file_id));
        fileDto.setFileName(fileDto.getFileId());
        fileDto.setContext(reqJson.getString("photo"));
        fileDto.setSuffix("jpeg");
        fileDto.setCommunityId(reqJson.getString("communityId"));
        String fileName = fileInnerServiceSMOImpl.saveFile(fileDto);

        MonitorMachinePo monitorMachinePo = new MonitorMachinePo();
        monitorMachinePo.setCommunityId(reqJson.getString("communityId"));
        monitorMachinePo.setMachineId(reqJson.getString("machineId"));
        monitorMachinePo.setPhotoUrl(fileName);
        monitorMachineV1InnerServiceSMOImpl.updateMonitorMachine(monitorMachinePo);
    }
}
