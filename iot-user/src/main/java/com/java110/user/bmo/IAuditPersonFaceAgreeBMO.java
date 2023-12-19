package com.java110.user.bmo;

import com.java110.po.personFace.PersonFacePo;

/**
 * 审核人脸同意
 */
public interface IAuditPersonFaceAgreeBMO {

    /**
     * 人脸审核同意
     * @param personFacePo
     */
    void agree(PersonFacePo personFacePo);
}
