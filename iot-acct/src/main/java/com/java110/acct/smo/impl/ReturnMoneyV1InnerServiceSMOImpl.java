/*
 * Copyright 2017-2020 吴学文 and java110 team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.java110.acct.smo.impl;


import com.java110.acct.payment.IRefundMoneyAdapt;
import com.java110.bean.ResultVo;
import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.onlinePay.OnlinePayDto;
import com.java110.dto.onlinePay.OnlinePayRefundDto;
import com.java110.dto.paymentPool.PaymentPoolDto;
import com.java110.intf.acct.IOnlinePayV1InnerServiceSMO;
import com.java110.intf.acct.IPaymentPoolV1InnerServiceSMO;
import com.java110.intf.acct.IReturnMoneyV1InnerServiceSMO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 类表述： 服务之前调用的接口实现类，不对外提供接口能力 只用于接口建调用
 * add by 吴学文 at 2021-12-21 13:05:25 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@RestController
public class ReturnMoneyV1InnerServiceSMOImpl implements IReturnMoneyV1InnerServiceSMO {

    private static final Logger logger = LoggerFactory.getLogger(ReturnMoneyV1InnerServiceSMOImpl.class);

    private static final String DEFAULT_PAYMENT_NOTIFY_ADAPT = "wechatPaymentFactory";// 默认微信通用支付

    protected static final String DEFAULT_NATIVE_QRCODE_PAYMENT_ADAPT = "wechatNativeQrcodePaymentFactory";// 默认微信通用支付

    @Autowired
    private IPaymentPoolV1InnerServiceSMO paymentPoolV1InnerServiceSMOImpl;

    @Autowired
    private IOnlinePayV1InnerServiceSMO onlinePayV1InnerServiceSMOImpl;


    @Override
    public ResultVo returnMoney(@RequestBody OnlinePayRefundDto onlinePayRefundDto) {

        Assert.hasLength(onlinePayRefundDto.getRefundId(), "未包含退款单号");
        Assert.hasLength(onlinePayRefundDto.getPayId(), "未包含支付记录ID");

        OnlinePayDto onlinePayDto = queryOnlinePay(onlinePayRefundDto.getPayId());

        PaymentPoolDto paymentPoolDto = new PaymentPoolDto();
        paymentPoolDto.setPpId(onlinePayDto.getPaymentPoolId());
        List<PaymentPoolDto> paymentPoolDtos = paymentPoolV1InnerServiceSMOImpl.queryPaymentPools(paymentPoolDto);

        Assert.listOnlyOne(paymentPoolDtos, "支付厂家不存在");

        if (StringUtil.isEmpty(paymentPoolDtos.get(0).getBeanRefund())) {
            throw new IllegalArgumentException("厂家不支持退款");
        }

        IRefundMoneyAdapt refundMoneyAdapt = ApplicationContextFactory.getBean(paymentPoolDtos.get(0).getBeanRefund(), IRefundMoneyAdapt.class);
        if (refundMoneyAdapt == null) {
            throw new IllegalArgumentException("厂家退款适配器配置错误" + paymentPoolDtos.get(0).getBeanRefund());
        }

        ResultVo resultVo = null;
        try {
            resultVo = refundMoneyAdapt.refund(onlinePayDto, onlinePayRefundDto, paymentPoolDtos.get(0));
            logger.info("退款单{} 支付记录{} 退款金额{} 退款结果{}", onlinePayRefundDto.getRefundId(), onlinePayDto.getPayId(),
                    onlinePayRefundDto.getRefundFee(), resultVo == null ? "" : resultVo.getMsg());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("退款单" + onlinePayRefundDto.getRefundId() + " 调用退款接口异常", e);
            resultVo = new ResultVo(ResultVo.CODE_ERROR, e.getLocalizedMessage());
        }
        return resultVo;
    }

    /**
     * 查询原支付记录（退款金额取本次退款单，原订单金额取支付记录）
     *
     * @param payId 支付记录ID
     * @return 支付记录
     */
    private OnlinePayDto queryOnlinePay(String payId) {
        OnlinePayDto queryDto = new OnlinePayDto();
        queryDto.setPayId(payId);
        List<OnlinePayDto> onlinePayDtos = onlinePayV1InnerServiceSMOImpl.queryOnlinePays(queryDto);
        if (ListUtil.isNull(onlinePayDtos)) {
            throw new IllegalArgumentException("支付记录不存在");
        }
        return onlinePayDtos.get(0);
    }

}
