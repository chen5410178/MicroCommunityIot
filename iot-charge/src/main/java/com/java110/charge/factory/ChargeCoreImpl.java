package com.java110.charge.factory;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.coupon.ParkingCouponCarDto;
import com.java110.bean.dto.coupon.ParkingCouponShopDto;
import com.java110.bean.po.coupon.ParkingCouponCarPo;
import com.java110.bean.po.coupon.ParkingCouponShopPo;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.*;
import com.java110.dto.account.AccountDto;
import com.java110.dto.chargeMachine.*;
import com.java110.dto.chargeMonthOrder.ChargeMonthOrderDto;
import com.java110.dto.chargeRulePrice.ChargeRulePriceDto;
import com.java110.dto.onlinePay.OnlinePayDto;
import com.java110.dto.parkingCouponCharge.ParkingCouponChargeDto;
import com.java110.dto.userAttr.UserAttrDto;
import com.java110.intf.acct.IAccountInnerServiceSMO;
import com.java110.intf.acct.IOnlinePayRefundV1InnerServiceSMO;
import com.java110.intf.acct.IOnlinePayV1InnerServiceSMO;
import com.java110.intf.acct.IReturnMoneyV1InnerServiceSMO;
import com.java110.intf.car.IParkingCouponCarV1InnerServiceSMO;
import com.java110.intf.car.IParkingCouponChargeV1InnerServiceSMO;
import com.java110.intf.car.IParkingCouponShopV1InnerServiceSMO;
import com.java110.intf.car.IParkingCouponV1InnerServiceSMO;
import com.java110.intf.charge.*;
import com.java110.intf.user.IStoreShopV1InnerServiceSMO;
import com.java110.intf.user.IUserAttrV1InnerServiceSMO;
import com.java110.po.accountDetail.AccountDetailPo;
import com.java110.po.chargeMachineOrder.ChargeMachineOrderPo;
import com.java110.po.chargeMachineOrderAcct.ChargeMachineOrderAcctPo;
import com.java110.po.chargeMachineOrderCoupon.ChargeMachineOrderCouponPo;
import com.java110.po.chargeMachinePort.ChargeMachinePortPo;
import com.java110.po.onlinePay.OnlinePayPo;
import com.java110.po.onlinePay.OnlinePayRefundPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * 充电核心类
 */
@Service
public class ChargeCoreImpl implements IChargeCore {

    @Autowired
    private IAccountInnerServiceSMO accountInnerServiceSMOImpl;

    @Autowired
    private IChargeMachineFactoryV1InnerServiceSMO chargeMachineFactoryV1InnerServiceSMOImpl;

    @Autowired
    private IChargeMachineOrderV1InnerServiceSMO chargeMachineOrderV1InnerServiceSMOImpl;

    @Autowired
    private IChargeMachineV1InnerServiceSMO chargeMachineV1InnerServiceSMOImpl;

    @Autowired
    private IChargeMachinePortV1InnerServiceSMO chargeMachinePortV1InnerServiceSMOImpl;


    @Autowired
    private IChargeMachineOrderAcctV1InnerServiceSMO chargeMachineOrderAcctV1InnerServiceSMOImpl;

    @Autowired
    private IChargeRuleFeeV1InnerServiceSMO chargeRuleFeeV1InnerServiceSMOImpl;

    @Autowired
    private IChargeMachineOrderCouponV1InnerServiceSMO chargeMachineOrderCouponV1InnerServiceSMOImpl;

    @Autowired
    private IChargeMonthOrderV1InnerServiceSMO chargeMonthOrderV1InnerServiceSMOImpl;

    @Autowired
    private IChargeRulePriceV1InnerServiceSMO chargeRulePriceV1InnerServiceSMO;

    @Autowired
    private IUserAttrV1InnerServiceSMO userAttrV1InnerServiceSMOImpl;

    @Autowired
    private IParkingCouponChargeV1InnerServiceSMO parkingCouponChargeV1InnerServiceSMOImpl;

    @Autowired
    private IParkingCouponCarV1InnerServiceSMO parkingCouponCarV1InnerServiceSMOImpl;

    @Autowired
    private IParkingCouponV1InnerServiceSMO parkingCouponV1InnerServiceSMOImpl;

    @Autowired
    private IParkingCouponShopV1InnerServiceSMO parkingCouponShopV1InnerServiceSMOImpl;
    @Autowired
    private IStoreShopV1InnerServiceSMO shopInnerServiceSMOImpl;

    @Autowired
    private IOnlinePayV1InnerServiceSMO onlinePayV1InnerServiceSMOImpl;

    @Autowired
    private IReturnMoneyV1InnerServiceSMO returnMoneyV1InnerServiceSMOImpl;

    @Autowired
    private IOnlinePayRefundV1InnerServiceSMO onlinePayRefundV1InnerServiceSMOImpl;

    @Override
    public ResultVo startCharge(ChargeMachineDto chargeMachineDto, ChargeMachinePortDto chargeMachinePortDto, String chargeType, double duration, String orderId) {

        ChargeMachineFactoryDto chargeMachineFactoryDto = new ChargeMachineFactoryDto();
        chargeMachineFactoryDto.setFactoryId(chargeMachineDto.getImplBean());
        List<ChargeMachineFactoryDto> chargeMachineFactoryDtos = chargeMachineFactoryV1InnerServiceSMOImpl.queryChargeMachineFactorys(chargeMachineFactoryDto);

        Assert.listOnlyOne(chargeMachineFactoryDtos, "充电桩厂家不存在");

        IChargeFactoryAdapt chargeFactoryAdapt = ApplicationContextFactory.getBean(chargeMachineFactoryDtos.get(0).getBeanImpl(), IChargeFactoryAdapt.class);
        if (chargeFactoryAdapt == null) {
            throw new CmdException("厂家接口未实现");
        }

        chargeMachinePortDto = chargeFactoryAdapt.getChargePortState(chargeMachineDto, chargeMachinePortDto);

        if (!ChargeMachinePortDto.STATE_FREE.equals(chargeMachinePortDto.getState())) {
            throw new IllegalArgumentException("充电插槽不是空闲状态");
        }

        return chargeFactoryAdapt.startCharge(chargeMachineDto, chargeMachinePortDto, chargeType, duration, orderId);
    }

    @Override
    public ResultVo stopCharge(ChargeMachineDto chargeMachineDto, ChargeMachinePortDto chargeMachinePortDto) {
        ChargeMachineFactoryDto chargeMachineFactoryDto = new ChargeMachineFactoryDto();
        chargeMachineFactoryDto.setFactoryId(chargeMachineDto.getImplBean());
        List<ChargeMachineFactoryDto> chargeMachineFactoryDtos = chargeMachineFactoryV1InnerServiceSMOImpl.queryChargeMachineFactorys(chargeMachineFactoryDto);

        Assert.listOnlyOne(chargeMachineFactoryDtos, "充电桩厂家不存在");

        IChargeFactoryAdapt chargeFactoryAdapt = ApplicationContextFactory.getBean(chargeMachineFactoryDtos.get(0).getBeanImpl(), IChargeFactoryAdapt.class);
        if (chargeFactoryAdapt == null) {
            throw new CmdException("厂家接口未实现");
        }

        ResultVo resultVo = chargeFactoryAdapt.stopCharge(chargeMachineDto, chargeMachinePortDto);
        if (resultVo.getCode() != ResultVo.CODE_OK) {
            return resultVo;
        }


        return resultVo;
    }

    /**
     * 汽车订单退款
     *
     * @param chargeMachineDto
     */
    private void returnCarOrderMoney(ChargeMachineDto chargeMachineDto,
                                     String remark,
                                     String amount,
                                     String energy,
                                     String feeAmount,
                                     String serviceAmount,
                                     List<ChargeMachineOrderDto> chargeMachineOrderDtos) {
        ChargeRulePriceDto chargeRulePriceDto = new ChargeRulePriceDto();
        chargeRulePriceDto.setRuleId(chargeMachineDto.getRuleId());
        chargeRulePriceDto.setCommunityId(chargeMachineDto.getCommunityId());
        List<ChargeRulePriceDto> chargeRulePriceDtos = chargeRulePriceV1InnerServiceSMO.queryChargeRulePrices(chargeRulePriceDto);
        if (ListUtil.isNull(chargeRulePriceDtos)) {
            throw new CmdException("未设置充值收费标准，请联系管理员！");
        }
        BigDecimal usedHoursDec = new BigDecimal(amount);

        BigDecimal returnMoneyDec = new BigDecimal(Double.parseDouble(chargeMachineOrderDtos.get(0).getAmount())).subtract(usedHoursDec);
        double returnMoney = returnMoneyDec.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

        ChargeMachineOrderPo chargeMachineOrderPo = new ChargeMachineOrderPo();
        chargeMachineOrderPo.setOrderId(chargeMachineOrderDtos.get(0).getOrderId());
        chargeMachineOrderPo.setRemark(remark);
        chargeMachineOrderPo.setState(ChargeMachineOrderDto.STATE_FINISH);
        chargeMachineOrderPo.setAmount(amount);
        chargeMachineOrderPo.setEndTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        chargeMachineOrderPo.setCommunityId(chargeMachineOrderDtos.get(0).getCommunityId());
        chargeMachineOrderPo.setDurationPrice(feeAmount);
        chargeMachineOrderPo.setServicePrice(serviceAmount);
        chargeMachineOrderPo.setEnergy(energy);

        int flag = chargeMachineOrderV1InnerServiceSMOImpl.updateChargeMachineOrder(chargeMachineOrderPo);
        if (flag < 1) {
            throw new IllegalArgumentException("修改订单失败");
        }

        AccountDto accountDto = new AccountDto();
        accountDto.setAcctId(chargeMachineOrderDtos.get(0).getAcctDetailId());
        List<AccountDto> accountDtos = accountInnerServiceSMOImpl.queryAccounts(accountDto);
        if (ListUtil.isNull(accountDtos)) {
            return;
        }

        AccountDetailPo accountDetailPo = new AccountDetailPo();
        accountDetailPo.setAcctId(accountDtos.get(0).getAcctId());
        accountDetailPo.setObjId(accountDtos.get(0).getObjId());
        accountDetailPo.setObjType(accountDtos.get(0).getObjType());
        accountDetailPo.setDetailId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_detailId));
        if (returnMoney < 0) {
            accountDetailPo.setAmount((-1 * returnMoney) + "");
            accountDetailPo.setRemark("充电扣款金额-" + chargeMachineOrderDtos.get(0).getOrderId());
            accountInnerServiceSMOImpl.withholdAccount(accountDetailPo);
        } else {
            //直接微信退回即可
            try {
                refundAmount(accountDtos.get(0).getAcctId(), returnMoney, chargeRulePriceDtos.get(0).getCommunityId());
            }catch (Exception e){
                accountDetailPo.setAmount(returnMoney + "");
                accountDetailPo.setRemark("充电退回金额-" + chargeMachineOrderDtos.get(0).getOrderId());
                accountInnerServiceSMOImpl.prestoreAccount(accountDetailPo);
            }
        }

        //充电表中加入退款金额
        ChargeMachineOrderAcctPo chargeMachineOrderAcctPo = new ChargeMachineOrderAcctPo();
        chargeMachineOrderAcctPo.setAcctDetailId(accountDetailPo.getDetailId());
        chargeMachineOrderAcctPo.setAmount((-1 * returnMoney) + "");

        chargeMachineOrderAcctPo.setCmoaId(GenerateCodeFactory.getGeneratorId("11"));
        chargeMachineOrderAcctPo.setOrderId(chargeMachineOrderDtos.get(0).getOrderId());
        chargeMachineOrderAcctPo.setAcctId(accountDtos.get(0).getAcctId());
        chargeMachineOrderAcctPo.setStartTime(chargeMachineOrderDtos.get(0).getStartTime());
        chargeMachineOrderAcctPo.setEnergy(energy);
        chargeMachineOrderAcctPo.setEndTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        if (returnMoney < 0) {
            chargeMachineOrderAcctPo.setRemark("账户扣款-" + remark);
        } else {
            chargeMachineOrderAcctPo.setRemark("账户退款-" + remark);
        }
        chargeMachineOrderAcctPo.setCommunityId(chargeMachineOrderDtos.get(0).getCommunityId());
        //chargeMachineOrderAcctPo.setEnergy("0");
        chargeMachineOrderAcctPo.setDurationPrice("1");
        chargeMachineOrderAcctV1InnerServiceSMOImpl.saveChargeMachineOrderAcct(chargeMachineOrderAcctPo);
    }

    private void refundAmount(String acctId, double returnMoney,String communityId) {
        // 1. 根据acctId查询Online_pay表中的记录（busiId = acctId）
        OnlinePayDto onlinePayDto = new OnlinePayDto();
        onlinePayDto.setBusiId(acctId);
        onlinePayDto.setPage(1);
        onlinePayDto.setRow(100); // 查询足够多的记录

        List<OnlinePayDto> onlinePayDtos = onlinePayV1InnerServiceSMOImpl.queryOnlinePays(onlinePayDto);

        if (onlinePayDtos == null || onlinePayDtos.isEmpty()) {
            throw new CmdException("未找到对应的支付记录");
        }
        String refundId = GenerateCodeFactory.getGeneratorId("10");
        OnlinePayRefundPo refundPo = new OnlinePayRefundPo();
        refundPo.setRefundId(refundId);
        refundPo.setPayId(onlinePayDtos.get(0).getPayId());
        refundPo.setRefundFee(new BigDecimal(returnMoney));
        refundPo.setBusiId(acctId);
        refundPo.setCommunityId(communityId);
        refundPo.setState(OnlinePayDto.STATE_WT); // P-处理中
        refundPo.setMessage("退款申请中");
        refundPo.setRefundUserId("-1");

        onlinePayRefundV1InnerServiceSMOImpl.saveOnlinePayRefund(refundPo);

        //退款
       ResultVo resultVo = returnMoneyV1InnerServiceSMOImpl.returnMoney(onlinePayDtos.get(0));
        if(resultVo.getCode() != ResultVo.CODE_OK){
            refundPo = new OnlinePayRefundPo();
            refundPo.setRefundId(refundId);
            refundPo.setCommunityId(communityId);
            refundPo.setState(OnlinePayDto.STATE_FT); // P-处理中
            refundPo.setMessage(resultVo.getMsg());
            onlinePayRefundV1InnerServiceSMOImpl.updateOnlinePayRefund(refundPo);
            throw new CmdException(resultVo.getMsg());
        }
        refundPo = new OnlinePayRefundPo();
        refundPo.setRefundId(refundId);
        refundPo.setCommunityId(communityId);
        refundPo.setState(OnlinePayDto.STATE_C); // P-处理中
        refundPo.setMessage(resultVo.getMsg());
        onlinePayRefundV1InnerServiceSMOImpl.updateOnlinePayRefund(refundPo);

        // 更新online_pay表中的剩余金额
        OnlinePayPo onlinePayPo = new OnlinePayPo();
        onlinePayPo.setPayId(onlinePayDtos.get(0).getPayId());
        onlinePayPo.setRemainingFee(onlinePayDtos.get(0).getRemainingFee().subtract(new BigDecimal(returnMoney)).toString());
        onlinePayV1InnerServiceSMOImpl.updateOnlinePay(onlinePayPo);
    }

    /**
     * 订单退款
     *
     * @param chargeMachineDto
     */
    private void returnOrderMoney(ChargeMachineDto chargeMachineDto,
                                  NotifyChargeOrderDto notifyChargeOrderDto,
                                  List<ChargeMachineOrderDto> chargeMachineOrderDtos) {

        String remark = notifyChargeOrderDto.getReason();
        String energy = notifyChargeOrderDto.getEnergy();

        Date startTime = DateUtil.getDateFromStringA(chargeMachineOrderDtos.get(0).getStartTime());

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -2); // 这里减掉两分钟，设备反应通知平台的时间

        double usedHours = Math.ceil((calendar.getTime().getTime() - startTime.getTime()) / (60 * 60 * 1000.00));

//        if (usedHours <= 0) {
//            usedHours = 0;
//        } else if ("1".equals(notifyChargeOrderDto.getEnergyFlag())) { // todo 说明传进来的是电量
//            BigDecimal powerDec = new BigDecimal(Double.parseDouble(energy)).divide(new BigDecimal(usedHours), 2, BigDecimal.ROUND_HALF_UP);
//            power = powerDec.doubleValue() + "";
//        }
        //适配器给功率，不要给电量 如果给电量的话 页面上显示的是电量 这样 导致 客户搞不清楚
//        else {
//            BigDecimal powerDec = new BigDecimal(Double.parseDouble(energy)).divide(new BigDecimal(usedHours), 2, BigDecimal.ROUND_HALF_UP);
//            power = powerDec.doubleValue() + "";
//        }

        // todo 优惠券抵扣
        JSONObject result = useCoupon(usedHours, chargeMachineOrderDtos);
        usedHours = result.getDoubleValue("usedHours");
        if (StringUtil.isEmpty(remark)) {
            remark = result.getString("remark");
        } else {
            remark = remark + ";" + result.getString("remark");
        }

        ChargeRuleFeeDto chargeRuleFeeDto = new ChargeRuleFeeDto();
        chargeRuleFeeDto.setRuleId(chargeMachineDto.getRuleId());
        chargeRuleFeeDto.setCommunityId(chargeMachineDto.getCommunityId());
        List<ChargeRuleFeeDto> chargeRuleFeeDtos = chargeRuleFeeV1InnerServiceSMOImpl.queryChargeRuleFees(chargeRuleFeeDto);

        if (ListUtil.isNull(chargeRuleFeeDtos)) {
            throw new CmdException("未找到收费规则");
        }

        // 电价
        BigDecimal durationPriceDec = new BigDecimal(Double.parseDouble(chargeRuleFeeDtos.get(0).getDurationPrice()));
        // 电量
        BigDecimal energyDec = new BigDecimal(energy);
        BigDecimal servicePriceDec = new BigDecimal(chargeRuleFeeDtos.get(0).getServicePrice());
        //充电时间
        BigDecimal usedHoursDec = new BigDecimal(usedHours);
        // 电费部分 电量 * 单价
        BigDecimal priceDec = energyDec.multiply(durationPriceDec).divide(new BigDecimal("1000"),2,BigDecimal.ROUND_HALF_UP);

        // 服务费部分
        servicePriceDec = servicePriceDec.multiply(usedHoursDec);
        // 总费用
        BigDecimal useMoneyDec = priceDec.add(servicePriceDec).setScale(2,BigDecimal.ROUND_HALF_UP);

        BigDecimal returnMoneyDec = new BigDecimal(Double.parseDouble(chargeMachineOrderDtos.get(0).getAmount())).subtract(useMoneyDec);

        double returnMoney = returnMoneyDec.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();


        ChargeMachineOrderPo chargeMachineOrderPo = new ChargeMachineOrderPo();
        chargeMachineOrderPo.setOrderId(chargeMachineOrderDtos.get(0).getOrderId());
        chargeMachineOrderPo.setRemark(remark);
        chargeMachineOrderPo.setState(ChargeMachineOrderDto.STATE_FINISH);
        chargeMachineOrderPo.setAmount(useMoneyDec.doubleValue() + "");
        chargeMachineOrderPo.setEndTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        chargeMachineOrderPo.setCommunityId(chargeMachineOrderDtos.get(0).getCommunityId());
        chargeMachineOrderPo.setDurationPrice(priceDec.doubleValue()+"");
        chargeMachineOrderPo.setServicePrice(servicePriceDec.doubleValue()+"");
        chargeMachineOrderPo.setEnergy(energy);

        int flag = chargeMachineOrderV1InnerServiceSMOImpl.updateChargeMachineOrder(chargeMachineOrderPo);
        if (flag < 1) {
            throw new IllegalArgumentException("修改订单失败");
        }


        AccountDto accountDto = new AccountDto();
        accountDto.setAcctId(chargeMachineOrderDtos.get(0).getAcctDetailId());
        List<AccountDto> accountDtos = accountInnerServiceSMOImpl.queryAccounts(accountDto);

        if (ListUtil.isNull(accountDtos)) {
            return;
        }


        AccountDetailPo accountDetailPo = new AccountDetailPo();
        accountDetailPo.setAcctId(accountDtos.get(0).getAcctId());
        accountDetailPo.setObjId(accountDtos.get(0).getObjId());
        accountDetailPo.setObjType(accountDtos.get(0).getObjType());
        accountDetailPo.setDetailId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_detailId));
        if (returnMoney < 0) {
            accountDetailPo.setAmount((-1 * returnMoney) + "");
            accountDetailPo.setRemark("充电扣款金额-" + chargeMachineOrderDtos.get(0).getOrderId());
            accountInnerServiceSMOImpl.withholdAccount(accountDetailPo);
        } else {
            accountDetailPo.setAmount(returnMoney + "");
            accountDetailPo.setRemark("充电退回金额-" + chargeMachineOrderDtos.get(0).getOrderId());
            accountInnerServiceSMOImpl.prestoreAccount(accountDetailPo);
        }


        //充电表中加入退款金额
        ChargeMachineOrderAcctPo chargeMachineOrderAcctPo = new ChargeMachineOrderAcctPo();
        chargeMachineOrderAcctPo.setAcctDetailId(accountDetailPo.getDetailId());
        chargeMachineOrderAcctPo.setAmount((-1 * returnMoney) + "");

        chargeMachineOrderAcctPo.setCmoaId(GenerateCodeFactory.getGeneratorId("11"));
        chargeMachineOrderAcctPo.setOrderId(chargeMachineOrderDtos.get(0).getOrderId());
        chargeMachineOrderAcctPo.setAcctId(accountDtos.get(0).getAcctId());
        chargeMachineOrderAcctPo.setStartTime(chargeMachineOrderDtos.get(0).getStartTime());
        chargeMachineOrderAcctPo.setEnergy(energy);
        chargeMachineOrderAcctPo.setEndTime(chargeMachineOrderDtos.get(0).getEndTime());
        if (returnMoney < 0) {
            chargeMachineOrderAcctPo.setRemark("账户扣款-" + remark);
        } else {
            chargeMachineOrderAcctPo.setRemark("账户退款-" + remark);
        }
        chargeMachineOrderAcctPo.setCommunityId(chargeMachineOrderDtos.get(0).getCommunityId());
        //chargeMachineOrderAcctPo.setEnergy("0");
        chargeMachineOrderAcctPo.setDurationPrice(useMoneyDec.doubleValue()+"");

        chargeMachineOrderAcctV1InnerServiceSMOImpl.saveChargeMachineOrderAcct(chargeMachineOrderAcctPo);
    }

    public boolean ifMonthCard(String personTel, String communityId) {

        ChargeMonthOrderDto chargeMonthOrderDto = new ChargeMonthOrderDto();
        chargeMonthOrderDto.setPersonTel(personTel);
        chargeMonthOrderDto.setCommunityId(communityId);
        chargeMonthOrderDto.setQueryTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        List<ChargeMonthOrderDto> chargeMonthOrderDtos = chargeMonthOrderV1InnerServiceSMOImpl.queryChargeMonthOrders(chargeMonthOrderDto);
        if (ListUtil.isNull(chargeMonthOrderDtos)) {
            return false;
        }

        //todo 今天是否又充过电
        ChargeMachineOrderDto chargeMachineOrderDto = new ChargeMachineOrderDto();
        chargeMachineOrderDto.setPersonTel(personTel);
        chargeMachineOrderDto.setCommunityId(communityId);
        //chargeMachineOrderDto.setQueryTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        chargeMachineOrderDto.setQueryStartTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_B));
        chargeMachineOrderDto.setQueryEndTime(DateUtil.getAddDayStringB(new Date(), 1));
        List<ChargeMachineOrderDto> chargeMachineOrderDtos = chargeMachineOrderV1InnerServiceSMOImpl.queryChargeMachineOrders(chargeMachineOrderDto);
        if (ListUtil.isNull(chargeMachineOrderDtos)) {
            return true;
        }


        Date startTime = DateUtil.getDateFromStringA(chargeMachineOrderDtos.get(0).getStartTime());
        Date endTime = DateUtil.getDateFromStringA(chargeMachineOrderDtos.get(0).getEndTime());


        double usedHours = (endTime.getTime() - startTime.getTime()) / (60 * 60 * 1000.00);

        if (usedHours < 1) {
            return true;
        }

        return false;
    }


    /**
     * 优惠券抵扣 小时
     *
     * @param usedHours
     * @param chargeMachineOrderDtos
     * @return {
     * usedHours:'',
     * remark:''
     * }
     */
    private JSONObject useCoupon(double usedHours, List<ChargeMachineOrderDto> chargeMachineOrderDtos) {
        double hours = 0;
        JSONObject useHoursInfo = new JSONObject();
        ChargeMachineOrderCouponDto chargeMachineOrderCouponDto = new ChargeMachineOrderCouponDto();
        chargeMachineOrderCouponDto.setOrderId(chargeMachineOrderDtos.get(0).getOrderId());
        chargeMachineOrderCouponDto.setCommunityId(chargeMachineOrderDtos.get(0).getCommunityId());
        chargeMachineOrderCouponDto.setState("W");
        List<ChargeMachineOrderCouponDto> chargeMachineOrderCouponDtos
                = chargeMachineOrderCouponV1InnerServiceSMOImpl.queryChargeMachineOrderCoupons(chargeMachineOrderCouponDto);
        if (chargeMachineOrderCouponDtos == null || chargeMachineOrderCouponDtos.size() < 1) {
            useHoursInfo.put("usedHours", usedHours);
            useHoursInfo.put("remark", "");
            return useHoursInfo;
        }
        String couponNames = "使用优惠券-";
        for (ChargeMachineOrderCouponDto tmpChargeMachineOrderCouponDto : chargeMachineOrderCouponDtos) {
            couponNames += ("名称：" + tmpChargeMachineOrderCouponDto.getCouponName() + "(" + tmpChargeMachineOrderCouponDto.getCouponId() + "),小时：" + tmpChargeMachineOrderCouponDto.getHours() + ";");

            hours += Double.parseDouble(tmpChargeMachineOrderCouponDto.getHours());
        }

        //将优惠券修改为已使用状态
        ChargeMachineOrderCouponPo chargeMachineOrderCouponPo = new ChargeMachineOrderCouponPo();
        chargeMachineOrderCouponPo.setOrderId(chargeMachineOrderDtos.get(0).getOrderId());
        chargeMachineOrderCouponPo.setCommunityId(chargeMachineOrderDtos.get(0).getCommunityId());
        chargeMachineOrderCouponPo.setState("C");
        chargeMachineOrderCouponV1InnerServiceSMOImpl.updateChargeMachineOrderCoupon(chargeMachineOrderCouponPo);

        BigDecimal useDec = new BigDecimal(usedHours).subtract(new BigDecimal(hours)).setScale(2, BigDecimal.ROUND_HALF_UP);
        usedHours = useDec.doubleValue();
        if (usedHours < 0) {
            useHoursInfo.put("usedHours", 0);
            useHoursInfo.put("remark", couponNames);
            return useHoursInfo;
        }
        useHoursInfo.put("usedHours", usedHours);
        useHoursInfo.put("remark", couponNames);
        return useHoursInfo;
    }

    @Override
    public ChargeMachinePortDto getChargePortState(ChargeMachineDto chargeMachineDto, ChargeMachinePortDto chargeMachinePortDto) {
        ChargeMachineFactoryDto chargeMachineFactoryDto = new ChargeMachineFactoryDto();
        chargeMachineFactoryDto.setFactoryId(chargeMachineDto.getImplBean());
        List<ChargeMachineFactoryDto> chargeMachineFactoryDtos = chargeMachineFactoryV1InnerServiceSMOImpl.queryChargeMachineFactorys(chargeMachineFactoryDto);

        Assert.listOnlyOne(chargeMachineFactoryDtos, "充电桩厂家不存在");

        IChargeFactoryAdapt chargeFactoryAdapt = ApplicationContextFactory.getBean(chargeMachineFactoryDtos.get(0).getBeanImpl(), IChargeFactoryAdapt.class);
        if (chargeFactoryAdapt == null) {
            throw new CmdException("厂家接口未实现");
        }

        return chargeFactoryAdapt.getChargePortState(chargeMachineDto, chargeMachinePortDto);
    }


    /**
     * 汽车开启充电失败
     *
     * @param notifyChargeOrderDto{ orderId,
     *                              machineCode,
     *                              portCode,
     *                              reason
     *                              }
     * @return
     */
    @Override
    public ResultVo startChargeFail(NotifyChargeOrderDto notifyChargeOrderDto) {

        ChargeMachineDto chargeMachineDto = new ChargeMachineDto();
        chargeMachineDto.setMachineCode(notifyChargeOrderDto.getMachineCode());
        List<ChargeMachineDto> chargeMachineDtos = chargeMachineV1InnerServiceSMOImpl.queryChargeMachines(chargeMachineDto);

        if (ListUtil.isNull(chargeMachineDtos)) {
            return new ResultVo(ResultVo.CODE_OK, "成功");
        }

        // todo 插槽是否空闲

        ChargeMachinePortDto chargeMachinePortDto = new ChargeMachinePortDto();
        chargeMachinePortDto.setMachineId(chargeMachineDtos.get(0).getMachineId());
        chargeMachinePortDto.setPortCode(notifyChargeOrderDto.getPortCode());
        List<ChargeMachinePortDto> chargeMachinePortDtos = chargeMachinePortV1InnerServiceSMOImpl.queryChargeMachinePorts(chargeMachinePortDto);
        if (ListUtil.isNull(chargeMachinePortDtos)) {
            throw new CmdException("插座不存在");
        }
        // todo 插座修改为空闲
        ChargeMachinePortPo chargeMachinePortPo = new ChargeMachinePortPo();
        chargeMachinePortPo.setPortId(chargeMachinePortDtos.get(0).getPortId());
        chargeMachinePortPo.setState(ChargeMachinePortDto.STATE_FREE);
        chargeMachinePortV1InnerServiceSMOImpl.updateChargeMachinePort(chargeMachinePortPo);

        ChargeMachineOrderDto chargeMachineOrderDto = new ChargeMachineOrderDto();
        chargeMachineOrderDto.setMachineId(chargeMachineDtos.get(0).getMachineId());
        chargeMachineOrderDto.setPortId(chargeMachinePortDtos.get(0).getPortId());
        chargeMachineOrderDto.setOrderId(notifyChargeOrderDto.getOrderId());
        List<ChargeMachineOrderDto> chargeMachineOrderDtos = chargeMachineOrderV1InnerServiceSMOImpl.queryChargeMachineOrders(chargeMachineOrderDto);

        if (ListUtil.isNull(chargeMachineOrderDtos)) {
            return new ResultVo(ResultVo.CODE_OK, "成功");
        }

        ChargeMachineOrderPo chargeMachineOrderPo = new ChargeMachineOrderPo();
        chargeMachineOrderPo.setOrderId(chargeMachineOrderDtos.get(0).getOrderId());
        chargeMachineOrderPo.setState(ChargeMachineOrderDto.STATE_FAIL);
        chargeMachineOrderPo.setRemark(notifyChargeOrderDto.getReason());
        chargeMachineOrderPo.setCommunityId(chargeMachineOrderDtos.get(0).getCommunityId());
        int flag = chargeMachineOrderV1InnerServiceSMOImpl.updateChargeMachineOrder(chargeMachineOrderPo);
        if (flag < 1) {
            throw new IllegalArgumentException("修改订单失败");
        }

        //todo 查询扣款金额
        ChargeMachineOrderAcctDto chargeMachineOrderAcctDto = new ChargeMachineOrderAcctDto();
        chargeMachineOrderAcctDto.setOrderId(chargeMachineOrderDtos.get(0).getOrderId());
        chargeMachineOrderAcctDto.setCommunityId(chargeMachineOrderDtos.get(0).getCommunityId());
        List<ChargeMachineOrderAcctDto> chargeMachineOrderAcctDtos =
                chargeMachineOrderAcctV1InnerServiceSMOImpl.queryChargeMachineOrderAccts(chargeMachineOrderAcctDto);

        if (ListUtil.isNull(chargeMachineOrderAcctDtos)) {
            return new ResultVo(ResultVo.CODE_OK, "成功");
        }

        AccountDto accountDto = new AccountDto();
        accountDto.setAcctId(chargeMachineOrderDtos.get(0).getAcctDetailId());
        List<AccountDto> accountDtos = accountInnerServiceSMOImpl.queryAccounts(accountDto);
        if (ListUtil.isNull(accountDtos)) {
            return new ResultVo(ResultVo.CODE_OK, "成功");
        }

        double returnMoney = Double.parseDouble(chargeMachineOrderAcctDtos.get(0).getAmount());


        AccountDetailPo accountDetailPo = new AccountDetailPo();
        accountDetailPo.setAcctId(accountDtos.get(0).getAcctId());
        accountDetailPo.setObjId(accountDtos.get(0).getObjId());
        accountDetailPo.setObjType(accountDtos.get(0).getObjType());
        accountDetailPo.setDetailId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_detailId));

        accountDetailPo.setAmount(returnMoney + "");
        accountDetailPo.setRemark("由于" + notifyChargeOrderDto.getReason() + "，充电退回金额-" + chargeMachineOrderDtos.get(0).getOrderId());
        accountInnerServiceSMOImpl.prestoreAccount(accountDetailPo);


        //充电表中加入退款金额
        ChargeMachineOrderAcctPo chargeMachineOrderAcctPo = new ChargeMachineOrderAcctPo();
        chargeMachineOrderAcctPo.setAcctDetailId(accountDetailPo.getDetailId());
        chargeMachineOrderAcctPo.setAmount((-1 * returnMoney) + "");

        chargeMachineOrderAcctPo.setCmoaId(GenerateCodeFactory.getGeneratorId("11"));
        chargeMachineOrderAcctPo.setOrderId(chargeMachineOrderDtos.get(0).getOrderId());
        chargeMachineOrderAcctPo.setAcctId(accountDtos.get(0).getAcctId());
        chargeMachineOrderAcctPo.setStartTime(chargeMachineOrderDtos.get(0).getStartTime());
        chargeMachineOrderAcctPo.setEnergy("0");
        chargeMachineOrderAcctPo.setEndTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));

        chargeMachineOrderAcctPo.setRemark("账户退款-" + notifyChargeOrderDto.getReason());

        chargeMachineOrderAcctPo.setCommunityId(chargeMachineOrderDtos.get(0).getCommunityId());
        //chargeMachineOrderAcctPo.setEnergy("0");
        chargeMachineOrderAcctPo.setDurationPrice("1");
        chargeMachineOrderAcctV1InnerServiceSMOImpl.saveChargeMachineOrderAcct(chargeMachineOrderAcctPo);
        return new ResultVo(ResultVo.CODE_OK, "成功");
    }

    /**
     * 汽车开启充电成功
     *
     * @param notifyChargeOrderDto{ orderId,
     *                              machineCode,
     *                              portCode,
     *                              reason
     *                              }
     * @return
     */
    @Override
    public ResultVo startChargeSuccess(NotifyChargeOrderDto notifyChargeOrderDto) {
        ChargeMachineDto chargeMachineDto = new ChargeMachineDto();
        chargeMachineDto.setMachineCode(notifyChargeOrderDto.getMachineCode());
        List<ChargeMachineDto> chargeMachineDtos = chargeMachineV1InnerServiceSMOImpl.queryChargeMachines(chargeMachineDto);

        if (ListUtil.isNull(chargeMachineDtos)) {
            return new ResultVo(ResultVo.CODE_OK, "成功");
        }

        // todo 插槽
        ChargeMachinePortDto chargeMachinePortDto = new ChargeMachinePortDto();
        chargeMachinePortDto.setMachineId(chargeMachineDtos.get(0).getMachineId());
        chargeMachinePortDto.setPortCode(notifyChargeOrderDto.getPortCode());
        List<ChargeMachinePortDto> chargeMachinePortDtos = chargeMachinePortV1InnerServiceSMOImpl.queryChargeMachinePorts(chargeMachinePortDto);
        if (ListUtil.isNull(chargeMachinePortDtos)) {
            throw new CmdException("插座不存在");
        }
        // todo 插座修改为工作中
        ChargeMachinePortPo chargeMachinePortPo = new ChargeMachinePortPo();
        chargeMachinePortPo.setPortId(chargeMachinePortDtos.get(0).getPortId());
        chargeMachinePortPo.setState(ChargeMachinePortDto.STATE_WORKING);
        chargeMachinePortV1InnerServiceSMOImpl.updateChargeMachinePort(chargeMachinePortPo);

        ChargeMachineOrderDto chargeMachineOrderDto = new ChargeMachineOrderDto();
        chargeMachineOrderDto.setMachineId(chargeMachineDtos.get(0).getMachineId());
        chargeMachineOrderDto.setPortId(chargeMachinePortDtos.get(0).getPortId());
        chargeMachineOrderDto.setOrderId(notifyChargeOrderDto.getOrderId());
        List<ChargeMachineOrderDto> chargeMachineOrderDtos = chargeMachineOrderV1InnerServiceSMOImpl.queryChargeMachineOrders(chargeMachineOrderDto);

        if (ListUtil.isNull(chargeMachineOrderDtos)) {
            return new ResultVo(ResultVo.CODE_OK, "成功");
        }
        // todo 将订单修改为开始充电中
        ChargeMachineOrderPo chargeMachineOrderPo = new ChargeMachineOrderPo();
        chargeMachineOrderPo.setOrderId(chargeMachineOrderDtos.get(0).getOrderId());
        chargeMachineOrderPo.setState(ChargeMachineOrderDto.STATE_DOING);
        chargeMachineOrderPo.setRemark(notifyChargeOrderDto.getReason());
        chargeMachineOrderPo.setCommunityId(chargeMachineOrderDtos.get(0).getCommunityId());
        int flag = chargeMachineOrderV1InnerServiceSMOImpl.updateChargeMachineOrder(chargeMachineOrderPo);
        if (flag < 1) {
            throw new IllegalArgumentException("修改订单失败");
        }

        return new ResultVo(ResultVo.CODE_OK, "成功");

    }

    /**
     * 完成充电
     *
     * @param notifyChargeOrderDto
     * @return
     */
    @Override
    public ResultVo finishCharge(NotifyChargeOrderDto notifyChargeOrderDto) {


        ChargeMachineDto chargeMachineDto = new ChargeMachineDto();
        chargeMachineDto.setMachineCode(notifyChargeOrderDto.getMachineCode());
        List<ChargeMachineDto> chargeMachineDtos = chargeMachineV1InnerServiceSMOImpl.queryChargeMachines(chargeMachineDto);

        if (ListUtil.isNull(chargeMachineDtos)) {
            return new ResultVo(ResultVo.CODE_OK, "成功");
        }

        // todo 插槽是否空闲

        ChargeMachinePortDto chargeMachinePortDto = new ChargeMachinePortDto();
        chargeMachinePortDto.setMachineId(chargeMachineDtos.get(0).getMachineId());
        chargeMachinePortDto.setPortCode(notifyChargeOrderDto.getPortCode());
        //chargeMachinePortDto.setState(ChargeMachinePortDto.STATE_WORKING);
        List<ChargeMachinePortDto> chargeMachinePortDtos = chargeMachinePortV1InnerServiceSMOImpl.queryChargeMachinePorts(chargeMachinePortDto);
        //Assert.listOnlyOne(chargeMachinePortDtos, "插槽空闲");
        if (ListUtil.isNull(chargeMachinePortDtos)) {
            throw new CmdException("插座不存在");
        }

        ChargeMachinePortPo chargeMachinePortPo = new ChargeMachinePortPo();
        chargeMachinePortPo.setPortId(chargeMachinePortDtos.get(0).getPortId());
        chargeMachinePortPo.setState(ChargeMachinePortDto.STATE_FREE);
        chargeMachinePortV1InnerServiceSMOImpl.updateChargeMachinePort(chargeMachinePortPo);

        ChargeMachineOrderDto chargeMachineOrderDto = new ChargeMachineOrderDto();
        chargeMachineOrderDto.setMachineId(chargeMachineDtos.get(0).getMachineId());
        chargeMachineOrderDto.setPortId(chargeMachinePortDtos.get(0).getPortId());
        chargeMachineOrderDto.setOrderId(notifyChargeOrderDto.getOrderId());
        List<ChargeMachineOrderDto> chargeMachineOrderDtos = chargeMachineOrderV1InnerServiceSMOImpl.queryChargeMachineOrders(chargeMachineOrderDto);

        if (ListUtil.isNull(chargeMachineOrderDtos)) {
            return new ResultVo(ResultVo.CODE_OK, "成功");
        }


        ChargeMachineOrderPo chargeMachineOrderPo = new ChargeMachineOrderPo();
        chargeMachineOrderPo.setOrderId(chargeMachineOrderDtos.get(0).getOrderId());
        chargeMachineOrderPo.setState(ChargeMachineOrderDto.STATE_FINISHING);
        chargeMachineOrderPo.setCommunityId(chargeMachineOrderDtos.get(0).getCommunityId());
        int flag = chargeMachineOrderV1InnerServiceSMOImpl.updateChargeMachineOrder(chargeMachineOrderPo);
        if (flag < 1) {
            throw new IllegalArgumentException("修改订单失败");
        }

        if (ChargeMachineDto.CHARGE_TYPE_BIKE.equals(chargeMachineDtos.get(0).getChargeType())) {
            //todo 月卡直接修改状态
            if (ifMonthCard(chargeMachineOrderDtos.get(0).getPersonTel(), chargeMachinePortDtos.get(0).getCommunityId())) {
                finishMonthCardChargeOrder(notifyChargeOrderDto, chargeMachineOrderDtos);
                return new ResultVo(ResultVo.CODE_OK, "成功");
            }
            returnOrderMoney(chargeMachineDtos.get(0), notifyChargeOrderDto, chargeMachineOrderDtos);
        } else if (ChargeMachineDto.CHARGE_TYPE_CAR.equals(chargeMachineDtos.get(0).getChargeType())) {
            returnCarOrderMoney(chargeMachineDtos.get(0), notifyChargeOrderDto.getReason(), notifyChargeOrderDto.getAmount(),
                    notifyChargeOrderDto.getEnergy(),
                    notifyChargeOrderDto.getFeeAmount(),
                    notifyChargeOrderDto.getServiceAmount(),
                    chargeMachineOrderDtos);

            // todo 给车辆送停车券
            giftCouponToChargeCar(chargeMachineDtos.get(0), chargeMachineOrderDtos.get(0));
        }
        return new ResultVo(ResultVo.CODE_OK, "成功");
    }


    /**
     * 结束月卡 充电订单
     *
     * @param notifyChargeOrderDto
     * @param chargeMachineOrderDtos
     */
    private void finishMonthCardChargeOrder(NotifyChargeOrderDto notifyChargeOrderDto, List<ChargeMachineOrderDto> chargeMachineOrderDtos) {
        ChargeMachineOrderPo chargeMachineOrderPo = new ChargeMachineOrderPo();
        chargeMachineOrderPo.setOrderId(chargeMachineOrderDtos.get(0).getOrderId());
        chargeMachineOrderPo.setRemark(notifyChargeOrderDto.getReason());
        chargeMachineOrderPo.setState(ChargeMachineOrderDto.STATE_FINISH);
        chargeMachineOrderPo.setAmount("0");
        chargeMachineOrderPo.setEndTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        chargeMachineOrderPo.setCommunityId(chargeMachineOrderDtos.get(0).getCommunityId());
        //chargeMachineOrderPo.setDurationPrice(durationPrice);
        chargeMachineOrderPo.setEnergy(notifyChargeOrderDto.getEnergy());
        int flag = chargeMachineOrderV1InnerServiceSMOImpl.updateChargeMachineOrder(chargeMachineOrderPo);
        if (flag < 1) {
            throw new IllegalArgumentException("修改订单失败");
        }
    }

    @Override
    public ResultVo workHeartbeat(NotifyChargeOrderDto notifyChargeOrderDto) {

        ChargeMachineDto chargeMachineDto = new ChargeMachineDto();
        chargeMachineDto.setMachineCode(notifyChargeOrderDto.getMachineCode());
        List<ChargeMachineDto> chargeMachineDtos = chargeMachineV1InnerServiceSMOImpl.queryChargeMachines(chargeMachineDto);

        if (chargeMachineDtos == null || chargeMachineDtos.size() < 1) {
            return new ResultVo(ResultVo.CODE_OK, "成功");
        }


        ChargeMachineFactoryDto chargeMachineFactoryDto = new ChargeMachineFactoryDto();
        chargeMachineFactoryDto.setFactoryId(chargeMachineDtos.get(0).getImplBean());
        List<ChargeMachineFactoryDto> chargeMachineFactoryDtos = chargeMachineFactoryV1InnerServiceSMOImpl.queryChargeMachineFactorys(chargeMachineFactoryDto);

        Assert.listOnlyOne(chargeMachineFactoryDtos, "充电桩厂家不存在");

        IChargeFactoryAdapt chargeFactoryAdapt = ApplicationContextFactory.getBean(chargeMachineFactoryDtos.get(0).getBeanImpl(), IChargeFactoryAdapt.class);
        if (chargeFactoryAdapt == null) {
            throw new CmdException("厂家接口未实现");
        }

        chargeFactoryAdapt.workHeartbeat(chargeMachineDtos.get(0), notifyChargeOrderDto.getBodyParam());

        return new ResultVo(ResultVo.CODE_OK, "成功");

    }

    @Override
    public void queryChargeMachineState(List<ChargeMachineDto> chargeMachineDtos) {

        for (ChargeMachineDto chargeMachineDto : chargeMachineDtos) {
            try {
                ChargeMachineFactoryDto chargeMachineFactoryDto = new ChargeMachineFactoryDto();
                chargeMachineFactoryDto.setFactoryId(chargeMachineDto.getImplBean());
                List<ChargeMachineFactoryDto> chargeMachineFactoryDtos = chargeMachineFactoryV1InnerServiceSMOImpl.queryChargeMachineFactorys(chargeMachineFactoryDto);

                Assert.listOnlyOne(chargeMachineFactoryDtos, "充电桩厂家不存在");

                IChargeFactoryAdapt chargeFactoryAdapt = ApplicationContextFactory.getBean(chargeMachineFactoryDtos.get(0).getBeanImpl(), IChargeFactoryAdapt.class);
                if (chargeFactoryAdapt == null) {
                    throw new CmdException("厂家接口未实现");
                }
                chargeFactoryAdapt.queryChargeMachineState(chargeMachineDto);
            } catch (Exception e) {
                e.printStackTrace();
                chargeMachineDto.setState(ChargeMachineDto.STATE_OFFLINE);
                chargeMachineDto.setStateName("离线");
            }
        }
    }

    private void giftCouponToChargeCar(ChargeMachineDto chargeMachineDto, ChargeMachineOrderDto chargeMachineOrderDto) {
        String userId = chargeMachineOrderDto.getPersonId();
        if (StringUtil.isEmpty(userId)) {
            return;
        }
        UserAttrDto userAttrDto = new UserAttrDto();
        userAttrDto.setUserId(userId);
        userAttrDto.setSpecCd(UserAttrDto.CHARGE_CAR);
        List<UserAttrDto> userAttrDtos = userAttrV1InnerServiceSMOImpl.queryUserAttrs(userAttrDto);
        if (ListUtil.isNull(userAttrDtos)) {
            return;
        }
        String carNum = userAttrDtos.get(0).getValue();

        ParkingCouponChargeDto parkingCouponChargeDto = new ParkingCouponChargeDto();
        parkingCouponChargeDto.setMachineId(chargeMachineDto.getMachineId());
        parkingCouponChargeDto.setCommunityId(chargeMachineDto.getCommunityId());
        List<ParkingCouponChargeDto> parkingCouponChargeDtos = parkingCouponChargeV1InnerServiceSMOImpl.queryParkingCouponCharges(parkingCouponChargeDto);
        if (ListUtil.isNull(parkingCouponChargeDtos)) {
            return;
        }

        ParkingCouponShopDto parkingCouponShopDto = new ParkingCouponShopDto();
        parkingCouponShopDto.setCouponShopId(parkingCouponChargeDtos.get(0).getCouponShopId());
        parkingCouponShopDto.setShopId(parkingCouponChargeDtos.get(0).getShopId());
        List<ParkingCouponShopDto> parkingCouponShopDtos = parkingCouponShopV1InnerServiceSMOImpl.queryParkingCouponShops(parkingCouponShopDto);

        if(ListUtil.isNull(parkingCouponShopDtos)){
            return;
        }

        int quantity = Integer.parseInt(parkingCouponShopDtos.get(0).getQuantity());

        if (quantity < 1) {
            return;
        }
        int flag = 0;
        // 这里加全局锁 防止 并发
        String requestId = DistributedLock.getLockUUID();
        String key = this.getClass().getSimpleName() + parkingCouponChargeDtos.get(0).getCouponShopId();
        try {
            DistributedLock.waitGetDistributedLock(key, requestId);
            parkingCouponShopDto = new ParkingCouponShopDto();
            parkingCouponShopDto.setCouponShopId(parkingCouponChargeDtos.get(0).getCouponShopId());
            parkingCouponShopDto.setShopId(parkingCouponChargeDtos.get(0).getShopId());
            parkingCouponShopDtos = parkingCouponShopV1InnerServiceSMOImpl.queryParkingCouponShops(parkingCouponShopDto);
            quantity = Integer.parseInt(parkingCouponShopDtos.get(0).getQuantity());
            if (quantity < 1) {
                return;
            }
            ParkingCouponShopPo parkingCouponShopPo = new ParkingCouponShopPo();
            parkingCouponShopPo.setCouponShopId(parkingCouponShopDtos.get(0).getCouponShopId());
            parkingCouponShopPo.setQuantity((quantity - 1) + "");
            flag = parkingCouponShopV1InnerServiceSMOImpl.updateParkingCouponShop(parkingCouponShopPo);
            if (flag < 1) {
                return;
            }
        } finally {
            DistributedLock.releaseDistributedLock(key, requestId);
        }

        ParkingCouponCarPo parkingCouponCarPo = new ParkingCouponCarPo();
        parkingCouponCarPo.setGiveWay("1001");
        parkingCouponCarPo.setCarNum(carNum);
        parkingCouponCarPo.setCouponShopId(parkingCouponChargeDtos.get(0).getCouponShopId());
        parkingCouponCarPo.setRemark("车辆充电赠送停车券");
        parkingCouponCarPo.setShopId(parkingCouponChargeDtos.get(0).getShopId());
        parkingCouponCarPo.setPccId(GenerateCodeFactory.getGeneratorId("11"));
        parkingCouponCarPo.setCouponId(parkingCouponShopDtos.get(0).getCouponId());
        parkingCouponCarPo.setCommunityId(parkingCouponShopDtos.get(0).getCommunityId());
        parkingCouponCarPo.setStartTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        parkingCouponCarPo.setEndTime(DateUtil.getAddDayString(DateUtil.getCurrentDate(), DateUtil.DATE_FORMATE_STRING_A, 1));
        parkingCouponCarPo.setPaId(parkingCouponShopDtos.get(0).getPaId());
        parkingCouponCarPo.setState(ParkingCouponCarDto.STATE_WAIT);
        parkingCouponCarPo.setTypeCd(parkingCouponShopDtos.get(0).getTypeCd());
        parkingCouponCarPo.setValue(parkingCouponShopDtos.get(0).getValue());

        parkingCouponCarV1InnerServiceSMOImpl.saveParkingCouponCar(parkingCouponCarPo);


    }


    @Override
    public ResultVo orderRefund(String orderId,String money,String remark) {
        ChargeMachineOrderDto chargeMachineOrderDto = new ChargeMachineOrderDto();
        chargeMachineOrderDto.setOrderId(orderId);
        List<ChargeMachineOrderDto> chargeMachineOrderDtos = chargeMachineOrderV1InnerServiceSMOImpl.queryChargeMachineOrders(chargeMachineOrderDto);
        AccountDto accountDto = new AccountDto();
        accountDto.setAcctId(chargeMachineOrderDtos.get(0).getAcctDetailId());
        List<AccountDto> accountDtos = accountInnerServiceSMOImpl.queryAccounts(accountDto);
        if (ListUtil.isNull(accountDtos)) {
            return new ResultVo(ResultVo.CODE_OK, "未查询到账户信息");
        }

        AccountDetailPo accountDetailPo = new AccountDetailPo();
        accountDetailPo.setAcctId(accountDtos.get(0).getAcctId());
        accountDetailPo.setObjId(accountDtos.get(0).getObjId());
        accountDetailPo.setObjType(accountDtos.get(0).getObjType());
        accountDetailPo.setDetailId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_detailId));
        double returnMoney = Double.parseDouble(money);

        if (returnMoney < 0) {
            accountDetailPo.setAmount((-1 * returnMoney) + "");
            accountDetailPo.setRemark("充电扣款金额-" + chargeMachineOrderDtos.get(0).getOrderId());
            accountInnerServiceSMOImpl.withholdAccount(accountDetailPo);
        } else {
            accountDetailPo.setAmount(returnMoney + "");
            accountDetailPo.setRemark(remark+"；充电退回金额："+ returnMoney +"元，" + chargeMachineOrderDtos.get(0).getOrderId());
            accountInnerServiceSMOImpl.prestoreAccount(accountDetailPo);
        }

        //充电表中加入退款金额
        ChargeMachineOrderAcctPo chargeMachineOrderAcctPo = new ChargeMachineOrderAcctPo();
        chargeMachineOrderAcctPo.setAcctDetailId(accountDetailPo.getDetailId());
        chargeMachineOrderAcctPo.setAmount((-1 * returnMoney) + "");

        chargeMachineOrderAcctPo.setCmoaId(GenerateCodeFactory.getGeneratorId("11"));
        chargeMachineOrderAcctPo.setOrderId(chargeMachineOrderDtos.get(0).getOrderId());
        chargeMachineOrderAcctPo.setAcctId(accountDtos.get(0).getAcctId());
        chargeMachineOrderAcctPo.setStartTime(chargeMachineOrderDtos.get(0).getStartTime());
        chargeMachineOrderAcctPo.setEnergy("0");
        chargeMachineOrderAcctPo.setEndTime(chargeMachineOrderDtos.get(0).getEndTime());
        if (returnMoney < 0) {
            chargeMachineOrderAcctPo.setRemark("账户扣款-" + remark);
        } else {
            chargeMachineOrderAcctPo.setRemark(remark+"；账户手动退款：" + returnMoney +"元，" + chargeMachineOrderDtos.get(0).getOrderId());
        }
        chargeMachineOrderAcctPo.setCommunityId(chargeMachineOrderDtos.get(0).getCommunityId());
        //chargeMachineOrderAcctPo.setEnergy("0");
        chargeMachineOrderAcctPo.setDurationPrice("0");

        chargeMachineOrderAcctV1InnerServiceSMOImpl.saveChargeMachineOrderAcct(chargeMachineOrderAcctPo);
        return new ResultVo(ResultVo.CODE_OK, "成功");
    }
}
