package com.java110.charge.smo.impl;


import com.java110.bean.dto.PageDto;
import com.java110.charge.dao.ISmallWeChatServiceDao;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.dto.smallWeChat.SmallWeChatDto;
import com.java110.intf.charge.ISmallWeChatInnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ClassName FloorInnerServiceSMOImpl
 * @Description 小程序管理内部服务实现类
 * @Author wuxw
 * @Date 2019/4/24 9:20
 * @Version 1.0
 * add by wuxw 2019/4/24
 **/
@RestController
public class SmallWeChatInnerServiceSMOImpl implements ISmallWeChatInnerServiceSMO {

    @Autowired
    private ISmallWeChatServiceDao smallWeChatServiceDaoImpl;

    @Override
    public List<SmallWeChatDto> querySmallWeChats(@RequestBody SmallWeChatDto smallWeChatDto) {

        //校验是否传了 分页信息

        int page = smallWeChatDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            smallWeChatDto.setPage((page - 1) * smallWeChatDto.getRow());
        }

        List<SmallWeChatDto> smallWeChats = BeanConvertUtil.covertBeanList(smallWeChatServiceDaoImpl.getSmallWeChatInfo(BeanConvertUtil.beanCovertMap(smallWeChatDto)), SmallWeChatDto.class);

        return smallWeChats;
    }




    @Override
    public int querySmallWeChatsCount(@RequestBody SmallWeChatDto smallWeChatDto) {
        return smallWeChatServiceDaoImpl.querySmallWeChatsCount(BeanConvertUtil.beanCovertMap(smallWeChatDto));
    }

    public ISmallWeChatServiceDao getSmallWeChatServiceDaoImpl() {
        return smallWeChatServiceDaoImpl;
    }

    public void setSmallWeChatServiceDaoImpl(ISmallWeChatServiceDao smallWeChatServiceDaoImpl) {
        this.smallWeChatServiceDaoImpl = smallWeChatServiceDaoImpl;
    }
}
