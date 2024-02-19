package com.java110.accessControl.bmo.impl;

import com.java110.accessControl.bmo.IDeleteAccessControlFaceBMO;
import com.java110.accessControl.bmo.ISaveAccessControlFaceBMO;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.file.FileRelDto;
import com.java110.bean.dto.org.OrgStaffRelDto;
import com.java110.bean.dto.owner.OwnerDto;
import com.java110.bean.dto.room.RoomDto;
import com.java110.core.cache.MappingCache;
import com.java110.core.constant.MappingConstant;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.ListUtil;
import com.java110.dto.accessControlFace.AccessControlFaceDto;
import com.java110.dto.accessControlFloor.AccessControlFloorDto;
import com.java110.dto.user.UserDto;
import com.java110.intf.accessControl.IAccessControlFaceV1InnerServiceSMO;
import com.java110.intf.community.IRoomV1InnerServiceSMO;
import com.java110.intf.system.IFileRelInnerServiceSMO;
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
public class DeleteAccessControlFaceBMOImpl implements IDeleteAccessControlFaceBMO {

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

    @Autowired
    private IFileRelInnerServiceSMO fileRelInnerServiceSMOImpl;



    @Override
    @Async
    public void delete(AccessControlFloorDto accessControlFloorDto) {
        AccessControlFaceDto accessControlFaceDto = new AccessControlFaceDto();
        accessControlFaceDto.setComeId(accessControlFloorDto.getAcfId());
        accessControlFaceDto.setCommunityId(accessControlFloorDto.getCommunityId());
        List<AccessControlFaceDto> accessControlFaceDtos = accessControlFaceV1InnerServiceSMOImpl.queryAccessControlFaces(accessControlFaceDto);

        if (ListUtil.isNull(accessControlFaceDtos)) {
            return;
        }

        for (AccessControlFaceDto tmpAccessControlFaceDto : accessControlFaceDtos) {
            //todo 删除员工授权
            AccessControlFacePo accessControlFacePo = new AccessControlFacePo();
            accessControlFacePo.setComeId(tmpAccessControlFaceDto.getComeId());
            accessControlFacePo.setCommunityId(tmpAccessControlFaceDto.getCommunityId());
            accessControlFacePo.setMfId(tmpAccessControlFaceDto.getMfId());
            accessControlFacePo.setPersonId(tmpAccessControlFaceDto.getPersonId());
            accessControlFacePo.setName(tmpAccessControlFaceDto.getName());
            accessControlFacePo.setMachineId(tmpAccessControlFaceDto.getMachineId());
            accessControlFaceV1InnerServiceSMOImpl.deleteAccessControlFace(accessControlFacePo);
        }



    }
}
