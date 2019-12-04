package com.dyw.register.task;

import com.dyw.register.controller.EgciController;
import com.dyw.register.service.ProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.TimerTask;

@Service
public class QueueDataCleanTaskService extends TimerTask {
    private Logger logger = LoggerFactory.getLogger(QueueDataCleanTaskService.class);

    @Override
    public void run() {
        for (ProducerService producerService : EgciController.producerServiceList) {
            try {
                producerService.cleanData();
            } catch (Exception e) {
                logger.error("队列:" + producerService.getQueueName() + "删除数据出错", e);
            }
        }
    }
}
