package com.java110.accessControl.manufactor.adapt.accessControl.yiteSmart;

import com.alibaba.fastjson.JSONObject;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.po.accessControlFace.AccessControlFacePo;

public interface ISyncUser {


    /**
     * 同步 添加 或者修改用户
     * @param accessControlDto
     * @param accessControlFacePo
     * @return
     */
    JSONObject getAddUser(AccessControlDto accessControlDto, AccessControlFacePo accessControlFacePo);

    /**
     * 同步删除用户
     * @param accessControlDto
     * @param accessControlFacePo
     * @return
     */
    JSONObject getDeleteUser(AccessControlDto accessControlDto, AccessControlFacePo accessControlFacePo);
}
