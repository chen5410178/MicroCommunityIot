package com.java110.job.task;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.dto.task.TaskDto;
import com.java110.core.factory.LoggerFactory;
import com.java110.intf.charge.IChargeTimeoutV1InnerServiceSMO;
import com.java110.job.quartz.TaskSystemQuartz;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 充电订单超时自动结算与停止
 * <p>
 * 两轮：充电时长（充满自停 999 按 10 小时）+ 10 分钟缓冲后，任务内直接结算并下发停止；
 * 四轮：超过 24 小时，开启中(1000) 直接置空闲并置失败退预扣款，充电中(1001) 只下发停止指令。
 */
@Component
public class ChargeOrderTimeoutTask extends TaskSystemQuartz {

    private static Logger logger = LoggerFactory.getLogger(ChargeOrderTimeoutTask.class);

    @Autowired
    private IChargeTimeoutV1InnerServiceSMO chargeTimeoutV1InnerServiceSMOImpl;

    @Override
    protected void process(TaskDto taskDto) {
        logger.debug("开始执行充电订单超时自动结算与停止任务" + taskDto.toString());

        try {
            int count = chargeTimeoutV1InnerServiceSMOImpl.timeoutStopCharge(new JSONObject());
            logger.debug("充电订单超时自动结算与停止任务执行完成，处理订单数: {}", count);
        } catch (Exception e) {
            logger.error("充电订单超时自动结算与停止任务执行失败", e);
        }
    }
}
