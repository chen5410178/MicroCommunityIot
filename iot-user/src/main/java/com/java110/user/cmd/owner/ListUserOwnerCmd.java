package com.java110.user.cmd.owner;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.file.FileRelDto;
import com.java110.bean.dto.owner.OwnerDto;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cache.MappingCache;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.constant.MappingConstant;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.CmdContextUtils;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.appUser.AppUserDto;
import com.java110.intf.car.IOwnerCarV1InnerServiceSMO;
import com.java110.intf.system.IFileRelInnerServiceSMO;
import com.java110.intf.user.IAppUserV1InnerServiceSMO;
import com.java110.intf.user.IOwnerInnerServiceSMO;
import com.java110.intf.user.IOwnerRoomRelV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 查询用户业主信息
 */
@Java110Cmd(serviceCode = "owner.listUserOwner")
public class ListUserOwnerCmd extends Cmd {

    @Autowired
    private IAppUserV1InnerServiceSMO appUserV1InnerServiceSMOImpl;

    @Autowired
    private IOwnerInnerServiceSMO ownerInnerServiceSMOImpl;

    @Autowired
    private IFileRelInnerServiceSMO fileRelInnerServiceSMOImpl;

    @Autowired
    private IOwnerRoomRelV1InnerServiceSMO ownerRoomRelV1InnerServiceSMOImpl;

    @Autowired
    private IOwnerCarV1InnerServiceSMO ownerCarV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        String userId = CmdContextUtils.getUserId(context);


        AppUserDto appUserDto = new AppUserDto();
        appUserDto.setUserId(userId);
        appUserDto.setCommunityId(reqJson.getString("communityId"));
        List<AppUserDto> appUserDtos = appUserV1InnerServiceSMOImpl.queryAppUsers(appUserDto);

        if (ListUtil.isNull(appUserDtos)) {
            throw new CmdException("业主未认证");
        }

        reqJson.put("memberId", appUserDtos.get(0).getMemberId());
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        //查询总记录数
        int total = ownerInnerServiceSMOImpl.queryOwnersCount(BeanConvertUtil.covertBean(reqJson, OwnerDto.class));
        List<OwnerDto> ownerDtos = null;
        if (total > 0) {
            ownerDtos = ownerInnerServiceSMOImpl.queryOwners(BeanConvertUtil.covertBean(reqJson, OwnerDto.class));
            String imgUrl = MappingCache.getValue(MappingConstant.FILE_DOMAIN, "IMG_PATH");

            // todo 查询 房屋数量
            queryRoomCount(ownerDtos);

            // todo 查询 车辆数
            queryCarCount(ownerDtos);

            for (OwnerDto ownerDto : ownerDtos) {
                //查询照片
                FileRelDto fileRelDto = new FileRelDto();
                fileRelDto.setObjId(ownerDto.getMemberId());
                fileRelDto.setRelTypeCd("10000"); //人员照片
                List<FileRelDto> fileRelDtos = fileRelInnerServiceSMOImpl.queryFileRels(fileRelDto);
                if (ListUtil.isNull(fileRelDtos)) {
                    continue;
                }

                List<String> urls = new ArrayList<>();
                for (FileRelDto fileRel : fileRelDtos) {

                    if (fileRel.getFileSaveName().startsWith("http")) {
                        urls.add(fileRel.getFileRealName());
                        ownerDto.setUrl(fileRel.getFileRealName());
                    } else {
                        urls.add(imgUrl + fileRel.getFileRealName());
                        ownerDto.setUrl(imgUrl + fileRel.getFileRealName());
                    }

                }
                ownerDto.setUrls(urls);
            }
        }

        context.setResponseEntity(ResultVo.createResponseEntity(ownerDtos));
    }

    private void queryRoomCount(List<OwnerDto> ownerDtos) {

        if (ListUtil.isNull(ownerDtos)) {
            return;
        }

        List<String> ownerIds = new ArrayList<>();
        for (OwnerDto ownerDto : ownerDtos) {
            ownerIds.add(ownerDto.getMemberId());
        }

        //查询业主房屋数
        List<Map> ownerRoomCounts = ownerRoomRelV1InnerServiceSMOImpl.queryRoomCountByOwnerIds(ownerIds);

        for (OwnerDto ownerDto : ownerDtos) {
            for (Map count : ownerRoomCounts) {
                if (StringUtil.isEmpty(ownerDto.getMemberId()) || StringUtil.isEmpty(count.get("ownerId").toString())) {
                    continue;
                }
                if (ownerDto.getMemberId().equals(count.get("ownerId").toString())) {
                    ownerDto.setRoomCount(count.get("roomCount").toString());
                }

            }
        }
    }

    private void queryCarCount(List<OwnerDto> ownerDtos) {
        if (ListUtil.isNull(ownerDtos)) {
            return;
        }

        List<String> ownerIds = new ArrayList<>();
        for (OwnerDto ownerDto : ownerDtos) {
            ownerIds.add(ownerDto.getMemberId());
        }

        List<Map> memberCounts = ownerCarV1InnerServiceSMOImpl.queryOwnerCarCountByOwnerIds(ownerIds);

        for (OwnerDto ownerDto : ownerDtos) {
            for (Map count : memberCounts) {
                if (ownerDto.getOwnerId().equals(count.get("ownerId"))) {
                    ownerDto.setCarCount(count.get("carCount").toString());
                }
            }
        }
    }
}
