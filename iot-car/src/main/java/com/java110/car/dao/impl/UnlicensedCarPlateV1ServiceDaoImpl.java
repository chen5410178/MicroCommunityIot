package com.java110.car.dao.impl;

import com.java110.car.dao.IUnlicensedCarPlateV1ServiceDao;
import com.java110.core.db.dao.BaseServiceDao;
import com.java110.core.exception.DAOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("unlicensedCarPlateV1ServiceDaoImpl")
public class UnlicensedCarPlateV1ServiceDaoImpl extends BaseServiceDao implements IUnlicensedCarPlateV1ServiceDao {

    private static Logger logger = LoggerFactory.getLogger(UnlicensedCarPlateV1ServiceDaoImpl.class);

    @Override
    public int saveUnlicensedCarPlateInfo(Map info) throws DAOException {
        logger.debug("保存 saveUnlicensedCarPlateInfo 入参 info : {}", info);
        return sqlSessionTemplate.insert("unlicensedCarPlateV1ServiceDaoImpl.saveUnlicensedCarPlateInfo", info);
    }

    @Override
    public List<Map> getUnlicensedCarPlateInfo(Map info) throws DAOException {
        logger.debug("查询 getUnlicensedCarPlateInfo 入参 info : {}", info);
        return sqlSessionTemplate.selectList("unlicensedCarPlateV1ServiceDaoImpl.getUnlicensedCarPlateInfo", info);
    }

    @Override
    public int queryUnlicensedCarPlateCount(Map info) throws DAOException {
        logger.debug("查询 queryUnlicensedCarPlateCount 入参 info : {}", info);
        List<Map> list = sqlSessionTemplate.selectList("unlicensedCarPlateV1ServiceDaoImpl.queryUnlicensedCarPlateCount", info);
        if (list.size() < 1) {
            return 0;
        }
        return Integer.parseInt(list.get(0).get("count").toString());
    }
}
