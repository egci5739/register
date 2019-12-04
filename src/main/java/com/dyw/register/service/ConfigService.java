package com.dyw.register.service;

import com.dyw.register.dao.ConfigDao;
import com.dyw.register.entity.ConfigEntity;
import com.dyw.register.entity.ConfigTableEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConfigService {
    @Autowired
    private ConfigDao configDao;

    public ConfigEntity getConfig() {
        return getConfig(configDao.getConfig());
    }

    /*
     * 读取本地配置文件
     * */
    private ConfigEntity getConfig(List<ConfigTableEntity> configTableEntityList) {
        ConfigEntity configEntity = new ConfigEntity();
        //创建一个DocumentBuilder的对象
        for (ConfigTableEntity configTableEntity : configTableEntityList) {
            //获取了属性名
            String attrName = configTableEntity.getConfigName();
            if (attrName.equals("devicePort")) {
                configEntity.setDevicePort(Short.parseShort(configTableEntity.getConfigValue()));
            }
            if (attrName.equals("deviceName")) {
                configEntity.setDeviceName(configTableEntity.getConfigValue());
            }
            if (attrName.equals("devicePass")) {
                configEntity.setDevicePass(configTableEntity.getConfigValue());
            }
            if (attrName.equals("dataBaseIp")) {
                configEntity.setDataBaseIp(configTableEntity.getConfigValue());
            }
            if (attrName.equals("dataBasePort")) {
                configEntity.setDataBasePort(Short.parseShort(configTableEntity.getConfigValue()));
            }
            if (attrName.equals("dataBaseName")) {
                configEntity.setDataBaseName(configTableEntity.getConfigValue());
            }
            if (attrName.equals("dataBasePass")) {
                configEntity.setDataBasePass(configTableEntity.getConfigValue());
            }
            if (attrName.equals("dataBaseLib")) {
                configEntity.setDataBaseLib(configTableEntity.getConfigValue());
            }
            if (attrName.equals("dataBaseTime")) {
                configEntity.setDataBaseTime(Long.parseLong(configTableEntity.getConfigValue()));
            }
            if (attrName.equals("queueIp")) {
                configEntity.setQueueIp(configTableEntity.getConfigValue());
            }
            if (attrName.equals("socketRegisterPort")) {
                configEntity.setSocketRegisterPort(Short.parseShort(configTableEntity.getConfigValue()));
            }
            if (attrName.equals("socketMonitorPort")) {
                configEntity.setSocketMonitorPort(Short.parseShort(configTableEntity.getConfigValue()));
            }
            if (attrName.equals("synchronization")) {
                configEntity.setSynchronization(configTableEntity.getConfigValue());
            }
            if (attrName.equals("synchronizationHour")) {
                configEntity.setSynchronizationHour(Integer.parseInt(configTableEntity.getConfigValue()));
            }
            if (attrName.equals("synchronizationMinute")) {
                configEntity.setSynchronizationMinute(Integer.parseInt(configTableEntity.getConfigValue()));
            }
            if (attrName.equals("synchronizationSecond")) {
                configEntity.setSynchronizationSecond(Integer.parseInt(configTableEntity.getConfigValue()));
            }
            if (attrName.equals("synchronizationTime")) {
                configEntity.setSynchronizationTime(Long.parseLong(configTableEntity.getConfigValue()));
            }
            if (attrName.equals("onGuardIp")) {
                configEntity.setOnGuardIp(configTableEntity.getConfigValue());
            }
            if (attrName.equals("onGuardPort")) {
                configEntity.setOnGuardPort(Short.parseShort(configTableEntity.getConfigValue()));
            }
            if (attrName.equals("alarmTime")) {
                configEntity.setAlarmTime(Long.parseLong(configTableEntity.getConfigValue()));
            }
            if (attrName.equals("pushTime")) {
                configEntity.setPushTime(Long.parseLong(configTableEntity.getConfigValue()));
            }
            if (attrName.equals("callBackTime")) {
                configEntity.setCallBackTime(Long.parseLong(configTableEntity.getConfigValue()));
            }
            if (attrName.equals("ntpServerIp")) {
                configEntity.setNtpServerIp(configTableEntity.getConfigValue());
            }
            if (attrName.equals("faceServerIp")) {
                configEntity.setFaceServerIp(configTableEntity.getConfigValue());
            }
            if (attrName.equals("faceServerPort")) {
                configEntity.setFaceServerPort(Short.parseShort(configTableEntity.getConfigValue()));
            }
            if (attrName.equals("nvrServerIp")) {
                configEntity.setNvrServerIp(configTableEntity.getConfigValue());
            }
            if (attrName.equals("nvrServerPort")) {
                configEntity.setNvrServerPort(Short.parseShort(configTableEntity.getConfigValue()));
            }
        }
        return configEntity;
    }
}
