package com.java110.openapi.bmo.adminImpl;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.smo.IComputeFeeSMO;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.carInout.CarInoutDto;
import com.java110.dto.community.CommunityDto;
import com.java110.dto.parking.ParkingBoxAreaDto;
import com.java110.dto.payment.CarInoutPaymentDto;
import com.java110.intf.barrier.ICarInoutPaymentV1InnerServiceSMO;
import com.java110.intf.barrier.ICarInoutV1InnerServiceSMO;
import com.java110.intf.barrier.IParkingBoxAreaV1InnerServiceSMO;
import com.java110.intf.community.ICommunityV1InnerServiceSMO;
import com.java110.openapi.bmo.IIotAdminApiBmo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("listAdminCarInoutDetailBmoImpl")
public class ListAdminCarInoutDetailBmoImpl implements IIotAdminApiBmo {

    @Autowired
    private ICarInoutV1InnerServiceSMO carInoutV1InnerServiceSMOImpl;

    @Autowired
    private ICarInoutPaymentV1InnerServiceSMO carInoutPaymentV1InnerServiceSMOImpl;

    @Autowired
    private IComputeFeeSMO computeFeeSMOImpl;
    @Autowired
    private IParkingBoxAreaV1InnerServiceSMO parkingBoxAreaV1InnerServiceSMOImpl;

    @Autowired
    private ICommunityV1InnerServiceSMO communityV1InnerServiceSMOImpl;

    @Override
    public void validate(ICmdDataFlowContext context, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "page", "未包含page");
        Assert.hasKeyAndValue(reqJson, "row", "未包含row");
    }

    @Override
    public void doCmd(ICmdDataFlowContext context, JSONObject reqJson) {
        CarInoutDto carInoutDetailDto = BeanConvertUtil.covertBean(reqJson, CarInoutDto.class);
        String communityIdStr = reqJson.getString("communityIdStr");
        if(!StringUtil.isEmpty(communityIdStr)){
            carInoutDetailDto.setCommunityIds(communityIdStr.split(","));
        }
        if (reqJson.containsKey("boxId")) {
            carInoutDetailDto.setPaIds(getPaIds(reqJson));
        } else {
            carInoutDetailDto.setPaId(reqJson.getString("paId"));
        }
        int count = carInoutV1InnerServiceSMOImpl.queryCarInoutDetailsCount(carInoutDetailDto);

        List<CarInoutDto> carInoutDetailDtos = null;

        if (count > 0) {
            carInoutDetailDtos = carInoutV1InnerServiceSMOImpl.queryCarInoutDetails(carInoutDetailDto);
            // 计算实收
            computeRealCharge(carInoutDetailDtos);
            carInoutDetailDtos = computeCarInoutDetail(carInoutDetailDtos);
        } else {
            carInoutDetailDtos = new ArrayList<>();
        }


        refreshCommunityName(carInoutDetailDtos);

        ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) reqJson.getInteger("row")), count, carInoutDetailDtos);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        context.setResponseEntity(responseEntity);
    }

    private List<CarInoutDto> computeCarInoutDetail(List<CarInoutDto> carInoutDetailDtos) {
        return computeFeeSMOImpl.computeTempCarInoutDetailStopTimeAndFee(carInoutDetailDtos);
    }
    private void refreshCommunityName(List<CarInoutDto> carInoutDetailDtos) {
        if (ListUtil.isNull(carInoutDetailDtos)) {
            return;
        }

        List<String> communityIds = new ArrayList<>();
        for (CarInoutDto carInoutDto : carInoutDetailDtos) {
            communityIds.add(carInoutDto.getCommunityId());
        }

        if (ListUtil.isNull(communityIds)) {
            return;
        }
        CommunityDto communityDto = new CommunityDto();
        communityDto.setCommunityIds(communityIds.toArray(new String[communityIds.size()]));
        List<CommunityDto> communityDtos = communityV1InnerServiceSMOImpl.queryCommunitys(communityDto);
        if (ListUtil.isNull(communityDtos)) {
            return;
        }
        for (CarInoutDto carInoutDto : carInoutDetailDtos) {
            for (CommunityDto tCommunityDto : communityDtos) {
                if (!carInoutDto.getCommunityId().equals(tCommunityDto.getCommunityId())) {
                    continue;
                }
                carInoutDto.setCommunityName(tCommunityDto.getName());
            }
        }
    }

    private void computeRealCharge(List<CarInoutDto> carInoutDetailDtos) {

        if (ListUtil.isNull(carInoutDetailDtos)) {
            return;
        }

        List<String> inoutIds = new ArrayList<>();
        for (CarInoutDto tmpCarInoutDto : carInoutDetailDtos) {
            inoutIds.add(tmpCarInoutDto.getInoutId());
        }

        CarInoutPaymentDto carInoutPaymentDto = new CarInoutPaymentDto();
        carInoutPaymentDto.setCommunityId(carInoutDetailDtos.get(0).getCommunityId());
        carInoutPaymentDto.setInoutIds(inoutIds.toArray(new String[inoutIds.size()]));

        List<CarInoutPaymentDto> carInoutPaymentDtos = carInoutPaymentV1InnerServiceSMOImpl.queryRealChargeByInoutId(carInoutPaymentDto);

        if (ListUtil.isNull(carInoutPaymentDtos)) {
            return;
        }
        for (CarInoutDto tmpCarInoutDto : carInoutDetailDtos) {
            for (CarInoutPaymentDto tmpCarInoutPaymentDto : carInoutPaymentDtos) {
                if (tmpCarInoutPaymentDto.getInoutId().equals(tmpCarInoutDto.getInoutId())) {
                    tmpCarInoutDto.setRealCharge(tmpCarInoutPaymentDto.getRealCharge());
                }
            }
        }

    }

    private String[] getPaIds(JSONObject reqJson) {
        if (reqJson.containsKey("boxId") && !StringUtil.isEmpty(reqJson.getString("boxId"))) {
            ParkingBoxAreaDto parkingBoxAreaDto = new ParkingBoxAreaDto();
            parkingBoxAreaDto.setBoxId(reqJson.getString("boxId"));
            parkingBoxAreaDto.setCommunityId(reqJson.getString("communityId"));
            List<ParkingBoxAreaDto> parkingBoxAreaDtos = parkingBoxAreaV1InnerServiceSMOImpl.queryParkingBoxAreas(parkingBoxAreaDto);

            if (ListUtil.isNull(parkingBoxAreaDtos)) {
                throw new CmdException("未查到停车场信息");
            }
            List<String> paIds = new ArrayList<>();
            for (ParkingBoxAreaDto parkingBoxAreaDto1 : parkingBoxAreaDtos) {
                paIds.add(parkingBoxAreaDto1.getPaId());
            }
            String[] paIdss = paIds.toArray(new String[paIds.size()]);
            return paIdss;
        }
        return null;
    }


}
