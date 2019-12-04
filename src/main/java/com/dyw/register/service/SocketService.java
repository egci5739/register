package com.dyw.register.service;

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
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

@Service
public class SocketService extends Thread {
    @Autowired
    private StaffDao staffDao;

    private Logger logger = LoggerFactory.getLogger(SocketService.class);

    public void setSocketInfo(Socket socketInfo) {
        this.socketInfo = socketInfo;
    }

    private Socket socketInfo;
    private ModeService modeService;
    private StatusService statusService;

    public SocketService() {
        //初始化设备状态
        statusService = new StatusService();
        //更改设备模式
        modeService = new ModeService();
    }

    /*
     * 数据处理
     * */
    @Override
    public void run() {
        //读取客户端发送来的信息
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(socketInfo.getInputStream()));
        } catch (IOException e) {
            logger.error("获取客户端消息失败：", e);
        }
        try {
            String mess = br.readLine();
            logger.info("客户端发来的消息：" + mess);
            String staffInfo;//结构体信息
            String operationCode = mess.substring(0, 1);
            switch (Integer.parseInt(operationCode)) {
                case 1://下发卡号人脸
                    //读取数据库获取人员信息
                    try {
                        List<StaffEntity> staffEntityList = staffDao.getSingleStaff(mess.substring(2));
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
                        if (staffEntityList.get(0).getStaffValidity() == 0) {
                            staffInfo = "2#" + staffEntityList.get(0).getStaffCardNumber() + "#test#none#";
                        } else {
                            staffInfo = "1#" + staffEntityList.get(0).getStaffCardNumber() + "#" + staffEntityList.get(0).getStaffName() + "#" + Base64.encodeBytes(staffEntityList.get(0).getStaffImage()) + "#";
                        }
                    } catch (Exception e) {
                        logger.error("读取数据库获取人员信息出错");
                        return;
                    }
                    //重新组织人员信息:操作码+卡号+名称+图片
                    //发送消息到队列中
                    for (int i = 0; i < EgciController.equipmentEntityList.size(); i++) {
                        EgciController.producerServiceList.get(i).sendToQueue(staffInfo.concat(EgciController.equipmentEntityList.get(i).getEquipmentIp()));
                    }
                    //返回正确消息给客户端
                    sendToClient(socketInfo, br, "success");
                    break;
                case 2://删除卡号和人脸
                    staffInfo = "2#" + mess.substring(2) + "#test#none#";
                    //发送消息到队列中
                    for (int i = 0; i < EgciController.equipmentEntityList.size(); i++) {
                        EgciController.producerServiceList.get(i).sendToQueue(staffInfo.concat(EgciController.equipmentEntityList.get(i).getEquipmentIp()));
                    }
                    //返回消息给客户端
                    sendToClient(socketInfo, br, "success");
                    break;
                case 6://设置采集采集人脸方式：0是身份证+人脸，1是不刷身份证
                    LoginService loginServiceFace = new LoginService();
                    String[] infoFace = mess.split("#");
                    loginServiceFace.login(infoFace[1], EgciController.devicePort, EgciController.deviceName, EgciController.devicePass);
                    //身份证+人脸
                    if (infoFace[2].equals("0")) {
                        modeService.changeMode(loginServiceFace.getlUserID(), (byte) 13);
                    }
                    //人脸
                    if (infoFace[2].equals("1")) {
                        modeService.changeMode(loginServiceFace.getlUserID(), (byte) 14);
                    }
                    //返回消息给客户端
                    sendToClient(socketInfo, br, "success");
                    loginServiceFace.logout();
                    break;
                case 7://获取采集设备的图片和身份证信息
                    String[] info7 = mess.split("#");
                    if (EgciController.deviceIpsFaceCollection.contains(info7[1])) {
                        EgciController.deviceIpsFaceCollection.remove(info7[1]);
                    }
                    if (EgciController.faceCollectionIpWithLogin.containsKey(info7[1])) {
                        EgciController.faceCollectionIpWithLogin.get(info7[1]).logout();
                    }
                    //先删除采集设备推送的队列
                    if (EgciController.faceCollectionIpWithProducer.containsKey(info7[1])) {
                        EgciController.faceCollectionIpWithProducer.get(info7[1]).deleteQueue();
                    }
                    Thread.sleep(2000);
                    //创建采集设备推送的队列
                    ProducerService producerService = new ProducerService("face:" + info7[1], EgciController.queueIp);
                    CustomerMonitorService customerMonitorService = new CustomerMonitorService("face:" + info7[1], producerService.getChannel(), socketInfo);
                    customerMonitorService.start();
                    //对采集设备布防
                    LoginService loginService7 = new LoginService();
                    loginService7.login(info7[1], EgciController.devicePort, EgciController.deviceName, EgciController.devicePass);
                    //每次连接恢复刷身份证的模式
                    modeService.changeMode(loginService7.getlUserID(), (byte) 13);
                    AlarmService alarmService = new AlarmService();
                    Boolean alarmStatus = alarmService.setupAlarmChan(loginService7.getlUserID());
                    EgciController.deviceIpsFaceCollection.add(info7[1]);
                    EgciController.faceCollectionIpWithProducer.put(info7[1], producerService);
                    EgciController.faceCollectionIpWithLogin.put(info7[1], loginService7);
                    if (!alarmStatus) {
                        //这里不要断开长连接
                        OutputStream os = socketInfo.getOutputStream();
                        os.write(("error\r\n").getBytes());
                        os.flush();
                    } else {
                        OutputStream os = socketInfo.getOutputStream();
                        os.write(("success\r\n").getBytes());
                        os.flush();
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            logger.error("socket数据处理出错：", e);
            sendToClient(socketInfo, br, "error");
        }
    }

    /*
     *返回消息到客户端
     * */
    private void sendToClient(Socket socket, BufferedReader br, String message) {
        try {
            OutputStream os = socket.getOutputStream();
            os.write((message + "\r\n").getBytes());
            os.flush();
            br.close();
            os.close();
            socket.close();
        } catch (IOException e) {
            logger.error("返回消息到客户端出错：" + e);
        }
    }
}
