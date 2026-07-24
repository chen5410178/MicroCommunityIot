package com.java110.barrier.smo.impl;

import com.java110.barrier.engine.migrate.CarInoutHistoryMigrateEngine;
import com.java110.intf.barrier.ICarInoutHistoryMigrateV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CarInoutHistoryMigrateV1InnerServiceSMOImpl implements ICarInoutHistoryMigrateV1InnerServiceSMO {

    @Autowired
    private CarInoutHistoryMigrateEngine carInoutHistoryMigrateEngine;

    @Override
    public int migrateCarInoutHis() {
        return carInoutHistoryMigrateEngine.migrate();
    }
}
