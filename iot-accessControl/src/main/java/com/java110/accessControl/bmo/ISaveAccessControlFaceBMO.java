package com.java110.accessControl.bmo;

import com.java110.bean.dto.unit.UnitDto;
import com.java110.po.accessControlFloor.AccessControlFloorPo;
import com.java110.po.accessControlOrg.AccessControlOrgPo;

public interface ISaveAccessControlFaceBMO {

    /**
     * 异步 保存门禁下发
     *
     * @param accessControlFloorPo
     * @param machineId
     */
    void save(AccessControlFloorPo accessControlFloorPo, String machineId);

    /**
     * 组织员工 下发门禁
     * @param accessControlOrgPo
     * @param machineId
     */
    void saveOrgStaff(AccessControlOrgPo accessControlOrgPo, String machineId);
}
