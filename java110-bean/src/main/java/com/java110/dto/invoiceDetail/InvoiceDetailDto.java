package com.java110.dto.invoiceDetail;

import com.java110.dto.PageDto;
import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName FloorDto
 * @Description 发票明细数据层封装
 * @Author wuxw
 * @Date 2019/4/24 8:52
 * @Version 1.0
 * add by wuxw 2019/4/24
 **/
public class InvoiceDetailDto extends PageDto implements Serializable {

    private String taxRate;
private String unit;
private String price;
private String invId;
private String num;
private String withTaxFlag;
private String detailId;
private String tax;
private String goodsCode;
private String goodsName;


    private Date createTime;

    private String statusCd = "0";


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
}
