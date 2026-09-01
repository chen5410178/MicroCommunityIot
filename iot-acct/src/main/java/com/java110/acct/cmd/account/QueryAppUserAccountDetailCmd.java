package com.java110.acct.cmd.account;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.CmdContextUtils;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.account.AccountDetailDto;
import com.java110.dto.account.AccountDto;
import com.java110.dto.user.UserDto;
import com.java110.intf.acct.IAccountDetailInnerServiceSMO;
import com.java110.intf.acct.IAccountInnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * 查询App用户账户交易明细（业主端专用，按登录态定位账户，杜绝越权）
 */
@Java110Cmd(serviceCode = "account.queryAppUserAccountDetail")
public class QueryAppUserAccountDetailCmd extends Cmd {

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Autowired
    private IAccountInnerServiceSMO accountInnerServiceSMOImpl;

    @Autowired
    private IAccountDetailInnerServiceSMO accountDetailInnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson, "communityId", "未包含小区ID");
        super.validatePageInfo(reqJson);
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        String userId = CmdContextUtils.getUserId(context);

        UserDto userDto = new UserDto();
        userDto.setUserId(userId);
        List<UserDto> userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);
        if (ListUtil.isNull(userDtos)) {
            throw new CmdException("参数错误");
        }
        String link = userDtos.get(0).getTel();

        AccountDto accountDto = new AccountDto();
        accountDto.setLink(link);
        accountDto.setPartId(reqJson.getString("communityId"));
        accountDto.setAcctType(StringUtil.isEmpty(reqJson.getString("acctType")) ? AccountDto.ACCT_TYPE_CASH : reqJson.getString("acctType"));

        List<AccountDto> accountDtos = accountInnerServiceSMOImpl.queryAccounts(accountDto);
        if (ListUtil.isNull(accountDtos)) {
            ResultVo resultVo = new ResultVo(0, 0, new ArrayList<>());
            context.setResponseEntity(new ResponseEntity<>(resultVo.toString(), HttpStatus.OK));
            return;
        }

        AccountDetailDto accountDetailDto = new AccountDetailDto();
        accountDetailDto.setAcctId(accountDtos.get(0).getAcctId());
        accountDetailDto.setPage(reqJson.getIntValue("page"));
        accountDetailDto.setRow(reqJson.getIntValue("row"));
        if (!StringUtil.isEmpty(reqJson.getString("detailType"))) {
            accountDetailDto.setDetailType(reqJson.getString("detailType"));
        }

        int count = accountDetailInnerServiceSMOImpl.queryAccountDetailsCount(accountDetailDto);
        List<AccountDetailDto> accountDetailDtos = new ArrayList<>();
        if (count > 0) {
            accountDetailDtos = accountDetailInnerServiceSMOImpl.queryAccountDetails(accountDetailDto);
        }

        ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) accountDetailDto.getRow()), count, accountDetailDtos);
        context.setResponseEntity(new ResponseEntity<>(resultVo.toString(), HttpStatus.OK));
    }
}
