package com.java110.user.cmd.store;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.CmdContextUtils;
import com.java110.dto.shop.ShopDto;
import com.java110.dto.shop.StoreShopDto;
import com.java110.dto.store.StoreDto;
import com.java110.intf.user.IStoreShopV1InnerServiceSMO;
import com.java110.intf.user.IStoreV1InnerServiceSMO;
import com.java110.utils.util.BeanConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Java110Cmd(serviceCode = "store.queryShop")
public class QueryShopCmd extends Cmd {

    @Autowired
    private IStoreShopV1InnerServiceSMO storeShopV1InnerServiceSMOImpl;

    @Autowired
    private IStoreV1InnerServiceSMO storeV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {

    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        String storeId = CmdContextUtils.getStoreId(context);
        StoreShopDto shopDto = BeanConvertUtil.covertBean(reqJson, StoreShopDto.class);
        String states = reqJson.getString("states");
        if (states != null && states.contains(",")) {
            shopDto.setStates(states.split(","));
        }
        shopDto.setStoreId(storeId);

        int count = storeShopV1InnerServiceSMOImpl.queryStoreShopsCount(shopDto);

        List<ShopDto> shopDtos = null;
        if (count > 0) {
            shopDtos = storeShopV1InnerServiceSMOImpl.queryStoreShops(shopDto);
            List<String> shopIds = new ArrayList<>();
            for (ShopDto prod : shopDtos) {
                shopIds.add(prod.getShopId());
            }

            freshStore(shopDtos);
        } else {
            shopDtos = new ArrayList<>();
        }

        ResultVo resultVo = new ResultVo((int) Math.ceil((double) count / (double) shopDto.getRow()), count, shopDtos);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        context.setResponseEntity(responseEntity);
    }

    /**
     * 刷入商户信息
     *
     * @param shopDtos
     */
    private void freshStore(List<ShopDto> shopDtos) {

        if (shopDtos == null || shopDtos.size() < 1) {
            return;
        }
        List<String> storeIds = new ArrayList<>();
        for (ShopDto shopDto : shopDtos) {
            storeIds.add(shopDto.getStoreId());
        }

        StoreDto storeDto = new StoreDto();
        storeDto.setStoreIds(storeIds.toArray(new String[storeIds.size()]));
        List<StoreDto> storeDtos = storeV1InnerServiceSMOImpl.queryStores(storeDto);

        for (ShopDto shopDto : shopDtos) {
            for (StoreDto tmpStoreDto : storeDtos) {
                if (shopDto.getStoreId().equals(tmpStoreDto.getStoreId())) {
                    shopDto.setStoreName(tmpStoreDto.getName());
                    shopDto.setStoreTel(tmpStoreDto.getTel());
                    shopDto.setStoreAddress(tmpStoreDto.getAddress());
                }
            }
        }
    }

}
