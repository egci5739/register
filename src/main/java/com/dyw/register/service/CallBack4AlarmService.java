package com.dyw.register.service;

import com.dyw.register.HCNetSDK;
import com.dyw.register.controller.EgciController;
import com.dyw.register.dao.FaceCollectionDao;
import com.dyw.register.entity.FaceCollectionEntity;
import com.dyw.register.tool.Tool;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

@Service
public class CallBack4AlarmService {
    @Autowired
    private FaceCollectionDao faceCollectionDao;

    private Logger logger = LoggerFactory.getLogger(CallBack4AlarmService.class);

    public void alarmNotice(NativeLong lCommand,
                            HCNetSDK.NET_DVR_ALARMER equipmentInfo,
                            Pointer alarmInfo,
                            int dwBufLen,
                            Pointer pUser) {
        try {
            int alarmType = lCommand.intValue();
            switch (alarmType) {
                case HCNetSDK.COMM_ALARM_ACS: //门禁主机报警信息
                    COMM_ALARM_ACS_info(equipmentInfo, alarmInfo);
                    break;
                case HCNetSDK.COMM_ID_INFO_ALARM: //身份证信息
                    COMM_ID_INFO_ALARM_info(equipmentInfo, alarmInfo);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            logger.error("接收消息出错", e);
        }
    }

    private void COMM_ID_INFO_ALARM_info(HCNetSDK.NET_DVR_ALARMER equipment, Pointer alarm) throws UnsupportedEncodingException {
        HCNetSDK.NET_DVR_ID_CARD_INFO_ALARM alarmInfo = new HCNetSDK.NET_DVR_ID_CARD_INFO_ALARM();
        alarmInfo.write();
        alarmInfo.getPointer().write(0, alarm.getByteArray(0, alarmInfo.size()), 0, alarmInfo.size());
        alarmInfo.read();
        //人证比对失败，不保存和推送信息
        if (alarmInfo.dwMinor == 112) {
            return;
        }
        if (alarmInfo.dwPicDataLen <= 0) {
            return;
        }
        FaceCollectionEntity faceCollectionEntity = new FaceCollectionEntity();
        faceCollectionEntity.setFaceCollectionName(new String(alarmInfo.struIDCardCfg.byName, "utf-8").trim());//姓名
        faceCollectionEntity.setFaceCollectionCardId(new String(alarmInfo.struIDCardCfg.byIDNum).trim());//身份证号
        faceCollectionEntity.setFaceCollectionNation(String.valueOf((alarmInfo.struIDCardCfg.byNation)));//民族
        faceCollectionEntity.setFaceCollectionGender((alarmInfo.struIDCardCfg.bySex));//性别
        faceCollectionEntity.setFaceCollectionBirthday(alarmInfo.struIDCardCfg.struBirth.wYear + "-" + alarmInfo.struIDCardCfg.struBirth.byMonth + "-" + alarmInfo.struIDCardCfg.struBirth.byDay);//出生日期
        faceCollectionEntity.setFaceCollectionExpirationDate(alarmInfo.struIDCardCfg.struStartDate.wYear + "-" + alarmInfo.struIDCardCfg.struStartDate.byMonth + "-" + alarmInfo.struIDCardCfg.struStartDate.byDay + " 到 " + alarmInfo.struIDCardCfg.struEndDate.wYear + "-" + alarmInfo.struIDCardCfg.struEndDate.byMonth + "-" + alarmInfo.struIDCardCfg.struEndDate.byDay);//有效期
        faceCollectionEntity.setFaceCollectionOrganization(new String(alarmInfo.struIDCardCfg.byIssuingAuthority, "utf-8").trim());//签发机关
        faceCollectionEntity.setFaceCollectionSimilarity(Tool.getRandom(89, 76, 13));
//        ByteBuffer buffersId = alarmInfo.pPicData.getByteBuffer(0, alarmInfo.dwPicDataLen);
        ByteBuffer buffersId = alarmInfo.pPicData.getByteBuffer(0, alarmInfo.dwPicDataLen);
        byte[] bytesId = new byte[alarmInfo.dwPicDataLen];
        buffersId.rewind();//attention
        buffersId.get(bytesId);
        faceCollectionEntity.setFaceCollectionCardImage(bytesId);//身份证图片
        try {
            ByteBuffer buffersCp = alarmInfo.pCapturePicData.getByteBuffer(0, alarmInfo.dwCapturePicDataLen);
            byte[] bytesCp = new byte[alarmInfo.dwCapturePicDataLen];
            buffersCp.rewind();//attention
            buffersCp.get(bytesCp);
            faceCollectionEntity.setFaceCollectionStaffImage(bytesCp);
        } catch (Exception e) {
            faceCollectionEntity.setFaceCollectionStaffImage(null);
            return;
        }
        faceCollectionDao.insertFaceCollection(faceCollectionEntity);
        try {
            EgciController.faceCollectionIpWithProducer.get(new String(equipment.sDeviceIP).trim()).sendToQueue(faceCollectionEntity.getFaceCollectionId() + "");
        } catch (Exception e) {
            logger.error("推送通信到消费者失败", e);
        }
    }

    private void COMM_ALARM_ACS_info(HCNetSDK.NET_DVR_ALARMER equipment, Pointer alarm) {
        HCNetSDK.NET_DVR_ACS_ALARM_INFO alarmInfo = new HCNetSDK.NET_DVR_ACS_ALARM_INFO();
        alarmInfo.write();
        alarmInfo.getPointer().write(0, alarm.getByteArray(0, alarmInfo.size()), 0, alarmInfo.size());
        alarmInfo.read();
        if (EgciController.deviceIpsFaceCollection.contains(new String(equipment.sDeviceIP).trim())) {
            //采集设备回传的信息
            FaceCollectionEntity faceCollectionEntity = new FaceCollectionEntity();
            if (alarmInfo.dwPicDataLen == 0) {
                return;
            }
            try {
                ByteBuffer bufferSnap = alarmInfo.pPicData.getByteBuffer(0, alarmInfo.dwPicDataLen);
                byte[] byteSnap = new byte[alarmInfo.dwPicDataLen];
                bufferSnap.rewind();
                bufferSnap.get(byteSnap);
                faceCollectionEntity.setFaceCollectionStaffImage(byteSnap);
            } catch (Exception e) {
                logger.info("获取采集的照片出错", e);
                faceCollectionEntity.setFaceCollectionStaffImage(null);
                return;
            }
            try {
                faceCollectionDao.insertFaceCollection(faceCollectionEntity);
            } catch (Exception e) {
                logger.error("插入采集设备信息出错", e);
                return;
            }
            try {
                EgciController.faceCollectionIpWithProducer.get(new String(equipment.sDeviceIP).trim()).sendToQueue(faceCollectionEntity.getFaceCollectionId() + "");
                logger.info("信息id：" + faceCollectionEntity.getFaceCollectionId());
            } catch (Exception e) {
                logger.error("推送通信到消费者失败", e);
            }
        }
    }
}
