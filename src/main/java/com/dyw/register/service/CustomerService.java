package com.dyw.register.service;

import com.dyw.register.controller.EgciController;
import com.rabbitmq.client.*;
import net.iharder.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class CustomerService implements Runnable {
    private Logger logger = LoggerFactory.getLogger(CustomerService.class);
    private String queueName;
    private Channel channel;
    private CardService cardService = new CardService();
    private FaceService faceService = new FaceService();
    private Thread t;
    private LoginService loginService = new LoginService();
    private NetStateService netStateService = new NetStateService();

    public CustomerService(String queueName, Channel channel) {
        this.queueName = queueName;
        this.channel = channel;
    }

    @Override
    public void run() {
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                String[] personInfo = new String(body).split("#");//人员信息：卡号、名称、人脸
//                String operationCode = personInfo[0];//操作码
//                String cardNo = personInfo[1];//卡号
//                String cardName = personInfo[2];//姓名
//                String picInfo = personInfo[3];//人脸信息
//                String ip = personInfo[4];//ip地址
                try {
                    if (!netStateService.ping(personInfo[4])) {
                        try {
                            channel.basicReject(envelope.getDeliveryTag(), true);
                        } catch (IOException e) {
                            logger.error("重新加入队列出错", e);
                        }
                        return;
                    }
                } catch (Exception e) {
                    logger.error("ping设备出错", e);
                }
                try {
                    //登陆
                    loginService.login(personInfo[4], EgciController.configEntity.getDevicePort(), EgciController.configEntity.getDeviceName(), EgciController.configEntity.getDevicePass());
                    if (loginService.getlUserID().longValue() > -1) {
                        //判断卡号是否存在，存在卡号则先删除和人脸;如果命令是2，则正好只执行删除操作
                        if (cardService.getCardInfo(personInfo[1], loginService.getlUserID(), queueName)) {
                            //删除卡号
                            if (cardService.delCardInfo(personInfo[1], loginService.getlUserID(), queueName)) {
                                //删除人脸，删除失败不需要操作
                                if (!faceService.delFace(personInfo[1], loginService.getlUserID())) {
                                    logger.info(personInfo[4] + ":人脸删除失败,错误码：" + EgciController.hcNetSDK.NET_DVR_GetLastError());
                                    channel.basicReject(envelope.getDeliveryTag(), false);//人脸删除失败不必重回队列
                                    return;
                                }
                            } else {
                                channel.basicReject(envelope.getDeliveryTag(), true);
                                return;
                            }
                        }
                        if (personInfo[0].equals("2")) {
                            channel.basicReject(envelope.getDeliveryTag(), false);
                            return;
                        }
                        //判断操作码
                        if (personInfo[0].equals("1")) {
                            //卡号姓名下发
                            if (cardService.setCardInfo(loginService.getlUserID(), personInfo[1], personInfo[2], "666666", queueName)) {
                                //人脸图片下发
                                if (faceService.setFaceInfo(personInfo[1], Base64.decode(personInfo[3]), loginService.getlUserID())) {
                                    channel.basicReject(envelope.getDeliveryTag(), false);
                                } else {
                                    channel.basicReject(envelope.getDeliveryTag(), true);
                                }
                            } else {
                                channel.basicReject(envelope.getDeliveryTag(), true);
                            }
                        }
                    } else {
                        channel.basicReject(envelope.getDeliveryTag(), true);
                    }
                } catch (Exception e) {
                    logger.error(personInfo[4] + ":卡号和人脸操作出错：" + e);
                    try {
                        channel.basicReject(envelope.getDeliveryTag(), true);
                    } catch (IOException e1) {
                        logger.error("重新加入队列出错", e1);
                    }
                } finally {
                    //不管有没有执行成功都执行资源释放操作
                    loginService.logout();
                }
            }
        };
        try {
            channel.basicConsume(queueName, false, consumer);
        } catch (IOException e) {
            logger.error("配置消费者出错", e);
        }
    }

    public void start() {
        logger.info("Starting: " + queueName);
        if (t == null) {
            t = new Thread(this, queueName);
            t.start();
        }
    }
}
