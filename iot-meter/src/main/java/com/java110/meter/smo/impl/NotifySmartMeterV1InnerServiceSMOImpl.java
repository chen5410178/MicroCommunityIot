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
package com.java110.meter.smo.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Transactional;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.meter.MeterMachineDetailDto;
import com.java110.dto.meter.MeterMachineDto;
import com.java110.dto.meter.MeterMachineFactoryDto;
import com.java110.dto.meter.NotifyMeterWaterOrderDto;
import com.java110.intf.meter.IMeterMachineDetailV1InnerServiceSMO;
import com.java110.intf.meter.IMeterMachineFactoryV1InnerServiceSMO;
import com.java110.intf.meter.IMeterMachineV1InnerServiceSMO;
import com.java110.intf.meter.INotifySmartMeterV1InnerServiceSMO;
import com.java110.meter.factory.ISmartMeterFactoryAdapt;
import com.java110.meter.factory.tqdianbiao.TdDianBiaoUtil;
import com.java110.po.meter.MeterMachineDetailPo;
import com.java110.po.meter.MeterMachinePo;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 类表述： 服务之前调用的接口实现类，不对外提供接口能力 只用于接口建调用
 * add by 吴学文 at 2022-08-08 09:22:37 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@RestController
public class NotifySmartMeterV1InnerServiceSMOImpl implements INotifySmartMeterV1InnerServiceSMO {

    private static final Logger logger = LoggerFactory.getLogger(NotifySmartMeterV1InnerServiceSMOImpl.class);


    private ISmartMeterFactoryAdapt smartMeterFactoryAdapt;

    @Autowired
    private IMeterMachineV1InnerServiceSMO meterMachineV1InnerServiceSMOImpl;

    @Autowired
    private IMeterMachineFactoryV1InnerServiceSMO meterMachineFactoryV1InnerServiceSMOImpl;

    @Autowired
    private IMeterMachineDetailV1InnerServiceSMO meterMachineDetailV1InnerServiceSMOImpl;

    /**
     * 通知类
     *
     * @param notifyMeterWaterOrderDto 数据对象分享
     * @return
     */
    @Override
    public ResponseEntity<String> notifySmartMater(@RequestBody NotifyMeterWaterOrderDto notifyMeterWaterOrderDto) {

        try {
            MeterMachineFactoryDto meterMachineFactoryDto = new MeterMachineFactoryDto();
            meterMachineFactoryDto.setBeanImpl(notifyMeterWaterOrderDto.getImplBean());
            List<MeterMachineFactoryDto> meterMachineFactoryDtos = meterMachineFactoryV1InnerServiceSMOImpl.queryMeterMachineFactorys(meterMachineFactoryDto);
            Assert.listOnlyOne(meterMachineFactoryDtos, "智能水电表厂家不存在");

            try {
                smartMeterFactoryAdapt = ApplicationContextFactory.getBean(meterMachineFactoryDtos.get(0).getBeanImpl(), ISmartMeterFactoryAdapt.class);
            } finally {
                if (smartMeterFactoryAdapt == null) {
                    throw new CmdException("厂家接口未实现");
                }
            }

            // 通知 厂家适配器数据
            ResultVo resultVo = smartMeterFactoryAdapt.notifyReadData(notifyMeterWaterOrderDto.getParam());
            return ResultVo.createResponseEntity(resultVo);
        } catch (Exception e) {
            logger.error("通知是配置异常", e);
            throw e;
        }
    }

    @Override
    @Java110Transactional
    public ResponseEntity<String> switchControlNotify(String implBean, String postInfo) {
        JSONObject data = JSONObject.parseObject(postInfo);

        String response_content = data.getString("response_content");
        String timestamp = data.getString("timestamp");
        String sign = data.getString("sign");

        if (!TdDianBiaoUtil.checkSign(response_content, timestamp, sign)) {
            System.out.println("sign check failed");
            return ResultVo.createResponseEntity(new ResultVo(ResultVo.CODE_ERROR, "鉴权失败"));
        }

        JSONArray contentArray = JSON.parseArray(response_content);

        if (contentArray == null || contentArray.size() < 1) {
            return ResultVo.createResponseEntity(new ResultVo(ResultVo.CODE_ERROR, "没有数据"));
        }

        MeterMachineDetailDto meterMachineDetailDto = new MeterMachineDetailDto();
        meterMachineDetailDto.setDetailId(contentArray.getJSONObject(0).getString("opr_id"));
        meterMachineDetailDto.setState(MeterMachineDetailDto.STATE_W);
        List<MeterMachineDetailDto> meterMachineDetailDtos = meterMachineDetailV1InnerServiceSMOImpl.queryMeterMachineDetails(meterMachineDetailDto);
        if (meterMachineDetailDtos == null || meterMachineDetailDtos.size() < 1) {
            return ResultVo.createResponseEntity(new ResultVo(ResultVo.CODE_ERROR, "没有数据"));
        }

        if (!"SUCCESS".equals(contentArray.getJSONObject(0).getString("status"))) {
            meterMachineDetailDtos.get(0).setState(MeterMachineDetailDto.STATE_F);
            MeterMachineDetailPo meterMachineDetailPo = BeanConvertUtil.covertBean(meterMachineDetailDtos.get(0), MeterMachineDetailPo.class);
            meterMachineDetailV1InnerServiceSMOImpl.updateMeterMachineDetail(meterMachineDetailPo);
            return ResultVo.createResponseEntity(new ResultVo(ResultVo.CODE_ERROR, "拉合闸失败"));
        }

        meterMachineDetailDtos.get(0).setState(MeterMachineDetailDto.STATE_C);
        MeterMachineDetailPo meterMachineDetailPo = BeanConvertUtil.covertBean(meterMachineDetailDtos.get(0), MeterMachineDetailPo.class);
        meterMachineDetailV1InnerServiceSMOImpl.updateMeterMachineDetail(meterMachineDetailPo);

        MeterMachineDto meterMachineDto = new MeterMachineDto();
        meterMachineDto.setMachineId(meterMachineDetailDtos.get(0).getMachineId());
        List<MeterMachineDto> meterMachineDtos = meterMachineV1InnerServiceSMOImpl.queryMeterMachines(meterMachineDto);
        Assert.listOnlyOne(meterMachineDtos, "拉合闸失败");
        meterMachineDtos.get(0).setControlType(meterMachineDetailDtos.get(0).getDetailType());
        MeterMachinePo meterMachinePo = BeanConvertUtil.covertBean(meterMachineDtos.get(0), MeterMachinePo.class);
        meterMachineV1InnerServiceSMOImpl.updateMeterMachine(meterMachinePo);

        return ResultVo.createResponseEntity(new ResultVo(ResultVo.CODE_OK, "成功"));
    }

    @Override
    @Java110Transactional
    public ResponseEntity<String> cleanControlNotify(String implBean, String postInfo) {
        JSONObject data = JSONObject.parseObject(postInfo);

        String response_content = data.getString("response_content");
        String timestamp = data.getString("timestamp");
        String sign = data.getString("sign");

        if (!TdDianBiaoUtil.checkSign(response_content, timestamp, sign)) {
            System.out.println("sign check failed");
            return ResultVo.createResponseEntity(new ResultVo(ResultVo.CODE_ERROR, "鉴权失败"));
        }

        JSONArray contentArray = JSON.parseArray(response_content);

        if (contentArray == null || contentArray.size() < 1) {
            return ResultVo.createResponseEntity(new ResultVo(ResultVo.CODE_ERROR, "没有数据"));
        }
        MeterMachineDetailDto meterMachineDetailDto = new MeterMachineDetailDto();
        meterMachineDetailDto.setDetailId(contentArray.getJSONObject(0).getString("opr_id"));
        meterMachineDetailDto.setState(MeterMachineDetailDto.STATE_W);
        List<MeterMachineDetailDto> meterMachineDetailDtos = meterMachineDetailV1InnerServiceSMOImpl.queryMeterMachineDetails(meterMachineDetailDto);
        if (meterMachineDetailDtos == null || meterMachineDetailDtos.size() < 1) {
            return ResultVo.createResponseEntity(new ResultVo(ResultVo.CODE_ERROR, "没有数据"));
        }
        if (!"SUCCESS".equals(contentArray.getJSONObject(0).getString("status"))) {
            meterMachineDetailDtos.get(0).setState(MeterMachineDetailDto.STATE_F);
            MeterMachineDetailPo meterMachineDetailPo = BeanConvertUtil.covertBean(meterMachineDetailDtos.get(0), MeterMachineDetailPo.class);
            meterMachineDetailV1InnerServiceSMOImpl.updateMeterMachineDetail(meterMachineDetailPo);
            return ResultVo.createResponseEntity(new ResultVo(ResultVo.CODE_ERROR, "电表清零失败"));
        }
        return ResultVo.createResponseEntity(new ResultVo(ResultVo.CODE_OK, "成功"));
    }

}
