package com.java110.acct.smo.impl;

import com.java110.bean.ResultVo;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.account.AccountDto;
import com.java110.dto.account.AccountRefundDto;
import com.java110.dto.onlinePay.OnlinePayDto;
import com.java110.intf.acct.IAccountInnerServiceSMO;
import com.java110.intf.acct.IAccountRefundV1InnerServiceSMO;
import com.java110.intf.acct.IOnlinePayRefundV1InnerServiceSMO;
import com.java110.intf.acct.IOnlinePayV1InnerServiceSMO;
import com.java110.intf.acct.IReturnMoneyV1InnerServiceSMO;
import com.java110.po.accountDetail.AccountDetailPo;
import com.java110.po.onlinePay.OnlinePayPo;
import com.java110.po.onlinePay.OnlinePayRefundPo;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * 业主账户余额退款内部服务实现：按 online_pay 逐笔原路退回，并扣减余额、写退款明细
 */
@RestController
public class AccountRefundV1InnerServiceSMOImpl implements IAccountRefundV1InnerServiceSMO {

    private static final Logger logger = LoggerFactory.getLogger(AccountRefundV1InnerServiceSMOImpl.class);

    @Autowired
    private IAccountInnerServiceSMO accountInnerServiceSMOImpl;

    @Autowired
    private IOnlinePayV1InnerServiceSMO onlinePayV1InnerServiceSMOImpl;

    @Autowired
    private IOnlinePayRefundV1InnerServiceSMO onlinePayRefundV1InnerServiceSMOImpl;

    @Autowired
    private IReturnMoneyV1InnerServiceSMO returnMoneyV1InnerServiceSMOImpl;

    @Override
    public AccountRefundDto refundAccountAmount(@RequestBody AccountRefundDto accountRefundDto) {

        String acctId = accountRefundDto.getAcctId();
        String communityId = accountRefundDto.getCommunityId();
        BigDecimal refundAmount = accountRefundDto.getAmount();

        // 1. 查询可退支付记录（state=C 且 remaining_fee > 0），按创建时间倒序逐笔退款
        List<OnlinePayDto> onlinePayDtos = queryRefundableOnlinePays(acctId);
        if (ListUtil.isNull(onlinePayDtos)) {
            throw new IllegalArgumentException("当前无可退余额");
        }

        BigDecimal refundableFee = BigDecimal.ZERO;
        for (OnlinePayDto onlinePayDto : onlinePayDtos) {
            refundableFee = refundableFee.add(getRemainingFee(onlinePayDto));
        }
        if (refundAmount.compareTo(refundableFee) > 0) {
            throw new IllegalArgumentException("超出可退金额，最多可退" + refundableFee.setScale(2, BigDecimal.ROUND_HALF_UP) + "元");
        }

        // 2. 按支付单逐笔原路退回（先落退款单 W → 调厂家退款 → 回写退款单/剩余可退金额）
        AccountRefundDto resultDto = new AccountRefundDto();
        BigDecimal leftFee = refundAmount;
        for (OnlinePayDto onlinePayDto : onlinePayDtos) {
            if (leftFee.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }
            BigDecimal remainingFee = getRemainingFee(onlinePayDto);
            if (remainingFee.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            BigDecimal thisRefundFee = leftFee.compareTo(remainingFee) > 0 ? remainingFee : leftFee;
            String refundId = refundOnlinePay(onlinePayDto, thisRefundFee, communityId, accountRefundDto.getRefundUserId());
            leftFee = leftFee.subtract(thisRefundFee);
            resultDto.setRefundId(refundId);
        }

        // 3. 扣减账户余额并写退款明细（detail_type=3003）
        AccountDto accountDto = queryAccount(acctId, communityId);

        AccountDetailPo accountDetailPo = new AccountDetailPo();
        accountDetailPo.setAcctId(accountDto.getAcctId());
        accountDetailPo.setObjId(accountDto.getObjId());
        accountDetailPo.setObjType(accountDto.getObjType());
        accountDetailPo.setAmount(refundAmount.toString());
        accountDetailPo.setOrderId(resultDto.getRefundId());
        accountDetailPo.setRemark(StringUtil.isEmpty(accountRefundDto.getRemark())
                ? AccountRefundDto.REFUND_REMARK : accountRefundDto.getRemark());
        accountInnerServiceSMOImpl.refundAccount(accountDetailPo);

        resultDto.setAcctId(acctId);
        resultDto.setCommunityId(communityId);
        resultDto.setRefundFee(refundAmount);
        resultDto.setAmount(refundAmount);
        return resultDto;
    }

    /**
     * 查询可退支付记录
     *
     * @param acctId 账户ID
     * @return 可退支付记录（state=C 且 remaining_fee > 0）
     */
    public List<OnlinePayDto> queryRefundableOnlinePays(String acctId) {
        OnlinePayDto onlinePayDto = new OnlinePayDto();
        onlinePayDto.setBusiId(acctId);
        onlinePayDto.setState(OnlinePayDto.STATE_C);
        onlinePayDto.setRemainingFeeMoreThanZero("1");
        onlinePayDto.setPage(1);
        onlinePayDto.setRow(100);
        return onlinePayV1InnerServiceSMOImpl.queryOnlinePays(onlinePayDto);
    }

    /**
     * 单笔支付记录原路退回（先落退款单 W，再调用退款，最后回写退款单状态与剩余可退金额）
     *
     * @param onlinePayDto 支付记录
     * @param refundFee    本次退款金额
     * @param communityId  小区ID
     * @param refundUserId 退款操作人
     * @return 退款单ID
     */
    private String refundOnlinePay(OnlinePayDto onlinePayDto, BigDecimal refundFee, String communityId, String refundUserId) {
        String refundId = GenerateCodeFactory.getGeneratorId("10");
        OnlinePayRefundPo refundPo = new OnlinePayRefundPo();
        refundPo.setRefundId(refundId);
        refundPo.setPayId(onlinePayDto.getPayId());
        refundPo.setRefundFee(refundFee.setScale(2, BigDecimal.ROUND_HALF_UP));
        refundPo.setBusiId(onlinePayDto.getBusiId());
        refundPo.setCommunityId(communityId);
        refundPo.setRefundUserId(StringUtil.isEmpty(refundUserId) ? "-1" : refundUserId);
        refundPo.setState(OnlinePayDto.STATE_WT);
        refundPo.setMessage("退款申请中");
        onlinePayRefundV1InnerServiceSMOImpl.saveOnlinePayRefund(refundPo);

        OnlinePayRefundPo updateRefundPo = new OnlinePayRefundPo();
        updateRefundPo.setRefundId(refundId);
        updateRefundPo.setStatusCd("0");

        ResultVo resultVo;
        try {
            resultVo = returnMoneyV1InnerServiceSMOImpl.returnMoney(onlinePayDto);
        } catch (Exception e) {
            logger.error("余额退款调用退款接口异常", e);
            updateRefundPo.setState(OnlinePayDto.STATE_FT);
            updateRefundPo.setMessage(e.getLocalizedMessage());
            onlinePayRefundV1InnerServiceSMOImpl.updateOnlinePayRefund(updateRefundPo);
            throw new IllegalArgumentException(StringUtil.isEmpty(e.getLocalizedMessage()) ? "退款失败" : e.getLocalizedMessage());
        }

        if (resultVo == null || resultVo.getCode() != ResultVo.CODE_OK) {
            updateRefundPo.setState(OnlinePayDto.STATE_FT);
            updateRefundPo.setMessage(resultVo == null ? "退款失败" : resultVo.getMsg());
            onlinePayRefundV1InnerServiceSMOImpl.updateOnlinePayRefund(updateRefundPo);
            throw new IllegalArgumentException(resultVo == null ? "退款失败" : resultVo.getMsg());
        }

        updateRefundPo.setState(OnlinePayDto.STATE_C);
        updateRefundPo.setMessage(resultVo.getMsg());
        onlinePayRefundV1InnerServiceSMOImpl.updateOnlinePayRefund(updateRefundPo);

        // 扣减剩余可退金额，避免二次退款
        OnlinePayPo onlinePayPo = new OnlinePayPo();
        onlinePayPo.setPayId(onlinePayDto.getPayId());
        onlinePayPo.setRemainingFee(getRemainingFee(onlinePayDto).subtract(refundFee).toString());
        onlinePayV1InnerServiceSMOImpl.updateOnlinePay(onlinePayPo);
        return refundId;
    }

    /**
     * 查询账户信息
     *
     * @param acctId      账户ID
     * @param communityId 小区ID
     * @return 账户信息
     */
    private AccountDto queryAccount(String acctId, String communityId) {
        AccountDto accountDto = new AccountDto();
        accountDto.setAcctId(acctId);
        accountDto.setPartId(communityId);
        accountDto.setObjType(AccountDto.OBJ_TYPE_PERSON);
        List<AccountDto> accountDtos = accountInnerServiceSMOImpl.queryAccounts(accountDto);
        if (ListUtil.isNull(accountDtos)) {
            throw new IllegalArgumentException("账户不存在");
        }
        return accountDtos.get(0);
    }

    /**
     * 获取支付记录剩余可退金额
     *
     * @param onlinePayDto 支付记录
     * @return 剩余可退金额
     */
    private BigDecimal getRemainingFee(OnlinePayDto onlinePayDto) {
        if (onlinePayDto.getRemainingFee() == null) {
            return BigDecimal.ZERO;
        }
        return onlinePayDto.getRemainingFee();
    }
}
