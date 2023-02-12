package com.java110.intf.user;

import com.java110.bean.dto.area.AreaDto;
import com.java110.intf.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * 查询地区信息
 */
@FeignClient(name = "iot-user", configuration = {FeignConfiguration.class})
@RequestMapping("/areaApi")
public interface IAreaInnerServiceSMO {

    /**
     * <p>查询地区</p>
     *
     * @return AreaDto 对象数据
     */
    @RequestMapping(value = "/getArea", method = RequestMethod.POST)
    public List<AreaDto> getArea(@RequestBody AreaDto areaDto);

    @RequestMapping(value = "/getProvCityArea", method = RequestMethod.POST)
    public List<AreaDto> getProvCityArea(@RequestBody AreaDto areaDto);

    @RequestMapping(value = "/getWholeArea", method = RequestMethod.POST)
    public List<AreaDto> getWholeArea(@RequestBody AreaDto areaDto);

}
