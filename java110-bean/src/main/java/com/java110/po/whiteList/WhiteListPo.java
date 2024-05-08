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
package com.java110.po.whiteList;

import java.io.Serializable;
import java.util.Date;
/**
 * 类表述： Po 数据模型实体对象 基本保持与数据库模型一直 用于 增加修改删除 等时的数据载体
 * add by 吴学文 at 2024-05-08 11:27:27 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
public class WhiteListPo implements Serializable {

    private String tel1;
private String tel2;
private String machineId;
private String statusCd = "0";
private String id;
private String tel3;
private String tel4;
public String getTel1() {
        return tel1;
    }
public void setTel1(String tel1) {
        this.tel1 = tel1;
    }
public String getTel2() {
        return tel2;
    }
public void setTel2(String tel2) {
        this.tel2 = tel2;
    }
public String getMachineId() {
        return machineId;
    }
public void setMachineId(String machineId) {
        this.machineId = machineId;
    }
public String getStatusCd() {
        return statusCd;
    }
public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }
public String getId() {
        return id;
    }
public void setId(String id) {
        this.id = id;
    }
public String getTel3() {
        return tel3;
    }
public void setTel3(String tel3) {
        this.tel3 = tel3;
    }
public String getTel4() {
        return tel4;
    }
public void setTel4(String tel4) {
        this.tel4 = tel4;
    }



}
