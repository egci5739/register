package com.dyw.register.service;

import com.dyw.register.controller.EgciController;
import com.dyw.register.dao.EquipmentDao;
import com.dyw.register.entity.EquipmentEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EquipmentService {
    @Autowired
    private EquipmentDao equipmentDao;

    private Logger logger = LoggerFactory.getLogger(EquipmentService.class);

    public void initEquipmentInfo() {
        try {
            //获取设备ip列表
            EgciController.equipmentEntityList = equipmentDao.getAllEquipment();
            for (EquipmentEntity equipmentEntity : EgciController.equipmentEntityList) {
                //如果对象中有数据，就会循环打印出来
                EgciController.equipmentMaps.put(equipmentEntity.getEquipmentIp(), equipmentEntity);
                logger.info("设备IP:" + equipmentEntity.getEquipmentIp());
            }
        } catch (Exception e) {
            logger.error("连接数据库和获取全部设备IP失败：", e);
        }
    }
}
