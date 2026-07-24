package com.java110.car.dao;

import com.java110.core.exception.DAOException;

import java.util.List;
import java.util.Map;

public interface IUnlicensedCarPlateV1ServiceDao {

    int saveUnlicensedCarPlateInfo(Map info) throws DAOException;

    List<Map> getUnlicensedCarPlateInfo(Map info) throws DAOException;

    int queryUnlicensedCarPlateCount(Map info) throws DAOException;
}
