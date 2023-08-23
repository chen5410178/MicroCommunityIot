package com.java110.acct.cmd.account;

import com.alibaba.fastjson.JSONObject;
import com.java110.acct.bmo.account.IGetAccountBMO;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.owner.OwnerDto;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.CommunitySettingFactory;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.DistributedLock;
import com.java110.core.utils.StringUtil;
import com.java110.dto.account.AccountDto;
import com.java110.dto.chargeMonthCard.ChargeMonthCardDto;
import com.java110.dto.user.UserDto;
import com.java110.intf.acct.IAccountInnerServiceSMO;
import com.java110.intf.user.IOwnerInnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import com.java110.po.account.AccountPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Java110Cmd(serviceCode = "account.queryOwnerAccount")
public class queryOwnerAccountCmd extends Cmd {

    @Autowired
    private IAccountInnerServiceSMO accountInnerServiceSMOImpl;

    @Autowired
    private IOwnerInnerServiceSMO ownerInnerServiceSMOImpl;

    @Autowired
    private IUserV1InnerServiceSMO userInnerServiceSMOImpl;

    //键(积分账户最大使用积分)
    public static final String MAXIMUM_NUMBER = "MAXIMUM_NUMBER";

    //键(积分账户抵扣比例)
    public static final String DEDUCTION_PROPORTION = "DEDUCTION_PROPORTION";

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        super.validatePageInfo(reqJson);
        Assert.hasKeyAndValue(reqJson, "acctId", "acctId不能为空");
        Assert.hasKeyAndValue(reqJson, "communityId", "communityId不能为空");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        AccountDto accountDto = BeanConvertUtil.covertBean(reqJson, AccountDto.class);
        accountDto.setPartId(reqJson.getString("communityId"));
        accountDto.setObjType(AccountDto.OBJ_TYPE_PERSON);
        OwnerDto ownerDto = BeanConvertUtil.covertBean(reqJson, OwnerDto.class);

        List<OwnerDto> ownerDtos = null;
        List<AccountDto> accountDtos = null;
        int count = 0;
        if (!StringUtil.isEmpty(ownerDto.getIdCard())) {
            //先查询业主
            ownerDtos = ownerInnerServiceSMOImpl.queryOwners(ownerDto);
            if (ownerDtos != null && ownerDtos.size() > 0) {
                accountDto.setAcctName("");
                accountDto.setObjId(ownerDtos.get(0).getMemberId());
            }
        }

        count = accountInnerServiceSMOImpl.queryAccountsCount(accountDto);
        if (count > 0) {
            accountDtos = accountInnerServiceSMOImpl.queryAccounts(accountDto);
        } else {
            accountDtos = new ArrayList<>();
        }


        if (accountDtos == null || accountDtos.size() < 1) {
            //添加 账户
            accountDtos = addAccountDto(accountDto, ownerDto);

        }
        //积分账户最大使用积分
        String maximumNumber = CommunitySettingFactory.getValue(ownerDto.getCommunityId(), MAXIMUM_NUMBER);
        //积分账户抵扣比例
        String deductionProportion = CommunitySettingFactory.getValue(ownerDto.getCommunityId(), DEDUCTION_PROPORTION);
        List<AccountDto> accountList = new ArrayList<>();
        for (AccountDto account : accountDtos) {
            if (!StringUtil.isEmpty(maximumNumber)) {
                account.setMaximumNumber(maximumNumber);
            }
            if (!StringUtil.isEmpty(deductionProportion)) {
                account.setDeductionProportion(deductionProportion);
            }
            accountList.add(account);
        }

        ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) accountDto.getRow()), count, accountList);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        context.setResponseEntity(responseEntity);
    }

    private List<AccountDto> addAccountDto(AccountDto accountDto, OwnerDto ownerDto) {
        // todo  查询账户名称 这里如果存在业主则业主名称 不是业主 则 填写用户名称，如果用户都没有 则返回空
        String acctName = getAccountName(ownerDto);

        if (StringUtil.isEmpty(acctName)) {
            return new ArrayList<>();
        }
        //开始锁代码
        String requestId = DistributedLock.getLockUUID();
        String key = this.getClass().getSimpleName() + "AddCountDto" + ownerDto.getOwnerId();
        try {
            DistributedLock.waitGetDistributedLock(key, requestId);

            AccountPo accountPo = new AccountPo();
            accountPo.setAmount("0");
            accountPo.setAcctId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_acctId));
            accountPo.setObjId(ownerDto.getOwnerId());
            accountPo.setObjType(AccountDto.OBJ_TYPE_PERSON);
            accountPo.setAcctType(AccountDto.ACCT_TYPE_CASH);
            accountPo.setAcctName(acctName);
            accountPo.setPartId(ownerDto.getCommunityId());
            accountPo.setLink(ownerDto.getLink());
            accountInnerServiceSMOImpl.saveAccount(accountPo);
            List<AccountDto> accountDtos = accountInnerServiceSMOImpl.queryAccounts(accountDto);
            return accountDtos;
        } finally {
            DistributedLock.releaseDistributedLock(requestId, key);
        }
    }

    private String getAccountName(OwnerDto ownerDto) {

        // todo  owner
        if (!StringUtil.isEmpty(ownerDto.getOwnerId())) {
            OwnerDto tmpOwnerDto = new OwnerDto();
            tmpOwnerDto.setMemberId(ownerDto.getOwnerId());
            tmpOwnerDto.setCommunityId(ownerDto.getCommunityId());
            List<OwnerDto> ownerDtos = ownerInnerServiceSMOImpl.queryOwners(tmpOwnerDto);
            if (ownerDtos == null || ownerDtos.size() < 1) {
                return "";
            }
            ownerDto.setCommunityId(ownerDtos.get(0).getCommunityId());
            ownerDto.setLink(ownerDtos.get(0).getLink());
            return ownerDtos.get(0).getName();
        }

        // todo 必须包含 手机号和小区
        if (StringUtil.isEmpty(ownerDto.getLink()) || StringUtil.isEmpty(ownerDto.getCommunityId())) {
            return "";
        }

        // todo 业主用 手机号查询
        OwnerDto tmpOwnerDto = new OwnerDto();
        tmpOwnerDto.setLink(ownerDto.getLink());
        tmpOwnerDto.setCommunityId(ownerDto.getCommunityId());
        List<OwnerDto> ownerDtos = ownerInnerServiceSMOImpl.queryOwners(tmpOwnerDto);
        if (ownerDtos != null && ownerDtos.size() > 0) {
            ownerDto.setOwnerId(ownerDtos.get(0).getMemberId());
            return ownerDtos.get(0).getName();
        }

        //todo 非业主是游客
        UserDto userDto = new UserDto();
        userDto.setTel(ownerDto.getLink());
        List<UserDto> userDtos = userInnerServiceSMOImpl.queryUsers(userDto);
        if (userDtos != null && userDtos.size() > 0) {
            ownerDto.setOwnerId("-1");
            return userDtos.get(0).getName();
        }
        return "";
    }
}
