package com.java110.accessControl.bmo.impl;

import com.java110.accessControl.bmo.ISaveAccessControlFaceBMO;
import com.java110.bean.dto.org.OrgStaffRelDto;
import com.java110.bean.dto.owner.OwnerDto;
import com.java110.bean.dto.room.RoomDto;
import com.java110.bean.dto.unit.UnitDto;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.DateUtil;
import com.java110.dto.accessControlFace.AccessControlFaceDto;
import com.java110.dto.user.UserDto;
import com.java110.intf.accessControl.IAccessControlFaceV1InnerServiceSMO;
import com.java110.intf.community.IRoomV1InnerServiceSMO;
import com.java110.intf.user.IOrgStaffRelV1InnerServiceSMO;
import com.java110.intf.user.IOwnerV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import com.java110.po.accessControlFace.AccessControlFacePo;
import com.java110.po.accessControlFloor.AccessControlFloorPo;
import com.java110.po.accessControlOrg.AccessControlOrgPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 门禁授权
 */
@Service
public class SaveAccessControlFaceBMOImpl implements ISaveAccessControlFaceBMO {

    @Autowired
    private IRoomV1InnerServiceSMO roomV1InnerServiceSMOImpl;

    @Autowired
    private IOwnerV1InnerServiceSMO ownerV1InnerServiceSMOImpl;

    @Autowired
    private IAccessControlFaceV1InnerServiceSMO accessControlFaceV1InnerServiceSMOImpl;

    @Autowired
    private IOrgStaffRelV1InnerServiceSMO orgStaffRelV1InnerServiceSMOImpl;

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    /**
     * 将单元下的房屋
     *
     * @param accessControlFloorPo
     * @param machineId
     */
    @Override
    @Async
    public void save(AccessControlFloorPo accessControlFloorPo, String machineId,String startDate,String endDate) {

        RoomDto roomDto = new RoomDto();
        roomDto.setUnitId(accessControlFloorPo.getUnitId());
        roomDto.setCommunityId(accessControlFloorPo.getCommunityId());
        List<RoomDto> roomDtos = roomV1InnerServiceSMOImpl.queryRooms(roomDto);

        if (roomDtos == null || roomDtos.size() < 1) {
            return;
        }

        for (RoomDto tmpRoomDto : roomDtos) {
            try {
                doRoomOwner(accessControlFloorPo, tmpRoomDto, machineId,startDate,endDate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    @Async
    public void saveOrgStaff(AccessControlOrgPo accessControlOrgPo, String machineId,String startDate,String endDate) {

        OrgStaffRelDto orgStaffRelDto = new OrgStaffRelDto();
        orgStaffRelDto.setOrgId(accessControlOrgPo.getOrgId());
        List<OrgStaffRelDto> orgStaffRelDtos = orgStaffRelV1InnerServiceSMOImpl.queryOrgStaffRels(orgStaffRelDto);

        if (orgStaffRelDtos == null || orgStaffRelDtos.size() < 1) {
            return;
        }

        for (OrgStaffRelDto tmpOrgStaffRelDto : orgStaffRelDtos) {
            try {
                doOrgStaff(accessControlOrgPo, tmpOrgStaffRelDto, machineId,startDate,endDate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void doOrgStaff(AccessControlOrgPo accessControlOrgPo, OrgStaffRelDto tmpOrgStaffRelDto, String machineId,String startDate,String endDate) {
        UserDto userDto = new UserDto();
        userDto.setUserId(tmpOrgStaffRelDto.getStaffId());
        List<UserDto> staffDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);
        Assert.listOnlyOne(staffDtos, "用户不存在");

        AccessControlFacePo accessControlFacePo = new AccessControlFacePo();
        accessControlFacePo.setFacePath("http://");
        accessControlFacePo.setCommunityId(accessControlOrgPo.getCommunityId());
        accessControlFacePo.setIdNumber("");
        accessControlFacePo.setComeId(tmpOrgStaffRelDto.getOrgId());
        accessControlFacePo.setComeType(AccessControlFaceDto.COME_TYPE_STAFF);
        accessControlFacePo.setEndTime(endDate);
        accessControlFacePo.setStartTime(startDate);
        accessControlFacePo.setCardNumber("");
        accessControlFacePo.setMachineId(machineId);
        accessControlFacePo.setMessage("待下发到门禁");
        accessControlFacePo.setMfId(GenerateCodeFactory.getGeneratorId("11"));
        accessControlFacePo.setName(staffDtos.get(0).getName());
        accessControlFacePo.setPersonId(staffDtos.get(0).getUserId());
        accessControlFacePo.setPersonType(AccessControlFaceDto.PERSON_TYPE_OWNER);
        accessControlFacePo.setState(AccessControlFaceDto.STATE_WAIT);
        accessControlFacePo.setRoomName("");
        accessControlFaceV1InnerServiceSMOImpl.saveAccessControlFace(accessControlFacePo);
    }

    private void doRoomOwner(AccessControlFloorPo accessControlFloorPo, RoomDto tmpRoomDto, String machineId,String startDate,String endDate) {

        OwnerDto ownerDto = new OwnerDto();
        ownerDto.setRoomId(tmpRoomDto.getRoomId());
        ownerDto.setCommunityId(tmpRoomDto.getCommunityId());
        List<OwnerDto> ownerDtos = ownerV1InnerServiceSMOImpl.queryRoomOwners(ownerDto);

        if (ownerDtos == null || ownerDtos.size() < 1) {
            return;
        }
        for (OwnerDto tmpOwnerDto : ownerDtos) {
            AccessControlFacePo accessControlFacePo = new AccessControlFacePo();
            accessControlFacePo.setFacePath("http://");
            accessControlFacePo.setCommunityId(tmpOwnerDto.getCommunityId());
            accessControlFacePo.setIdNumber(tmpOwnerDto.getIdCard());
            accessControlFacePo.setComeId(accessControlFloorPo.getUnitId());
            accessControlFacePo.setComeType(AccessControlFaceDto.COME_TYPE_FLOOR);
            accessControlFacePo.setEndTime(endDate);
            accessControlFacePo.setStartTime(startDate);
            accessControlFacePo.setCardNumber(tmpOwnerDto.getCardNumber());
            accessControlFacePo.setMachineId(machineId);
            accessControlFacePo.setMessage("待下发到门禁");
            accessControlFacePo.setMfId(GenerateCodeFactory.getGeneratorId("11"));
            accessControlFacePo.setName(tmpOwnerDto.getName());
            accessControlFacePo.setPersonId(tmpOwnerDto.getMemberId());
            accessControlFacePo.setPersonType(AccessControlFaceDto.PERSON_TYPE_OWNER);
            accessControlFacePo.setState(AccessControlFaceDto.STATE_WAIT);
            accessControlFacePo.setRoomName(tmpRoomDto.getFloorNum() + "-" + tmpOwnerDto.getUnitNum() + "-" + tmpOwnerDto.getRoomNum());
            accessControlFaceV1InnerServiceSMOImpl.saveAccessControlFace(accessControlFacePo);
        }

    }
}
