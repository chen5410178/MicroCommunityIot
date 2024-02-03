package com.java110.user.bmo;

public interface ISyncAccessControlBMO {

    /**
     * 同步人脸到门禁
     * @param memberId
     */
    void syncAccessControl(String memberId);

    /**
     * 同步删除人脸门禁
     * @param memberId
     */
    void syncDeleteAccessControl(String memberId);
}
