package com.java110.accessControl.manufactor.adapt.accessControl;

import com.java110.accessControl.manufactor.AbstractAccessControlManufactorAdapt;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.po.accessControlFace.AccessControlFacePo;
import org.springframework.stereotype.Service;

/**
 * 海康门禁 （网关版）
 */
@Service("hikGatewayAccessControlAdapt")
public class HikGatewayAccessControlAdapt extends AbstractAccessControlManufactorAdapt {
    @Override
    public boolean addUser(AccessControlDto accessControlDto, AccessControlFacePo accessControlFacePo) {
        return false;
    }

    @Override
    public boolean updateUser(AccessControlDto accessControlDto, AccessControlFacePo accessControlFacePo) {
        return false;
    }

    @Override
    public boolean deleteUser(AccessControlDto accessControlDto, AccessControlFacePo accessControlFacePo) {
        return false;
    }

    @Override
    public boolean openDoor(AccessControlDto accessControlDto) {
        return false;
    }

    @Override
    public boolean restartMachine(AccessControlDto accessControlDto) {
        return false;
    }

    @Override
    public String accessControlResult(String topic, String param) {
        return null;
    }
}
