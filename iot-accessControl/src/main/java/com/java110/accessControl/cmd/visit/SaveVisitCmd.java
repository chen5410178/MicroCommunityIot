/*
 * Copyright 2017-2020 吴学文 and java110 team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.java110.accessControl.cmd.visit;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.room.RoomDto;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.annotation.Java110Transactional;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.visit.VisitTypeDto;
import com.java110.intf.accessControl.IVisitTypeV1InnerServiceSMO;
import com.java110.intf.accessControl.IVisitV1InnerServiceSMO;
import com.java110.intf.community.IRoomsTreeInnerServiceSMO;
import com.java110.po.visit.VisitPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 类表述：保存
 * 服务编码：visit.saveVisit
 * 请求路劲：/app/visit.SaveVisit
 * add by 吴学文 at 2023-12-12 10:12:32 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Java110Cmd(serviceCode = "visit.saveVisit")
public class SaveVisitCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(SaveVisitCmd.class);

    public static final String CODE_PREFIX_ID = "10";

    @Autowired
    private IVisitV1InnerServiceSMO visitV1InnerServiceSMOImpl;

    @Autowired
    private IVisitTypeV1InnerServiceSMO visitTypeV1InnerServiceSMOImpl;

    @Resource
    private IRoomsTreeInnerServiceSMO roomsTreeInnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "name", "name不能为空");
        Assert.hasKeyAndValue(reqJson, "visitGender", "visitGender不能为空");
        Assert.hasKeyAndValue(reqJson, "phoneNumber", "phoneNumber不能为空");
        Assert.hasKeyAndValue(reqJson, "roomName", "roomName不能为空");
        Assert.hasKeyAndValue(reqJson, "roomId", "roomId不能为空");
        Assert.hasKeyAndValue(reqJson, "typeId", "typeId不能为空");
        Assert.hasKeyAndValue(reqJson, "state", "state不能为空");
    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

        VisitTypeDto visitTypeDto = new VisitTypeDto();
        visitTypeDto.setTypeId(reqJson.getString("typeId"));
        List<VisitTypeDto> visitTypeDtos = visitTypeV1InnerServiceSMOImpl.queryVisitTypes(visitTypeDto);
        Assert.listOnlyOne(visitTypeDtos, "访客类型不存在");

        switch (visitTypeDtos.get(0).getVisitWay()) {
            case VisitTypeDto.VISIT_WAY_FACE: // todo:先不做
                break;
            case VisitTypeDto.VISIT_WAY_QRCODE:
                reqJson.put("qrcode", GenerateCodeFactory.getUUID());
                break;
            default:
                break;
        }

        if (reqJson.getDate("visitTime") != null) {
            Date visitTime = reqJson.getDate("visitTime");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(visitTime);
            calendar.add(Calendar.DAY_OF_YEAR, Integer.valueOf(visitTypeDtos.get(0).getVisitDay()));
            reqJson.put("departureTime", calendar.getTime());
        }

        RoomDto roomDto = new RoomDto();
        roomDto.setRoomId(reqJson.getString("roomId"));
        List<RoomDto> roomDtos = roomsTreeInnerServiceSMOImpl.queryRoomsTree(roomDto);
        if (!CollectionUtils.isEmpty(roomDtos)) {
            reqJson.put("ownerId", roomDtos.get(0).getOwnerId());
            reqJson.put("ownerName", roomDtos.get(0).getOwnerName());
        }

        VisitPo visitPo = BeanConvertUtil.covertBean(reqJson, VisitPo.class);
        visitPo.setVisitId(GenerateCodeFactory.getGeneratorId(CODE_PREFIX_ID));
        int flag = visitV1InnerServiceSMOImpl.saveVisit(visitPo);

        if (flag < 1) {
            throw new CmdException("保存数据失败");
        }

        cmdDataFlowContext.setResponseEntity(ResultVo.success());
    }
}
