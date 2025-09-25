package com.java110.acct.cmd.account;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.account.AccountDetailDto;
import com.java110.dto.account.AccountDto;
import com.java110.dto.user.UserDto;
import com.java110.intf.acct.IAccountDetailInnerServiceSMO;
import com.java110.intf.acct.IAccountInnerServiceSMO;
import com.java110.intf.community.IRoomInnerServiceSMO;
import com.java110.intf.user.IOwnerInnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import com.java110.po.account.AccountPo;
import com.java110.po.accountDetail.AccountDetailPo;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.List;

/**
 * 业主预存
 */
@Java110Cmd(serviceCode = "account.ownerPrestoreAccount")
public class OwnerPrestoreAccountCmd extends Cmd {

//    @Autowired
//    private IOwnerPrestoreAccountBMO ownerPrestoreAccountBMOImpl;

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Autowired
    private IAccountInnerServiceSMO accountInnerServiceSMOImpl;

    @Autowired
    private IAccountDetailInnerServiceSMO accountDetailInnerServiceSMOImpl;

    @Autowired
    private IOwnerInnerServiceSMO ownerInnerServiceSMOImpl;

//    @Autowired
//    private IAccountReceiptV1InnerServiceSMO accountReceiptV1InnerServiceSMOImpl;

    @Autowired
    private IRoomInnerServiceSMO roomInnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson, "communityId", "小区ID不能为空");
        Assert.hasKeyAndValue(reqJson, "tel", "业主不能为空");
        Assert.hasKeyAndValue(reqJson, "amount", "金额不能为空");
        Assert.hasKeyAndValue(reqJson, "acctType", "账户类型不能为空");
        Assert.hasKeyAndValue(reqJson, "primeRate", "未包含支付方式");


    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        UserDto userDto = new UserDto();
        userDto.setTel(reqJson.getString("tel"));
        userDto.setLevelCd(UserDto.LEVEL_CD_PHONE);
        List<UserDto> userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);
        if (ListUtil.isNull(userDtos)) {
            throw new CmdException("手机号还没有注册用户");
        }

        AccountDetailPo accountDetailPo = new AccountDetailPo();
        accountDetailPo.setRemark(reqJson.getString("remark"));
        accountDetailPo.setObjId(userDtos.get(0).getUserId());
        accountDetailPo.setAmount(reqJson.getString("amount"));
        accountDetailPo.setSwipeCardNo(reqJson.getString("swipeCardNo"));

        //查询 业主是否有账户
        AccountDto accountDto = new AccountDto();
        accountDto.setLink(reqJson.getString("tel"));
        accountDto.setObjType(AccountDto.OBJ_TYPE_PERSON);
        accountDto.setPartId(reqJson.getString("communityId"));
        accountDto.setAcctType(reqJson.getString("acctType"));
        List<AccountDto> accountDtos = accountInnerServiceSMOImpl.queryAccounts(accountDto);
        if (ListUtil.isNull(accountDtos)) {
            accountDto = addAccountDto(reqJson, userDtos.get(0));
            //保存交易明细
            AccountDetailPo accountDetail = BeanConvertUtil.covertBean(accountDetailPo, AccountDetailPo.class);
            accountDetail.setOrderId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_orderId));
            accountDetail.setAcctId(accountDto.getAcctId());
            accountDetail.setObjType(AccountDetailDto.ORDER_TYPE_USER);
            accountDetail.setDetailType(AccountDetailDto.DETAIL_TYPE_IN);
            if (StringUtil.isEmpty(accountDetail.getDetailId())) {
                accountDetail.setDetailId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_detailId));
            }
            if (StringUtil.isEmpty(accountDetail.getRelAcctId())) {
                accountDetail.setRelAcctId("-1");
            }
            accountDetailInnerServiceSMOImpl.saveAccountDetails(accountDetail);
        } else {
            accountDto = accountDtos.get(0);
            accountDetailPo.setOrderId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_orderId));
            accountDetailPo.setAcctId(accountDto.getAcctId());
            accountDetailPo.setObjType(AccountDetailDto.ORDER_TYPE_USER);
            accountDetailPo.setObjId(accountDto.getObjId());

            int flag = accountInnerServiceSMOImpl.prestoreAccount(accountDetailPo);
            if (flag < 1) {
                throw new CmdException("预存失败");
            }
        }
        // todo 记录账户收款单


//        AccountReceiptPo accountReceiptPo = new AccountReceiptPo();
//        accountReceiptPo.setOwnerId(reqJson.getString("ownerId"));
//        accountReceiptPo.setOwnerName(ownerDtos.get(0).getName());
//        accountReceiptPo.setLink(ownerDtos.get(0).getLink());
//        accountReceiptPo.setArId(GenerateCodeFactory.getGeneratorId("11"));
//        accountReceiptPo.setAcctId(accountDto.getAcctId());
//        accountReceiptPo.setPrimeRate(reqJson.getString("primeRate"));
//        accountReceiptPo.setReceivableAmount(reqJson.getString("amount"));
//        accountReceiptPo.setReceivedAmount(reqJson.getString("amount"));
//        accountReceiptPo.setRemark(reqJson.getString("remark"));
//        accountReceiptPo.setCommunityId(reqJson.getString("communityId"));
//        accountReceiptV1InnerServiceSMOImpl.saveAccountReceipt(accountReceiptPo);

        context.setResponseEntity(ResultVo.success());
    }

    /**
     * 添加账户
     *
     * @param reqJson
     * @return
     */
    private AccountDto addAccountDto(JSONObject reqJson, UserDto userDto) {

        AccountPo accountPo = new AccountPo();
        accountPo.setAmount(reqJson.getString("amount"));
        accountPo.setAcctId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_acctId));
        accountPo.setObjId(userDto.getUserId());
        accountPo.setObjType(AccountDto.OBJ_TYPE_PERSON);
        accountPo.setAcctType(reqJson.getString("acctType"));
        accountPo.setAcctName(userDto.getName());
        accountPo.setPartId(reqJson.getString("communityId"));
        accountPo.setLink(userDto.getTel());
        accountPo.setSwipeCardNo(reqJson.getString("swipeCardNo"));
        accountInnerServiceSMOImpl.saveAccount(accountPo);
        return BeanConvertUtil.covertBean(accountPo, AccountDto.class);
    }

}
