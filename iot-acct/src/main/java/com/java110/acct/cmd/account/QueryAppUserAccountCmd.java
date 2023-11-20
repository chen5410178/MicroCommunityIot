package com.java110.acct.cmd.account;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.*;
import com.java110.dto.account.AccountDto;
import com.java110.dto.user.UserDto;
import com.java110.intf.acct.IAccountInnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import com.java110.po.account.AccountPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * 查询App 用户账户信息
 */
@Java110Cmd(serviceCode = "account.queryAppUserAccount")
public class QueryAppUserAccountCmd extends Cmd {

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Autowired
    private IAccountInnerServiceSMO accountInnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        Assert.hasKeyAndValue(reqJson, "communityId", "未包含小区ID");
        Assert.hasKeyAndValue(reqJson, "link", "未包含手机号");

        String userId = CmdContextUtils.getUserId(context);


        UserDto userDto = new UserDto();
        userDto.setUserId(userId);
        userDto.setTel(reqJson.getString("link"));
        List<UserDto> userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);

        if (ListUtil.isNull(userDtos)) {
            throw new CmdException("参数错误");
        }

    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        String userId = CmdContextUtils.getUserId(context);

        AccountDto accountDto = new AccountDto();
        accountDto.setLink(reqJson.getString("link"));
        accountDto.setPartId(reqJson.getString("communityId"));

        int count = accountInnerServiceSMOImpl.queryAccountsCount(accountDto);
        List<AccountDto> accountDtos = null;
        if (count > 0) {
            accountDtos = accountInnerServiceSMOImpl.queryAccounts(accountDto);
        } else {
            accountDtos = new ArrayList<>();
        }


        if (ListUtil.isNull(accountDtos)) {
            //todo 添加 账户
            accountDtos = addAccountDto(accountDto,userId);
        }

        ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) accountDto.getRow()), count, accountDtos);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        context.setResponseEntity(responseEntity);
    }

    private List<AccountDto> addAccountDto(AccountDto accountDto,String userId) {

        // todo  查询账户名称 这里如果存在业主则业主名称 不是业主 则 填写用户名称，如果用户都没有 则返回空
        UserDto userDto = new UserDto();
        userDto.setUserId(userId);
        List<UserDto> userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);
        String acctName = userDtos.get(0).getName();

        if (StringUtil.isEmpty(acctName)) {
            return new ArrayList<>();
        }
        //开始锁代码
        String requestId = DistributedLock.getLockUUID();
        String key = this.getClass().getSimpleName() + "AddCountDto" + userId;
        try {
            DistributedLock.waitGetDistributedLock(key, requestId);
            AccountPo accountPo = new AccountPo();
            accountPo.setAmount("0");
            accountPo.setAcctId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_acctId));
            accountPo.setObjId(userId);
            accountPo.setObjType(AccountDto.OBJ_TYPE_PERSON);
            accountPo.setAcctType(AccountDto.ACCT_TYPE_CASH);
            accountPo.setAcctName(acctName);
            accountPo.setPartId(accountDto.getPartId());
            accountPo.setLink(accountDto.getLink());
            accountInnerServiceSMOImpl.saveAccount(accountPo);
            List<AccountDto> accountDtos = accountInnerServiceSMOImpl.queryAccounts(accountDto);
            return accountDtos;
        } finally {
            DistributedLock.releaseDistributedLock(requestId, key);
        }

    }


}
