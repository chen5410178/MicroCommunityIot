package com.java110.community.dao.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.community.dao.IRoomsTreeServiceDao;
import com.java110.core.db.dao.BaseServiceDao;
import com.java110.core.factory.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName RoomsTreeServiceDaoImpl
 * @Description TODO
 * @Author wuxw
 * @Date 2020/10/15 22:15
 * @Version 1.0
 * add by wuxw 2020/10/15
 **/
@Service("roomsTreeServiceDaoImpl")
public class RoomsTreeServiceDaoImpl extends BaseServiceDao implements IRoomsTreeServiceDao {

    private static Logger logger = LoggerFactory.getLogger(RoomsTreeServiceDaoImpl.class);

    @Override
    public List<Map> queryRoomsTree(Map info) {
        logger.debug("查询queryRoomsTree信息 入参 info : {}", JSONObject.toJSONString(info));

        List<Map> communityDtos = sqlSessionTemplate.selectList("roomsTreeServiceDaoImpl.queryRoomsTree", info);

        return communityDtos;
    }
}