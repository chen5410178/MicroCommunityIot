package com.java110.dev.cmd.task;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.task.TaskTemplateSpecDto;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.intf.job.ITaskInnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

@Java110Cmd(serviceCode = "task.listTaskTemplateSpec")
public class ListTaskTemplateSpecCmd extends Cmd {

    @Autowired
    private ITaskInnerServiceSMO taskInnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        super.validatePageInfo(reqJson);
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {

        TaskTemplateSpecDto taskTemplateSpecDto = BeanConvertUtil.covertBean(reqJson, TaskTemplateSpecDto.class);

        int count = taskInnerServiceSMOImpl.queryTaskTemplateSpecCount(taskTemplateSpecDto);

        List<TaskTemplateSpecDto> taskTemplateSpecDtos = null;

        if (count > 0) {
            taskTemplateSpecDtos = taskInnerServiceSMOImpl.queryTaskTemplateSpec(taskTemplateSpecDto);
        } else {
            taskTemplateSpecDtos = new ArrayList<>();
        }

        ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) reqJson.getInteger("row")), count, taskTemplateSpecDtos);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        context.setResponseEntity(responseEntity);
    }
}
