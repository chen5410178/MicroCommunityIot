package com.java110.car.smo.impl;

import com.java110.bean.dto.PageDto;
import com.java110.bean.dto.car.UnlicensedCarPlateDto;
import com.java110.car.dao.IUnlicensedCarPlateV1ServiceDao;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.intf.car.IUnlicensedCarPlateV1InnerServiceSMO;
import com.java110.po.car.UnlicensedCarPlatePo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UnlicensedCarPlateV1InnerServiceSMOImpl implements IUnlicensedCarPlateV1InnerServiceSMO {

    @Autowired
    private IUnlicensedCarPlateV1ServiceDao unlicensedCarPlateV1ServiceDaoImpl;

    @Override
    public int saveUnlicensedCarPlate(@RequestBody UnlicensedCarPlatePo unlicensedCarPlatePo) {
        return unlicensedCarPlateV1ServiceDaoImpl.saveUnlicensedCarPlateInfo(BeanConvertUtil.beanCovertMap(unlicensedCarPlatePo));
    }

    @Override
    public List<UnlicensedCarPlateDto> queryUnlicensedCarPlates(@RequestBody UnlicensedCarPlateDto unlicensedCarPlateDto) {
        int page = unlicensedCarPlateDto.getPage();
        if (page != PageDto.DEFAULT_PAGE) {
            unlicensedCarPlateDto.setPage((page - 1) * unlicensedCarPlateDto.getRow());
        }
        return BeanConvertUtil.covertBeanList(
                unlicensedCarPlateV1ServiceDaoImpl.getUnlicensedCarPlateInfo(BeanConvertUtil.beanCovertMap(unlicensedCarPlateDto)),
                UnlicensedCarPlateDto.class);
    }

    @Override
    public int getUnlicensedCarPlateCount(@RequestBody UnlicensedCarPlateDto unlicensedCarPlateDto) {
        return unlicensedCarPlateV1ServiceDaoImpl.queryUnlicensedCarPlateCount(BeanConvertUtil.beanCovertMap(unlicensedCarPlateDto));
    }
}
