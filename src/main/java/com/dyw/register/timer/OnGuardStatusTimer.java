package com.dyw.register.timer;

import com.dyw.register.task.OnGuardStatusTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;

public class OnGuardStatusTimer {
    private static Logger logger = LoggerFactory.getLogger(OnGuardStatusTimer.class);

    public static void open() {
        Timer timer = new Timer();
        OnGuardStatusTaskService onGuardStatusTaskService = new OnGuardStatusTaskService();
        timer.schedule(onGuardStatusTaskService, 60000, 20000);
        logger.info("启用onGuard状态监测功能");
    }
}
