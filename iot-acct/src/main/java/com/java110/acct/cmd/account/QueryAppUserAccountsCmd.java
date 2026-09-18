package com.java110.acct.cmd.account;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.PageDto;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.CmdContextUtils;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.account.AccountCommunityDto;
import com.java110.dto.account.AccountDto;
import com.java110.dto.community.CommunityDto;
import com.java110.dto.user.UserDto;
import com.java110.intf.acct.IAccountInnerServiceSMO;
import com.java110.intf.community.ICommunityV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 查询App 用户多个小区账户信息（按登录态手机号查询有余额的账户，不校验小区ID）
 */
@Java110Cmd(serviceCode = "account.queryAppUserAccounts")
public class QueryAppUserAccountsCmd extends Cmd {

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Autowired
    private IAccountInnerServiceSMO accountInnerServiceSMOImpl;

    @Autowired
    private ICommunityV1InnerServiceSMO communityV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        String userId = CmdContextUtils.getUserId(context);
        if (StringUtil.isEmpty(userId)) {
            throw new CmdException("用户未登录");
        }
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        String userId = CmdContextUtils.getUserId(context);

        UserDto userDto = new UserDto();
        userDto.setUserId(userId);
        List<UserDto> userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);
        if (ListUtil.isNull(userDtos)) {
            throw new CmdException("用户不存在");
        }

        String acctType = reqJson.getString("acctType");

        AccountDto accountDto = new AccountDto();
        accountDto.setLink(userDtos.get(0).getTel());
        accountDto.setAcctType(StringUtil.isEmpty(acctType) ? AccountDto.ACCT_TYPE_CASH : acctType);
        accountDto.setHasMoney("1");
        accountDto.setStatusCd("0");
        accountDto.setPage(PageDto.DEFAULT_PAGE);

        List<AccountCommunityDto> accountCommunityDtos = queryAccountCommunitys(accountDto);

        ResponseEntity<String> responseEntity = ResultVo.createResponseEntity(accountCommunityDtos.size(), accountCommunityDtos.size(), accountCommunityDtos);
        context.setResponseEntity(responseEntity);
    }

    /**
     * 按手机号查询有余额的账户（不限小区，不分页），并按小区去重、补小区名称
     *
     * @param accountDto 账户查询条件
     * @return 账户 + 小区信息
     */
    private List<AccountCommunityDto> queryAccountCommunitys(AccountDto accountDto) {

        if (StringUtil.isEmpty(accountDto.getLink())) {
            return new ArrayList<>();
        }

        List<AccountDto> accountDtos = accountInnerServiceSMOImpl.queryAccounts(accountDto);
        if (ListUtil.isNull(accountDtos)) {
            return new ArrayList<>();
        }

        // 同一小区可能存在多条账户，按 partId 去重（SQL 已按 create_time desc 排序，保留第一条）
        Map<String, AccountCommunityDto> communityMap = new LinkedHashMap<>();
        for (AccountDto tmpAccountDto : accountDtos) {
            String partId = tmpAccountDto.getPartId();
            if (StringUtil.isEmpty(partId) || communityMap.containsKey(partId)) {
                continue;
            }
            AccountCommunityDto accountCommunityDto = BeanConvertUtil.covertBean(tmpAccountDto, AccountCommunityDto.class);
            accountCommunityDto.setCommunityId(partId);
            communityMap.put(partId, accountCommunityDto);
        }

        if (communityMap.isEmpty()) {
            return new ArrayList<>();
        }

        refreshCommunityName(communityMap);

        return new ArrayList<>(communityMap.values());
    }

    /**
     * 批量查询小区名称并回填
     *
     * @param communityMap 小区ID -> 账户信息
     */
    private void refreshCommunityName(Map<String, AccountCommunityDto> communityMap) {

        CommunityDto communityDto = new CommunityDto();
        communityDto.setCommunityIds(communityMap.keySet().toArray(new String[communityMap.size()]));
        List<CommunityDto> communityDtos = communityV1InnerServiceSMOImpl.queryCommunitys(communityDto);
        if (ListUtil.isNull(communityDtos)) {
            return;
        }

        for (AccountCommunityDto accountCommunityDto : communityMap.values()) {
            for (CommunityDto tmpCommunityDto : communityDtos) {
                if (!accountCommunityDto.getCommunityId().equals(tmpCommunityDto.getCommunityId())) {
                    continue;
                }
                accountCommunityDto.setCommunityName(StringUtil.isEmpty(tmpCommunityDto.getName()) ? tmpCommunityDto.getCommunityName() : tmpCommunityDto.getName());
            }
        }
    }
}
