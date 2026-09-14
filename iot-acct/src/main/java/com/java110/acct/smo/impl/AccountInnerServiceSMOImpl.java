package com.java110.acct.smo.impl;


import com.java110.acct.dao.IAccountDetailServiceDao;
import com.java110.acct.dao.IAccountServiceDao;
import com.java110.bean.dto.PageDto;
import com.java110.core.annotation.Java110Transactional;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.DistributedLock;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.account.AccountDetailDto;
import com.java110.dto.account.AccountDto;
import com.java110.dto.user.UserDto;
import com.java110.intf.acct.IAccountInnerServiceSMO;
import com.java110.po.account.AccountPo;
import com.java110.po.accountDetail.AccountDetailPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName FloorInnerServiceSMOImpl
 * @Description 账户内部服务实现类
 * @Author wuxw
 * @Date 2019/4/24 9:20
 * @Version 1.0
 * add by wuxw 2019/4/24
 **/
@RestController
public class AccountInnerServiceSMOImpl implements IAccountInnerServiceSMO {

    @Autowired
    private IAccountServiceDao accountServiceDaoImpl;

    @Autowired
    private IAccountDetailServiceDao accountDetailServiceDaoImpl;


    @Override
    public List<AccountDto> queryAccounts(@RequestBody AccountDto accountDto) {

        //校验是否传了 分页信息

        int page = accountDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            accountDto.setPage((page - 1) * accountDto.getRow());
        }

        List<AccountDto> accounts = BeanConvertUtil.covertBeanList(accountServiceDaoImpl.getAccountInfo(BeanConvertUtil.beanCovertMap(accountDto)), AccountDto.class);


        return accounts;
    }

    /**
     * 从用户列表中查询用户，将用户中的信息 刷新到 floor对象中
     *
     * @param account 小区账户信息
     * @param users   用户列表
     */
    private void refreshAccount(AccountDto account, List<UserDto> users) {
        for (UserDto user : users) {
            if (account.getAcctId().equals(user.getUserId())) {
                BeanConvertUtil.covertBean(user, account);
            }
        }
    }

    /**
     * 获取批量userId
     *
     * @param accounts 小区楼信息
     * @return 批量userIds 信息
     */
    private String[] getUserIds(List<AccountDto> accounts) {
        List<String> userIds = new ArrayList<String>();
        for (AccountDto account : accounts) {
            userIds.add(account.getAcctId());
        }

        return userIds.toArray(new String[userIds.size()]);
    }

    @Override
    public int queryAccountsCount(@RequestBody AccountDto accountDto) {
        return accountServiceDaoImpl.queryAccountsCount(BeanConvertUtil.beanCovertMap(accountDto));
    }

    @Override
    @Java110Transactional
    public int updateAccount(@RequestBody AccountPo accountPo) {
        return accountServiceDaoImpl.updateAccount(BeanConvertUtil.beanCovertMap(accountPo));
    }

    @Override
    public int saveAccount(@RequestBody AccountPo accountPo) {
        //todo 查询账户是否已经存在
        AccountDto accountDto = new AccountDto();
        accountDto.setObjId(accountPo.getObjId());
        accountDto.setPartId(accountPo.getPartId());
        accountDto.setLink(accountPo.getLink());
        int flag = accountServiceDaoImpl.queryAccountsCount(BeanConvertUtil.beanCovertMap(accountDto));
        if (flag > 0) {
            return flag;
        }
        //todo 保存
        accountServiceDaoImpl.saveAccount(BeanConvertUtil.beanCovertMap(accountPo));
        return 1;
    }

    /**
     * 预存接口
     *
     * @param accountDetailPo
     * @return
     */
    @Override
    @Java110Transactional
    public int prestoreAccount(@RequestBody AccountDetailPo accountDetailPo) {

        if (StringUtil.isEmpty(accountDetailPo.getAcctId())) {
            throw new IllegalArgumentException("账户ID为空");
        }
        //开始枷锁
        String requestId = DistributedLock.getLockUUID();
        String key = accountDetailPo.getAcctId();
        AccountDto accountDto = null;
        List<AccountDto> accounts = null;
        int flag;
        try {
            DistributedLock.waitGetDistributedLock(key, requestId);
            accountDto = new AccountDto();
            accountDto.setObjId(accountDetailPo.getObjId());
            accountDto.setAcctId(accountDetailPo.getAcctId());
            accounts = BeanConvertUtil.covertBeanList(accountServiceDaoImpl.getAccountInfo(BeanConvertUtil.beanCovertMap(accountDto)), AccountDto.class);
            if (ListUtil.isNull(accounts)) {
                throw new IllegalArgumentException("账户不存在");
            }
            //在账户增加
            BigDecimal amount = toDecimalAmount(accounts.get(0).getAmount()).add(toDecimalAmount(accountDetailPo.getAmount()));
            AccountPo accountPo = new AccountPo();
            accountPo.setObjId(accountDetailPo.getObjId());
            accountPo.setAcctId(accountDetailPo.getAcctId());
            accountPo.setAmount(amount.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            accountPo.setSwipeCardNo(accountDetailPo.getSwipeCardNo());
            flag = accountServiceDaoImpl.updateAccount(BeanConvertUtil.beanCovertMap(accountPo));
            if (flag < 1) {
                throw new IllegalArgumentException("更新账户失败");
            }
        } finally {
            DistributedLock.releaseDistributedLock(key, requestId);
        }
        accountDetailPo.setDetailType(AccountDetailDto.DETAIL_TYPE_IN);
        if (StringUtil.isEmpty(accountDetailPo.getDetailId())) {
            accountDetailPo.setDetailId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_detailId));
        }
        if (StringUtil.isEmpty(accountDetailPo.getOrderId())) {
            accountDetailPo.setOrderId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_orderId));
        }
        if (StringUtil.isEmpty(accountDetailPo.getRelAcctId())) {
            accountDetailPo.setRelAcctId("-1");
        }

        //保存交易明细
        return accountDetailServiceDaoImpl.saveAccountDetails(BeanConvertUtil.beanCovertMap(accountDetailPo));
    }

    /**
     * 扣款接口
     *
     * @param accountDetailPo
     * @return
     */
    @Override
    @Java110Transactional
    public int withholdAccount(@RequestBody AccountDetailPo accountDetailPo) {
        return subtractAccount(accountDetailPo, AccountDetailDto.DETAIL_TYPE_OUT);
    }

    /**
     * 余额退款扣款接口（明细类型 3003，便于前端识别为退款）
     *
     * @param accountDetailPo
     * @return
     */
    @Override
    @Java110Transactional
    public int refundAccount(@RequestBody AccountDetailPo accountDetailPo) {
        return subtractAccount(accountDetailPo, AccountDetailDto.DETAIL_TYPE_IN_CANCEL);
    }

    /**
     * 账户扣减公共方法
     *
     * @param accountDetailPo 扣款信息
     * @param detailType      明细类型
     * @return
     */
    private int subtractAccount(AccountDetailPo accountDetailPo, String detailType) {

        if (StringUtil.isEmpty(accountDetailPo.getAcctId())) {
            throw new IllegalArgumentException("账户ID为空");
        }
        //开始枷锁
        String requestId = DistributedLock.getLockUUID();
        String key = accountDetailPo.getAcctId();
        AccountDto accountDto = null;
        List<AccountDto> accounts = null;
        int flag;
        try {
            DistributedLock.waitGetDistributedLock(key, requestId);
            accountDto = new AccountDto();
            accountDto.setObjId(accountDetailPo.getObjId());
            accountDto.setAcctId(accountDetailPo.getAcctId());
            accounts = BeanConvertUtil.covertBeanList(accountServiceDaoImpl.getAccountInfo(BeanConvertUtil.beanCovertMap(accountDto)), AccountDto.class);
            if (accounts == null || accounts.size() < 1) {
                throw new IllegalArgumentException("账户不存在");
            }
            //在账户减少
            BigDecimal amount = toDecimalAmount(accounts.get(0).getAmount()).subtract(toDecimalAmount(accountDetailPo.getAmount()));
            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("余额不足");
            }
            AccountPo accountPo = new AccountPo();
            accountPo.setObjId(accountDetailPo.getObjId());
            accountPo.setAcctId(accountDetailPo.getAcctId());
            accountPo.setAmount(amount.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            flag = accountServiceDaoImpl.updateAccount(BeanConvertUtil.beanCovertMap(accountPo));
            if (flag < 1) {
                throw new IllegalArgumentException("更新账户失败");
            }
            accountDetailPo.setObjType(accounts.get(0).getObjType());
        } finally {
            DistributedLock.releaseDistributedLock(key, requestId);
        }
        accountDetailPo.setDetailType(detailType);
        if (StringUtil.isEmpty(accountDetailPo.getDetailId())) {
            accountDetailPo.setDetailId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_detailId));
        }
        if (StringUtil.isEmpty(accountDetailPo.getOrderId())) {
            accountDetailPo.setOrderId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_orderId));
        }
        if (StringUtil.isEmpty(accountDetailPo.getRelAcctId())) {
            accountDetailPo.setRelAcctId("-1");
        }
        //保存交易明细
        return accountDetailServiceDaoImpl.saveAccountDetails(BeanConvertUtil.beanCovertMap(accountDetailPo));
    }

    /**
     * 金额字符串转 BigDecimal，账户记账统一使用 BigDecimal，避免 double 运算产生浮点尾差
     *
     * @param amount 金额字符串
     * @return 金额
     */
    private BigDecimal toDecimalAmount(String amount) {
        if (StringUtil.isEmpty(amount)) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(amount.trim());
        } catch (Exception e) {
            throw new IllegalArgumentException("金额格式错误：" + amount);
        }
    }

    public IAccountServiceDao getAccountServiceDaoImpl() {
        return accountServiceDaoImpl;
    }

    public void setAccountServiceDaoImpl(IAccountServiceDao accountServiceDaoImpl) {
        this.accountServiceDaoImpl = accountServiceDaoImpl;
    }


}
