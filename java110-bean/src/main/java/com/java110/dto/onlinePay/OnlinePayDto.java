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
package com.java110.dto.onlinePay;


import com.java110.bean.dto.PageDto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 类表述： 在线支付订单数据传输对象
 * add by 吴学文 at 2022-11-21 12:08:05 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
public class OnlinePayDto extends PageDto implements Serializable {

    public static final String STATE_WT = "W";
    public static final String STATE_FT = "FT";
    public static final String STATE_C = "C";
    private String payId;
    private String orderId;
    private String transactionId;
    private String busiId;
    private BigDecimal totalFee;
    private BigDecimal remainingFee;
    private String payName;
    private String openId;
    private String state;
    private String message;
    private Date createTime;
    private String statusCd;
    private String paymentPoolId;
    private String appId;
    // 只查询剩余可退金额大于0的记录，1 标识是
    private String remainingFeeMoreThanZero;

    public String getPayId() {
        return payId;
    }

    public void setPayId(String payId) {
        this.payId = payId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getBusiId() {
        return busiId;
    }

    public void setBusiId(String busiId) {
        this.busiId = busiId;
    }

    public BigDecimal getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(BigDecimal totalFee) {
        this.totalFee = totalFee;
    }

    public BigDecimal getRemainingFee() {
        return remainingFee;
    }

    public void setRemainingFee(BigDecimal remainingFee) {
        this.remainingFee = remainingFee;
    }

    public String getPayName() {
        return payName;
    }

    public void setPayName(String payName) {
        this.payName = payName;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }

    public String getPaymentPoolId() {
        return paymentPoolId;
    }

    public void setPaymentPoolId(String paymentPoolId) {
        this.paymentPoolId = paymentPoolId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getRemainingFeeMoreThanZero() {
        return remainingFeeMoreThanZero;
    }

    public void setRemainingFeeMoreThanZero(String remainingFeeMoreThanZero) {
        this.remainingFeeMoreThanZero = remainingFeeMoreThanZero;
    }
}