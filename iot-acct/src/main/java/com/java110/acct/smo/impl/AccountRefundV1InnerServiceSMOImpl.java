package com.java110.acct.smo.impl;

import com.java110.bean.ResultVo;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.utils.DistributedLock;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.account.AccountDto;
import com.java110.dto.account.AccountRefundDto;
import com.java110.dto.onlinePay.OnlinePayDto;
import com.java110.dto.onlinePay.OnlinePayRefundDto;
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
 * 业主账户余额退款内部服务实现：按 online_pay 逐笔原路退回，并逐笔扣减余额、写退款明细
 * <p>
 * 逐笔闭环（退一笔、扣一笔）：每笔原路退回成功后立即扣减该笔剩余可退金额、扣减账户余额并写一条
 * detail_type=3003 明细，处理完一笔再进入下一笔；任一笔失败即停止后续笔，已成功笔不回退。
 */
@RestController
public class AccountRefundV1InnerServiceSMOImpl implements IAccountRefundV1InnerServiceSMO {

    private static final Logger logger = LoggerFactory.getLogger(AccountRefundV1InnerServiceSMOImpl.class);

    // 单次退款可能包含多笔退款请求，锁超时时间需覆盖整个退款过程
    private static final int REFUND_LOCK_EXPIRE_TIME = 300000;

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
        if (StringUtil.isEmpty(acctId)) {
            throw new IllegalArgumentException("未包含账户ID");
        }
        if (accountRefundDto.getAmount() == null || accountRefundDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("退款金额必须大于0");
        }

        AccountRefundDto resultDto = new AccountRefundDto();
        // 是否扣减账户余额：未传按扣减处理，兼容业主余额退款旧调用
        boolean deductAccountAmount =
                !AccountRefundDto.DEDUCT_ACCOUNT_AMOUNT_NO.equals(accountRefundDto.getDeductAccountAmount());
        // 同一账户退款串行化，避免重复提交造成重复退款
        String requestId = DistributedLock.getLockUUID();
        String key = this.getClass().getSimpleName() + "RefundAmount" + acctId;
        String failMsg;
        try {
            DistributedLock.waitGetDistributedLock(key, requestId, REFUND_LOCK_EXPIRE_TIME);
            failMsg = doRefundAccountAmount(accountRefundDto, resultDto, deductAccountAmount);
        } finally {
            DistributedLock.releaseDistributedLock(key, requestId);
        }

        if (resultDto.getRefundFee() == null || resultDto.getRefundFee().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(StringUtil.isEmpty(failMsg) ? "退款失败，请稍后重试" : failMsg);
        }
        return resultDto;
    }

    /**
     * 逐笔退款：每笔退款成功后立即扣减该笔剩余可退金额，需要扣减账户余额时同时扣减余额并写退款明细
     *
     * @param accountRefundDto     退款请求
     * @param resultDto            退款结果
     * @param deductAccountAmount  是否扣减账户余额并写退款明细
     * @return 失败原因（全部退款成功时为空）
     */
    private String doRefundAccountAmount(AccountRefundDto accountRefundDto, AccountRefundDto resultDto,
                                         boolean deductAccountAmount) {

        String acctId = accountRefundDto.getAcctId();
        String communityId = accountRefundDto.getCommunityId();
        BigDecimal refundAmount = accountRefundDto.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP);

        // 1. 查询可退支付记录（state=C 且 remaining_fee > 0），校验可退金额
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

        // 不扣减余额时不校验账户，避免账户类型、小区口径不一致导致退款误失败
        AccountDto accountDto = null;
        if (deductAccountAmount) {
            accountDto = queryAccount(acctId, communityId);
        }

        // 2. 逐笔原路退回，处理完一笔再进入下一笔
        BigDecimal leftFee = refundAmount;
        BigDecimal refundFee = BigDecimal.ZERO;
        String failMsg = "";
        for (OnlinePayDto onlinePayDto : onlinePayDtos) {
            if (leftFee.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }
            BigDecimal remainingFee = getRemainingFee(onlinePayDto);
            if (remainingFee.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            BigDecimal thisRefundFee = leftFee.compareTo(remainingFee) > 0 ? remainingFee : leftFee;

            String refundId = GenerateCodeFactory.getGeneratorId("10");
            // ① 落退款单 W
            saveRefund(onlinePayDto, refundId, thisRefundFee, communityId, accountRefundDto.getRefundUserId());

            // ② 调用厂家退款（out_trade_no=该笔原订单号，out_refund_no=本次退款单号，refund_fee=本次退款金额）
            ResultVo resultVo = refundMoney(onlinePayDto, refundId, thisRefundFee, communityId);

            // ③ 失败：回写 FT 并记录失败原因，停止后续笔（前面已成功的笔不回退）
            if (resultVo == null || resultVo.getCode() != ResultVo.CODE_OK) {
                failMsg = resultVo == null ? "退款失败" : resultVo.getMsg();
                updateRefundState(refundId, OnlinePayDto.STATE_FT, failMsg);
                break;
            }

            // ④ 成功：按开关扣减账户余额并写 3003 明细 → 扣减该笔剩余可退金额 → 回写退款单 C
            commitRefundPay(onlinePayDto, accountDto, deductAccountAmount, refundId, thisRefundFee, accountRefundDto.getRemark());
            refundFee = refundFee.add(thisRefundFee);
            leftFee = leftFee.subtract(thisRefundFee);
            if (StringUtil.isEmpty(resultDto.getRefundId())) {
                resultDto.setRefundId(refundId);
            }
        }

        resultDto.setAcctId(acctId);
        resultDto.setCommunityId(communityId);
        resultDto.setAmount(refundAmount);
        resultDto.setRefundFee(refundFee);
        return failMsg;
    }

    /**
     * 调用厂家退款接口
     *
     * @param onlinePayDto 支付记录
     * @param refundId     本次退款单号
     * @param refundFee    本次退款金额
     * @param communityId  小区ID
     * @return 退款结果
     */
    private ResultVo refundMoney(OnlinePayDto onlinePayDto, String refundId, BigDecimal refundFee, String communityId) {
        OnlinePayRefundDto refundDto = new OnlinePayRefundDto();
        refundDto.setRefundId(refundId);
        refundDto.setPayId(onlinePayDto.getPayId());
        refundDto.setRefundFee(refundFee);
        refundDto.setBusiId(onlinePayDto.getBusiId());
        refundDto.setCommunityId(communityId);
        refundDto.setState(OnlinePayDto.STATE_WT);
        refundDto.setMessage("退款申请中");
        try {
            return returnMoneyV1InnerServiceSMOImpl.returnMoney(refundDto);
        } catch (Exception e) {
            logger.error("退款单" + refundId + " 调用退款接口异常", e);
            return new ResultVo(ResultVo.CODE_ERROR,
                    StringUtil.isEmpty(e.getLocalizedMessage()) ? "退款失败" : e.getLocalizedMessage());
        }
    }

    /**
     * 单笔退款成功后的账务处理（逐笔闭环）
     *
     * @param onlinePayDto        支付记录
     * @param accountDto          账户信息（不扣减余额时为空）
     * @param deductAccountAmount 是否扣减账户余额并写退款明细
     * @param refundId            退款单号（同时作为账户明细的订单号）
     * @param refundFee           本次退款金额
     * @param remark              备注
     */
    private void commitRefundPay(OnlinePayDto onlinePayDto, AccountDto accountDto, boolean deductAccountAmount,
                                 String refundId, BigDecimal refundFee, String remark) {
        // 1. 需要扣减余额时，扣减账户余额并写 detail_type=3003 明细（同一次调用内同时生效）
        if (deductAccountAmount) {
            AccountDetailPo accountDetailPo = new AccountDetailPo();
            accountDetailPo.setAcctId(accountDto.getAcctId());
            accountDetailPo.setObjId(accountDto.getObjId());
            accountDetailPo.setObjType(accountDto.getObjType());
            accountDetailPo.setAmount(refundFee.toString());
            accountDetailPo.setOrderId(refundId);
            accountDetailPo.setRemark(StringUtil.isEmpty(remark) ? AccountRefundDto.REFUND_REMARK : remark);
            accountInnerServiceSMOImpl.refundAccount(accountDetailPo);
        }

        // 2. 扣减该笔剩余可退金额，作为同一笔重复退款的资金闸门
        BigDecimal newRemainingFee = getRemainingFee(onlinePayDto).subtract(refundFee);
        if (newRemainingFee.compareTo(BigDecimal.ZERO) < 0) {
            newRemainingFee = BigDecimal.ZERO;
        }
        OnlinePayPo onlinePayPo = new OnlinePayPo();
        onlinePayPo.setPayId(onlinePayDto.getPayId());
        onlinePayPo.setRemainingFee(newRemainingFee.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        onlinePayV1InnerServiceSMOImpl.updateOnlinePay(onlinePayPo);

        // 3. 回写退款单成功
        updateRefundState(refundId, OnlinePayDto.STATE_C, "退款完成");
    }

    /**
     * 落地退款单（待退款）
     *
     * @param onlinePayDto 支付记录
     * @param refundId     退款单号
     * @param refundFee    退款金额
     * @param communityId  小区ID
     * @param refundUserId 退款操作人
     */
    private void saveRefund(OnlinePayDto onlinePayDto, String refundId, BigDecimal refundFee, String communityId,
                            String refundUserId) {
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
    }

    /**
     * 回写退款单状态
     *
     * @param refundId 退款单号
     * @param state    状态
     * @param message  说明（厂家原始错误信息需落库，便于对账排查）
     */
    private void updateRefundState(String refundId, String state, String message) {
        OnlinePayRefundPo updateRefundPo = new OnlinePayRefundPo();
        updateRefundPo.setRefundId(refundId);
        updateRefundPo.setStatusCd("0");
        updateRefundPo.setState(state);
        if (!StringUtil.isEmpty(message)) {
            updateRefundPo.setMessage(message.length() > 1000 ? message.substring(0, 1000) : message);
        }
        onlinePayRefundV1InnerServiceSMOImpl.updateOnlinePayRefund(updateRefundPo);
    }

    /**
     * 查询可退支付记录（state=C 且 remaining_fee > 0）
     *
     * @param acctId 账户ID
     * @return 可退支付记录
     */
    private List<OnlinePayDto> queryRefundableOnlinePays(String acctId) {
        OnlinePayDto onlinePayDto = new OnlinePayDto();
        onlinePayDto.setBusiId(acctId);
        onlinePayDto.setState(OnlinePayDto.STATE_C);
        onlinePayDto.setRemainingFeeMoreThanZero("1");
        onlinePayDto.setPage(1);
        onlinePayDto.setRow(100);
        return onlinePayV1InnerServiceSMOImpl.queryOnlinePays(onlinePayDto);
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
