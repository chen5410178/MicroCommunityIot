package com.java110.user.smo.impl;


import com.java110.bean.dto.PageDto;
import com.java110.bean.dto.org.OrgCommunityDto;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.intf.user.IOrgCommunityInnerServiceSMO;
import com.java110.user.dao.IOrgCommunityServiceDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ClassName FloorInnerServiceSMOImpl
 * @Description 隶属小区内部服务实现类
 * @Author wuxw
 * @Date 2019/4/24 9:20
 * @Version 1.0
 * add by wuxw 2019/4/24
 **/
@RestController
public class OrgCommunityInnerServiceSMOImpl  implements IOrgCommunityInnerServiceSMO {

    @Autowired
    private IOrgCommunityServiceDao orgCommunityServiceDaoImpl;



    @Override
    public List<OrgCommunityDto> queryOrgCommunitys(@RequestBody OrgCommunityDto orgCommunityDto) {

        //校验是否传了 分页信息

        int page = orgCommunityDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            orgCommunityDto.setPage((page - 1) * orgCommunityDto.getRow());
        }

        List<OrgCommunityDto> orgCommunitys = BeanConvertUtil.covertBeanList(orgCommunityServiceDaoImpl.getOrgCommunityInfo(BeanConvertUtil.beanCovertMap(orgCommunityDto)), OrgCommunityDto.class);

        return orgCommunitys;
    }


    @Override
    public int queryOrgCommunitysCount(@RequestBody OrgCommunityDto orgCommunityDto) {
        return orgCommunityServiceDaoImpl.queryOrgCommunitysCount(BeanConvertUtil.beanCovertMap(orgCommunityDto));
    }
}
