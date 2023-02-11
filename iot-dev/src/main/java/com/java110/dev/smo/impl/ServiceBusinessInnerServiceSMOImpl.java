package com.java110.dev.smo.impl;


import com.java110.bean.dto.PageDto;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dev.dao.IServiceBusinessServiceDao;
import com.java110.dto.route.ServiceBusinessDto;
import com.java110.intf.dev.IServiceBusinessInnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ClassName FloorInnerServiceSMOImpl
 * @Description 服务实现内部服务实现类
 * @Author wuxw
 * @Date 2019/4/24 9:20
 * @Version 1.0
 * add by wuxw 2019/4/24
 **/
@RestController
public class ServiceBusinessInnerServiceSMOImpl  implements IServiceBusinessInnerServiceSMO {

    @Autowired
    private IServiceBusinessServiceDao serviceBusinessServiceDaoImpl;

    @Autowired
    private IUserV1InnerServiceSMO userInnerServiceSMOImpl;

    @Override
    public List<ServiceBusinessDto> queryServiceBusinesss(@RequestBody  ServiceBusinessDto serviceBusinessDto) {

        //校验是否传了 分页信息

        int page = serviceBusinessDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            serviceBusinessDto.setPage((page - 1) * serviceBusinessDto.getRow());
        }

        List<ServiceBusinessDto> serviceBusinesss = BeanConvertUtil.covertBeanList(serviceBusinessServiceDaoImpl.getServiceBusinessInfo(BeanConvertUtil.beanCovertMap(serviceBusinessDto)), ServiceBusinessDto.class);


        return serviceBusinesss;
    }





    @Override
    public int queryServiceBusinesssCount(@RequestBody ServiceBusinessDto serviceBusinessDto) {
        return serviceBusinessServiceDaoImpl.queryServiceBusinesssCount(BeanConvertUtil.beanCovertMap(serviceBusinessDto));    }

    @Override
    public int saveServiceBusiness(@RequestBody ServiceBusinessDto serviceBusinessDto) {
        return serviceBusinessServiceDaoImpl.saveServiceBusiness(BeanConvertUtil.beanCovertMap(serviceBusinessDto));
    }

    @Override
    public int updateServiceBusiness(@RequestBody ServiceBusinessDto serviceBusinessDto) {
        return serviceBusinessServiceDaoImpl.updateServiceBusiness(BeanConvertUtil.beanCovertMap(serviceBusinessDto));
    }

    @Override
    public int deleteServiceBusiness(@RequestBody ServiceBusinessDto serviceBusinessDto) {
        return serviceBusinessServiceDaoImpl.deleteServiceBusiness(BeanConvertUtil.beanCovertMap(serviceBusinessDto));
    }

    public IServiceBusinessServiceDao getServiceBusinessServiceDaoImpl() {
        return serviceBusinessServiceDaoImpl;
    }

    public void setServiceBusinessServiceDaoImpl(IServiceBusinessServiceDao serviceBusinessServiceDaoImpl) {
        this.serviceBusinessServiceDaoImpl = serviceBusinessServiceDaoImpl;
    }

}
