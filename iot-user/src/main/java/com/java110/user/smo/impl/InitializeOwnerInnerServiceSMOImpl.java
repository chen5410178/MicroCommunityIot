package com.java110.user.smo.impl;

import com.java110.dto.appUser.AppUserDto;
import com.java110.intf.user.IAppUserV1InnerServiceSMO;
import com.java110.intf.user.IInitializeOwnerInnerServiceSMO;
import com.java110.user.dao.IInitializeOwneServiceDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName FloorInnerServiceSMOImpl
 * @Description 业主内部服务实现类
 * @Author wuxw
 * @Date 2019/4/24 9:20
 * @Version 1.0
 * add by wuxw 2019/4/24
 **/
@RestController
public class InitializeOwnerInnerServiceSMOImpl  implements IInitializeOwnerInnerServiceSMO {


    public static final int DEFAULT_ROW = 200;

    @Autowired
    private IInitializeOwneServiceDao initializeOwnerServiceDaoImpl;

    @Autowired
    private IAppUserV1InnerServiceSMO ownerAppUserV1InnerServiceSMOImpl;

    @Override
    public int deleteBuildingOwner(@RequestBody Map communityId) {
        int deleteFast = initializeOwnerServiceDaoImpl.deleteBuildingOwner(communityId);

        //todo 删除 业主绑定数据

        AppUserDto ownerAppUserDto = new AppUserDto();
        ownerAppUserDto.setCommunityId(communityId.get("communityId").toString());
        int count = ownerAppUserV1InnerServiceSMOImpl.queryAppUsersCount(ownerAppUserDto);
        int page = (int) Math.floor(count / DEFAULT_ROW);
        List<String> userIds = null;
        for (int pageIndex = 1; pageIndex < page + 1; pageIndex++) {
            ownerAppUserDto = new AppUserDto();
            ownerAppUserDto.setPage(pageIndex);
            ownerAppUserDto.setRow(DEFAULT_ROW);
            ownerAppUserDto.setCommunityId(communityId.get("communityId").toString());
            List<AppUserDto> ownerAppUserDtos = ownerAppUserV1InnerServiceSMOImpl.queryAppUsers(ownerAppUserDto);
            if (ownerAppUserDtos == null || ownerAppUserDtos.size() < 1) {
                continue;
            }
            userIds = new ArrayList<>();
            for (AppUserDto tmpOwnerAppUserDto : ownerAppUserDtos) {
                userIds.add(tmpOwnerAppUserDto.getUserId());
            }
            communityId.put("userIds", userIds.toArray(new String[userIds.size()]));
            initializeOwnerServiceDaoImpl.deleteUser(communityId);

        }
        initializeOwnerServiceDaoImpl.deleteOwnerAppUser(communityId);
        return deleteFast;
    }

    public IInitializeOwneServiceDao getInitializeOwnerServiceDaoImpl() {
        return initializeOwnerServiceDaoImpl;
    }

    public void setInitializeOwnerServiceDaoImpl(IInitializeOwneServiceDao initializeOwnerServiceDaoImpl) {
        this.initializeOwnerServiceDaoImpl = initializeOwnerServiceDaoImpl;
    }
}
