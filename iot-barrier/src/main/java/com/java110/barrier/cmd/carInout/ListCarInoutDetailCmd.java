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
package com.java110.barrier.cmd.carInout;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.smo.IComputeFeeSMO;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.carInout.CarInoutDto;
import com.java110.dto.parking.ParkingBoxAreaDto;
import com.java110.dto.payment.CarInoutPaymentDto;
import com.java110.intf.barrier.ICarInoutPaymentV1InnerServiceSMO;
import com.java110.intf.barrier.ICarInoutV1InnerServiceSMO;
import com.java110.intf.barrier.IParkingBoxAreaV1InnerServiceSMO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;


/**
 * 类表述：查询
 * 服务编码：carInoutDetail.listCarInoutDetail
 * 请求路劲：/app/carInoutDetail.ListCarInoutDetail
 * add by 吴学文 at 2021-10-13 16:12:25 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Java110Cmd(serviceCode = "carInoutDetail.listCarInoutDetail")
public class ListCarInoutDetailCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(ListCarInoutDetailCmd.class);
    @Autowired
    private ICarInoutV1InnerServiceSMO carInoutV1InnerServiceSMOImpl;

    @Autowired
    private ICarInoutPaymentV1InnerServiceSMO carInoutPaymentV1InnerServiceSMOImpl;

    @Autowired
    private IComputeFeeSMO computeFeeSMOImpl;
    @Autowired
    private IParkingBoxAreaV1InnerServiceSMO parkingBoxAreaV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        super.validatePageInfo(reqJson);
        Assert.hasKeyAndValue(reqJson, "communityId", "communityId不能为空");

    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

        CarInoutDto carInoutDetailDto = BeanConvertUtil.covertBean(reqJson, CarInoutDto.class);
        if (reqJson.containsKey("boxId")) {
            carInoutDetailDto.setPaIds(getPaIds(reqJson));
        } else {
            carInoutDetailDto.setPaId(reqJson.getString("paId"));
        }
        int count = carInoutV1InnerServiceSMOImpl.queryCarInoutDetailsCount(carInoutDetailDto);

        List<CarInoutDto> carInoutDetailDtos = null;

        if (count > 0) {
            carInoutDetailDtos = carInoutV1InnerServiceSMOImpl.queryCarInoutDetails(carInoutDetailDto);
            // 计算实收
            computeRealCharge(carInoutDetailDtos);
            carInoutDetailDtos = computeCarInoutDetail(carInoutDetailDtos);
        } else {
            carInoutDetailDtos = new ArrayList<>();
        }

        ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) reqJson.getInteger("row")), count, carInoutDetailDtos);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        cmdDataFlowContext.setResponseEntity(responseEntity);
    }

    private void computeRealCharge(List<CarInoutDto> carInoutDetailDtos) {

        if (ListUtil.isNull(carInoutDetailDtos)) {
            return;
        }

        List<String> inoutIds = new ArrayList<>();
        for (CarInoutDto tmpCarInoutDto : carInoutDetailDtos) {
            inoutIds.add(tmpCarInoutDto.getInoutId());
        }

        CarInoutPaymentDto carInoutPaymentDto = new CarInoutPaymentDto();
        carInoutPaymentDto.setCommunityId(carInoutDetailDtos.get(0).getCommunityId());
        carInoutPaymentDto.setInoutIds(inoutIds.toArray(new String[inoutIds.size()]));

        List<CarInoutPaymentDto> carInoutPaymentDtos = carInoutPaymentV1InnerServiceSMOImpl.queryRealChargeByInoutId(carInoutPaymentDto);

        if (ListUtil.isNull(carInoutPaymentDtos)) {
            return;
        }
        for (CarInoutDto tmpCarInoutDto : carInoutDetailDtos) {
            for (CarInoutPaymentDto tmpCarInoutPaymentDto : carInoutPaymentDtos) {
                if (tmpCarInoutPaymentDto.getInoutId().equals(tmpCarInoutDto.getInoutId())) {
                    tmpCarInoutDto.setRealCharge(tmpCarInoutPaymentDto.getRealCharge());
                }
            }
        }

    }


    private String[] getPaIds(JSONObject reqJson) {
        if (reqJson.containsKey("boxId") && !StringUtil.isEmpty(reqJson.getString("boxId"))) {
            ParkingBoxAreaDto parkingBoxAreaDto = new ParkingBoxAreaDto();
            parkingBoxAreaDto.setBoxId(reqJson.getString("boxId"));
            parkingBoxAreaDto.setCommunityId(reqJson.getString("communityId"));
            List<ParkingBoxAreaDto> parkingBoxAreaDtos = parkingBoxAreaV1InnerServiceSMOImpl.queryParkingBoxAreas(parkingBoxAreaDto);

            if (parkingBoxAreaDtos == null || parkingBoxAreaDtos.size() < 1) {
                throw new CmdException("未查到停车场信息");
            }
            List<String> paIds = new ArrayList<>();
            for (ParkingBoxAreaDto parkingBoxAreaDto1 : parkingBoxAreaDtos) {
                paIds.add(parkingBoxAreaDto1.getPaId());
            }
            String[] paIdss = paIds.toArray(new String[paIds.size()]);
            return paIdss;
        }
        return null;
    }

    private List<CarInoutDto> computeCarInoutDetail(List<CarInoutDto> carInoutDetailDtos) {
        return computeFeeSMOImpl.computeTempCarInoutDetailStopTimeAndFee(carInoutDetailDtos);
    }
}
