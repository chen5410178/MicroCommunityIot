package com.java110.car.manufactor;

import com.java110.bean.ResultVo;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.dto.parkingSpaceMachine.ParkingSpaceMachineDto;
import com.java110.dto.parkingSpaceMachine.ParkingSpaceMachineNotifyDto;
import com.java110.dto.user.UserDto;
import com.java110.po.accessControl.AccessControlPo;
import com.java110.po.accessControlFace.AccessControlFacePo;

/**
 * 门禁厂家实现接口类
 */
public interface IParkingSpaceMachineManufactor {

    /**
     * 心跳
     * @param parkingSpaceMachineDto
     * @param reqParam
     * @return
     */
    String heartbeat(ParkingSpaceMachineDto parkingSpaceMachineDto,String reqParam);

    /**
     * 识别结果
     * @param parkingSpaceMachineDto
     * @param reqParam
     * @return
     */
    String plateResult(ParkingSpaceMachineDto parkingSpaceMachineDto,String reqParam);


    /**
     * 事件信息推送
     * @param parkingSpaceMachineDto
     * @param reqParam
     * @return
     */
    String eventPush(ParkingSpaceMachineDto parkingSpaceMachineDto,String reqParam);


    /**
     * 车位设置
     * @param parkingSpaceMachineDto
     * @param reqParam
     * @return
     */
    String getEmptyFrame(ParkingSpaceMachineDto parkingSpaceMachineDto,String reqParam);

}
