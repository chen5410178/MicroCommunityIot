package com.java110.accessControl.bmo.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.accessControl.bmo.IAuthVisitBMO;
import com.java110.core.cache.CommonCache;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.carInoutTempAuth.CarInoutTempAuthDto;
import com.java110.dto.visit.VisitDto;
import com.java110.intf.accessControl.IVisitV1InnerServiceSMO;
import com.java110.intf.barrier.IBarrierV1InnerServiceSMO;
import com.java110.intf.barrier.ICarInoutV1InnerServiceSMO;
import com.java110.po.visit.VisitPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthVisitBMOImpl implements IAuthVisitBMO {

    @Autowired
    private IVisitV1InnerServiceSMO visitV1InnerServiceSMOImpl;


    @Autowired
    private IBarrierV1InnerServiceSMO barrierV1InnerServiceSMOImpl;

    @Override
    public boolean auth(VisitDto visitDto, String state, String msg) {

        VisitPo visitPo = new VisitPo();
        visitPo.setVisitId(visitDto.getVisitId());
        visitPo.setState(state);
        visitPo.setMsg(msg);
        int flag = visitV1InnerServiceSMOImpl.updateVisit(visitPo);

        if (flag < 1) {
            throw new CmdException("更新数据失败");
        }


        String carNum = visitDto.getCarNum();

       String info =  CommonCache.getAndRemoveValue(carNum+"_inCarEngine");

       if(StringUtil.isEmpty(info)){
           return true;
       }

        JSONObject tempCarAuth = JSONObject.parseObject(info);

        CarInoutTempAuthDto carInoutTempAuthDto = BeanConvertUtil.covertBean(tempCarAuth, CarInoutTempAuthDto.class);
        barrierV1InnerServiceSMOImpl.tempCarAuthOpen(carInoutTempAuthDto);

        return true;
    }
}
