package com.java110.accessControl.bmo;

import com.java110.bean.dto.unit.UnitDto;
import com.java110.po.accessControlFloor.AccessControlFloorPo;

public interface ISaveAccessControlFaceBMO {

    /**
     * 异步 保存门禁下发
     *
     * @param accessControlFloorPo
     * @param machineId
     */
    void save(AccessControlFloorPo accessControlFloorPo, String machineId);
}
