package com.dyw.register.handler;

import com.dyw.register.HCNetSDK;
import com.dyw.register.service.CallBack4AlarmService;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AlarmHandler implements HCNetSDK.FMSGCallBack_V31 {
    @Autowired
    private CallBack4AlarmService callBack4AlarmService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean invoke(NativeLong lCommand,
                          HCNetSDK.NET_DVR_ALARMER equipmentInfo,
                          Pointer alarmInfo,
                          int dwBufLen,
                          Pointer pUser) {
        try {
            callBack4AlarmService.alarmNotice(lCommand, equipmentInfo, alarmInfo, dwBufLen, pUser);
            Thread.sleep(300);//延迟
            return true;
        } catch (Exception e) {
            logger.error("获取一体机通行信息出错", e);
            return true;
        }
    }
}
