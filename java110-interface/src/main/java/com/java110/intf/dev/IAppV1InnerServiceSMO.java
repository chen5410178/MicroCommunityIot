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
package com.java110.intf.dev;

import com.java110.dto.app.AppDto;
import com.java110.intf.FeignConfiguration;
import com.java110.po.app.AppPo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * 类表述： 服务之前调用的接口类，不对外提供接口能力 只用于接口建调用
 * add by 吴学文 at 2023-02-11 00:59:31 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@FeignClient(name = "iot-dev", configuration = {FeignConfiguration.class})
@RequestMapping("/appV1Api")
public interface IAppV1InnerServiceSMO {


    @RequestMapping(value = "/saveApp", method = RequestMethod.POST)
    public int saveApp(@RequestBody  AppPo appPo);

    @RequestMapping(value = "/updateApp", method = RequestMethod.POST)
    public int updateApp(@RequestBody  AppPo appPo);

    @RequestMapping(value = "/deleteApp", method = RequestMethod.POST)
    public int deleteApp(@RequestBody  AppPo appPo);

    /**
     * <p>查询小区楼信息</p>
     *
     *
     * @param appDto 数据对象分享
     * @return AppDto 对象数据
     */
    @RequestMapping(value = "/queryApps", method = RequestMethod.POST)
    List<AppDto> queryApps(@RequestBody AppDto appDto);

    /**
     * 查询<p>小区楼</p>总记录数
     *
     * @param appDto 数据对象分享
     * @return 小区下的小区楼记录数
     */
    @RequestMapping(value = "/queryAppsCount", method = RequestMethod.POST)
    int queryAppsCount(@RequestBody AppDto appDto);

    @RequestMapping(value = "/queryAppsServiceData", method = RequestMethod.POST)
    List<AppDto> queryAppsServiceData(@RequestBody AppDto appDto);
}
