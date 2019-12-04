package com.dyw.register.service;

import com.alibaba.fastjson.JSON;
import com.dyw.register.controller.EgciController;
import com.dyw.register.dao.StaffDao;
import com.dyw.register.entity.StaffEntity;
import net.iharder.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;

@Service
public class OnguardService extends Thread {
    @Autowired
    private StaffDao staffDao;

    private static Logger logger = LoggerFactory.getLogger(OnguardService.class);

    @Override
    public void run() {
        try {
            Socket socket = new Socket(EgciController.configEntity.getOnGuardIp(), EgciController.configEntity.getOnGuardPort());
            //接口服务端信息
            logger.info("连接onGuard服务器成功，等待接收数据...");
            EgciController.onGuardStatus = 1;
            int staffCount;
            StaffEntity temporaryStaffEntity = new StaffEntity();
            while (true) {
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String info = br.readLine();
                    try {
                        temporaryStaffEntity = JSON.parseObject(info, StaffEntity.class);//onGuard传过来的信息
                    } catch (Exception e) {
                        logger.error("字符串转对象出错", e);
                        continue;
                    }

                    logger.info("从onGuard接收到的信息：" + temporaryStaffEntity.toString());
                    //判断卡号是否已存在
                    staffCount = staffDao.getSingleStaff(temporaryStaffEntity.getStaffCardNumber()).size();
                    switch (temporaryStaffEntity.getType()) {
                        /*
                         * type
                         * 1:新增
                         * 2.修改
                         * 3.删除
                         * 4.修改卡号
                         * */
                        case 1://新增
                            if (staffCount > 0) {//卡号已存在
                                temporaryStaffEntity.setStaffValidity(1);//设定为 待拍照 状态  2-1
                                update(temporaryStaffEntity);
                            } else {//卡号不存在
                                temporaryStaffEntity.setStaffValidity(1);//设定为 待拍照 状态  2-1
                                insert(temporaryStaffEntity);
                            }
                            break;
                        case 2://修改：非卡号
                            if (staffCount > 0) {
                                temporaryStaffEntity.setStaffValidity(1);//  2-1
                                update(temporaryStaffEntity);
                            } else {//attention：这里需要完善
                                temporaryStaffEntity.setStaffValidity(1);//  2-1
                                insert(temporaryStaffEntity);
                            }
                            break;
                        case 3://删除
                            if (staffCount > 0)
                                delete(temporaryStaffEntity);
                            break;
                        case 4://修改卡号
//                            if (staffCount > 0) {
                            update(temporaryStaffEntity);
//                            }
                            break;
                        default:
                            break;
                    }
                } catch (IOException e) {
                    try {
                        socket.close();
                    } catch (Exception ignore) {
                    }
                    EgciController.onGuardStatus = 0;
                    logger.error("与onGuard对接程序断开", e);
                    break;
                }
            }
        } catch (Exception e) {
            EgciController.onGuardStatus = 0;
            logger.error("连接onGuard服务器出错", e);
        }
    }

    /*
     * 新增数据
     * */
    private void insert(StaffEntity temporaryStaffEntity) {
        try {
            staffDao.insertStaff(temporaryStaffEntity);
        } catch (Exception e) {
            logger.error("onGuard数据新增失败:", e);
        }
    }

    /*
     * 更新数据
     * */
    private void update(StaffEntity temporaryStaffEntity) {
        //更新人员表信息
        if (temporaryStaffEntity.getType() == 4) {//修改卡号操作
            try {
                staffDao.updateStaffWithCardChange(temporaryStaffEntity);
                //先删除一体机旧卡号
                delMachineInfo(temporaryStaffEntity.getStaffOldCardNumber());
                Thread.sleep(2000);
                //下发新卡号
                addMachineInfo(temporaryStaffEntity.getStaffCardNumber());
            } catch (Exception e) {
                logger.error("更新人员表信息出错", e);
            }
        } else {
            try {
                staffDao.updateStaff(temporaryStaffEntity);
//                if (temporaryStaffEntity.getStaffValidity() == 1) {//根据状态值判断是否更新一体机设备中的人员信息
//                    //下发操作
//                    addMachineInfo(temporaryStaffEntity.getStaffCardNumber());
//                } else if (temporaryStaffEntity.getStaffValidity() == 0) {
//                    //删除一体机数据
//                    delMachineInfo(temporaryStaffEntity.getStaffCardNumber());
//                }
                //先删除一体机旧卡号
                delMachineInfo(temporaryStaffEntity.getStaffOldCardNumber());
                Thread.sleep(2000);
                //下发新卡号
                addMachineInfo(temporaryStaffEntity.getStaffCardNumber());
            } catch (Exception e) {
                logger.error("更新人员表信息出错", e);
            }
        }
    }

    /*
     * 删除数据
     * */
    private void delete(StaffEntity temporaryStaffEntity) {
        try {
            //删除人员表数据
            staffDao.deleteStaff(temporaryStaffEntity.getStaffCardNumber());
            //删除一体机设备人员信息
            delMachineInfo(temporaryStaffEntity.getStaffCardNumber());
        } catch (Exception e) {
            logger.error("人员数据删除出错", e);
        }
    }

    /*
     * 下发一体机信息
     * */
    private void addMachineInfo(String cardNumber) {
        try {
            //读取数据库获取人员信息
            List<StaffEntity> staffEntityList = staffDao.getSingleStaff(cardNumber);
            if (staffEntityList.size() == 0) {
                logger.info("人员信息不存在");
                return;
            }
            try {
                if (staffEntityList.get(0).getStaffImage().length == 0) {
                    logger.info("该卡号没有图片");
                    return;
                }
            } catch (Exception ignore) {
                logger.error("该卡号没有图片");
                return;
            }
            //重新组织人员信息:操作码+卡号+名称+图片
            String staffInfo = "1#" + staffEntityList.get(0).getStaffCardNumber() + "#" + staffEntityList.get(0).getStaffName() + "#" + Base64.encodeBytes(staffEntityList.get(0).getStaffImage()) + "#";
            //发送消息到队列中
            for (int i = 0; i < EgciController.equipmentEntityList.size(); i++) {
                EgciController.producerServiceList.get(i).sendToQueue(staffInfo.concat(EgciController.equipmentEntityList.get(i).getEquipmentIp()));
            }
        } catch (Exception e) {
            logger.error("更新设备人员信息出错", e);
        }
    }

    /*
     * 删除一体机信息
     * */
    private void delMachineInfo(String cardNumber) {
        try {
            String staffInfo = "2#" + cardNumber + "#test#none#";
            for (int i = 0; i < EgciController.equipmentEntityList.size(); i++) {
                EgciController.producerServiceList.get(i).sendToQueue(staffInfo.concat(EgciController.equipmentEntityList.get(i).getEquipmentIp()));
            }
        } catch (Exception e) {
            logger.error("删除设备人员信息出错", e);
        }
    }
}