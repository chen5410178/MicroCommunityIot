package com.java110.accessControl.bmo;

import com.java110.dto.accessControlFloor.AccessControlFloorDto;
import com.java110.po.accessControlFloor.AccessControlFloorPo;
import com.java110.po.accessControlOrg.AccessControlOrgPo;

public interface IDeleteAccessControlFaceBMO {


    /**
     * 异步 删除门禁下发
     *
     * @param accessControlFloorDto
     */
    void delete(AccessControlFloorDto accessControlFloorDto);


}
