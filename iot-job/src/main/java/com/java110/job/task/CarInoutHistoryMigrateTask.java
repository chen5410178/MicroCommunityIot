package com.java110.job.task;

import com.java110.bean.dto.task.TaskDto;
import com.java110.core.factory.LoggerFactory;
import com.java110.intf.barrier.ICarInoutHistoryMigrateV1InnerServiceSMO;
import com.java110.job.quartz.TaskSystemQuartz;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CarInoutHistoryMigrateTask extends TaskSystemQuartz {

    private static Logger logger = LoggerFactory.getLogger(CarInoutHistoryMigrateTask.class);

    @Autowired
    private ICarInoutHistoryMigrateV1InnerServiceSMO carInoutHistoryMigrateV1InnerServiceSMOImpl;

    @Override
    protected void process(TaskDto taskDto) {
        logger.debug("开始执行车辆出入场记录历史归档任务" + taskDto.toString());

        try {
            int totalMigrated = 0;
            int batchCount;
            do {
                batchCount = carInoutHistoryMigrateV1InnerServiceSMOImpl.migrateCarInoutHis();
                totalMigrated += batchCount;
                logger.debug("本批次迁移记录数: {}, 累计迁移记录数: {}", batchCount, totalMigrated);
            } while (batchCount > 0);
            logger.debug("车辆出入场记录历史归档任务执行完成，共迁移记录数: {}", totalMigrated);
        } catch (Exception e) {
            logger.error("车辆出入场记录历史归档任务执行失败", e);
        }
    }
}
