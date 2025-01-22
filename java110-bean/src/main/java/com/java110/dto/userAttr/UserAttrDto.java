package com.java110.dto.userAttr;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName FloorDto
 * @Description 用户属性数据层封装
 * @Author wuxw
 * @Date 2019/4/24 8:52
 * @Version 1.0
 * add by wuxw 2019/4/24
 **/
public class UserAttrDto extends PageDto implements Serializable {

    public static final String SPEC_KEY = "100202061602";//用户临时key

    public static final String SPEC_OPEN_ID = "100201911001";//用户微信OPENID
    public static final String SPEC_UNION_ID = "100201911002";//用户微信UNIONID
    public static final String SPEC_MALL_OPEN_ID = "100201911003"; // 商城openId

    public static final String CHARGE_CAR = "100202501001";//充电车牌号

    public static final String SPEC_PROPERTY_USER_ID = "100202106001";//物业系统用户ID


    private String attrId;
    private String specCd;
    private String id;
    private String userId;
    private String value;


    private Date createTime;

    private String statusCd = "0";


    public String getAttrId() {
        return attrId;
    }

    public void setAttrId(String attrId) {
        this.attrId = attrId;
    }

    public String getSpecCd() {
        return specCd;
    }

    public void setSpecCd(String specCd) {
        this.specCd = specCd;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
