package com.java110.intf.community;

import com.java110.bean.dto.room.RoomDto;
import com.java110.intf.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * @ClassName IRoomsTreeInnerServiceSMO
 * @Description 业主缴费明细接口类
 * @Author wuxw
 * @Date 2019/4/24 9:04
 * @Version 1.0
 * add by wuxw 2019/4/24
 **/
@FeignClient(name = "iot-community", configuration = {FeignConfiguration.class})
@RequestMapping("/reportCommunityApi")
public interface IRoomsTreeInnerServiceSMO {

    /**
     * 查询房屋树形
     * @param roomDto
     * @return
     */
    @RequestMapping(value = "/queryRoomsTree", method = RequestMethod.POST)
    List<RoomDto> queryRoomsTree(@RequestBody RoomDto roomDto);
}
