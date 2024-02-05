package com.java110.user.bmo.impl;

import com.java110.bean.dto.file.FileRelDto;
import com.java110.bean.dto.owner.OwnerDto;
import com.java110.bean.dto.owner.OwnerRoomRelDto;
import com.java110.bean.dto.room.RoomDto;
import com.java110.core.cache.MappingCache;
import com.java110.core.constant.MappingConstant;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.accessControlFace.AccessControlFaceDto;
import com.java110.dto.accessControlFloor.AccessControlFloorDto;
import com.java110.intf.accessControl.IAccessControlFaceV1InnerServiceSMO;
import com.java110.intf.accessControl.IAccessControlFloorV1InnerServiceSMO;
import com.java110.intf.community.IRoomV1InnerServiceSMO;
import com.java110.intf.system.IFileRelInnerServiceSMO;
import com.java110.intf.user.IOwnerRoomRelV1InnerServiceSMO;
import com.java110.intf.user.IOwnerV1InnerServiceSMO;
import com.java110.po.accessControlFace.AccessControlFacePo;
import com.java110.user.bmo.ISyncAccessControlBMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SyncAccessControlBMOImpl implements ISyncAccessControlBMO {

    @Autowired
    private IOwnerV1InnerServiceSMO ownerV1InnerServiceSMOImpl;


    @Autowired
    private IAccessControlFaceV1InnerServiceSMO accessControlFaceV1InnerServiceSMOImpl;

    @Autowired
    private IOwnerRoomRelV1InnerServiceSMO ownerRoomRelV1InnerServiceSMOImpl;

    @Autowired
    private IRoomV1InnerServiceSMO roomV1InnerServiceSMOImpl;

    @Autowired
    private IAccessControlFloorV1InnerServiceSMO accessControlFloorV1InnerServiceSMOImpl;

    @Autowired
    private IFileRelInnerServiceSMO fileRelInnerServiceSMOImpl;

    @Override
    public void syncAccessControl(String memberId) {

        OwnerDto ownerDto = new OwnerDto();
        ownerDto.setMemberId(memberId);
        List<OwnerDto> ownerDtos = ownerV1InnerServiceSMOImpl.queryOwners(ownerDto);
        if (ListUtil.isNull(ownerDtos)) {
            return;
        }

        ownerDto = ownerDtos.get(0);

        String imgUrl = MappingCache.getValue(MappingConstant.FILE_DOMAIN, "IMG_PATH");
        FileRelDto fileRelDto = new FileRelDto();
        fileRelDto.setObjId(ownerDto.getMemberId());
        fileRelDto.setRelTypeCd("10000"); //人员照片
        List<FileRelDto> fileRelDtos = fileRelInnerServiceSMOImpl.queryFileRels(fileRelDto);
        if (fileRelDtos != null && !fileRelDtos.isEmpty()) {
            ownerDto.setUrl(imgUrl + fileRelDtos.get(0).getFileRealName());
        }


        AccessControlFaceDto accessControlFaceDto = new AccessControlFaceDto();
        accessControlFaceDto.setPersonId(memberId);
        List<AccessControlFaceDto> accessControlFaceDtos = accessControlFaceV1InnerServiceSMOImpl.queryAccessControlFaces(accessControlFaceDto);
        AccessControlFacePo accessControlFacePo = null;
        if (!ListUtil.isNull(accessControlFaceDtos)) {
            for (AccessControlFaceDto tmpAccessControlFaceDto : accessControlFaceDtos) {
                accessControlFacePo = BeanConvertUtil.covertBean(tmpAccessControlFaceDto, AccessControlFacePo.class);
                accessControlFacePo.setState(AccessControlFaceDto.STATE_WAIT);
                accessControlFacePo.setMessage("待下发到门禁");
                accessControlFacePo.setFacePath(ownerDto.getUrl());
                accessControlFaceV1InnerServiceSMOImpl.updateAccessControlFace(accessControlFacePo);
            }
            return;
        }
        // todo 查询业主房屋

        OwnerRoomRelDto ownerRoomRelDto = new OwnerRoomRelDto();
        ownerRoomRelDto.setOwnerId(ownerDto.getMemberId());
        List<OwnerRoomRelDto> ownerRoomRelDtos = ownerRoomRelV1InnerServiceSMOImpl.queryOwnerRoomRels(ownerRoomRelDto);

        if (ListUtil.isNull(ownerRoomRelDtos)) {
            return;
        }

        List<String> roomIds = new ArrayList<>();

        for (OwnerRoomRelDto tmpOwnerRoomRelDto : ownerRoomRelDtos) {
            roomIds.add(tmpOwnerRoomRelDto.getRoomId());
        }

        RoomDto roomDto = new RoomDto();
        roomDto.setRoomIds(roomIds.toArray(new String[roomIds.size()]));
        List<RoomDto> roomDtos = roomV1InnerServiceSMOImpl.queryRooms(roomDto);

        if (ListUtil.isNull(roomDtos)) {
            return;
        }

        for (RoomDto tmpRoomDto : roomDtos) {
            String unitId = tmpRoomDto.getUnitId();
            if (StringUtil.isEmpty(unitId)) {
                continue;
            }

            AccessControlFloorDto accessControlFloorDto = new AccessControlFloorDto();
            accessControlFloorDto.setUnitId(unitId);
            accessControlFloorDto.setCommunityId(roomDtos.get(0).getCommunityId());
            List<AccessControlFloorDto> accessControlFloorDtos = accessControlFloorV1InnerServiceSMOImpl.queryAccessControlFloors(accessControlFloorDto);
            if (ListUtil.isNull(accessControlFloorDtos)) {
                continue;
            }
            for (AccessControlFloorDto tmpAccessControlFloorDto : accessControlFloorDtos) {
                accessControlFacePo = new AccessControlFacePo();
                accessControlFacePo.setFacePath(ownerDto.getUrl());
                accessControlFacePo.setCommunityId(tmpAccessControlFloorDto.getCommunityId());
                accessControlFacePo.setIdNumber(ownerDto.getIdCard());
                accessControlFacePo.setComeId(tmpAccessControlFloorDto.getAcfId());
                accessControlFacePo.setComeType(AccessControlFaceDto.COME_TYPE_FLOOR);
                accessControlFacePo.setEndTime(DateUtil.getFormatTimeStringA(ownerRoomRelDtos.get(0).getEndTime()));
                accessControlFacePo.setStartTime(DateUtil.getFormatTimeStringA(ownerRoomRelDtos.get(0).getStartTime()));
                accessControlFacePo.setCardNumber(ownerDto.getCardNumber());
                accessControlFacePo.setMachineId(tmpAccessControlFloorDto.getMachineId());
                accessControlFacePo.setMessage("待下发到门禁");
                accessControlFacePo.setMfId(GenerateCodeFactory.getGeneratorId("11"));
                accessControlFacePo.setName(ownerDto.getName());
                accessControlFacePo.setPersonId(ownerDto.getMemberId());
                accessControlFacePo.setPersonType(AccessControlFaceDto.PERSON_TYPE_OWNER);
                accessControlFacePo.setState(AccessControlFaceDto.STATE_WAIT);
                accessControlFacePo.setRoomName(roomDtos.get(0).getFloorNum() + "-" + roomDtos.get(0).getUnitNum() + "-" + roomDtos.get(0).getRoomNum());
                accessControlFaceV1InnerServiceSMOImpl.saveAccessControlFace(accessControlFacePo);
            }
        }
    }

    @Override
    public void syncDeleteAccessControl(String memberId) {
        AccessControlFaceDto accessControlFaceDto = new AccessControlFaceDto();
        accessControlFaceDto.setPersonId(memberId);
        List<AccessControlFaceDto> accessControlFaceDtos = accessControlFaceV1InnerServiceSMOImpl.queryAccessControlFaces(accessControlFaceDto);
        AccessControlFacePo accessControlFacePo = null;
        if (ListUtil.isNull(accessControlFaceDtos)) {
            return;
        }
        for (AccessControlFaceDto tmpAccessControlFaceDto : accessControlFaceDtos) {
            accessControlFacePo = BeanConvertUtil.covertBean(tmpAccessControlFaceDto, AccessControlFacePo.class);
            accessControlFacePo.setMfId(tmpAccessControlFaceDto.getMfId());
            accessControlFaceV1InnerServiceSMOImpl.deleteAccessControlFace(accessControlFacePo);

        }

    }
}
