package com.java110.barrier.engine;

import com.alibaba.fastjson.JSONObject;
import com.java110.barrier.engine.inout.SendInfoEngine;
import com.java110.barrier.factory.BarrierGateControlDto;
import com.java110.barrier.factory.CarMachineProcessFactory;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.coupon.ParkingCouponCarDto;
import com.java110.bean.po.coupon.ParkingCouponCarPo;
import com.java110.core.cache.CommonCache;
import com.java110.core.constant.ResponseConstant;
import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.utils.*;
import com.java110.dto.barrier.BarrierDto;
import com.java110.dto.carInout.CarInoutDto;
import com.java110.dto.fee.TempCarFeeConfigDto;
import com.java110.dto.fee.TempCarFeeResult;
import com.java110.dto.fee.TempCarPayOrderDto;
import com.java110.dto.manualOpenDoorLog.ManualOpenDoorLogDto;
import com.java110.dto.parking.*;
import com.java110.intf.barrier.*;
import com.java110.intf.car.IOwnerCarV1InnerServiceSMO;
import com.java110.intf.car.IParkingAreaV1InnerServiceSMO;
import com.java110.intf.car.IParkingCouponCarV1InnerServiceSMO;
import com.java110.po.carInout.CarInoutPo;
import com.java110.po.manualOpenDoorLog.ManualOpenDoorLogPo;
import com.java110.po.ownerCar.CarInoutPaymentPo;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service
public class BarrierEngineImpl implements IBarrierEngine {
    private static Logger logger = LoggerFactory.getLogger(BarrierEngineImpl.class);

    @Autowired
    private IBarrierV1InnerServiceSMO barrierV1InnerServiceSMOImpl;

    @Autowired
    private IParkingAreaV1InnerServiceSMO parkingAreaV1InnerServiceSMOImpl;

    @Autowired
    private ICarInoutV1InnerServiceSMO carInoutV1InnerServiceSMOImpl;

    @Autowired
    private SendInfoEngine sendInfoEngine;

    @Autowired
    private IManualOpenDoorLogV1InnerServiceSMO manualOpenDoorLogV1InnerServiceSMOImpl;

    @Autowired
    private IOwnerCarV1InnerServiceSMO ownerCarV1InnerServiceSMOImpl;

    @Autowired
    private ITempCarFeeConfigV1InnerServiceSMO tempCarFeeConfigV1InnerServiceSMOImpl;

    @Autowired
    private IParkingCouponCarV1InnerServiceSMO parkingCouponCarV1InnerServiceSMOImpl;

    @Autowired
    private ICarInoutPaymentV1InnerServiceSMO carInoutPaymentV1InnerServiceSMOImpl;

    @Override
    public ResultVo getTempCarFeeOrder(TempCarPayOrderDto tempCarPayOrderDto) {


        BarrierDto machineDto = new BarrierDto();
        machineDto.setPaId(tempCarPayOrderDto.getPaId());
        //machineDto.setLocationType(MachineDto.LOCATION_TYPE_PARKING_AREA);
        List<BarrierDto> machineDtos = barrierV1InnerServiceSMOImpl.queryBoxMachines(machineDto);

        if (ListUtil.isNull(machineDtos)) {
            return new ResultVo(ResultVo.CODE_ERROR, "未找到设备信息");
        }

        tempCarPayOrderDto = getCustomeTempCarFeeOrder(tempCarPayOrderDto, machineDtos.get(0));

        if (tempCarPayOrderDto == null) {
            return new ResultVo(ResultVo.CODE_ERROR, "查询失败");
        }
        JSONObject data = JSONObject.parseObject(JSONObject.toJSONString(tempCarPayOrderDto));
        data.put("inTime", DateUtil.getFormatTimeString(tempCarPayOrderDto.getInTime(), DateUtil.DATE_FORMATE_STRING_A));
        data.put("queryTime", DateUtil.getFormatTimeString(tempCarPayOrderDto.getQueryTime(), DateUtil.DATE_FORMATE_STRING_A));
        return new ResultVo(ResultVo.CODE_OK, ResultVo.MSG_OK, data);

    }


    @Override
    public ResultVo notifyTempCarFeeOrder(TempCarPayOrderDto tempCarPayOrderDto) {
        ResultVo resultDto = null;

        //这里预留 调用自己的 算费系统算费 暂不实现
        BarrierDto machineDto = new BarrierDto();
        machineDto.setPaId(tempCarPayOrderDto.getPaId());
        //
        List<BarrierDto> machineDtos = barrierV1InnerServiceSMOImpl.queryBoxMachines(machineDto);

        if (machineDtos == null || machineDtos.isEmpty()) {
            return new ResultVo(ResultVo.CODE_ERROR, "未找到设备信息");
        }

        resultDto = notifyCustomTempCarFeeOrder(machineDtos.get(0), tempCarPayOrderDto);
        return resultDto;
    }

    @Override
    public ResultVo customCarInOut(JSONObject reqJson) {


        BarrierDto machineDto = new BarrierDto();
        machineDto.setMachineId(reqJson.getString("machineId"));
        List<BarrierDto> machineDtos = barrierV1InnerServiceSMOImpl.queryBarriers(machineDto);

        Assert.listOnlyOne(machineDtos, "设备不存在");
        machineDto = machineDtos.get(0);
        ResultVo resultDto = null;
        ResultParkingAreaTextDto parkingAreaTextDto = null;
        if ("1101".equals(reqJson.getString("type"))) {
            uploadcarin(machineDto, reqJson);
            parkingAreaTextDto
                    = new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_IN_SUCCESS, reqJson.getString("carNum"),
                    "欢迎光临", "", "", reqJson.getString("carNum") + ",欢迎光临", reqJson.getString("carNum"));
        } else {
            uploadcarout(machineDto, reqJson,"手工出场");
            parkingAreaTextDto
                    = new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_OUT_SUCCESS, reqJson.getString("carNum"),
                    "一路平安", "", "", reqJson.getString("carNum") + ",一路平安", reqJson.getString("carNum"));
        }
        parkingAreaTextDto.setCarNum(reqJson.getString("carNum"));
        resultDto = openDoor(machineDtos.get(0), parkingAreaTextDto, reqJson);

        return resultDto;
    }

    public ResultVo openDoor(BarrierDto machineDto, ParkingAreaTextDto parkingAreaTextDto, JSONObject paramObj) {


        CarMachineProcessFactory.getCarImpl(machineDto.getHmId()).openDoor(machineDto, parkingAreaTextDto);


        if (paramObj != null && paramObj.containsKey("staffId")) {
            ManualOpenDoorLogPo manualOpenDoorLogPo = new ManualOpenDoorLogPo();
            manualOpenDoorLogPo.setLogId(GenerateCodeFactory.getGeneratorId("11"));
            manualOpenDoorLogPo.setMachineName(machineDto.getMachineName());
            manualOpenDoorLogPo.setCommunityId(machineDto.getCommunityId());
            manualOpenDoorLogPo.setExtMachineId(machineDto.getMachineId());
            manualOpenDoorLogPo.setExtStaffId(paramObj.getString("staffId"));
            manualOpenDoorLogPo.setMachineId(machineDto.getMachineId());
            manualOpenDoorLogPo.setStaffId("-1");
            manualOpenDoorLogPo.setStaffName(paramObj.getString("staffName"));
            manualOpenDoorLogV1InnerServiceSMOImpl.saveManualOpenDoorLog(manualOpenDoorLogPo);


            CarMachineProcessFactory.getCarImpl(machineDto.getHmId()).triggerImage(machineDto, manualOpenDoorLogPo);

        }

//        JSONObject data = new JSONObject();
//        if (StringUtil.isEmpty(machineDto.getTaskId())) {
//            data.put("taskId", machineDto.getTaskId());
//        }
        return new ResultVo(ResultVo.CODE_OK, ResultVo.MSG_OK);
    }

    @Override
    public ResultVo closeDoor(BarrierDto barrierDto, ParkingAreaTextDto parkingAreaTextDto, JSONObject reqJson) {
        CarMachineProcessFactory.getCarImpl(barrierDto.getHmId()).closeDoor(barrierDto, parkingAreaTextDto);

        return new ResultVo(ResultVo.CODE_OK, ResultVo.MSG_OK);
    }

    /**
     * 车辆进场记录
     *
     * @param acceptJson
     * @return
     */
    private JSONObject uploadcarin(BarrierDto machineDto, JSONObject acceptJson) {

        String paId = "";

        //查询 岗亭
        ParkingBoxDto parkingBoxDto = new ParkingBoxDto();
        parkingBoxDto.setBoxId(machineDto.getBoxId());
        parkingBoxDto.setCommunityId(machineDto.getCommunityId());
        parkingBoxDto.setDefaultArea(ParkingBoxAreaDto.DEFAULT_AREA_TRUE);
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaV1InnerServiceSMOImpl.queryParkingAreasByBox(parkingBoxDto);
        //Assert.listOnlyOne(parkingAreaDtos, "停车场不存在");
        if (parkingAreaDtos == null || parkingAreaDtos.size() < 1) {
            throw new IllegalArgumentException("停车场不存在");
        }

        paId = parkingAreaDtos.get(0).getPaId();


        //2.0 手工进场
        CarInoutPo carInoutPo = new CarInoutPo();
        carInoutPo.setCarNum(acceptJson.getString("carNum"));
        carInoutPo.setCarType(CarInoutDto.CAR_TYPE_TEMP);
        carInoutPo.setCarTypeName("临时车");
        carInoutPo.setCommunityId(machineDto.getCommunityId());
        carInoutPo.setInoutId(GenerateCodeFactory.getGeneratorId("11"));
        carInoutPo.setMachineCode(machineDto.getMachineCode());
        carInoutPo.setMachineId(machineDto.getMachineId());
        carInoutPo.setOpenTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        carInoutPo.setPaId(paId);
        carInoutPo.setPaNum(parkingAreaDtos.get(0).getNum());
        carInoutPo.setCarInout(CarInoutDto.CAR_INOUT_IN);
        carInoutPo.setState(CarInoutDto.STATE_IN);
        carInoutPo.setRemark("手工进场");
        carInoutPo.setCiId(GenerateCodeFactory.getGeneratorId("11"));
        carInoutPo.setInTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        carInoutPo.setUnlicense("F");
        //无牌车标识
        if(acceptJson.containsKey("unlicense")){
            carInoutPo.setUnlicense("T");
        }



        carInoutV1InnerServiceSMOImpl.saveCarInout(carInoutPo);

        BarrierGateControlDto barrierGateControlDto
                = new BarrierGateControlDto(BarrierGateControlDto.ACTION_FEE_INFO, acceptJson.getString("carNum"), machineDto, 0, null, acceptJson.getString("carNum") + "手工进场", "开门成功");
        sendInfoEngine.sendInfo(barrierGateControlDto, machineDto.getBoxId(), machineDto);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 0);
        jsonObject.put("msg", "成功");
        return jsonObject;
    }


    private JSONObject uploadcarout(BarrierDto machineDto, JSONObject acceptJson,String remark) {

        String paId = "";

        //查询 岗亭
        ParkingBoxDto parkingBoxDto = new ParkingBoxDto();
        parkingBoxDto.setBoxId(machineDto.getBoxId());
        parkingBoxDto.setCommunityId(machineDto.getCommunityId());
        parkingBoxDto.setDefaultArea(ParkingBoxAreaDto.DEFAULT_AREA_TRUE);
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaV1InnerServiceSMOImpl.queryParkingAreasByBox(parkingBoxDto);
        //Assert.listOnlyOne(parkingAreaDtos, "停车场不存在");
        if (parkingAreaDtos == null || parkingAreaDtos.size() < 1) {
            throw new IllegalArgumentException("停车场不存在");
        }

        paId = parkingAreaDtos.get(0).getPaId();

        //查询是否有入场数据
        CarInoutDto carInoutDto = new CarInoutDto();
        carInoutDto.setCarNum(acceptJson.getString("carNum"));
        carInoutDto.setCarInout(CarInoutDto.CAR_INOUT_IN);
        carInoutDto.setPaId(paId);
        carInoutDto.setStates(new String[]{CarInoutDto.STATE_IN, CarInoutDto.STATE_PAY});
        List<CarInoutDto> carInoutDtos = carInoutV1InnerServiceSMOImpl.queryCarInouts(carInoutDto);

        String inoutId = "";
        String inTime = "";
        if (carInoutDtos != null && !carInoutDtos.isEmpty()) {
            CarInoutPo carInoutPo = new CarInoutPo();
            carInoutPo.setCiId(carInoutDtos.get(0).getCiId());
            carInoutPo.setState(CarInoutDto.STATE_OUT);
            carInoutV1InnerServiceSMOImpl.updateCarInout(carInoutPo);

            inoutId = carInoutDtos.get(0).getInoutId();
            inTime = carInoutDtos.get(0).getInTime();
        } else {
            inoutId = GenerateCodeFactory.getGeneratorId("11");
            inTime = DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A);
        }
        CarInoutPo carInoutPo = new CarInoutPo();
        carInoutPo.setCarNum(acceptJson.getString("carNum"));
        carInoutPo.setCarType(CarInoutDto.CAR_TYPE_TEMP);
        carInoutPo.setCarTypeName("临时车");
        carInoutPo.setCommunityId(machineDto.getCommunityId());
        carInoutPo.setInoutId(inoutId);
        carInoutPo.setMachineCode(machineDto.getMachineCode());
        carInoutPo.setMachineId(machineDto.getMachineId());
        carInoutPo.setOpenTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        carInoutPo.setPaId(paId);
        carInoutPo.setPaNum(parkingAreaDtos.get(0).getNum());
        carInoutPo.setCarInout(CarInoutDto.CAR_INOUT_OUT);
        carInoutPo.setState(CarInoutDto.STATE_OUT);
        carInoutPo.setRemark(remark);
        carInoutPo.setCiId(GenerateCodeFactory.getGeneratorId("11"));
        carInoutPo.setInTime(inTime);


//        carInoutPo.setCarType(CarInoutDto.C);
//        if (acceptJson.containsKey("payCharge")) {
//            carInoutPo.setPayCharge(acceptJson.getString("payCharge"));
//        } else {
//            carInoutPo.setPayCharge(acceptJson.getString("amount"));
//        }
//        carInoutPo.setRealCharge(acceptJson.getString("amount"));
//        carInoutPo.setPayType("1");
        carInoutPo.setMachineCode(machineDto.getMachineCode());
        carInoutV1InnerServiceSMOImpl.saveCarInout(carInoutPo);

        BarrierGateControlDto barrierGateControlDto
                = new BarrierGateControlDto(BarrierGateControlDto.ACTION_FEE_INFO, acceptJson.getString("carNum"), machineDto, 0, null, acceptJson.getString("carNum") + "手工出场", "开门成功");
        sendInfoEngine.sendInfo(barrierGateControlDto, machineDto.getBoxId(), machineDto);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 0);
        jsonObject.put("msg", "成功");
        return jsonObject;
    }


    /**
     * 调用自己的算费逻辑
     *
     * @param tempCarPayOrderDto
     * @param machineDto
     * @return
     */
    private TempCarPayOrderDto getCustomeTempCarFeeOrder(TempCarPayOrderDto tempCarPayOrderDto, BarrierDto machineDto) {
        ParkingAreaDto parkingAreaDto = new ParkingAreaDto();
        parkingAreaDto.setPaId(tempCarPayOrderDto.getPaId());
        parkingAreaDto.setCommunityId(machineDto.getCommunityId());
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaV1InnerServiceSMOImpl.queryParkingAreas(parkingAreaDto);

        Assert.listOnlyOne(parkingAreaDtos, "未找到停车场信息");

        String carNum = tempCarPayOrderDto.getCarNum();

        //查询是否有入场数据
        CarInoutDto carInoutDto = new CarInoutDto();
        carInoutDto.setCarNum(carNum);
        carInoutDto.setPaId(parkingAreaDtos.get(0).getPaId());
        carInoutDto.setStates(new String[]{CarInoutDto.STATE_IN, CarInoutDto.STATE_PAY, CarInoutDto.STATE_REPAY});
        carInoutDto.setCarInout(CarInoutDto.CAR_INOUT_IN);
        List<CarInoutDto> carInoutDtos = carInoutV1InnerServiceSMOImpl.queryCarInouts(carInoutDto);

        if (carInoutDtos == null || carInoutDtos.size() == 0) {
            throw new IllegalArgumentException("未查询到进场记录");
        }


        TempCarFeeConfigDto tempCarFeeConfigDto = new TempCarFeeConfigDto();
        tempCarFeeConfigDto.setPaId(parkingAreaDtos.get(0).getPaId());
        tempCarFeeConfigDto.setCommunityId(machineDto.getCommunityId());
        List<TempCarFeeConfigDto> tempCarFeeConfigDtos = tempCarFeeConfigV1InnerServiceSMOImpl.queryTempCarFeeConfigs(tempCarFeeConfigDto);

        if (tempCarFeeConfigDtos == null || tempCarFeeConfigDtos.isEmpty()) {
            throw new IllegalArgumentException("未查询到临时车费用规则");
        }

        //根据extPccIds 查询停车劵

        List<ParkingCouponCarDto> parkingCouponCarDtos = getCarCoupons(tempCarPayOrderDto);

        IComputeTempCarFee computeTempCarFee = ApplicationContextFactory.getBean(tempCarFeeConfigDtos.get(0).getRuleId(), IComputeTempCarFee.class);
        TempCarFeeResult result = computeTempCarFee.computeTempCarFee(carInoutDtos.get(0), tempCarFeeConfigDtos.get(0), getDurationCarCoupons(parkingCouponCarDtos));


        // 停车劵处理 这里主要处理 全免 打折 金额
        double amount = dealParkingCouponCar(result.getPayCharge(), parkingCouponCarDtos);


        tempCarPayOrderDto = new TempCarPayOrderDto();
        tempCarPayOrderDto.setPaId(parkingAreaDtos.get(0).getPaId());
        tempCarPayOrderDto.setInTime(DateUtil.getDateFromStringA(carInoutDtos.get(0).getOpenTime()));
        tempCarPayOrderDto.setQueryTime(DateUtil.getCurrentDate());
        tempCarPayOrderDto.setCarNum(carNum);
        tempCarPayOrderDto.setPayCharge(result.getPayCharge());
        tempCarPayOrderDto.setAmount(amount);
        tempCarPayOrderDto.setStopTimeTotal(result.getHours() * 60 + result.getMin());
        tempCarPayOrderDto.setPaId(parkingAreaDtos.get(0).getPaId());
        tempCarPayOrderDto.setOrderId(carInoutDtos.get(0).getInoutId());
        return tempCarPayOrderDto;
    }

    /**
     * 停车劵处理 这里主要处理 全免 打折 金额
     *
     * @param payCharge
     * @param parkingCouponCarDtos
     * @return
     */
    public double dealParkingCouponCar(double payCharge, List<ParkingCouponCarDto> parkingCouponCarDtos) {


        if (parkingCouponCarDtos == null || parkingCouponCarDtos.isEmpty()) {
            return payCharge;
        }

        if (payCharge == 0) {
            return payCharge;
        }

        BigDecimal amountDec = new BigDecimal(payCharge);

        double couponAmount = 0.0;

        for (ParkingCouponCarDto parkingCouponCarDto : parkingCouponCarDtos) {
            try {
                if (ParkingCouponCarDto.TYPE_CD_HOURS.equals(parkingCouponCarDto.getTypeCd())) {
                    continue;
                }
                if (ParkingCouponCarDto.TYPE_CD_FREE.equals(parkingCouponCarDto.getTypeCd())) {
                    continue;
                }
                parkingCouponCarDto.setHasUser(true);

                couponAmount = Double.parseDouble(parkingCouponCarDto.getValue());

                if (ParkingCouponCarDto.TYPE_CD_MONEY.equals(parkingCouponCarDto.getTypeCd())) {
                    if (amountDec.doubleValue() < couponAmount) {
                        return 0;
                    }
                    amountDec = amountDec.subtract(new BigDecimal(couponAmount));
                }

                if (ParkingCouponCarDto.TYPE_CD_DISCOUNT.equals(parkingCouponCarDto.getTypeCd())) {
                    if (amountDec.doubleValue() < couponAmount) {
                        return 0;
                    }
                    amountDec = amountDec.multiply(new BigDecimal(couponAmount)).setScale(2, BigDecimal.ROUND_HALF_UP);
                }
            } catch (Exception e) {
                logger.error("停车劵 处理失败", e);
            }

        }


        return amountDec.doubleValue();
    }

    /**
     * 获取时长优惠券
     *
     * @param parkingCouponCarDtos
     * @return
     */
    private List<ParkingCouponCarDto> getDurationCarCoupons(List<ParkingCouponCarDto> parkingCouponCarDtos) {

        List<ParkingCouponCarDto> tmpParkingCouponCarDtos = new ArrayList<>();

        if (parkingCouponCarDtos == null || parkingCouponCarDtos.isEmpty()) {
            return tmpParkingCouponCarDtos;
        }

        for (ParkingCouponCarDto parkingCouponCarDto : parkingCouponCarDtos) {
            if (ParkingCouponCarDto.TYPE_CD_HOURS.equals(parkingCouponCarDto.getTypeCd())
                    || ParkingCouponCarDto.TYPE_CD_FREE.equals(parkingCouponCarDto.getTypeCd())) {
                tmpParkingCouponCarDtos.add(parkingCouponCarDto);
            }
        }

        return tmpParkingCouponCarDtos;
    }


    private List<ParkingCouponCarDto> getCarCoupons(TempCarPayOrderDto tempCarPayOrderDto) {

        if (tempCarPayOrderDto.getPccIds() == null) {
            return new ArrayList<>();
        }

        ParkingCouponCarDto parkingCouponCarDto = new ParkingCouponCarDto();
        parkingCouponCarDto.setExtPccIds(tempCarPayOrderDto.getPccIds());
        parkingCouponCarDto.setState(ParkingCouponCarDto.STATE_W);
        List<ParkingCouponCarDto> parkingCouponCarDtos = parkingCouponCarV1InnerServiceSMOImpl.queryParkingCouponCars(parkingCouponCarDto);

        return parkingCouponCarDtos;
    }


    private ResultVo notifyCustomTempCarFeeOrder(BarrierDto barrierDto, TempCarPayOrderDto tempCarPayOrderDto) {
        /**
         *  postParameters.put("plateNum", tempCarPayOrderDto.getCarNum());
         *         postParameters.put("orderId", tempCarPayOrderDto.getOrderId());
         *         postParameters.put("amount", tempCarPayOrderDto.getAmount());
         *         postParameters.put("payTime", tempCarPayOrderDto.getPayTime());
         *         postParameters.put("payType", tempCarPayOrderDto.getPayType());
         */
        //查询是否有入场数据
        CarInoutDto carInoutDto = new CarInoutDto();
        carInoutDto.setInoutId(tempCarPayOrderDto.getOrderId());
        carInoutDto.setStates(new String[]{CarInoutDto.STATE_IN, CarInoutDto.STATE_PAY, CarInoutDto.STATE_REPAY});
        carInoutDto.setCarInout(CarInoutDto.CAR_INOUT_IN);
        List<CarInoutDto> carInoutDtos = carInoutV1InnerServiceSMOImpl.queryCarInouts(carInoutDto);

        if (carInoutDtos == null || carInoutDtos.isEmpty()) {
            return new ResultVo(ResultVo.CODE_ERROR, "支付订单不存在");
        }
        CarInoutPo carInoutPo = new CarInoutPo();
        carInoutPo.setState(CarInoutDto.STATE_PAY);

        carInoutPo.setCiId(carInoutDtos.get(0).getCiId());
        carInoutV1InnerServiceSMOImpl.updateCarInout(carInoutPo);

        //todo 写入支付表
        CarInoutPaymentPo carInoutPaymentPo = new CarInoutPaymentPo();
        carInoutPaymentPo.setCommunityId(carInoutDtos.get(0).getCommunityId());
        carInoutPaymentPo.setInoutId(carInoutDtos.get(0).getInoutId());
        carInoutPaymentPo.setPaId(carInoutDtos.get(0).getPaId());
        carInoutPaymentPo.setPayType(tempCarPayOrderDto.getPayType());
        carInoutPaymentPo.setPayCharge(tempCarPayOrderDto.getAmount() + "");
        carInoutPaymentPo.setRealCharge(tempCarPayOrderDto.getAmount() + "");
        carInoutPaymentPo.setPaymentId(GenerateCodeFactory.getGeneratorId("9"));
        if (tempCarPayOrderDto.getPayCharge() != 0) {
            carInoutPaymentPo.setPayCharge(tempCarPayOrderDto.getPayCharge() + "");
        }

        carInoutPaymentV1InnerServiceSMOImpl.saveCarInoutPayment(carInoutPaymentPo);


        //判断是否有停车劵
        dealParkingCouponCar(tempCarPayOrderDto);

        //场内二维码支付
        if (StringUtil.isEmpty(tempCarPayOrderDto.getMachineId())) {
            return new ResultVo(ResultVo.CODE_OK, "支付成功");
        }
        BarrierDto machineDto = new BarrierDto();
        machineDto.setMachineId(tempCarPayOrderDto.getMachineId());
        List<BarrierDto> machineDtos = barrierV1InnerServiceSMOImpl.queryBarriers(machineDto);
        Assert.listOnlyOne(machineDtos, "设备不存在");
        machineDto = machineDtos.get(0);

        String manualTriggerOrOpenDoor = CommonCache.getValue("manualTriggerOrOpenDoor");
        if ("MANUAL_TRIGGER".equals(manualTriggerOrOpenDoor)) {
            //门口二维码支付
            CarMachineProcessFactory.getCarImpl(machineDtos.get(0).getHmId()).manualTrigger(machineDtos.get(0));

            return new ResultVo(ResultVo.CODE_OK, "支付成功");
        }

        // 校验是否有车辆 在门口
        if (!carHasOpenError(carInoutDtos.get(0).getCarNum(), carInoutDtos.get(0).getPaId(), carInoutDtos.get(0).getUnlicense())) {
            return new ResultVo(ResultVo.CODE_OK, "支付成功");
        }


        JSONObject param = new JSONObject();
        param.put("carNum", carInoutDtos.get(0).getCarNum());
        param.put("payCharge", tempCarPayOrderDto.getPayCharge());
        param.put("amount", tempCarPayOrderDto.getAmount());
        param.put("payType", tempCarPayOrderDto.getPayType());
        uploadcarout(machineDto, param,"岗亭口扫码");
        ResultParkingAreaTextDto parkingAreaTextDto
                = new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_OUT_SUCCESS, carInoutDtos.get(0).getCarNum(),
                "一路平安", "", "", carInoutDtos.get(0).getCarNum() + ",一路平安", carInoutDtos.get(0).getCarNum());
        parkingAreaTextDto.setCarNum(carInoutDtos.get(0).getCarNum());
        openDoor(machineDto, parkingAreaTextDto, new JSONObject());


        return new ResultVo(ResultVo.CODE_OK, "支付成功");
    }

    private void dealParkingCouponCar(TempCarPayOrderDto tempCarPayOrderDto) {

        if (tempCarPayOrderDto.getPccIds() == null || tempCarPayOrderDto.getPccIds().length < 1) {
            return;
        }

        ParkingCouponCarPo tmpParkingCouponCarPo = null;
        for (String extPccId : tempCarPayOrderDto.getPccIds()) {
            tmpParkingCouponCarPo = new ParkingCouponCarPo();
            tmpParkingCouponCarPo.setPccId(extPccId);
            tmpParkingCouponCarPo.setState(ParkingCouponCarDto.STATE_F);
            tmpParkingCouponCarPo.setRemark("手机端支付核销");
            parkingCouponCarV1InnerServiceSMOImpl.updateParkingCouponCar(tmpParkingCouponCarPo);
        }
    }

    private boolean carHasOpenError(String carNum, String paId, String unlicense) {

        //无牌车直接 出场
        if ("T".equals(unlicense)) {
            return true;
        }

        int openDoorTime = 5 * 60;
        String openDoorTimeStr = CommonCache.getValue("CAR_OPEN_DOOR_TIME");

        if (StringUtil.isNumber(openDoorTimeStr)) {
            openDoorTime = Integer.parseInt(openDoorTimeStr);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, -1 * openDoorTime);

        CarInoutDto tmpCarInoutDto = new CarInoutDto();
        tmpCarInoutDto.setCarNum(carNum);
        tmpCarInoutDto.setPaId(paId);
        tmpCarInoutDto.setOpenTime(DateUtil.getFormatTimeString(calendar.getTime(), DateUtil.DATE_FORMATE_STRING_A));
        List<CarInoutDto> carInoutDtos = carInoutV1InnerServiceSMOImpl.hasOpenDoorError(tmpCarInoutDto);
        if (carInoutDtos == null || carInoutDtos.size() < 1) {
            return false;
        }

        return true;
    }


}
