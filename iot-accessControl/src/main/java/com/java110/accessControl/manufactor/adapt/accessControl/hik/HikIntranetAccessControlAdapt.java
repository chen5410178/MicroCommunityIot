package com.java110.accessControl.manufactor.adapt.accessControl.hik;

import com.java110.accessControl.manufactor.AbstractAccessControlManufactorAdapt;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.po.accessControlFace.AccessControlFacePo;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.springframework.stereotype.Service;

/**
 * 海康门禁 （内网使用）
 * <p>
 * 门禁设备 部署海康网关设备后添加；
 */
@Service("hikIntranetAccessControlAdapt")
public class HikIntranetAccessControlAdapt extends AbstractAccessControlManufactorAdapt {
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
