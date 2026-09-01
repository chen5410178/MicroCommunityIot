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
package com.java110.intf.system;

import com.java110.bean.dto.registerProtocol.RegisterProtocolDto;
import com.java110.bean.po.registerProtocol.RegisterProtocolPo;
import com.java110.intf.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * 类表述： 注册协议内部服务调用接口类，不对外提供接口能力 只用于接口间调用
 * add by java110 team at 2026-09-01
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@FeignClient(name = "iot-system", configuration = {FeignConfiguration.class})
@RequestMapping("/registerProtocolV1Api")
public interface IRegisterProtocolV1InnerServiceSMO {

    @RequestMapping(value = "/saveRegisterProtocol", method = RequestMethod.POST)
    int saveRegisterProtocol(@RequestBody RegisterProtocolPo registerProtocolPo);

    @RequestMapping(value = "/updateRegisterProtocol", method = RequestMethod.POST)
    int updateRegisterProtocol(@RequestBody RegisterProtocolPo registerProtocolPo);

    /**
     * <p>查询注册协议信息</p>
     *
     * @param registerProtocolDto 数据对象分享
     * @return RegisterProtocolDto 对象数据
     */
    @RequestMapping(value = "/queryRegisterProtocols", method = RequestMethod.POST)
    List<RegisterProtocolDto> queryRegisterProtocols(@RequestBody RegisterProtocolDto registerProtocolDto);

    /**
     * 查询注册协议总记录数
     *
     * @param registerProtocolDto 数据对象分享
     * @return 注册协议记录数
     */
    @RequestMapping(value = "/queryRegisterProtocolsCount", method = RequestMethod.POST)
    int queryRegisterProtocolsCount(@RequestBody RegisterProtocolDto registerProtocolDto);
}
