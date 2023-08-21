package com.java110.accessControl.manufactor;

import com.java110.bean.ResultVo;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.dto.accessControlFace.AccessControlFaceDto;
import com.java110.dto.user.UserDto;
import com.java110.po.accessControl.AccessControlPo;
import com.java110.po.accessControlFace.AccessControlFacePo;

/**
 * 门禁厂家实现接口类
 */
public interface IAccessControlManufactor {

    /**
     * 设备初始化，可能像 海康大华 等门禁他们需要在初始化时 做一些业务，看情况是否需要调用
     *
     * @param accessControlDto 门禁
     * @return
     */
    boolean initMachine(AccessControlDto accessControlDto);

    /**
     * 添加门禁，
     *
     * @param accessControlPo 门禁信息
     * @return
     */
    boolean addMachine(AccessControlPo accessControlPo);

    /**
     * 修改门禁，
     *
     * @param accessControlPo 门禁信息
     * @return
     */
    boolean updateMachine(AccessControlPo accessControlPo);


    /**
     * 删除门禁
     *
     * @param accessControlPo 门禁信息
     * @return
     */
    boolean deleteMachine(AccessControlPo accessControlPo);


    /**
     * 添加用户
     *
     * @param accessControlDto 门禁信息
     * @param accessControlFacePo          人员信息
     * @return
     */
    boolean addUser(AccessControlDto accessControlDto, AccessControlFacePo accessControlFacePo);

    /**
     * 修改用户
     *
     * @param accessControlDto 门禁信息
     * @param userDto          人员信息
     * @return
     */
    boolean updateUser(AccessControlDto accessControlDto, AccessControlFacePo accessControlFacePo);

    /**
     * 删除用户
     *
     * @param accessControlDto 门禁信息
     * @param userDto          人员信息
     * @return
     */
    boolean deleteUser(AccessControlDto accessControlDto, AccessControlFacePo accessControlFacePo);


    /**
     * 清空用户
     *
     * @param accessControlDto 门禁信息
     * @return
     */
    boolean clearUser(AccessControlDto accessControlDto);

    /**
     * 远程开门
     *
     * @param accessControlDto 门禁信息
     * @return
     */
    boolean openDoor(AccessControlDto accessControlDto);

    /**
     * 获取二维码
     *
     * @param userDto 用户
     * @return
     */
    ResultVo getQRCode(UserDto userDto);

    /**
     * 二维码核验接口
     *
     * @param accessControlDto 门禁
     * @param param
     * @return
     */
    String qrCode(AccessControlDto accessControlDto, String param);

    /**
     * 结果上报
     *
     * @param accessControlDto
     * @param param
     * @return
     */
    String accessControlResult(AccessControlDto accessControlDto, String param);

}
