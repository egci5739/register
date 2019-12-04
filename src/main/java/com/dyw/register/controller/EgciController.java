package com.dyw.register.controller;

import com.dyw.register.HCNetSDK;
import com.dyw.register.entity.ConfigEntity;
import com.dyw.register.entity.EquipmentEntity;
import com.dyw.register.handler.AlarmHandler;
import com.dyw.register.service.*;
import com.dyw.register.timer.OnGuardStatusTimer;
import com.dyw.register.timer.QueueDataCleanTimer;
import com.dyw.register.timer.QueueTimer;
import com.dyw.register.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.*;

@Controller
public class EgciController {
    @Autowired
    private ConfigService configService;
    @Autowired
    private ProcessService processService;
    @Autowired
    private EquipmentService equipmentService;
    @Autowired
    private AlarmHandler alarmHandler;
    @Autowired
    private QueueDataCleanTimer queueDataCleanTimer;

    //配置文件
    public static ConfigEntity configEntity;
    //一体机变量
    public static short devicePort;
    public static String deviceName;
    public static String devicePass;
    //全局变量
    private static Logger Elogger;
    public static List<EquipmentEntity> equipmentEntityList = new ArrayList<>();//所有一体机设备信息
    public static Set<EquipmentEntity> equipmentEntitySetOnline = new HashSet<>();//在线设备
    public static Map<String, EquipmentEntity> equipmentMaps = new HashMap<>();//所有设备的信息，包含设备名称
    //队列的ip
    public static String queueIp;
    //初始化生产者数组
    public static List<ProducerService> producerServiceList;
    //初始化静态对象
    public static HCNetSDK hcNetSDK;
    //采集设备IP合集，用来判断返回的消息来自采集设备还是一体机
    public static Set<String> deviceIpsFaceCollection;
    //采集设备和对应的生产者的map
    public static Map<String, ProducerService> faceCollectionIpWithProducer;
    //采集设备和对应的登陆信息的map
    public static Map<String, LoginService> faceCollectionIpWithLogin;
    //onGuard连接状态:1-成功；0-失败
    public static int onGuardStatus;

    /*
     * 初始化函数
     * */
    public void initServer() {
        //初始化日志对象
        try {
            Elogger = LoggerFactory.getLogger(EgciController.class);
        } catch (Exception e) {
            Elogger.error("初始化日志对象出错", e);
        }
        Elogger.info(System.getProperty("user.dir"));
        Elogger.info("进程id：" + Tool.getProcessID());
        /*
         * 查看系统资源状态
         * */
        Elogger.info("Runtime max: " + mb(Runtime.getRuntime().maxMemory()));
        MemoryMXBean m = ManagementFactory.getMemoryMXBean();

        Elogger.info("Non-heap: " + mb(m.getNonHeapMemoryUsage().getMax()));
        Elogger.info("Heap: " + mb(m.getHeapMemoryUsage().getMax()));

        for (MemoryPoolMXBean mp : ManagementFactory.getMemoryPoolMXBeans()) {
            Elogger.info("Pool: " + mp.getName() + " (type " + mp.getType() + ")" + " = " + mb(mp.getUsage().getMax()));
        }
        //初始化SDK静态对象
        try {
            hcNetSDK = HCNetSDK.INSTANCE;
        } catch (Exception e) {
            Elogger.error("初始化SDK静态对象，失败", e);
        }
        //初始化SDK
        if (!hcNetSDK.NET_DVR_Init()) {
            Elogger.info("SDK初始化失败");
            return;
        }
        //读取配置文件
        try {
            configEntity = configService.getConfig();
        } catch (Exception e) {
            Elogger.error("读取配置信息出错", e);
            Tool.showMessage(e.getMessage(), "连接数据库出错", 0);
            return;
        }
        //一体机参数配置
        devicePort = configEntity.getDevicePort();
        deviceName = configEntity.getDeviceName();
        devicePass = configEntity.getDevicePass();
        //将pid写入数据库
        processService.setProcessId(Tool.getProcessID());
        //启用onGuard数据接收服务
        OnguardService onguardService = new OnguardService();
        onguardService.start();
        //启用onGuard状态监测功能
        OnGuardStatusTimer.open();
        //初始化采集设备相关信息
        deviceIpsFaceCollection = new HashSet<String>();
        faceCollectionIpWithProducer = new HashMap<String, ProducerService>();
        faceCollectionIpWithLogin = new HashMap<String, LoginService>();
        if (!HCNetSDK.INSTANCE.NET_DVR_SetDVRMessageCallBack_V31(alarmHandler, null)) {
            Elogger.info("设置回调函数失败，错误码：" + hcNetSDK.NET_DVR_GetLastError());
        }
        //对所有一体机设备初始化
        equipmentService.initEquipmentInfo();
        //初始化下发队列
        producerServiceList = new ArrayList<>();
        queueIp = configEntity.getQueueIp();//获取队列ip
        for (EquipmentEntity equipmentEntity : equipmentEntityList) {
            if (equipmentEntity.getEquipmentType() == 1) {
                ProducerService producerService = new ProducerService(equipmentEntity.getEquipmentName() + "-" + equipmentEntity.getEquipmentIp(), queueIp);
                producerServiceList.add(producerService);
                CustomerService customerService = new CustomerService(equipmentEntity.getEquipmentName() + "-" + equipmentEntity.getEquipmentIp(), producerService.getChannel());
                customerService.start();
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    Elogger.error("每生成一个队列后延迟300毫秒出错", e);
                }
                //获取消费者的数量
                try {
                    Elogger.info("队列名称：" + equipmentEntity.getEquipmentName() + "-" + equipmentEntity.getEquipmentIp() + "  数量：" + producerService.getChannel().consumerCount(equipmentEntity.getEquipmentName() + "-" + equipmentEntity.getEquipmentIp()));
                } catch (IOException e) {
                    Elogger.error("获取消费者的数量出错", e);
                }
            }
        }
        //启用队列重连功能
        QueueTimer.open();
        //启用夜间队列数据删除
        queueDataCleanTimer.open();
        //获取系统默认编码
        Elogger.info("系统默认编码：" + System.getProperty("file.encoding")); //查询结果GBK
        //系统默认字符编码
        Elogger.info("系统默认字符编码：" + Charset.defaultCharset()); //查询结果GBK
        //操作系统用户使用的语言
        Elogger.info("系统默认语言：" + System.getProperty("user.language")); //查询结果zh
        //启动成功
        processService.setRegisterStatus();
        //启用socket服务
        try {
            Elogger.info("本机IP地址" + InetAddress.getLocalHost());
            ServerSocket serverSocket = new ServerSocket(configEntity.getSocketRegisterPort());
            serverSocket.setSoTimeout(0);
            serverSocket.setReuseAddress(true);
            Elogger.info("等待客户端连接..............................................................................");
            while (true) {
                Socket socket = serverSocket.accept();
                socket.setReuseAddress(true);
                SocketService socketService = new SocketService();
                socketService.setSocketInfo(socket);
                socketService.start();
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            Elogger.error("开启socket服务失败：", e);
        }
    }

    static String mb(long s) {
        return String.format("%d (%.2f M)", s, (double) s / (1024 * 1024));
    }
}
