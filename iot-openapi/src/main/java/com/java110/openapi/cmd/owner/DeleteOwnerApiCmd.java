package com.java110.openapi.cmd.owner;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.dto.car.OwnerCarDto;
import com.java110.bean.dto.owner.OwnerDto;
import com.java110.bean.dto.owner.OwnerRoomRelDto;
import com.java110.bean.po.owner.OwnerPo;
import com.java110.bean.po.owner.OwnerRoomRelPo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.CmdContextUtils;
import com.java110.core.utils.ListUtil;
import com.java110.dto.store.StoreDto;
import com.java110.intf.car.IOwnerCarV1InnerServiceSMO;
import com.java110.intf.user.IOwnerRoomRelV1InnerServiceSMO;
import com.java110.intf.user.IOwnerV1InnerServiceSMO;
import com.java110.intf.user.IStoreV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import com.java110.po.ownerCar.OwnerCarPo;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.List;

/**
 * 接口添加业主
 * <p>
 * 协议 请查看 《三方系统对接HC物联网系统协议1.0》下的 四、删除业主
 */
@Java110Cmd(serviceCode = "owner.deleteOwnerApi")
public class DeleteOwnerApiCmd extends Cmd {

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Autowired
    private IStoreV1InnerServiceSMO storeV1InnerServiceSMOImpl;

    @Autowired
    private IOwnerV1InnerServiceSMO ownerV1InnerServiceSMOImpl;

    @Autowired
    private IOwnerRoomRelV1InnerServiceSMO ownerRoomRelV1InnerServiceSMOImpl;

    @Autowired
    private IOwnerCarV1InnerServiceSMO ownerCarV1InnerServiceSMOImpl;




    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        Assert.hasKeyAndValue(reqJson, "communityId", "未包含communityId");
        Assert.hasKeyAndValue(reqJson, "memberId", "未包含memberId");
        Assert.hasKeyAndValue(reqJson, "ownerId", "未包含ownerId");


        // todo 只有运营团队的账号才能操作接口

        String storeId = CmdContextUtils.getStoreId(context);

        StoreDto storeDto = new StoreDto();
        storeDto.setStoreId(storeId);
        storeDto.setStoreTypeCd(StoreDto.STORE_TYPE_ADMIN);
        List<StoreDto> storeDtos = storeV1InnerServiceSMOImpl.queryStores(storeDto);

        if (ListUtil.isNull(storeDtos)) {
            throw new CmdException("登陆账户没有权限");
        }
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

        //todo 检查 业主是否有房屋，如果有房屋 删除业主和房屋之间的关系
        //todo 解绑业主下所有房屋

        OwnerRoomRelDto ownerRoomRelDto = new OwnerRoomRelDto();
        ownerRoomRelDto.setOwnerId(reqJson.getString("memberId"));
        List<OwnerRoomRelDto> ownerRoomRelDtos = ownerRoomRelV1InnerServiceSMOImpl.queryOwnerRoomRels(ownerRoomRelDto);

        if (!ListUtil.isNull(ownerRoomRelDtos)) {
            OwnerRoomRelPo ownerRoomRelPo = new OwnerRoomRelPo();
            ownerRoomRelPo.setOwnerId(reqJson.getString("memberId"));
            ownerRoomRelV1InnerServiceSMOImpl.deleteOwnerRoomRel(ownerRoomRelPo);
        }


        //todo 检查业主是否有车辆 ，如果有删除车辆

        OwnerCarDto ownerCarDto = new OwnerCarDto();
        ownerCarDto.setOwnerId(reqJson.getString("memberId"));
        List<OwnerCarDto> ownerCarDtos = ownerCarV1InnerServiceSMOImpl.queryOwnerCars(ownerCarDto);
        if (!ListUtil.isNull(ownerCarDtos)) {
            OwnerCarPo ownerCarPo = new OwnerCarPo();
            ownerCarPo.setOwnerId(reqJson.getString("memberId"));
            ownerCarV1InnerServiceSMOImpl.deleteOwnerCar(ownerCarPo);
        }


        //todo 删除业主
        OwnerDto ownerDto = new OwnerDto();
        ownerDto.setMemberId(reqJson.getString("memberId"));

        List<OwnerDto> ownerDtos = ownerV1InnerServiceSMOImpl.queryOwners(ownerDto);

        if(!ListUtil.isNull(ownerDtos)){
            OwnerPo ownerPo = new OwnerPo();
            ownerPo.setMemberId(ownerDtos.get(0).getMemberId());
            ownerV1InnerServiceSMOImpl.deleteOwner(ownerPo);
        }

    }
}
