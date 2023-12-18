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
package com.java110.po.personFaceQrcode;

import java.io.Serializable;
import java.util.Date;
/**
 * 类表述： Po 数据模型实体对象 基本保持与数据库模型一直 用于 增加修改删除 等时的数据载体
 * add by 吴学文 at 2023-12-18 18:37:12 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
public class PersonFaceQrcodePo implements Serializable {

    private String statusCd = "0";
private String remark;
private String pfqId;
private String smsValidate;
private String telValidate;
private String createStaffName;
private String createStaffId;
private String audit;
private String inviteCode;
private String invite;
private String state;
private String qrcodeName;
private String communityId;
public String getStatusCd() {
        return statusCd;
    }
public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }
public String getRemark() {
        return remark;
    }
public void setRemark(String remark) {
        this.remark = remark;
    }
public String getPfqId() {
        return pfqId;
    }
public void setPfqId(String pfqId) {
        this.pfqId = pfqId;
    }
public String getSmsValidate() {
        return smsValidate;
    }
public void setSmsValidate(String smsValidate) {
        this.smsValidate = smsValidate;
    }
public String getTelValidate() {
        return telValidate;
    }
public void setTelValidate(String telValidate) {
        this.telValidate = telValidate;
    }
public String getCreateStaffName() {
        return createStaffName;
    }
public void setCreateStaffName(String createStaffName) {
        this.createStaffName = createStaffName;
    }
public String getCreateStaffId() {
        return createStaffId;
    }
public void setCreateStaffId(String createStaffId) {
        this.createStaffId = createStaffId;
    }
public String getAudit() {
        return audit;
    }
public void setAudit(String audit) {
        this.audit = audit;
    }
public String getInviteCode() {
        return inviteCode;
    }
public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }
public String getInvite() {
        return invite;
    }
public void setInvite(String invite) {
        this.invite = invite;
    }
public String getState() {
        return state;
    }
public void setState(String state) {
        this.state = state;
    }
public String getQrcodeName() {
        return qrcodeName;
    }
public void setQrcodeName(String qrcodeName) {
        this.qrcodeName = qrcodeName;
    }
public String getCommunityId() {
        return communityId;
    }
public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }



}
