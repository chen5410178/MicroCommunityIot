package com.java110.lift.factory;

import com.java110.dto.lift.LiftCameraDto;

/**
 * 设备上报
 */
public interface ILiftCameraAdapt {
    /**
     * 心跳接口
     *
     * @param liftCameraDto
     * @param reqBody
     * @return
     */
    String heartbeat(LiftCameraDto liftCameraDto, String reqBody);

    /**
     * 告警上报
     *
     * @param liftCameraDto
     * @param reqBody
     * @return
     */
    String liftWarnResult(LiftCameraDto liftCameraDto, String reqBody);
}
