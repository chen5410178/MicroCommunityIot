package com.java110.accessControl.manufactor;

import com.java110.bean.ResultVo;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.dto.user.UserDto;
import com.java110.po.accessControl.AccessControlPo;
import com.java110.po.accessControlFace.AccessControlFacePo;

public abstract class AbstractAccessControlManufactorAdapt implements IAccessControlManufactor {
    @Override
    public boolean initMachine(AccessControlDto accessControlDto) {
        return true;
    }

    @Override
    public boolean addMachine(AccessControlPo accessControlPo) {
        return true;
    }

    @Override
    public boolean updateMachine(AccessControlPo accessControlPo) {
        return true;
    }

    @Override
    public boolean deleteMachine(AccessControlPo accessControlPo) {
        return true;
    }

    /**
     * 添加用户
     *
     * @param accessControlDto 门禁信息
     * @param accessControlFacePo          人员信息
     * @return
     */
    @Override
    public abstract boolean addUser(AccessControlDto accessControlDto, AccessControlFacePo accessControlFacePo);

    /**
     * 修改用户
     *
     * @param accessControlDto 门禁信息
     * @param accessControlFacePo          人员信息
     * @return
     */
    @Override
    public abstract boolean updateUser(AccessControlDto accessControlDto, AccessControlFacePo accessControlFacePo);

    /**
     * 删除用户
     *
     * @param accessControlDto 门禁信息
     * @param accessControlFacePo          人员信息
     * @return
     */
    @Override
    public abstract boolean deleteUser(AccessControlDto accessControlDto, AccessControlFacePo accessControlFacePo);

    @Override
    public boolean clearUser(AccessControlDto accessControlDto) {
        return true;
    }

    /**
     * 设备开门
     *
     * @param accessControlDto 门禁信息
     * @return
     */
    @Override
    public abstract boolean openDoor(AccessControlDto accessControlDto);

    @Override
    public ResultVo getQRCode(UserDto userDto) {
        return null;
    }

    @Override
    public String qrCode(AccessControlDto accessControlDto, String param) {
        return null;
    }

    /**
     * 门禁记录上报
     *
     * @param accessControlDto 门禁
     * @param param            上报参数
     * @return
     */
    @Override
    public abstract String accessControlResult(AccessControlDto accessControlDto, String param);
}
