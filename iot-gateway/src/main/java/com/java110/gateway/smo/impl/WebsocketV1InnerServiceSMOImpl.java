package com.java110.gateway.smo.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.dto.idSeq.IdSeqDto;
import com.java110.core.configuration.ServiceInfoListener;
import com.java110.core.factory.LoggerFactory;
import com.java110.dto.parking.ParkingBoxAreaDto;
import com.java110.gateway.dao.ISnowflakeldWorker;
import com.java110.gateway.websocket.ParkingAreaWebsocket;
import com.java110.gateway.websocket.ParkingBoxWebsocket;
import com.java110.intf.barrier.IParkingBoxAreaV1InnerServiceSMO;
import com.java110.intf.gateway.IIdSeqInnerServiceSMO;
import com.java110.intf.gateway.IWebsocketV1InnerSMO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 用户服务信息管理业务信息实现
 * Created by wuxw on 2017/4/5.
 */
@RestController
public class WebsocketV1InnerServiceSMOImpl implements IWebsocketV1InnerSMO {
    protected final static Logger logger = LoggerFactory.getLogger(WebsocketV1InnerServiceSMOImpl.class);

    @Autowired
    private IParkingBoxAreaV1InnerServiceSMO parkingBoxAreaV1InnerServiceSMOImpl;

    @Override
    public int webSentParkingArea(@RequestBody JSONObject reqJson) {
        JSONObject param = JSONObject.parseObject(reqJson.toString());
        try {
            ParkingBoxWebsocket.sendInfo(param.toJSONString(), param.getString("boxId"));

        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        ParkingBoxAreaDto parkingBoxAreaDto = new ParkingBoxAreaDto();
        parkingBoxAreaDto.setBoxId(reqJson.getString("extBoxId"));
        parkingBoxAreaDto.setDefaultArea(ParkingBoxAreaDto.DEFAULT_AREA_TRUE);

        List<ParkingBoxAreaDto> parkingBoxAreaDtos = parkingBoxAreaV1InnerServiceSMOImpl.queryParkingBoxAreas(parkingBoxAreaDto);

        if (parkingBoxAreaDtos == null || parkingBoxAreaDtos.isEmpty()) {
            return 1;
        }

        try {
            ParkingAreaWebsocket.sendInfo(param.toJSONString(), parkingBoxAreaDtos.get(0).getPaId());
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        return 1;
    }
}
