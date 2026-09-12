package com.java110.acct.cmd.account;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.owner.OwnerDto;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.annotation.Java110Transactional;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.CmdContextUtils;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.account.AccountDto;
import com.java110.dto.account.AccountRefundDto;
import com.java110.dto.onlinePay.OnlinePayDto;
import com.java110.dto.user.UserDto;
import com.java110.intf.acct.IAccountInnerServiceSMO;
import com.java110.intf.acct.IAccountRefundV1InnerServiceSMO;
import com.java110.intf.acct.IOnlinePayV1InnerServiceSMO;
import com.java110.intf.user.IOwnerInnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;

/**
 * 业主账户余额退款（微信原路退回）
 */
@Java110Cmd(serviceCode = "account.ownerRefundAccount")
public class AppRefundOwnerAccountCmd extends Cmd {

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Autowired
    private IOwnerInnerServiceSMO ownerInnerServiceSMOImpl;

    @Autowired
    private IAccountInnerServiceSMO accountInnerServiceSMOImpl;

    @Autowired
    private IAccountRefundV1InnerServiceSMO accountRefundV1InnerServiceSMOImpl;

    @Autowired
    private IOnlinePayV1InnerServiceSMO onlinePayV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson, "communityId", "未包含小区ID");
        Assert.hasKeyAndValue(reqJson, "acctId", "未包含账户ID");

        String userId = CmdContextUtils.getUserId(context);
        if (StringUtil.isEmpty(userId)) {
            throw new CmdException("未登录，请先登录");
        }

        UserDto userDto = new UserDto();
        userDto.setUserId(userId);
        List<UserDto> userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);
        if (ListUtil.isNull(userDtos)) {
            throw new CmdException("用户不存在");
        }

        AccountDto accountDto = new AccountDto();
        accountDto.setAcctId(reqJson.getString("acctId"));
        accountDto.setPartId(reqJson.getString("communityId"));
        accountDto.setAcctType(AccountDto.ACCT_TYPE_CASH);
        List<AccountDto> accountDtos = accountInnerServiceSMOImpl.queryAccounts(accountDto);
        if (ListUtil.isNull(accountDtos)) {
            throw new CmdException("账户不存在");
        }

        assertAccountOwner(userId, userDtos.get(0).getTel(), reqJson.getString("communityId"), accountDtos.get(0));

        BigDecimal balance = new BigDecimal(StringUtil.isEmpty(accountDtos.get(0).getAmount()) ? "0" : accountDtos.get(0).getAmount());

        BigDecimal refundableFee = BigDecimal.ZERO;
        OnlinePayDto onlinePayDto = new OnlinePayDto();
        onlinePayDto.setBusiId(accountDtos.get(0).getAcctId());
        onlinePayDto.setState(OnlinePayDto.STATE_C);
        onlinePayDto.setRemainingFeeMoreThanZero("1");
        onlinePayDto.setPage(1);
        onlinePayDto.setRow(100);
        List<OnlinePayDto> onlinePayDtos = onlinePayV1InnerServiceSMOImpl.queryOnlinePays(onlinePayDto);
        if (!ListUtil.isNull(onlinePayDtos)) {
            for (OnlinePayDto payDto : onlinePayDtos) {
                refundableFee = refundableFee.add(payDto.getRemainingFee() == null ? BigDecimal.ZERO : payDto.getRemainingFee());
            }
        }

        String amount = reqJson.getString("amount");
        BigDecimal refundAmount;
        if (StringUtil.isEmpty(amount)) {
            refundAmount = balance;
        } else {
            try {
                refundAmount = new BigDecimal(amount);
            } catch (Exception e) {
                throw new CmdException("退款金额格式错误");
            }
        }

        if (refundAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CmdException("当前无可退余额");
        }
        if (refundAmount.compareTo(balance) > 0) {
            throw new CmdException("退款金额不能大于余额");
        }
        if (refundAmount.compareTo(refundableFee) > 0) {
            throw new CmdException("部分金额非线上缴费，无法在线退费，请联系客服");
        }

        reqJson.put("amount", refundAmount.toPlainString());
    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        String userId = CmdContextUtils.getUserId(context);

        AccountRefundDto accountRefundDto = new AccountRefundDto();
        accountRefundDto.setAcctId(reqJson.getString("acctId"));
        accountRefundDto.setCommunityId(reqJson.getString("communityId"));
        accountRefundDto.setAmount(new BigDecimal(reqJson.getString("amount")).setScale(2, BigDecimal.ROUND_HALF_UP));
        accountRefundDto.setRefundUserId(userId);
        accountRefundDto.setRemark(reqJson.getString("remark"));

        AccountRefundDto refundDto = accountRefundV1InnerServiceSMOImpl.refundAccountAmount(accountRefundDto);

        JSONObject data = BeanConvertUtil.beanCovertJson(refundDto);

        context.setResponseEntity(ResultVo.createResponseEntity(ResultVo.CODE_OK, ResultVo.MSG_OK, data));
    }

    /**
     * 校验账户归属，杜绝越权退款
     *
     * @param userId      登录用户ID
     * @param tel         登录用户手机号
     * @param communityId 小区ID
     * @param accountDto  账户信息
     */
    private void assertAccountOwner(String userId, String tel, String communityId, AccountDto accountDto) {
        if (userId.equals(accountDto.getObjId())) {
            return;
        }
        if (StringUtil.isEmpty(tel)) {
            throw new CmdException("账户不存在");
        }
        OwnerDto ownerDto = new OwnerDto();
        ownerDto.setLink(tel);
        ownerDto.setCommunityId(communityId);
        List<OwnerDto> ownerDtos = ownerInnerServiceSMOImpl.queryOwners(ownerDto);
        if (ListUtil.isNull(ownerDtos) || !accountDto.getObjId().equals(ownerDtos.get(0).getMemberId())) {
            throw new CmdException("账户不存在");
        }
    }
}
