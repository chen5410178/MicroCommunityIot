package com.java110.barrier.engine.migrate;

import com.java110.barrier.dao.ICarInoutHisV1ServiceDao;
import com.java110.barrier.dao.ICarInoutV1ServiceDao;
import com.java110.core.utils.ListUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("carInoutHistoryMigrateEngine")
public class CarInoutHistoryMigrateEngine {

    private static final int BATCH_LIMIT = 1000;
    private static final int MIGRATE_DAYS = 30;

    private static Logger logger = LoggerFactory.getLogger(CarInoutHistoryMigrateEngine.class);

    @Autowired
    private ICarInoutV1ServiceDao carInoutV1ServiceDaoImpl;

    @Autowired
    private ICarInoutHisV1ServiceDao carInoutHisV1ServiceDaoImpl;

    public int migrate() {
        String beforeTime = getBeforeTime();

        int outCount = migrateOutCarRecords(beforeTime);

        int inCount = migrateInCarRecords(beforeTime);

        return outCount + inCount;
    }

    private String getBeforeTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DAY_OF_MONTH, -MIGRATE_DAYS);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(calendar.getTime());
    }

    private int migrateOutCarRecords(String beforeTime) {
        Map param = new HashMap();
        param.put("beforeTime", beforeTime);
        param.put("limit", BATCH_LIMIT);

        List<Map> migrationList = carInoutV1ServiceDaoImpl.queryMigrationOutCarInouts(param);
        if (ListUtil.isNull(migrationList)) {
            return 0;
        }

        List<String> ciIds = new ArrayList<>();
        for (Map item : migrationList) {
            ciIds.add((String) item.get("ciId"));
        }

        Map saveParam = new HashMap();
        saveParam.put("ciIds", ciIds);
        carInoutHisV1ServiceDaoImpl.saveCarInoutHisInfos(saveParam);

        Map deleteParam = new HashMap();
        deleteParam.put("ciIds", ciIds);
        carInoutV1ServiceDaoImpl.deleteCarInoutByIds(deleteParam);

        logger.debug("出厂记录迁移完成，本批数量: {}", ciIds.size());
        return ciIds.size();
    }

    private int migrateInCarRecords(String beforeTime) {
        Map param = new HashMap();
        param.put("beforeTime", beforeTime);
        param.put("limit", BATCH_LIMIT);

        List<Map> migrationList = carInoutV1ServiceDaoImpl.queryMigrationCarInouts(param);
        if (ListUtil.isNull(migrationList)) {
            return 0;
        }

        List<String> ciIds = new ArrayList<>();
        for (Map item : migrationList) {
            ciIds.add((String) item.get("ciId"));
        }

        Map saveParam = new HashMap();
        saveParam.put("ciIds", ciIds);
        carInoutHisV1ServiceDaoImpl.saveCarInoutHisInfos(saveParam);

        Map deleteParam = new HashMap();
        deleteParam.put("ciIds", ciIds);
        carInoutV1ServiceDaoImpl.deleteCarInoutByIds(deleteParam);

        logger.debug("进场记录迁移完成，本批数量: {}", ciIds.size());
        return ciIds.size();
    }
}
