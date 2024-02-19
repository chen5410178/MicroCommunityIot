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
package com.java110.user.cmd.appUser;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.file.FileRelDto;
import com.java110.bean.dto.owner.OwnerDto;
import com.java110.bean.dto.owner.OwnerRoomRelDto;
import com.java110.bean.po.file.FileRelPo;
import com.java110.bean.po.owner.OwnerPo;
import com.java110.bean.po.owner.OwnerRoomRelPo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.annotation.Java110Transactional;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.DateUtil;
import com.java110.dto.appUser.AppUserDto;
import com.java110.intf.system.IFileRelInnerServiceSMO;
import com.java110.intf.user.IAppUserV1InnerServiceSMO;
import com.java110.intf.user.IOwnerInnerServiceSMO;
import com.java110.intf.user.IOwnerRoomRelV1InnerServiceSMO;
import com.java110.intf.user.IOwnerV1InnerServiceSMO;
import com.java110.po.appUser.AppUserPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * 类表述：更新
 * 服务编码：appUser.updateAppUser
 * 请求路劲：/app/appUser.UpdateAppUser
 * add by 吴学文 at 2023-11-18 02:49:01 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Java110Cmd(serviceCode = "appUser.updateAppUser")
public class UpdateAppUserCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(UpdateAppUserCmd.class);


    @Autowired
    private IAppUserV1InnerServiceSMO appUserV1InnerServiceSMOImpl;

    @Autowired
    private IOwnerInnerServiceSMO ownerInnerServiceSMOImpl;

    @Autowired
    private IOwnerV1InnerServiceSMO ownerV1InnerServiceSMOImpl;

    @Autowired
    private IOwnerRoomRelV1InnerServiceSMO ownerRoomRelV1InnerServiceSMOImpl;

    @Autowired
    private IFileRelInnerServiceSMO fileRelInnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "auId", "auId不能为空");
        Assert.hasKeyAndValue(reqJson, "communityId", "communityId不能为空");

    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

        AppUserPo appUserPo = BeanConvertUtil.covertBean(reqJson, AppUserPo.class);

        if(AppUserDto.STATE_S.equals(appUserPo.getState())) {
            OwnerDto ownerDto = new OwnerDto();
            ownerDto.setLink(appUserPo.getLink());
            ownerDto.setCommunityId(appUserPo.getCommunityId());
            List<OwnerDto> ownerDtos = ownerInnerServiceSMOImpl.queryOwners(ownerDto);
            if (ownerDtos.size() > 0) {
                for (OwnerDto owner : ownerDtos) {
                    FileRelDto fileRelDto = new FileRelDto();
                    fileRelDto.setObjId(owner.getMemberId());
                    fileRelDto.setRelTypeCd("10000");
                    List<FileRelDto> fileRelDtos = fileRelInnerServiceSMOImpl.queryFileRels(fileRelDto);

                    if (fileRelDtos.size() > 0) {
                        FileRelPo fileRelPo = BeanConvertUtil.covertBean(fileRelDtos.get(0), FileRelPo.class);
                        fileRelPo.setFileRealName(appUserPo.getStateMsg());
                        fileRelPo.setFileSaveName(appUserPo.getStateMsg());
                        fileRelInnerServiceSMOImpl.updateFileRel(fileRelPo);
                    } else {
                        FileRelPo fileRelPo = new FileRelPo();
                        fileRelPo.setFileRelId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_fileRelId));
                        fileRelPo.setRelTypeCd(FileRelDto.REL_TYPE_CD_OWNER);
                        fileRelPo.setSaveWay(FileRelDto.SAVE_WAY_UPLOAD);
                        fileRelPo.setObjId(owner.getMemberId());
                        fileRelPo.setFileRealName(appUserPo.getStateMsg());
                        fileRelPo.setFileSaveName(appUserPo.getStateMsg());
                        fileRelInnerServiceSMOImpl.saveFileRel(fileRelPo);
                    }
                }
            } else {
                OwnerPo ownerPo = new OwnerPo();
                ownerPo.setMemberId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_ownerId));
                ownerPo.setOwnerId(ownerPo.getMemberId());
                ownerPo.setName(appUserPo.getOwnerName());
                ownerPo.setSex("0");
                ownerPo.setLink(appUserPo.getLink());
                ownerPo.setUserId("-1");
                ownerPo.setRemark("房屋认证通过");
                ownerPo.setOwnerTypeCd(OwnerDto.OWNER_TYPE_CD_OWNER);
                ownerPo.setCommunityId(appUserPo.getCommunityId());
                ownerPo.setState(OwnerDto.STATE_FINISH);
                ownerPo.setOwnerFlag(OwnerDto.OWNER_FLAG_TRUE);
                ownerV1InnerServiceSMOImpl.saveOwner(ownerPo);

                OwnerRoomRelPo ownerRoomRelPo = new OwnerRoomRelPo();
                ownerRoomRelPo.setRelId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_relId));
                ownerRoomRelPo.setOwnerId(ownerPo.getOwnerId());
                ownerRoomRelPo.setRoomId(appUserPo.getRoomId());
                ownerRoomRelPo.setState("2001");
                ownerRoomRelPo.setUserId("-1");
                ownerRoomRelPo.setStartTime(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                int yearToAdd = 50;
                calendar.add(Calendar.YEAR, yearToAdd);
                ownerRoomRelPo.setEndTime(new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
                ownerRoomRelV1InnerServiceSMOImpl.saveOwnerRoomRel(ownerRoomRelPo);

                FileRelPo fileRelPo = new FileRelPo();
                fileRelPo.setFileRelId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_fileRelId));
                fileRelPo.setRelTypeCd(FileRelDto.REL_TYPE_CD_OWNER);
                fileRelPo.setSaveWay(FileRelDto.SAVE_WAY_UPLOAD);
                fileRelPo.setObjId(ownerPo.getMemberId());
                fileRelPo.setFileRealName(appUserPo.getStateMsg());
                fileRelPo.setFileSaveName(appUserPo.getStateMsg());
                fileRelInnerServiceSMOImpl.saveFileRel(fileRelPo);
            }

            appUserPo.setStateMsg("成功");
        }

        int flag = appUserV1InnerServiceSMOImpl.updateAppUser(appUserPo);

        if (flag < 1) {
            throw new CmdException("更新数据失败");
        }

        cmdDataFlowContext.setResponseEntity(ResultVo.success());
    }
}
