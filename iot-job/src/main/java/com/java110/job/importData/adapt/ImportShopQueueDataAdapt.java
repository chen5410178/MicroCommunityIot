package com.java110.job.importData.adapt;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.dto.owner.OwnerDto;
import com.java110.bean.po.owner.OwnerPo;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.importData.ImportShopDto;
import com.java110.dto.log.AssetImportLogDetailDto;
import com.java110.dto.shop.BuildingShopDto;
import com.java110.dto.shop.BuildingShopOwnerDto;
import com.java110.dto.shop.ShopLocationDto;
import com.java110.intf.community.IBuildingShopOwnerV1InnerServiceSMO;
import com.java110.intf.community.IBuildingShopV1InnerServiceSMO;
import com.java110.intf.community.IShopLocationV1InnerServiceSMO;
import com.java110.intf.user.IOwnerV1InnerServiceSMO;
import com.java110.job.importData.DefaultImportData;
import com.java110.job.importData.IImportDataAdapt;
import com.java110.po.shop.BuildingShopOwnerPo;
import com.java110.po.shop.BuildingShopPo;
import com.java110.po.shop.ShopLocationPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("importShopQueueData")
public class ImportShopQueueDataAdapt extends DefaultImportData implements IImportDataAdapt {

    @Autowired
    private IShopLocationV1InnerServiceSMO shopLocationV1InnerServiceSMOImpl;

    @Autowired
    private IBuildingShopV1InnerServiceSMO buildingShopV1InnerServiceSMOImpl;

    @Autowired
    private IBuildingShopOwnerV1InnerServiceSMO buildingShopOwnerV1InnerServiceSMOImpl;

    @Autowired
    private IOwnerV1InnerServiceSMO ownerV1InnerServiceSMOImpl;

    @Override
    public void importData(List<AssetImportLogDetailDto> assetImportLogDetailDtos) {
        for (AssetImportLogDetailDto assetImportLogDetailDto : assetImportLogDetailDtos) {
            try {
                doImportData(assetImportLogDetailDto);
                updateImportLogDetailState(assetImportLogDetailDto.getDetailId());
            } catch (Exception e) {
                e.printStackTrace();
                updateImportLogDetailState(assetImportLogDetailDto.getDetailId(), e);
            }
        }
    }

    private void doImportData(AssetImportLogDetailDto assetImportLogDetailDto) {
        JSONObject data = JSONObject.parseObject(assetImportLogDetailDto.getContent());
        ImportShopDto importShopDto = BeanConvertUtil.covertBean(data, ImportShopDto.class);

        // 位置处理：按 - 逐级查询/创建
        String locationId = saveShopLocation(importShopDto);

        // 商铺落库
        BuildingShopPo buildingShopPo = new BuildingShopPo();
        buildingShopPo.setShopId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_shopId));
        buildingShopPo.setShopNum(importShopDto.getShopNum());
        buildingShopPo.setLocationId(locationId);
        buildingShopPo.setLocationName(importShopDto.getLocationName());
        buildingShopPo.setBuiltUpArea(importShopDto.getBuiltUpArea());
        buildingShopPo.setShopArea(importShopDto.getShopArea());
        buildingShopPo.setState(StringUtil.isEmpty(importShopDto.getShopState()) ? BuildingShopDto.STATE_FREE : importShopDto.getShopState());
        buildingShopPo.setShopRent(StringUtil.isEmpty(importShopDto.getShopRent()) ? "0.00" : importShopDto.getShopRent());
        buildingShopPo.setCommunityId(importShopDto.getCommunityId());
        buildingShopPo.setRemark("商铺导入");
        int flag = buildingShopV1InnerServiceSMOImpl.saveBuildingShop(buildingShopPo);
        if (flag < 1) {
            throw new CmdException("保存商铺失败");
        }

        // 业主为空 只建商铺
        if (StringUtil.isEmpty(importShopDto.getOwnerName()) && StringUtil.isEmpty(importShopDto.getLink())) {
            return;
        }

        // 业主处理：按小区+手机号查询 存在复用 不存在创建
        String ownerId = saveOwner(importShopDto);

        // 业主关联落库
        BuildingShopOwnerPo buildingShopOwnerPo = new BuildingShopOwnerPo();
        buildingShopOwnerPo.setShopOwnerId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_shopId));
        buildingShopOwnerPo.setShopId(buildingShopPo.getShopId());
        buildingShopOwnerPo.setOwnerId(ownerId);
        buildingShopOwnerPo.setOwnerName(importShopDto.getOwnerName());
        buildingShopOwnerPo.setLink(importShopDto.getLink());
        buildingShopOwnerPo.setState(buildingShopPo.getState());
        buildingShopOwnerPo.setStartTime(importShopDto.getStartTime());
        buildingShopOwnerPo.setEndTime(importShopDto.getEndTime());
        buildingShopOwnerPo.setCommunityId(importShopDto.getCommunityId());
        buildingShopOwnerPo.setRemark("商铺导入");
        int ownerFlag = buildingShopOwnerV1InnerServiceSMOImpl.saveBuildingShopOwner(buildingShopOwnerPo);
        if (ownerFlag < 1) {
            throw new CmdException("保存商铺业主关联失败");
        }
    }

    /**
     * 位置按层级逐级查询，不存在则创建
     */
    private String saveShopLocation(ImportShopDto importShopDto) {
        String[] locationItems = importShopDto.getLocationName().split("-");
        String parentId = "-1";
        for (int i = 0; i < locationItems.length; i++) {
            ShopLocationDto queryDto = new ShopLocationDto();
            queryDto.setLocationName(locationItems[i]);
            queryDto.setParentId(parentId);
            queryDto.setCommunityId(importShopDto.getCommunityId());
            List<ShopLocationDto> locations = shopLocationV1InnerServiceSMOImpl.queryShopLocations(queryDto);
            if (ListUtil.isNull(locations)) {
                ShopLocationPo shopLocationPo = new ShopLocationPo();
                shopLocationPo.setLocationId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_locationId));
                shopLocationPo.setLocationName(locationItems[i]);
                shopLocationPo.setParentId(parentId);
                shopLocationPo.setLevel(i + 1);
                shopLocationPo.setSeq(1);
                shopLocationPo.setRemark("商铺导入");
                shopLocationPo.setCommunityId(importShopDto.getCommunityId());
                int flag = shopLocationV1InnerServiceSMOImpl.saveShopLocation(shopLocationPo);
                if (flag < 1) {
                    throw new CmdException("保存商铺位置失败");
                }
                parentId = shopLocationPo.getLocationId();
            } else {
                parentId = locations.get(0).getLocationId();
            }
        }
        return parentId;
    }

    /**
     * 业主按小区+手机号查询，存在复用，不存在创建
     */
    private String saveOwner(ImportShopDto importShopDto) {
        String ownerId = "";
        if (!StringUtil.isEmpty(importShopDto.getLink())) {
            OwnerDto ownerDto = new OwnerDto();
            ownerDto.setLink(importShopDto.getLink());
            ownerDto.setCommunityId(importShopDto.getCommunityId());
            List<OwnerDto> ownerDtos = ownerV1InnerServiceSMOImpl.queryOwners(ownerDto);
            if (!ListUtil.isNull(ownerDtos)) {
                return ownerDtos.get(0).getOwnerId();
            }
        }
        OwnerPo ownerPo = new OwnerPo();
        ownerPo.setUserId(StringUtil.isEmpty(importShopDto.getUserId()) ? "-1" : importShopDto.getUserId());
        ownerPo.setAge("1");
        ownerPo.setCommunityId(importShopDto.getCommunityId());
        ownerPo.setIdCard(importShopDto.getIdCard());
        ownerPo.setLink(importShopDto.getLink());
        ownerPo.setSex("0".equals(importShopDto.getSex()) ? "0" : "1");
        ownerPo.setMemberId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_ownerId));
        ownerPo.setName(importShopDto.getOwnerName());
        ownerPo.setOwnerId(ownerPo.getMemberId()); //业主 所以和成员ID需要一样
        ownerPo.setOwnerTypeCd(StringUtil.isEmpty(importShopDto.getOwnerTypeCd()) ? OwnerDto.OWNER_TYPE_CD_OWNER : importShopDto.getOwnerTypeCd());
        ownerPo.setRemark("商铺导入");
        ownerPo.setState("2000");
        ownerPo.setAddress("无");
        ownerPo.setOwnerFlag(OwnerDto.OWNER_FLAG_TRUE);
        int flag = ownerV1InnerServiceSMOImpl.saveOwner(ownerPo);
        if (flag < 1) {
            throw new IllegalArgumentException("保存业主失败");
        }
        return ownerPo.getOwnerId();
    }
}
