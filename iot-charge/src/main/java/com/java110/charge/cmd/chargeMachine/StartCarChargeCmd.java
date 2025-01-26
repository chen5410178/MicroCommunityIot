package com.java110.charge.cmd.chargeMachine;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.charge.factory.IChargeCore;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.annotation.Java110Transactional;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.*;
import com.java110.dto.account.AccountDto;
import com.java110.dto.chargeMachine.ChargeMachineDto;
import com.java110.dto.chargeMachine.ChargeMachineOrderDto;
import com.java110.dto.chargeMachine.ChargeMachinePortDto;
import com.java110.dto.chargeMachine.ChargeRuleFeeDto;
import com.java110.dto.chargeRulePrice.ChargeRulePriceDto;
import com.java110.dto.couponPool.CouponPropertyPoolConfigDto;
import com.java110.dto.couponPool.CouponPropertyUserDto;
import com.java110.dto.user.UserDto;
import com.java110.intf.acct.IAccountInnerServiceSMO;
import com.java110.intf.acct.ICouponPropertyPoolConfigV1InnerServiceSMO;
import com.java110.intf.acct.ICouponPropertyUserDetailV1InnerServiceSMO;
import com.java110.intf.acct.ICouponPropertyUserV1InnerServiceSMO;
import com.java110.intf.charge.*;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import com.java110.po.accountDetail.AccountDetailPo;
import com.java110.po.chargeMachineOrder.ChargeMachineOrderPo;
import com.java110.po.chargeMachineOrderAcct.ChargeMachineOrderAcctPo;
import com.java110.po.chargeMachineOrderCoupon.ChargeMachineOrderCouponPo;
import com.java110.po.chargeMachinePort.ChargeMachinePortPo;
import com.java110.po.couponPropertyUser.CouponPropertyUserPo;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.java110.dto.chargeMachine.ChargeRuleFeeDto.CHARGE_TYPE_CAR;


/**
 * 汽车充电桩开始充电 处理类
 * <p>
 * add by wuxw 2023-03-09
 */
@Java110Cmd(serviceCode = "chargeMachine.startCarCharge")
public class StartCarChargeCmd extends Cmd {

    @Autowired
    private IChargeMachineV1InnerServiceSMO chargeMachineV1InnerServiceSMOImpl;

    @Autowired
    private IChargeMachinePortV1InnerServiceSMO chargeMachinePortV1InnerServiceSMOImpl;

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Autowired
    private IAccountInnerServiceSMO accountInnerServiceSMOImpl;

    @Autowired
    private IChargeCore chargeCoreImpl;

    @Autowired
    private IChargeMachineOrderV1InnerServiceSMO chargeMachineOrderV1InnerServiceSMOImpl;

    @Autowired
    private IChargeMachineOrderAcctV1InnerServiceSMO chargeMachineOrderAcctV1InnerServiceSMOImpl;
    @Autowired
    private ICouponPropertyUserV1InnerServiceSMO couponPropertyUserV1InnerServiceSMOImpl;

    @Autowired
    private ICouponPropertyUserDetailV1InnerServiceSMO couponPropertyUserDetailV1InnerServiceSMOImpl;

    @Autowired
    private ICouponPropertyPoolConfigV1InnerServiceSMO couponPropertyPoolConfigV1InnerServiceSMOImpl;

    @Autowired
    private IChargeRuleFeeV1InnerServiceSMO chargeRuleFeeV1InnerServiceSMOImpl;

    @Autowired
    private IChargeRulePriceV1InnerServiceSMO chargeRulePriceV1InnerServiceSMO;

    @Autowired
    private IChargeMachineOrderCouponV1InnerServiceSMO chargeMachineOrderCouponV1InnerServiceSMOImpl;

    @Autowired
    private IChargeMonthOrderV1InnerServiceSMO chargeMonthOrderV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson, "communityId", "未包含小区信息");
        Assert.hasKeyAndValue(reqJson, "machineId", "未包含设备号");
        Assert.hasKeyAndValue(reqJson, "portId", "未包含充电桩");
        Assert.hasKeyAndValue(reqJson, "money", "未包含充电金额");

        String userId = context.getReqHeaders().get("user-id");
        Assert.hasLength(userId, "用户不存在");
        //查询充电桩设备信息
        ChargeMachineDto chargeMachineDto = new ChargeMachineDto();
        chargeMachineDto.setCommunityId(reqJson.getString("communityId"));
        chargeMachineDto.setMachineId(reqJson.getString("machineId"));
        List<ChargeMachineDto> chargeMachineDtos = chargeMachineV1InnerServiceSMOImpl.queryChargeMachines(chargeMachineDto);
        Assert.listOnlyOne(chargeMachineDtos, "设备不存在");
        // todo 充电桩是否空闲
        ChargeMachinePortDto chargeMachinePortDto = new ChargeMachinePortDto();
        chargeMachinePortDto.setMachineId(reqJson.getString("machineId"));
        chargeMachinePortDto.setPortId(reqJson.getString("portId"));
        chargeMachinePortDto.setState(ChargeMachinePortDto.STATE_FREE);
        List<ChargeMachinePortDto> chargeMachinePortDtos = chargeMachinePortV1InnerServiceSMOImpl.queryChargeMachinePorts(chargeMachinePortDto);
        Assert.listOnlyOne(chargeMachinePortDtos, "充电桩正在使用");

        ChargeRulePriceDto chargeRulePriceDto = new ChargeRulePriceDto();
        chargeRulePriceDto.setRuleId(chargeMachineDtos.get(0).getRuleId());
        chargeRulePriceDto.setCommunityId(chargeMachineDtos.get(0).getCommunityId());
        List<ChargeRulePriceDto> chargeRulePriceDtos = chargeRulePriceV1InnerServiceSMO.queryChargeRulePrices(chargeRulePriceDto);
        if (ListUtil.isNull(chargeRulePriceDtos)) {
            throw new CmdException("未设置充值收费标准，请联系管理员！");
        }
        UserDto userDto = new UserDto();
        userDto.setUserId(userId);
        List<UserDto> userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);
        Assert.listOnlyOne(userDtos, "用户不存在");

        AccountDto accountDto = new AccountDto();
        accountDto.setLink(userDtos.get(0).getTel());
        accountDto.setAcctType(AccountDto.ACCT_TYPE_CASH);
        accountDto.setObjType(AccountDto.OBJ_TYPE_PERSON);
        accountDto.setPartId(reqJson.getString("communityId"));
        List<AccountDto> accountDtos = accountInnerServiceSMOImpl.queryAccounts(accountDto);

        if (ListUtil.isNull(accountDtos)) {
            throw new CmdException("请先充值，账户金额不足");
        }
        double payMoney = reqJson.getDoubleValue("money");
        if (Double.parseDouble(accountDtos.get(0).getAmount()) < payMoney) {
            throw new CmdException("您账户金额不足，无法支付此次充电费用,请您先充值。");
        }
        reqJson.put("acctId", accountDtos.get(0).getAcctId());
    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        String userId = context.getReqHeaders().get("user-id");
        UserDto userDto = new UserDto();
        userDto.setUserId(userId);
        List<UserDto> userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);

        Assert.listOnlyOne(userDtos, "用户不存在");
        ChargeMachineDto chargeMachineDto = new ChargeMachineDto();
        chargeMachineDto.setCommunityId(reqJson.getString("communityId"));
        chargeMachineDto.setMachineId(reqJson.getString("machineId"));
        List<ChargeMachineDto> chargeMachineDtos = chargeMachineV1InnerServiceSMOImpl.queryChargeMachines(chargeMachineDto);
        double money = reqJson.getDoubleValue("money");

        ChargeMachinePortDto chargeMachinePortDto = new ChargeMachinePortDto();
        chargeMachinePortDto.setMachineId(reqJson.getString("machineId"));
        chargeMachinePortDto.setPortId(reqJson.getString("portId"));
        chargeMachinePortDto.setState(ChargeMachinePortDto.STATE_FREE);
        List<ChargeMachinePortDto> chargeMachinePortDtos = chargeMachinePortV1InnerServiceSMOImpl.queryChargeMachinePorts(chargeMachinePortDto);
        String orderId = GenerateCodeFactory.getGeneratorId("12");
        int port = Integer.parseInt(chargeMachinePortDtos.get(0).getPortCode());
        String portHex = String.format("%02x", port);
        orderId = chargeMachineDtos.get(0).getMachineCode() + portHex + DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_DEFAULT) + orderId.substring(orderId.length() - 2);

        // todo 修改端口状态
        ChargeMachinePortPo chargeMachinePortPo = new ChargeMachinePortPo();
        chargeMachinePortPo.setPortId(chargeMachinePortDtos.get(0).getPortId());
        chargeMachinePortPo.setCommunityId(chargeMachinePortDtos.get(0).getCommunityId());
        chargeMachinePortPo.setState(ChargeMachinePortDto.STATE_STARTING);
        chargeMachinePortV1InnerServiceSMOImpl.updateChargeMachinePort(chargeMachinePortPo);

        // todo 生成 充电订单
        ChargeMachineOrderPo chargeMachineOrderPo = new ChargeMachineOrderPo();
        chargeMachineOrderPo.setAmount("0");
        chargeMachineOrderPo.setOrderId(orderId);
        chargeMachineOrderPo.setPortId(chargeMachinePortDtos.get(0).getPortId());
        chargeMachineOrderPo.setPersonName(userDtos.get(0).getName());
        chargeMachineOrderPo.setMachineId(chargeMachineDtos.get(0).getMachineId());
        chargeMachineOrderPo.setAcctDetailId(reqJson.getString("acctId"));
        chargeMachineOrderPo.setPersonId(userId);
        chargeMachineOrderPo.setChargeHours(reqJson.getString("duration"));
        chargeMachineOrderPo.setDurationPrice("0");
        chargeMachineOrderPo.setServicePrice("0");
        chargeMachineOrderPo.setStartTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        chargeMachineOrderPo.setEndTime(DateUtil.getAddHoursStringA(new Date(), 2));
        chargeMachineOrderPo.setState(ChargeMachineOrderDto.STATE_STARTING);
        chargeMachineOrderPo.setPersonTel(userDtos.get(0).getTel());
        chargeMachineOrderPo.setCommunityId(chargeMachineDtos.get(0).getCommunityId());
        chargeMachineOrderPo.setEnergy("1");
        int flag = chargeMachineOrderV1InnerServiceSMOImpl.saveChargeMachineOrder(chargeMachineOrderPo);

        if (flag < 1) {
            chargeCoreImpl.stopCharge(chargeMachineDtos.get(0), chargeMachinePortDtos.get(0));
            throw new CmdException("充电失败");
        }

        //调用充电桩充电,开启充电桩
        ResultVo resultVo = chargeCoreImpl.startCharge(chargeMachineDtos.get(0), chargeMachinePortDtos.get(0), CHARGE_TYPE_CAR, money, orderId);

        if (resultVo.getCode() != ResultVo.CODE_OK) {
            context.setResponseEntity(ResultVo.createResponseEntity(resultVo));
            return;
        }

        resultVo.setData(orderId);
        // todo 直接账户预扣款
        withholdAccount(reqJson, chargeMachineDtos, orderId, money);
        context.setResponseEntity(ResultVo.createResponseEntity(resultVo));
    }

    /**
     * 账户抵扣
     *
     * @param reqJson
     * @param chargeMachineDtos
     * @param orderId
     */
    private void withholdAccount(JSONObject reqJson, List<ChargeMachineDto> chargeMachineDtos, String orderId, double durationHours) {
        AccountDto accountDto = new AccountDto();
        accountDto.setAcctId(reqJson.getString("acctId"));
        List<AccountDto> accountDtos = accountInnerServiceSMOImpl.queryAccounts(accountDto);

        if (ListUtil.isNull(accountDtos)) {
            throw new CmdException("账户不存在");
        }

        double amount = durationHours;
        AccountDetailPo accountDetailPo = new AccountDetailPo();
        accountDetailPo.setAcctId(accountDtos.get(0).getAcctId());
        accountDetailPo.setObjId(accountDtos.get(0).getObjId());
        accountDetailPo.setObjType(accountDtos.get(0).getObjType());
        accountDetailPo.setAmount(amount + "");
        accountDetailPo.setDetailId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_detailId));
        accountInnerServiceSMOImpl.withholdAccount(accountDetailPo);
        ChargeMachineOrderAcctPo chargeMachineOrderAcctPo = new ChargeMachineOrderAcctPo();
        chargeMachineOrderAcctPo.setAcctDetailId(accountDetailPo.getDetailId());
        chargeMachineOrderAcctPo.setAmount(amount + "");

        chargeMachineOrderAcctPo.setCmoaId(GenerateCodeFactory.getGeneratorId("11"));
        chargeMachineOrderAcctPo.setOrderId(orderId);
        chargeMachineOrderAcctPo.setAcctId(accountDtos.get(0).getAcctId());
        chargeMachineOrderAcctPo.setStartTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        chargeMachineOrderAcctPo.setEndTime(DateUtil.getAddHoursStringA(DateUtil.getCurrentDate(), new Double(Math.ceil(durationHours)).intValue()));
        chargeMachineOrderAcctPo.setRemark("账户扣款");
        chargeMachineOrderAcctPo.setCommunityId(chargeMachineDtos.get(0).getCommunityId());
        chargeMachineOrderAcctPo.setEnergy("0");
        chargeMachineOrderAcctPo.setDurationPrice("0");
        //chargeMachineOrderAcctPo.setDurationPrice(reqJson.getString("durationPrice"));

        chargeMachineOrderAcctV1InnerServiceSMOImpl.saveChargeMachineOrderAcct(chargeMachineOrderAcctPo);
    }
}
