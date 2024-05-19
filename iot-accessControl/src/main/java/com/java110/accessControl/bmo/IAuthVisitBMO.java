package com.java110.accessControl.bmo;

import com.java110.dto.visit.VisitDto;

/**
 * 访客审核
 */
public interface IAuthVisitBMO {

    /**
     * 访客审核
     * @param visitDto
     * @param state
     * @param msg
     * @return
     */
    boolean auth(VisitDto visitDto,String state,String msg);
}
