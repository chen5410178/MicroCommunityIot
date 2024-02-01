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


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.barrier.dao.ICarInoutV1ServiceDao;
import com.java110.barrier.engine.IBarrierEngine;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.PageDto;
import com.java110.bean.dto.coupon.ParkingCouponCarDto;
import com.java110.bean.po.coupon.ParkingCouponCarOrderPo;
import com.java110.bean.po.coupon.ParkingCouponCarPo;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.fee.TempCarPayOrderDto;
import com.java110.dto.parking.ParkingBoxAreaDto;
import com.java110.intf.barrier.ICarInoutV1InnerServiceSMO;
import com.java110.dto.carInout.CarInoutDto;
import com.java110.intf.barrier.IParkingBoxAreaV1InnerServiceSMO;
import com.java110.intf.car.IParkingCouponCarOrderV1InnerServiceSMO;
import com.java110.intf.car.IParkingCouponCarV1InnerServiceSMO;
import com.java110.po.carInout.CarInoutPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 类表述： 服务之前调用的接口实现类，不对外提供接口能力 只用于接口建调用
 * add by 吴学文 at 2023-09-21 16:30:30 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@RestController
public class CarInoutV1InnerServiceSMOImpl implements ICarInoutV1InnerServiceSMO {

    @Autowired
    private ICarInoutV1ServiceDao carInoutV1ServiceDaoImpl;

    @Autowired
    private IBarrierEngine barrierEngineImpl;


    @Autowired
    private IParkingBoxAreaV1InnerServiceSMO parkingBoxAreaV1InnerServiceSMOImpl;


    @Autowired
    private IParkingCouponCarV1InnerServiceSMO parkingCouponCarV1InnerServiceSMOImpl;

    @Autowired
    private IParkingCouponCarOrderV1InnerServiceSMO parkingCouponCarOrderV1InnerServiceSMOImpl;

    @Override
    public int saveCarInout(@RequestBody  CarInoutPo carInoutPo) {
        int saveFlag = carInoutV1ServiceDaoImpl.saveCarInoutInfo(BeanConvertUtil.beanCovertMap(carInoutPo));
        return saveFlag;
    }

     @Override
    public int updateCarInout(@RequestBody  CarInoutPo carInoutPo) {
        int saveFlag = carInoutV1ServiceDaoImpl.updateCarInoutInfo(BeanConvertUtil.beanCovertMap(carInoutPo));
        return saveFlag;
    }

     @Override
    public int deleteCarInout(@RequestBody  CarInoutPo carInoutPo) {
       carInoutPo.setStatusCd("1");
       int saveFlag = carInoutV1ServiceDaoImpl.updateCarInoutInfo(BeanConvertUtil.beanCovertMap(carInoutPo));
       return saveFlag;
    }

    @Override
    public List<CarInoutDto> queryCarInouts(@RequestBody  CarInoutDto carInoutDto) {

        //校验是否传了 分页信息

        int page = carInoutDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            carInoutDto.setPage((page - 1) * carInoutDto.getRow());
        }

        List<CarInoutDto> carInouts = BeanConvertUtil.covertBeanList(carInoutV1ServiceDaoImpl.getCarInoutInfo(BeanConvertUtil.beanCovertMap(carInoutDto)), CarInoutDto.class);

        return carInouts;
    }


    @Override
    public int queryCarInoutsCount(@RequestBody CarInoutDto carInoutDto) {
        return carInoutV1ServiceDaoImpl.queryCarInoutsCount(BeanConvertUtil.beanCovertMap(carInoutDto));    }

    @Override
    public List<CarInoutDto> queryCarInoutDetails(@RequestBody  CarInoutDto carInoutDetailDto) {

        //校验是否传了 分页信息

        int page = carInoutDetailDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            carInoutDetailDto.setPage((page - 1) * carInoutDetailDto.getRow());
        }

        List<CarInoutDto> carInoutDetails = BeanConvertUtil.covertBeanList(carInoutV1ServiceDaoImpl.getCarInoutDetailInfo(BeanConvertUtil.beanCovertMap(carInoutDetailDto)), CarInoutDto.class);

        return carInoutDetails;
    }

    @Override
    public List<CarInoutDto> hasOpenDoorError(@RequestBody CarInoutDto tmpCarInoutDto) {
        List<CarInoutDto> carInoutDetails = BeanConvertUtil.covertBeanList(carInoutV1ServiceDaoImpl.hasOpenDoorError(BeanConvertUtil.beanCovertMap(tmpCarInoutDto)), CarInoutDto.class);

        return carInoutDetails;
    }


    @Override
    public int queryCarInoutDetailsCount(@RequestBody CarInoutDto carInoutDetailDto) {
        return carInoutV1ServiceDaoImpl.queryCarInoutDetailsCount(BeanConvertUtil.beanCovertMap(carInoutDetailDto));    }
    @Override
    public ResultVo carInout(@RequestBody JSONObject reqJson) {
        //出场时 先 补充费用信息
        if(!"1101".equals(reqJson.getString("type"))) {

            ParkingBoxAreaDto parkingBoxAreaDto = new ParkingBoxAreaDto();
            parkingBoxAreaDto.setBoxId(reqJson.getString("boxId"));
            parkingBoxAreaDto.setDefaultArea(ParkingBoxAreaDto.DEFAULT_AREA_TRUE);
            List<ParkingBoxAreaDto> parkingBoxAreaDtos = parkingBoxAreaV1InnerServiceSMOImpl.queryParkingBoxAreas(parkingBoxAreaDto);

            if(ListUtil.isNull(parkingBoxAreaDtos)){
                throw new CmdException("未包含停车场信息");
            }
            TempCarPayOrderDto tempCarPayOrderDto = new TempCarPayOrderDto();
            tempCarPayOrderDto.setCarNum(reqJson.getString("carNum"));
            tempCarPayOrderDto.setPaId(parkingBoxAreaDtos.get(0).getPaId());
            ResultVo resultVo = barrierEngineImpl.getTempCarFeeOrder(tempCarPayOrderDto);
            if(resultVo.getCode() != ResultVo.CODE_OK){
                throw new CmdException(resultVo.getMsg());
            }
            String data = JSONObject.toJSONString(resultVo.getData());
            JSONObject orderInfo = JSONObject.parseObject(data);
            //JSONObject orderInfo = JSONObject.parseObject(resultVo.getData().toString());

            tempCarPayOrderDto = new TempCarPayOrderDto();
            tempCarPayOrderDto.setCarNum(reqJson.getString("carNum"));
            tempCarPayOrderDto.setPaId(parkingBoxAreaDtos.get(0).getPaId());
            tempCarPayOrderDto.setOrderId(orderInfo.getString("orderId"));
            tempCarPayOrderDto.setAmount(Double.parseDouble(reqJson.getString("amount")));
            tempCarPayOrderDto.setPayCharge(Double.parseDouble(reqJson.getString("payCharge")));
            tempCarPayOrderDto.setPayType(reqJson.getString("payType"));
            //处理优惠券
            dealParkingCouponCar(reqJson,tempCarPayOrderDto);
            //tempCarPayOrderDto.setMachineId(reqJson.getString("machineId"));
            resultVo = barrierEngineImpl.notifyTempCarFeeOrder(tempCarPayOrderDto);
            if(resultVo.getCode() != ResultVo.CODE_OK){
                throw new CmdException(resultVo.getMsg());
            }
        }
        ResultVo resultVo = barrierEngineImpl.customCarInOut(reqJson);
       return resultVo;
    }

    private void dealParkingCouponCar(JSONObject reqJson,TempCarPayOrderDto tempCarPayOrderDto) {
        //处理停车劵

        if(!reqJson.containsKey("pccIds")) {
            return ;
        }

        JSONArray pccIds = reqJson.getJSONArray("pccIds");
        ParkingCouponCarPo parkingCouponCarPo = null;
        ParkingCouponCarOrderPo parkingCouponCarOrderPo = null;
        String pccId = "";
        List<String> tmpPccIds = new ArrayList<>();
        for(int pccIndex = 0;pccIndex <  pccIds.size();pccIndex++){
            pccId = pccIds.getString(pccIndex);
            tmpPccIds.add(pccId);
            parkingCouponCarPo = new ParkingCouponCarPo();
            parkingCouponCarPo.setPccId(pccId);
            parkingCouponCarPo.setState(ParkingCouponCarDto.STATE_FINISH);
            parkingCouponCarV1InnerServiceSMOImpl.updateParkingCouponCar(parkingCouponCarPo);

            parkingCouponCarOrderPo = new ParkingCouponCarOrderPo();
            parkingCouponCarOrderPo.setOrderId(GenerateCodeFactory.getGeneratorId("11"));
            parkingCouponCarOrderPo.setCarNum(reqJson.getString("carNum"));
            parkingCouponCarOrderPo.setCarOutId("-1");
            parkingCouponCarOrderPo.setCommunityId(reqJson.getString("communityId"));
            parkingCouponCarOrderPo.setMachineId(StringUtil.isEmpty(reqJson.getString("machineId"))?"-1":reqJson.getString("machineId"));
            parkingCouponCarOrderPo.setMachineName("未知");
            parkingCouponCarOrderPo.setPaId(reqJson.getString("paId"));
            parkingCouponCarOrderPo.setPccId(pccId);
            parkingCouponCarOrderPo.setRemark("pc端支付停车劵抵扣");

            parkingCouponCarOrderV1InnerServiceSMOImpl.saveParkingCouponCarOrder(parkingCouponCarOrderPo);
        }

        tempCarPayOrderDto.setPccIds(tmpPccIds.toArray(new String[tmpPccIds.size()]));
    }


}
