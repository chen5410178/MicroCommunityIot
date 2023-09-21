package com.java110.car.smo.impl;


import com.java110.bean.dto.PageDto;
import com.java110.bean.dto.car.CarBlackWhiteDto;
import com.java110.car.dao.ICarBlackWhiteServiceDao;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.parking.ParkingAreaDto;
import com.java110.intf.car.ICarBlackWhiteInnerServiceSMO;
import com.java110.intf.car.IParkingAreaV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName FloorInnerServiceSMOImpl
 * @Description 黑白名单内部服务实现类
 * @Author wuxw
 * @Date 2019/4/24 9:20
 * @Version 1.0
 * add by wuxw 2019/4/24
 **/
@RestController
public class CarBlackWhiteInnerServiceSMOImpl  implements ICarBlackWhiteInnerServiceSMO {

    @Autowired
    private ICarBlackWhiteServiceDao carBlackWhiteServiceDaoImpl;

    @Autowired
    private IUserV1InnerServiceSMO userInnerServiceSMOImpl;

    @Autowired
    private IParkingAreaV1InnerServiceSMO parkingAreaInnerServiceSMOImpl;

    @Override
    public List<CarBlackWhiteDto> queryCarBlackWhites(@RequestBody CarBlackWhiteDto carBlackWhiteDto) {

        //校验是否传了 分页信息

        int page = carBlackWhiteDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            carBlackWhiteDto.setPage((page - 1) * carBlackWhiteDto.getRow());
        }

        List<CarBlackWhiteDto> carBlackWhites = BeanConvertUtil.covertBeanList(carBlackWhiteServiceDaoImpl.getCarBlackWhiteInfo(BeanConvertUtil.beanCovertMap(carBlackWhiteDto)), CarBlackWhiteDto.class);


        if (carBlackWhiteDto.getRow() > 15) {
            return carBlackWhites;
        }
        freshCarBlackWhites(carBlackWhites);
        return carBlackWhites;
    }

    private void freshCarBlackWhites(List<CarBlackWhiteDto> carBlackWhites) {
        List<String> paIds = new ArrayList<>();
        for (CarBlackWhiteDto carBlackWhiteDto : carBlackWhites) {
            paIds.add(carBlackWhiteDto.getPaId());
        }

        if (paIds.size() < 1) {
            return;
        }

        ParkingAreaDto parkingAreaDto = new ParkingAreaDto();
        parkingAreaDto.setCommunityId(carBlackWhites.get(0).getCommunityId());
        parkingAreaDto.setPaIds(paIds.toArray(new String[paIds.size()]));
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaInnerServiceSMOImpl.queryParkingAreas(parkingAreaDto);

        for (ParkingAreaDto tmpParkingAreaDto : parkingAreaDtos) {
            for (CarBlackWhiteDto carBlackWhiteDto : carBlackWhites) {
                if (tmpParkingAreaDto.getPaId().endsWith(carBlackWhiteDto.getPaId())) {
                    carBlackWhiteDto.setPaNum(tmpParkingAreaDto.getNum());
                }
            }
        }

    }


    @Override
    public int queryCarBlackWhitesCount(@RequestBody CarBlackWhiteDto carBlackWhiteDto) {
        return carBlackWhiteServiceDaoImpl.queryCarBlackWhitesCount(BeanConvertUtil.beanCovertMap(carBlackWhiteDto));
    }


}
