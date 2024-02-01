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
package com.java110.barrier.smo.impl;


import com.alibaba.fastjson.JSONObject;
import com.java110.barrier.dao.IBarrierV1ServiceDao;
import com.java110.barrier.engine.IBarrierEngine;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.PageDto;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.intf.barrier.IBarrierV1InnerServiceSMO;
import com.java110.dto.barrier.BarrierDto;
import com.java110.po.barrier.BarrierPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 类表述： 服务之前调用的接口实现类，不对外提供接口能力 只用于接口建调用
 * add by 吴学文 at 2023-09-21 01:05:11 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@RestController
public class BarrierV1InnerServiceSMOImpl  implements IBarrierV1InnerServiceSMO {

    @Autowired
    private IBarrierV1ServiceDao barrierV1ServiceDaoImpl;
    @Autowired
    private IBarrierEngine barrierEngineImpl;

    @Override
    public int saveBarrier(@RequestBody  BarrierPo barrierPo) {
        int saveFlag = barrierV1ServiceDaoImpl.saveBarrierInfo(BeanConvertUtil.beanCovertMap(barrierPo));
        return saveFlag;
    }

     @Override
    public int updateBarrier(@RequestBody  BarrierPo barrierPo) {
        int saveFlag = barrierV1ServiceDaoImpl.updateBarrierInfo(BeanConvertUtil.beanCovertMap(barrierPo));
        return saveFlag;
    }

     @Override
    public int deleteBarrier(@RequestBody  BarrierPo barrierPo) {
       barrierPo.setStatusCd("1");
       int saveFlag = barrierV1ServiceDaoImpl.updateBarrierInfo(BeanConvertUtil.beanCovertMap(barrierPo));
       return saveFlag;
    }

    @Override
    public List<BarrierDto> queryBarriers(@RequestBody  BarrierDto barrierDto) {

        //校验是否传了 分页信息

        int page = barrierDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            barrierDto.setPage((page - 1) * barrierDto.getRow());
        }

        List<BarrierDto> barriers = BeanConvertUtil.covertBeanList(barrierV1ServiceDaoImpl.getBarrierInfo(BeanConvertUtil.beanCovertMap(barrierDto)), BarrierDto.class);

        return barriers;
    }


    @Override
    public int queryBarriersCount(@RequestBody BarrierDto barrierDto) {
        return barrierV1ServiceDaoImpl.queryBarriersCount(BeanConvertUtil.beanCovertMap(barrierDto));    }

    @Override
    public List<BarrierDto> queryBoxMachines(@RequestBody BarrierDto machineDto) {
        List<BarrierDto> barriers = BeanConvertUtil.covertBeanList(barrierV1ServiceDaoImpl.queryBoxMachines(BeanConvertUtil.beanCovertMap(machineDto)), BarrierDto.class);

        return barriers;
    }

    @Override
    public ResultVo openDoor(@RequestBody JSONObject reqJson) {
        BarrierDto barrierDto = new BarrierDto();
        barrierDto.setMachineCode(reqJson.getString("machineCode"));
        barrierDto.setCommunityId(reqJson.getString("communityId"));
        List<BarrierDto> barrierDtos = queryBarriers(barrierDto);

        Assert.listOnlyOne(barrierDtos, "摄像头不存在");

        ResultVo resultVo = barrierEngineImpl.openDoor(barrierDtos.get(0),null, reqJson);
        return resultVo;
    }

    @Override
    public ResultVo closeDoor(JSONObject reqJson) {

        BarrierDto barrierDto = new BarrierDto();
        barrierDto.setMachineCode(reqJson.getString("machineCode"));
        barrierDto.setCommunityId(reqJson.getString("communityId"));
        List<BarrierDto> barrierDtos = queryBarriers(barrierDto);

        Assert.listOnlyOne(barrierDtos, "摄像头不存在");

        ResultVo resultVo = barrierEngineImpl.closeDoor(barrierDtos.get(0),null, reqJson);
        return resultVo;
    }

}
