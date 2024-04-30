package com.java110.user.bmo.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.dto.file.FileRelDto;
import com.java110.bean.dto.owner.OwnerDto;
import com.java110.bean.dto.owner.OwnerRoomRelDto;
import com.java110.bean.dto.room.RoomDto;
import com.java110.bean.po.file.FileRelPo;
import com.java110.bean.po.owner.OwnerPo;
import com.java110.bean.po.owner.OwnerRoomRelPo;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.smo.IPhotoSMO;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ListUtil;
import com.java110.dto.personFace.PersonFaceDto;
import com.java110.intf.community.IRoomV1InnerServiceSMO;
import com.java110.intf.system.IFileRelInnerServiceSMO;
import com.java110.intf.user.IOwnerRoomRelV1InnerServiceSMO;
import com.java110.intf.user.IOwnerV1InnerServiceSMO;
import com.java110.intf.user.IPersonFaceV1InnerServiceSMO;
import com.java110.po.personFace.PersonFacePo;
import com.java110.user.bmo.IAuditPersonFaceAgreeBMO;
import com.java110.user.bmo.ISyncAccessControlBMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuditPersonFaceAgreeBMOImpl implements IAuditPersonFaceAgreeBMO {

    @Autowired
    private IRoomV1InnerServiceSMO roomV1InnerServiceSMOImpl;

    @Autowired
    private IOwnerRoomRelV1InnerServiceSMO ownerRoomRelV1InnerServiceSMOImpl;

    @Autowired
    private IOwnerV1InnerServiceSMO ownerV1InnerServiceSMOImpl;

    @Autowired
    private IPersonFaceV1InnerServiceSMO personFaceV1InnerServiceSMOImpl;

    @Autowired
    private IFileRelInnerServiceSMO fileRelInnerServiceSMOImpl;

    @Autowired
    private IPhotoSMO photoSMOImpl;

    @Autowired
    private ISyncAccessControlBMO syncAccessControlBMOImpl;

    @Override
    public void agree(PersonFacePo personFacePo) {


        PersonFaceDto personFaceDto = new PersonFaceDto();
        personFaceDto.setPfId(personFacePo.getPfId());
        personFaceDto.setCommunityId(personFacePo.getCommunityId());
        List<PersonFaceDto> personFaceDtos = personFaceV1InnerServiceSMOImpl.queryPersonFaces(personFaceDto);

        if (ListUtil.isNull(personFaceDtos)) {
            throw new IllegalArgumentException("审核人脸不存在");
        }

        RoomDto roomDto = new RoomDto();
        roomDto.setRoomId(personFaceDtos.get(0).getRoomId());
        roomDto.setCommunityId(personFaceDtos.get(0).getCommunityId());
        List<RoomDto> roomDtos = roomV1InnerServiceSMOImpl.queryRooms(roomDto);

        if (ListUtil.isNull(roomDtos)) {
            throw new CmdException("房屋不存在");
        }

        OwnerRoomRelDto ownerRoomRelDto = new OwnerRoomRelDto();
        ownerRoomRelDto.setRoomId(personFaceDtos.get(0).getRoomId());
        List<OwnerRoomRelDto> ownerRoomRelDtos = ownerRoomRelV1InnerServiceSMOImpl.queryOwnerRoomRels(ownerRoomRelDto);
        if (ListUtil.isNull(ownerRoomRelDtos)) {
            throw new CmdException("房屋未绑定业主");
        }
        List<String> memberIds = new ArrayList<>();
        for (OwnerRoomRelDto tmpOwnerRoomRelDto : ownerRoomRelDtos) {
            memberIds.add(tmpOwnerRoomRelDto.getOwnerId());
        }

        OwnerDto ownerDto = new OwnerDto();
        ownerDto.setMemberIds(memberIds.toArray(new String[memberIds.size()]));
        ownerDto.setCommunityId(personFaceDtos.get(0).getCommunityId());
        ownerDto.setLink(personFaceDtos.get(0).getPersonTel());
        List<OwnerDto> ownerDtos = ownerV1InnerServiceSMOImpl.queryOwners(ownerDto);

        if (ListUtil.isNull(ownerDtos)) {
            // todo 保存人员信息并且绑定关系
            addOwnerAndRel(personFaceDtos.get(0), memberIds);

        } else {
            /**
             * 修改业主人脸
             */
            editOwnerPhoto(ownerDtos, personFaceDtos);
        }


    }

    private void addOwnerAndRel(PersonFaceDto personFaceDto, List<String> memberIds) {

        OwnerPo ownerPo = new OwnerPo();
        ownerPo.setOwnerFlag(OwnerDto.OWNER_FLAG_TRUE);
        ownerPo.setAddress("无");
        ownerPo.setOwnerTypeCd(personFaceDto.getOwnerTypeCd());
        ownerPo.setCommunityId(personFaceDto.getCommunityId());
        ownerPo.setAge("1");
        ownerPo.setIdCard("");
        ownerPo.setLink(personFaceDto.getPersonTel());
        ownerPo.setMemberId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_ownerId));
        ownerPo.setName(personFaceDto.getPersonName());
        ownerPo.setSex("1");
        ownerPo.setState(OwnerDto.STATE_FINISH);
        ownerPo.setUserId("-1");
        ownerPo.setOwnerId(ownerPo.getMemberId());

        if (OwnerDto.OWNER_TYPE_CD_MEMBER.equals(personFaceDto.getOwnerTypeCd())) {
            OwnerDto ownerDto = new OwnerDto();
            ownerDto.setMemberIds(memberIds.toArray(new String[memberIds.size()]));
            ownerDto.setCommunityId(personFaceDto.getCommunityId());
            ownerDto.setOwnerTypeCd(OwnerDto.OWNER_TYPE_CD_OWNER);
            List<OwnerDto> ownerDtos = ownerV1InnerServiceSMOImpl.queryOwners(ownerDto);
            if (ListUtil.isNull(ownerDtos)) {
                ownerPo.setOwnerTypeCd(OwnerDto.OWNER_TYPE_CD_OWNER);
            } else {
                ownerPo.setOwnerId(ownerDtos.get(0).getOwnerId());
            }
        }

        int flag = ownerV1InnerServiceSMOImpl.saveOwner(ownerPo);
        if (flag < 1) {
            throw new CmdException("保存业主失败");
        }
        //有房屋信息，则直接绑定房屋和 业主的关系
        OwnerRoomRelPo ownerRoomRelPo = new OwnerRoomRelPo();
        ownerRoomRelPo.setRelId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_relId));
        ownerRoomRelPo.setOwnerId(ownerPo.getMemberId());
        ownerRoomRelPo.setRoomId(personFaceDto.getRoomId());
        ownerRoomRelPo.setState("2001");
        ownerRoomRelPo.setStartTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        ownerRoomRelPo.setEndTime(DateUtil.getLastTime());
        ownerRoomRelPo.setUserId("-1");

        flag = ownerRoomRelV1InnerServiceSMOImpl.saveOwnerRoomRel(ownerRoomRelPo);
        if (flag < 1) {
            throw new CmdException("保存业主房屋关系失败");
        }


        //保存照片
        photoSMOImpl.savePhoto(personFaceDto.getFaceUrl(),
                ownerPo.getMemberId(),
                ownerPo.getCommunityId(),
                "10000");

        //todo 同步门禁
        syncAccessControlBMOImpl.syncAccessControl(ownerPo.getMemberId());
    }

    private void editOwnerPhoto(List<OwnerDto> ownerDtos, List<PersonFaceDto> personFaceDtos) {
        FileRelDto fileRelDto = new FileRelDto();
        fileRelDto.setRelTypeCd("10000");
        fileRelDto.setObjId(ownerDtos.get(0).getMemberId());
        int flag = 0;
        List<FileRelDto> fileRelDtos = fileRelInnerServiceSMOImpl.queryFileRels(fileRelDto);
        if (fileRelDtos == null || fileRelDtos.size() == 0) {
            JSONObject businessUnit = new JSONObject();
            businessUnit.put("fileRelId", GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_fileRelId));
            businessUnit.put("relTypeCd", "10000");
            businessUnit.put("saveWay", "table");
            businessUnit.put("objId", ownerDtos.get(0).getMemberId());
            businessUnit.put("fileRealName", personFaceDtos.get(0).getFaceUrl());
            businessUnit.put("fileSaveName", personFaceDtos.get(0).getFaceUrl());
            FileRelPo fileRelPo = BeanConvertUtil.covertBean(businessUnit, FileRelPo.class);
            flag = fileRelInnerServiceSMOImpl.saveFileRel(fileRelPo);
            if (flag < 1) {
                throw new CmdException("保存文件失败");
            }
        } else {
            JSONObject businessUnit = new JSONObject();
            businessUnit.putAll(BeanConvertUtil.beanCovertMap(fileRelDtos.get(0)));
            businessUnit.put("fileRealName", personFaceDtos.get(0).getFaceUrl());
            businessUnit.put("fileSaveName", personFaceDtos.get(0).getFaceUrl());
            FileRelPo fileRelPo = BeanConvertUtil.covertBean(businessUnit, FileRelPo.class);
            flag = fileRelInnerServiceSMOImpl.updateFileRel(fileRelPo);
            if (flag < 1) {
                throw new CmdException("保存文件失败");
            }
        }


        //todo 同步门禁
        syncAccessControlBMOImpl.syncAccessControl(ownerDtos.get(0).getMemberId());
    }
}
