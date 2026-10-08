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
package com.java110.accessControl.smo.impl;


import com.java110.accessControl.dao.IAccessControlLogV1ServiceDao;
import com.java110.bean.dto.PageDto;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.intf.accessControl.IAccessControlLogV1InnerServiceSMO;
import com.java110.dto.accessControlLog.AccessControlLogDto;
import com.java110.po.accessControlLog.AccessControlLogPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类表述： 服务之前调用的接口实现类，不对外提供接口能力 只用于接口建调用
 * add by 吴学文 at 2023-08-16 17:22:49 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@RestController
public class AccessControlLogV1InnerServiceSMOImpl implements IAccessControlLogV1InnerServiceSMO {

    @Autowired
    private IAccessControlLogV1ServiceDao accessControlLogV1ServiceDaoImpl;


    private static final List<String> CONFIG_LOG_CMDS = Arrays.asList(
            AccessControlLogDto.CMD_ADD_FACE,
            AccessControlLogDto.CMD_UPDATE_FACE,
            AccessControlLogDto.CMD_DELETE_FACE,
            AccessControlLogDto.CMD_ADD_CARD,
            AccessControlLogDto.CMD_DELETE_CARD,
            AccessControlLogDto.CMD_ADD_USER,
            AccessControlLogDto.CMD_DELETE_USER);


    @Override
    public int saveAccessControlLog(@RequestBody AccessControlLogPo accessControlLogPo) {
        if (StringUtil.isEmpty(accessControlLogPo.getStatusCd())) {
            accessControlLogPo.setStatusCd(AccessControlLogDto.STATUS_NORMAL);
        }

        List<String> invalidLogCmds = getInvalidLogCmds(accessControlLogPo.getLogCmd());

        if (!ListUtil.isNull(invalidLogCmds)
                && !StringUtil.isEmpty(accessControlLogPo.getMachineId())
                && !StringUtil.isEmpty(accessControlLogPo.getUserId())) {
            Map<String, Object> invalidInfo = new HashMap<>();
            invalidInfo.put("machineId", accessControlLogPo.getMachineId());
            invalidInfo.put("userId", accessControlLogPo.getUserId());
            invalidInfo.put("logCmds", invalidLogCmds);
            accessControlLogV1ServiceDaoImpl.updateAccessControlLogInfoInvalid(invalidInfo);
        }

        int saveFlag = accessControlLogV1ServiceDaoImpl.saveAccessControlLogInfo(BeanConvertUtil.beanCovertMap(accessControlLogPo));
        return saveFlag;
    }

    private List<String> getInvalidLogCmds(String logCmd) {
        if (StringUtil.isEmpty(logCmd) || !CONFIG_LOG_CMDS.contains(logCmd)) {
            return new ArrayList<>();
        }

        return Arrays.asList(logCmd);
    }

    @Override
    public int updateAccessControlLog(@RequestBody AccessControlLogPo accessControlLogPo) {
        int saveFlag = accessControlLogV1ServiceDaoImpl.updateAccessControlLogInfo(BeanConvertUtil.beanCovertMap(accessControlLogPo));
        return saveFlag;
    }

    @Override
    public int deleteAccessControlLog(@RequestBody AccessControlLogPo accessControlLogPo) {
        int saveFlag = accessControlLogV1ServiceDaoImpl.updateAccessControlLogInfo(BeanConvertUtil.beanCovertMap(accessControlLogPo));
        return saveFlag;
    }

    @Override
    public List<AccessControlLogDto> queryAccessControlLogs(@RequestBody AccessControlLogDto accessControlLogDto) {

        //校验是否传了 分页信息

        int page = accessControlLogDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            accessControlLogDto.setPage((page - 1) * accessControlLogDto.getRow());
        }

        List<AccessControlLogDto> accessControlLogs = BeanConvertUtil.covertBeanList(accessControlLogV1ServiceDaoImpl.getAccessControlLogInfo(BeanConvertUtil.beanCovertMap(accessControlLogDto)), AccessControlLogDto.class);

        return accessControlLogs;
    }


    @Override
    public int queryAccessControlLogsCount(@RequestBody AccessControlLogDto accessControlLogDto) {
        return accessControlLogV1ServiceDaoImpl.queryAccessControlLogsCount(BeanConvertUtil.beanCovertMap(accessControlLogDto));
    }

    @Override
    public List<AccessControlLogDto> queryAccessControlLogParams(@RequestBody AccessControlLogDto accessControlLogDto) {
        List<AccessControlLogDto> accessControlLogs = BeanConvertUtil.covertBeanList(
                accessControlLogV1ServiceDaoImpl.queryAccessControlLogParams(BeanConvertUtil.beanCovertMap(accessControlLogDto)), AccessControlLogDto.class);

        return accessControlLogs;    }

    @Override
    public List<AccessControlLogDto> getAssetOpenDoor(@RequestBody AccessControlLogDto accessControlLogDto) {
        List<AccessControlLogDto> accessControlLogs = BeanConvertUtil.covertBeanList(
                accessControlLogV1ServiceDaoImpl.getAssetOpenDoor(BeanConvertUtil.beanCovertMap(accessControlLogDto)), AccessControlLogDto.class);

        return accessControlLogs;    }

}
