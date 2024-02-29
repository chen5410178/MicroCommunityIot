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
package com.java110.po.workLicensePos;

import java.io.Serializable;
import java.util.Date;
/**
 * 类表述： Po 数据模型实体对象 基本保持与数据库模型一直 用于 增加修改删除 等时的数据载体
 * add by 吴学文 at 2024-02-29 09:49:07 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
public class WorkLicensePosPo implements Serializable {

    private String machineId;
private String machineCode;
private String uploadDate;
private String wltId;
private String staffName;
private String lon;
private String statusCd = "0";
private String storeId;
private String staffId;
private String posType;
private String lat;
public String getMachineId() {
        return machineId;
    }
public void setMachineId(String machineId) {
        this.machineId = machineId;
    }
public String getMachineCode() {
        return machineCode;
    }
public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }
public String getUploadDate() {
        return uploadDate;
    }
public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }
public String getWltId() {
        return wltId;
    }
public void setWltId(String wltId) {
        this.wltId = wltId;
    }
public String getStaffName() {
        return staffName;
    }
public void setStaffName(String staffName) {
        this.staffName = staffName;
    }
public String getLon() {
        return lon;
    }
public void setLon(String lon) {
        this.lon = lon;
    }
public String getStatusCd() {
        return statusCd;
    }
public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }
public String getStoreId() {
        return storeId;
    }
public void setStoreId(String storeId) {
        this.storeId = storeId;
    }
public String getStaffId() {
        return staffId;
    }
public void setStaffId(String staffId) {
        this.staffId = staffId;
    }
public String getPosType() {
        return posType;
    }
public void setPosType(String posType) {
        this.posType = posType;
    }
public String getLat() {
        return lat;
    }
public void setLat(String lat) {
        this.lat = lat;
    }



}
