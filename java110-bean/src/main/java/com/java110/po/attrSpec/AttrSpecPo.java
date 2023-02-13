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
package com.java110.po.attrSpec;

import java.io.Serializable;
import java.util.Date;
/**
 * 类表述： Po 数据模型实体对象 基本保持与数据库模型一直 用于 增加修改删除 等时的数据载体
 * add by 吴学文 at 2023-02-13 11:47:43 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
public class AttrSpecPo implements Serializable {

    private String specType;
private String specId;
private String specName;
private String specHoldplace;
private String specValueType;
private String domain;
private String specCd;
private String statusCd = "0";
private String specShow;
private String required;
private String tableName;
private String listShow;
public String getSpecType() {
        return specType;
    }
public void setSpecType(String specType) {
        this.specType = specType;
    }
public String getSpecId() {
        return specId;
    }
public void setSpecId(String specId) {
        this.specId = specId;
    }
public String getSpecName() {
        return specName;
    }
public void setSpecName(String specName) {
        this.specName = specName;
    }
public String getSpecHoldplace() {
        return specHoldplace;
    }
public void setSpecHoldplace(String specHoldplace) {
        this.specHoldplace = specHoldplace;
    }
public String getSpecValueType() {
        return specValueType;
    }
public void setSpecValueType(String specValueType) {
        this.specValueType = specValueType;
    }
public String getDomain() {
        return domain;
    }
public void setDomain(String domain) {
        this.domain = domain;
    }
public String getSpecCd() {
        return specCd;
    }
public void setSpecCd(String specCd) {
        this.specCd = specCd;
    }
public String getStatusCd() {
        return statusCd;
    }
public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }
public String getSpecShow() {
        return specShow;
    }
public void setSpecShow(String specShow) {
        this.specShow = specShow;
    }
public String getRequired() {
        return required;
    }
public void setRequired(String required) {
        this.required = required;
    }
public String getTableName() {
        return tableName;
    }
public void setTableName(String tableName) {
        this.tableName = tableName;
    }
public String getListShow() {
        return listShow;
    }
public void setListShow(String listShow) {
        this.listShow = listShow;
    }



}
