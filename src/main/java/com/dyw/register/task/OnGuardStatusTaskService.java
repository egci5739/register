package com.dyw.register.task;

import com.dyw.register.controller.EgciController;
import com.dyw.register.service.OnguardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;

public class OnGuardStatusTaskService extends TimerTask {
    private Logger logger = LoggerFactory.getLogger(OnGuardStatusTaskService.class);

    @Override
    public void run() {
        if (EgciController.onGuardStatus == 0) {
            try {
                OnguardService onguardService = new OnguardService();
                onguardService.run();
                logger.info("与onGuard服务程序重连");
            } catch (Exception e) {
                EgciController.onGuardStatus = 0;
                logger.error("与onGuard服务程序重连出错", e);
            }
        }
    }
}
