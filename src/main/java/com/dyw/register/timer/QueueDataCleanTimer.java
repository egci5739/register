package com.dyw.register.timer;

import com.dyw.register.controller.EgciController;
import com.dyw.register.task.QueueDataCleanTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

@Service
public class QueueDataCleanTimer {
    @Autowired
    private QueueDataCleanTaskService queueDataCleanTaskService;

    private Logger logger = LoggerFactory.getLogger(QueueDataCleanTimer.class);
    //时间间隔(一天)
    private long PERIOD_DAY = 24 * 60 * 60 * 1000;

    public void open() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, EgciController.configEntity.getSynchronizationHour() - 1);//比同步时间提前一个小时
        calendar.set(Calendar.MINUTE, EgciController.configEntity.getSynchronizationMinute());
        calendar.set(Calendar.SECOND, EgciController.configEntity.getSynchronizationSecond());
        Date date = calendar.getTime(); //第一次执行定时任务的时间
        //如果第一次执行定时任务的时间 小于当前的时间
        //此时要在 第一次执行定时任务的时间加一天，以便此任务在下个时间点执行。如果不加一天，任务会立即执行。
        if (date.before(new Date())) {
            date = addDay(date, 1);
        }
        Timer timer = new Timer();
        logger.info("夜间同步时间：" + calendar.getTime());
        //安排指定的任务在指定的时间开始进行重复的固定延迟执行。
        timer.schedule(queueDataCleanTaskService, date, PERIOD_DAY);
    }

    // 增加或减少天数
    private Date addDay(Date date, int num) {
        Calendar startDT = Calendar.getInstance();
        startDT.setTime(date);
        startDT.add(Calendar.DAY_OF_MONTH, num);
        return startDT.getTime();
    }
}
