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
import com.java110.core.utils.*;
import com.java110.dto.appUser.AppUserDto;
import com.java110.intf.system.IFileRelInnerServiceSMO;
import com.java110.intf.user.*;
import com.java110.po.appUser.AppUserPo;
import com.java110.po.user.UserPo;
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
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Autowired
    private IFileRelInnerServiceSMO fileRelInnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "auId", "auId不能为空");
        Assert.hasKeyAndValue(reqJson, "communityId", "communityId不能为空");
        Assert.hasKeyAndValue(reqJson, "state", "state不能为空");

    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

        String userId = CmdContextUtils.getUserId(cmdDataFlowContext);


        AppUserDto appUserDto = new AppUserDto();
        appUserDto.setAuId(reqJson.getString("auId"));
        List<AppUserDto> appUserDtos = appUserV1InnerServiceSMOImpl.queryAppUsers(appUserDto);

        if (ListUtil.isNull(appUserDtos)) {
            throw new CmdException("审核记录不存在");
        }

        AppUserPo appUserPo = new AppUserPo();
        appUserPo.setAuId(reqJson.getString("auId"));
        appUserPo.setState(reqJson.getString("state"));

        appUserPo.setStateMsg("成功");
        if (!AppUserDto.STATE_S.equals(appUserPo.getState())) {
            appUserPo.setStateMsg("失败");
        }

        appUserV1InnerServiceSMOImpl.updateAppUser(appUserPo);

        //todo 审核不通过
        if (!AppUserDto.STATE_S.equals(appUserPo.getState())) {
            return;
        }
        OwnerDto ownerDto = new OwnerDto();
        ownerDto.setLink(appUserDtos.get(0).getLink());
        ownerDto.setCommunityId(appUserPo.getCommunityId());
        List<OwnerDto> ownerDtos = ownerInnerServiceSMOImpl.queryOwners(ownerDto);
        if (!ListUtil.isNull(ownerDtos)) {
            //todo 修改业主
            updateOwner(reqJson, ownerDtos, appUserPo, appUserDtos);
        } else {
            //todo 添加业主
            saveOwner(reqJson, appUserPo, userId, appUserDtos);
        }

        if (StringUtil.isEmpty(appUserDtos.get(0).getUserId())) {
            cmdDataFlowContext.setResponseEntity(ResultVo.success());
            return;
        }

        UserPo userPo = new UserPo();
        userPo.setUserId(appUserDtos.get(0).getUserId());
        userPo.setName(appUserDtos.get(0).getOwnerName());
        userV1InnerServiceSMOImpl.updateUser(userPo);


        cmdDataFlowContext.setResponseEntity(ResultVo.success());
    }

    /**
     * 修改业主
     *
     * @param reqJson
     * @param ownerDtos
     * @param appUserPo
     * @param appUserDtos
     */
    private void updateOwner(JSONObject reqJson, List<OwnerDto> ownerDtos, AppUserPo appUserPo, List<AppUserDto> appUserDtos) {
        appUserPo = new AppUserPo();
        appUserPo.setAuId(reqJson.getString("auId"));
        appUserPo.setMemberId(ownerDtos.get(0).getMemberId());
        int flag = appUserV1InnerServiceSMOImpl.updateAppUser(appUserPo);
        if (flag < 1) {
            throw new CmdException("更新数据失败");
        }
    }

    /**
     * 业主不存在时 添加业主
     *
     * @param reqJson
     * @param appUserPo
     * @param userId
     * @param appUserDtos
     */
    private void saveOwner(JSONObject reqJson, AppUserPo appUserPo, String userId, List<AppUserDto> appUserDtos) {
        OwnerPo ownerPo = new OwnerPo();
        ownerPo.setMemberId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_ownerId));
        ownerPo.setOwnerId(ownerPo.getMemberId());
        ownerPo.setName(appUserPo.getOwnerName());
        ownerPo.setSex("0");
        ownerPo.setLink(appUserPo.getLink());
        ownerPo.setUserId(userId);
        ownerPo.setRemark("房屋认证通过");
        ownerPo.setOwnerTypeCd(OwnerDto.OWNER_TYPE_CD_OWNER);
        ownerPo.setCommunityId(appUserPo.getCommunityId());
        ownerPo.setState(OwnerDto.STATE_FINISH);
        ownerPo.setOwnerFlag(OwnerDto.OWNER_FLAG_TRUE);

        // todo 查询房屋业主
        OwnerDto tmpOwnerDto = new OwnerDto();
        tmpOwnerDto.setRoomId(appUserDtos.get(0).getRoomId());
        tmpOwnerDto.setCommunityId(appUserDtos.get(0).getCommunityId());
        List<OwnerDto> tmpOwnerDtos = ownerInnerServiceSMOImpl.queryOwnersByRoom(tmpOwnerDto);
        if (!ListUtil.isNull(tmpOwnerDtos)) {
            ownerPo.setOwnerId(tmpOwnerDtos.get(0).getOwnerId());
            ownerPo.setOwnerTypeCd(OwnerDto.OWNER_TYPE_CD_MEMBER);
        }
        ownerV1InnerServiceSMOImpl.saveOwner(ownerPo);

        OwnerRoomRelPo ownerRoomRelPo = new OwnerRoomRelPo();
        ownerRoomRelPo.setRelId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_relId));
        ownerRoomRelPo.setOwnerId(ownerPo.getOwnerId());
        ownerRoomRelPo.setRoomId(appUserPo.getRoomId());
        ownerRoomRelPo.setState("2001");
        ownerRoomRelPo.setUserId(userId);
        ownerRoomRelPo.setStartTime(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int yearToAdd = 50;
        calendar.add(Calendar.YEAR, yearToAdd);
        ownerRoomRelPo.setEndTime(new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
        ownerRoomRelV1InnerServiceSMOImpl.saveOwnerRoomRel(ownerRoomRelPo);


        appUserPo = new AppUserPo();
        appUserPo.setAuId(reqJson.getString("auId"));
        appUserPo.setMemberId(ownerPo.getMemberId());

        int flag = appUserV1InnerServiceSMOImpl.updateAppUser(appUserPo);

        if (flag < 1) {
            throw new CmdException("更新数据失败");
        }


    }
}
