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
package com.java110.user.cmd.passcode;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.owner.OwnerDto;
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
import com.java110.core.utils.DateUtil;
import com.java110.dto.msg.SmsDto;
import com.java110.dto.passQrcode.PassQrcodeDto;
import com.java110.dto.passQrcodeSetting.PassQrcodeSettingDto;
import com.java110.intf.system.ISmsInnerServiceSMO;
import com.java110.intf.user.IOwnerV1InnerServiceSMO;
import com.java110.intf.user.IPassQrcodeSettingV1InnerServiceSMO;
import com.java110.intf.user.IPassQrcodeV1InnerServiceSMO;
import com.java110.po.passQrcode.PassQrcodePo;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 类表述：保存
 * 服务编码：passQrcode.savePassQrcode
 * 请求路劲：/app/passQrcode.SavePassQrcode
 * add by 吴学文 at 2023-12-20 01:17:14 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Java110Cmd(serviceCode = "passcode.applyPassQrcode")
public class ApplyPassQrcodeCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(ApplyPassQrcodeCmd.class);

    public static final String CODE_PREFIX_ID = "10";

    @Autowired
    private IPassQrcodeV1InnerServiceSMO passQrcodeV1InnerServiceSMOImpl;

    @Autowired
    private IPassQrcodeSettingV1InnerServiceSMO passQrcodeSettingV1InnerServiceSMOImpl;

    @Autowired
    private ISmsInnerServiceSMO smsInnerServiceSMOImpl;

    @Autowired
    private IOwnerV1InnerServiceSMO ownerV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "personTel", "请求报文中未包含personTel");
        Assert.hasKeyAndValue(reqJson, "pqsId", "请求报文中未包含pqsId");
        Assert.hasKeyAndValue(reqJson, "communityId", "请求报文中未包含communityId");
        Assert.hasKeyAndValue(reqJson, "openId", "请求报文中未包含openId");

        PassQrcodeSettingDto passQrcodeSettingDto = new PassQrcodeSettingDto();
        passQrcodeSettingDto.setCommunityId(reqJson.getString("communityId"));
        passQrcodeSettingDto.setPqsId(reqJson.getString("pqsId"));
        passQrcodeSettingDto.setState(PassQrcodeSettingDto.ON);
        List<PassQrcodeSettingDto> passQrcodeSettingDtos = passQrcodeSettingV1InnerServiceSMOImpl.queryPassQrcodeSettings(passQrcodeSettingDto);

        Assert.listOnlyOne(passQrcodeSettingDtos, "通行配置失效");
        reqJson.put("state", "C");
        if (PassQrcodeSettingDto.ON.equals(passQrcodeSettingDtos.get(0).getAudit())) {
            reqJson.put("state", "W");
        }

        if (!PassQrcodeSettingDto.ON.equals(passQrcodeSettingDtos.get(0).getSmsValidate())) {
            return;
        }

        SmsDto smsDto = new SmsDto();
        smsDto.setTel(reqJson.getString("link"));
        smsDto.setCode(reqJson.getString("msgCode"));
        smsDto = smsInnerServiceSMOImpl.validateCode(smsDto);

        if (!smsDto.isSuccess() && "ON".equals(MappingCache.getValue(MappingConstant.SMS_DOMAIN, SendSmsFactory.SMS_SEND_SWITCH))) {
            throw new IllegalArgumentException(smsDto.getMsg());
        }

    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

        OwnerDto ownerDto = new OwnerDto();
        ownerDto.setLink(reqJson.getString("personTel"));
        ownerDto.setCommunityId(reqJson.getString("communityId"));
        List<OwnerDto> ownerDtos = ownerV1InnerServiceSMOImpl.queryOwners(ownerDto);

        Assert.listOnlyOne(ownerDtos, "未包含业主");

        PassQrcodePo passQrcodePo = BeanConvertUtil.covertBean(reqJson, PassQrcodePo.class);
        passQrcodePo.setPqId(GenerateCodeFactory.getGeneratorId(CODE_PREFIX_ID));
        passQrcodePo.setQrcode(GenerateCodeFactory.getUUID());
        passQrcodePo.setQrcodeTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        passQrcodePo.setOwnerId(ownerDtos.get(0).getMemberId());
        passQrcodePo.setPersonName(ownerDtos.get(0).getName());
        int flag = passQrcodeV1InnerServiceSMOImpl.savePassQrcode(passQrcodePo);

        if (flag < 1) {
            throw new CmdException("保存数据失败");
        }

        cmdDataFlowContext.setResponseEntity(ResultVo.success());
    }
}
