package com.java110.car.cmd.carMonth;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.car.OwnerCarDto;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.carMonthCard.CarMonthCardDto;
import com.java110.intf.car.ICarMonthCardV1InnerServiceSMO;
import com.java110.intf.car.IOwnerCarV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * 查询车牌可以购买的月卡
 */
@Java110Cmd(serviceCode = "carMonth.queryMonthCardByCarNum")
public class QueryMonthCardByCarNumCmd extends Cmd {

    @Autowired
    private IOwnerCarV1InnerServiceSMO ownerCarV1InnerServiceSMOImpl;

    @Autowired
    private ICarMonthCardV1InnerServiceSMO carMonthCardV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        Assert.hasKeyAndValue(reqJson, "communityId", "communityId不能为空");
        Assert.hasKeyAndValue(reqJson, "carNum", "carNum不能为空");


        OwnerCarDto ownerCarDto = new OwnerCarDto();
        ownerCarDto.setCarNum(reqJson.getString("carNum"));
        ownerCarDto.setCommunityId(reqJson.getString("communityId"));
        ownerCarDto.setCarTypeCd(OwnerCarDto.CAR_TYPE_PRIMARY);
        List<OwnerCarDto> ownerCarDtos = ownerCarV1InnerServiceSMOImpl.queryOwnerCars(ownerCarDto);

        if (ListUtil.isNull(ownerCarDtos)) {
            throw new CmdException("车牌号不正确");
        }

        if (StringUtil.isEmpty(ownerCarDtos.get(0).getPaId()) || ownerCarDtos.get(0).getPaId().startsWith("-")) {
            throw new CmdException("车辆车位已经释放，请重新到前台申请车位");
        }

        reqJson.put("paId",ownerCarDtos.get(0).getPaId());
        reqJson.put("startTime",ownerCarDtos.get(0).getStartTime());
        reqJson.put("endTime",ownerCarDtos.get(0).getEndTime());
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        CarMonthCardDto carMonthCardDto = new CarMonthCardDto();
        carMonthCardDto.setPaId(reqJson.getString("paId"));
        carMonthCardDto.setCommunityId(reqJson.getString("communityId"));


        int count = carMonthCardV1InnerServiceSMOImpl.queryCarMonthCardsCount(carMonthCardDto);

        List<CarMonthCardDto> carMonthCardDtos = null;

        if (count > 0) {
            carMonthCardDtos = carMonthCardV1InnerServiceSMOImpl.queryCarMonthCards(carMonthCardDto);
        } else {
            carMonthCardDtos = new ArrayList<>();
        }

        JSONObject data = new JSONObject();
        data.put("paId",reqJson.getString("paId"));
        data.put("startTime",reqJson.getDate("startTime"));
        data.put("endTime",reqJson.getDate("endTime"));
        data.put("cards",carMonthCardDtos);
        ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) reqJson.getInteger("row")), count, data);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        context.setResponseEntity(responseEntity);
    }
}
