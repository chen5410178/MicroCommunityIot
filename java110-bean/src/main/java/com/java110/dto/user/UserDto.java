package com.java110.dto.user;

import com.java110.bean.dto.PageDto;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName FloorDto
 * @Description 用户数据层封装
 * @Author wuxw
 * @Date 2019/4/24 8:52
 * @Version 1.0
 * add by wuxw 2019/4/24
 **/
public class UserDto extends PageDto implements Serializable {
    public static final String LEVEL_CD_MALL = "03";//商城用户
    public static final String LEVEL_CD_ADMIN = "00";//系统管理员
    public static final String LEVEL_CD_STAFF = "01";//员工
    public static final String LEVEL_CD_USER = "02";//普通用户
    private String password;
    private String address;
    private String sex;
    private String name;
    private String tel;
    private String userId;
    private String levelCd;
    private String[] levelCds;
    private String email;


    private Date createTime;

    private String statusCd = "0";


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLevelCd() {
        return levelCd;
    }

    public void setLevelCd(String levelCd) {
        this.levelCd = levelCd;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String[] getLevelCds() {
        return levelCds;
    }

    public void setLevelCds(String[] levelCds) {
        this.levelCds = levelCds;
    }
}
