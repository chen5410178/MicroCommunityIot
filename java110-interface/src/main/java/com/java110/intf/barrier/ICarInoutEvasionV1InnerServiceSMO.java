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
package com.java110.intf.barrier;

import com.java110.dto.carInoutEvasion.CarInoutEvasionDto;
import com.java110.intf.FeignConfiguration;
import com.java110.po.carInoutEvasion.CarInoutEvasionPo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name = "iot-barrier", configuration = {FeignConfiguration.class})
@RequestMapping("/carInoutEvasionV1Api")
public interface ICarInoutEvasionV1InnerServiceSMO {

    @RequestMapping(value = "/saveCarInoutEvasion", method = RequestMethod.POST)
    int saveCarInoutEvasion(@RequestBody CarInoutEvasionPo carInoutEvasionPo);

    @RequestMapping(value = "/updateCarInoutEvasion", method = RequestMethod.POST)
    int updateCarInoutEvasion(@RequestBody CarInoutEvasionPo carInoutEvasionPo);

    @RequestMapping(value = "/deleteCarInoutEvasion", method = RequestMethod.POST)
    int deleteCarInoutEvasion(@RequestBody CarInoutEvasionPo carInoutEvasionPo);

    @RequestMapping(value = "/queryCarInoutEvasions", method = RequestMethod.POST)
    List<CarInoutEvasionDto> queryCarInoutEvasions(@RequestBody CarInoutEvasionDto carInoutEvasionDto);

    @RequestMapping(value = "/queryCarInoutEvasionsCount", method = RequestMethod.POST)
    int queryCarInoutEvasionsCount(@RequestBody CarInoutEvasionDto carInoutEvasionDto);
}
