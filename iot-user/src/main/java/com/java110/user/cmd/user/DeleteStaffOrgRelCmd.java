package com.java110.user.cmd.user;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.dto.org.OrgStaffRelDto;
import com.java110.bean.po.org.OrgStaffRelPo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.intf.user.IOrgStaffRelV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Java110Cmd(serviceCode = "org.deleteOrgRelStaff")
public class DeleteStaffOrgRelCmd extends Cmd {
    @Autowired
    private IOrgStaffRelV1InnerServiceSMO orgStaffRelV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        Assert.hasKeyAndValue(reqJson, "userId", "未包含用户信息");
        Assert.hasKeyAndValue(reqJson, "relId", "未包含组织关系信息");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        OrgStaffRelDto orgStaffRelDto = new OrgStaffRelDto();
        orgStaffRelDto.setStaffId(reqJson.getString("userId"));
        //组织关系数
        List<OrgStaffRelDto> orgStaffRelDtos1 = orgStaffRelV1InnerServiceSMOImpl.queryOrgStaffRels(orgStaffRelDto);
        if (orgStaffRelDtos1.size() < 2) {
            throw new CmdException("至少保留一个组织关系，暂时无法删除！");
        }
        orgStaffRelDto = new OrgStaffRelDto();
        orgStaffRelDto.setStaffId(reqJson.getString("userId"));
        orgStaffRelDto.setRelId(reqJson.getString("relId"));
        List<OrgStaffRelDto> orgStaffRelDtos = orgStaffRelV1InnerServiceSMOImpl.queryOrgStaffRels(orgStaffRelDto);
        if (orgStaffRelDtos == null || orgStaffRelDtos.size() < 1) {
            throw new CmdException("关系不存在");
        }
        for (OrgStaffRelDto tmpOrgStaffRelDto : orgStaffRelDtos) {
            OrgStaffRelPo orgStaffRelPo = new OrgStaffRelPo();
            orgStaffRelPo.setRelId(tmpOrgStaffRelDto.getRelId());
            int flag = orgStaffRelV1InnerServiceSMOImpl.deleteOrgStaffRel(orgStaffRelPo);
            if (flag < 1) {
                throw new CmdException("关联员工失败");
            }
        }
    }
}
