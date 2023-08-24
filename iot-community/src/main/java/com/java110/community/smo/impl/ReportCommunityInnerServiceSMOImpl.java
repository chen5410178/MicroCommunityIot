package com.java110.community.smo.impl;


import com.java110.bean.dto.room.RoomDto;
import com.java110.community.dao.IRoomsTreeServiceDao;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.intf.community.IRoomsTreeInnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ClassName ReportCommunityInnerServiceSMOImpl
 * @Description 业主缴费明细内部服务实现类
 * @Author wuxw
 * @Date 2019/4/24 9:20
 * @Version 1.0
 * add by wuxw 2019/4/24
 **/
@RestController
public class ReportCommunityInnerServiceSMOImpl implements IRoomsTreeInnerServiceSMO {

    @Autowired
    private IRoomsTreeServiceDao roomsTreeServiceDaoImpl;


    @Override
    public List<RoomDto> queryRoomsTree(@RequestBody RoomDto roomDto) {
        //校验是否传了 分页信息
        List<RoomDto> roomDtos = BeanConvertUtil.covertBeanList(roomsTreeServiceDaoImpl.queryRoomsTree(BeanConvertUtil.beanCovertMap(roomDto)), RoomDto.class);

        return roomDtos;
    }
}
