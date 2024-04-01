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
package com.java110.po.invoiceDetail;

import java.io.Serializable;
import java.util.Date;
/**
 * 类表述： Po 数据模型实体对象 基本保持与数据库模型一直 用于 增加修改删除 等时的数据载体
 * add by 吴学文 at 2024-03-29 17:51:54 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
public class InvoiceDetailPo implements Serializable {

    private String taxRate;
private String unit;
private String price;
private String invId;
private String num;
private String withTaxFlag;
private String detailId;
private String tax;
private String statusCd = "0";
private String goodsCode;
private String goodsName;
public String getTaxRate() {
        return taxRate;
    }
public void setTaxRate(String taxRate) {
        this.taxRate = taxRate;
    }
public String getUnit() {
        return unit;
    }
public void setUnit(String unit) {
        this.unit = unit;
    }
public String getPrice() {
        return price;
    }
public void setPrice(String price) {
        this.price = price;
    }
public String getInvId() {
        return invId;
    }
public void setInvId(String invId) {
        this.invId = invId;
    }
public String getNum() {
        return num;
    }
public void setNum(String num) {
        this.num = num;
    }
public String getWithTaxFlag() {
        return withTaxFlag;
    }
public void setWithTaxFlag(String withTaxFlag) {
        this.withTaxFlag = withTaxFlag;
    }
public String getDetailId() {
        return detailId;
    }
public void setDetailId(String detailId) {
        this.detailId = detailId;
    }
public String getTax() {
        return tax;
    }
public void setTax(String tax) {
        this.tax = tax;
    }
public String getStatusCd() {
        return statusCd;
    }
public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }
public String getGoodsCode() {
        return goodsCode;
    }
public void setGoodsCode(String goodsCode) {
        this.goodsCode = goodsCode;
    }
public String getGoodsName() {
        return goodsName;
    }
public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }



}
