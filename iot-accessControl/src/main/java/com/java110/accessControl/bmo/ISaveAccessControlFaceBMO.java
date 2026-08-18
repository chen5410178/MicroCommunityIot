package com.java110.accessControl.bmo;

import com.java110.bean.dto.unit.UnitDto;
import com.java110.po.accessControlFloor.AccessControlFloorPo;
import com.java110.po.accessControlOrg.AccessControlOrgPo;
import com.java110.po.accessControlShop.AccessControlShopPo;

public interface ISaveAccessControlFaceBMO {

    /**
     * 异步 保存门禁下发
     *
     * @param accessControlFloorPo
     * @param machineId
     */
    void save(AccessControlFloorPo accessControlFloorPo, String machineId,String startDate,String endDate);


    /**
     * 组织员工 下发门禁
     * @param accessControlOrgPo
     * @param machineId
     */
    void saveOrgStaff(AccessControlOrgPo accessControlOrgPo, String machineId,String startDate,String endDate);

    /**
     * 商铺业主 下发门禁（按位置维度，含去重）
     *
     * @param accessControlShopPo
     * @param machineId
     * @param startDate
     * @param endDate
     */
    void saveShopOwner(AccessControlShopPo accessControlShopPo, String machineId,String startDate,String endDate);

    /**
     * 异步重新下发人脸
     * @param machineId
     * @param communityId
     */
    void reSendFace(String machineId,String communityId,String mfId);
}
