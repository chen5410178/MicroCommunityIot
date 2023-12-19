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
package com.java110.user.cmd.personFace;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.owner.OwnerDto;
import com.java110.bean.dto.owner.OwnerRoomRelDto;
import com.java110.bean.dto.room.RoomDto;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.annotation.Java110Transactional;
import com.java110.core.cache.MappingCache;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.constant.MappingConstant;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.factory.SendSmsFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.ListUtil;
import com.java110.dto.msg.SmsDto;
import com.java110.dto.personFace.PersonFaceDto;
import com.java110.dto.personFaceQrcode.PersonFaceQrcodeDto;
import com.java110.intf.community.IRoomV1InnerServiceSMO;
import com.java110.intf.system.ISmsInnerServiceSMO;
import com.java110.intf.user.IOwnerRoomRelV1InnerServiceSMO;
import com.java110.intf.user.IOwnerV1InnerServiceSMO;
import com.java110.intf.user.IPersonFaceQrcodeV1InnerServiceSMO;
import com.java110.intf.user.IPersonFaceV1InnerServiceSMO;
import com.java110.po.personFace.PersonFacePo;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 类表述：保存
 * 服务编码：personFace.savePersonFace
 * 请求路劲：/app/personFace.SavePersonFace
 * add by 吴学文 at 2023-12-18 18:47:46 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Java110Cmd(serviceCode = "personFace.savePersonFace")
public class SavePersonFaceCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(SavePersonFaceCmd.class);

    public static final String CODE_PREFIX_ID = "10";

    @Autowired
    private IPersonFaceV1InnerServiceSMO personFaceV1InnerServiceSMOImpl;

    @Autowired
    private IPersonFaceQrcodeV1InnerServiceSMO personFaceQrcodeV1InnerServiceSMOImpl;

    @Autowired
    private ISmsInnerServiceSMO smsInnerServiceSMOImpl;

    @Autowired
    private IRoomV1InnerServiceSMO roomV1InnerServiceSMOImpl;

    @Autowired
    private IOwnerRoomRelV1InnerServiceSMO ownerRoomRelV1InnerServiceSMOImpl;

    @Autowired
    private IOwnerV1InnerServiceSMO ownerV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "pfqId", "请求报文中未包含pfqId");
        Assert.hasKeyAndValue(reqJson, "communityId", "请求报文中未包含communityId");
        Assert.hasKeyAndValue(reqJson, "roomId", "请求报文中未包含roomId");
        Assert.hasKeyAndValue(reqJson, "roomName", "请求报文中未包含roomName");
        Assert.hasKeyAndValue(reqJson, "personName", "请求报文中未包含personName");
        Assert.hasKeyAndValue(reqJson, "personTel", "请求报文中未包含personTel");
        Assert.hasKeyAndValue(reqJson, "ownerTypeCd", "请求报文中未包含ownerTypeCd");
        Assert.hasKeyAndValue(reqJson, "faceUrl", "请求报文中未包含faceUrl");
        Assert.hasKeyAndValue(reqJson, "openId", "请求报文中未包含openId");

        RoomDto roomDto = new RoomDto();
        roomDto.setRoomId(reqJson.getString("roomId"));
        roomDto.setCommunityId(reqJson.getString("communityId"));
        List<RoomDto> roomDtos = roomV1InnerServiceSMOImpl.queryRooms(roomDto);

        if (ListUtil.isNull(roomDtos)) {
            throw new CmdException("房屋不存在");
        }

        PersonFaceQrcodeDto personFaceQrcodeDto = new PersonFaceQrcodeDto();
        personFaceQrcodeDto.setPfqId(reqJson.getString("pfqId"));
        personFaceQrcodeDto.setCommunityId(reqJson.getString("communityId"));
        personFaceQrcodeDto.setState(PersonFaceQrcodeDto.STATE_NORMAL);
        List<PersonFaceQrcodeDto> personFaceQrcodeDtos = personFaceQrcodeV1InnerServiceSMOImpl.queryPersonFaceQrcodes(personFaceQrcodeDto);

        if (ListUtil.isNull(personFaceQrcodeDtos)) {
            throw new CmdException("二维码失效");
        }

        reqJson.put("audit", personFaceQrcodeDtos.get(0).getAudit());

        //todo 校验验证码
        if (!PersonFaceQrcodeDto.OFF.equals(personFaceQrcodeDtos.get(0).getSmsValidate())) {
            Assert.hasKeyAndValue(reqJson, "msgCode", "未包含验证码");
            SmsDto smsDto = new SmsDto();
            smsDto.setTel(reqJson.getString("personTel"));
            smsDto.setCode(reqJson.getString("msgCode"));
            smsDto = smsInnerServiceSMOImpl.validateCode(smsDto);

            if (!smsDto.isSuccess()) {
                throw new IllegalArgumentException(smsDto.getMsg());
            }
        }

        //todo 校验邀请码
        if (!PersonFaceQrcodeDto.OFF.equals(personFaceQrcodeDtos.get(0).getInvite())) {
            Assert.hasKeyAndValue(reqJson, "inviteCode", "未包含校验码");
            if (personFaceQrcodeDtos.get(0).getInviteCode().equals(reqJson.getString("inviteCode"))) {
                throw new IllegalArgumentException("邀请码错误");
            }
        }

        //todo 校验手机号
        if (PersonFaceQrcodeDto.OFF.equals(personFaceQrcodeDtos.get(0).getTelValidate())) {
            return;
        }

        //todo 家庭成员不校验，因为大部分可能没有添加
        if (!OwnerDto.OWNER_TYPE_CD_OWNER.equals(reqJson.getString("ownerTypeCd"))) {
            return;
        }


        OwnerRoomRelDto ownerRoomRelDto = new OwnerRoomRelDto();
        ownerRoomRelDto.setRoomId(reqJson.getString("roomId"));
        List<OwnerRoomRelDto> ownerRoomRelDtos = ownerRoomRelV1InnerServiceSMOImpl.queryOwnerRoomRels(ownerRoomRelDto);
        if (ListUtil.isNull(ownerRoomRelDtos)) {
            throw new CmdException("房屋未绑定业主");
        }
        List<String> memberIds = new ArrayList<>();
        for (OwnerRoomRelDto tmpOwnerRoomRelDto : ownerRoomRelDtos) {
            memberIds.add(tmpOwnerRoomRelDto.getOwnerId());
        }

        OwnerDto ownerDto = new OwnerDto();
        ownerDto.setMemberIds(memberIds.toArray(new String[memberIds.size()]));
        ownerDto.setCommunityId(reqJson.getString("communityId"));
        ownerDto.setLink(reqJson.getString("personTel"));
        List<OwnerDto> ownerDtos = ownerV1InnerServiceSMOImpl.queryOwners(ownerDto);

        if (ListUtil.isNull(ownerDtos)) {
            throw new CmdException("手机号错误，请联系物业修改!");
        }

    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

        PersonFacePo personFacePo = BeanConvertUtil.covertBean(reqJson, PersonFacePo.class);
        personFacePo.setPfId(GenerateCodeFactory.getGeneratorId(CODE_PREFIX_ID));
        personFacePo.setState("W");
        int flag = personFaceV1InnerServiceSMOImpl.savePersonFace(personFacePo);

        if (flag < 1) {
            throw new CmdException("保存数据失败");
        }


        //todo 直接 走审核逻辑
        if (!PersonFaceQrcodeDto.OFF.equals(reqJson.getString("audit"))) {

        }

        cmdDataFlowContext.setResponseEntity(ResultVo.success());
    }
}
