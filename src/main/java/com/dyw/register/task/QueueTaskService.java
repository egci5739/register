package com.dyw.register.task;

import com.dyw.register.controller.EgciController;
import com.dyw.register.service.CustomerService;
import com.dyw.register.service.ProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;

public class QueueTaskService extends TimerTask {
    private Logger logger = LoggerFactory.getLogger(QueueTaskService.class);

    @Override
    public void run() {
        for (ProducerService producerService : EgciController.producerServiceList) {
            if (producerService.getConsumerCount() < 1) {
                logger.info("队列：" + producerService.getQueueName() + " 异常");
                CustomerService customerService = new CustomerService(producerService.getQueueName(), producerService.getChannel());
                customerService.start();
            }
        }
    }
}
