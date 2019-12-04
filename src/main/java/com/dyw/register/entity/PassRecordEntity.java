package com.dyw.register.entity;

import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component
public class PassRecordEntity {
    private int passRecordId;
    private String passRecordName;
    private String passRecordCardNumber;
    private byte[] passRecordStaffImage;
    private byte[] passRecordCaptureImage;
    private Timestamp passRecordPassTime;
    private String passRecordEquipmentName;
    private String passRecordEquipmentIp;
    private int passRecordPassResult;
    private String passRecordNote;
    private int passRecordSimilarity;
    private int passRecordEventTypeId;

    public int getPassRecordId() {
        return passRecordId;
    }

    public void setPassRecordId(int passRecordId) {
        this.passRecordId = passRecordId;
    }

    public String getPassRecordName() {
        return passRecordName;
    }

    public void setPassRecordName(String passRecordName) {
        this.passRecordName = passRecordName;
    }

    public String getPassRecordCardNumber() {
        return passRecordCardNumber;
    }

    public void setPassRecordCardNumber(String passRecordCardNumber) {
        this.passRecordCardNumber = passRecordCardNumber;
    }

    public byte[] getPassRecordStaffImage() {
        return passRecordStaffImage;
    }

    public void setPassRecordStaffImage(byte[] passRecordStaffImage) {
        this.passRecordStaffImage = passRecordStaffImage;
    }

    public byte[] getPassRecordCaptureImage() {
        return passRecordCaptureImage;
    }

    public void setPassRecordCaptureImage(byte[] passRecordCaptureImage) {
        this.passRecordCaptureImage = passRecordCaptureImage;
    }

    public Timestamp getPassRecordPassTime() {
        return passRecordPassTime;
    }

    public void setPassRecordPassTime(Timestamp passRecordPassTime) {
        this.passRecordPassTime = passRecordPassTime;
    }

    public String getPassRecordEquipmentName() {
        return passRecordEquipmentName;
    }

    public void setPassRecordEquipmentName(String passRecordEquipmentName) {
        this.passRecordEquipmentName = passRecordEquipmentName;
    }

    public String getPassRecordEquipmentIp() {
        return passRecordEquipmentIp;
    }

    public void setPassRecordEquipmentIp(String passRecordEquipmentIp) {
        this.passRecordEquipmentIp = passRecordEquipmentIp;
    }

    public int getPassRecordPassResult() {
        return passRecordPassResult;
    }

    public void setPassRecordPassResult(int passRecordPassResult) {
        this.passRecordPassResult = passRecordPassResult;
    }

    public String getPassRecordNote() {
        return passRecordNote;
    }

    public void setPassRecordNote(String passRecordNote) {
        this.passRecordNote = passRecordNote;
    }

    public int getPassRecordSimilarity() {
        return passRecordSimilarity;
    }

    public void setPassRecordSimilarity(int passRecordSimilarity) {
        this.passRecordSimilarity = passRecordSimilarity;
    }

    public int getPassRecordEventTypeId() {
        return passRecordEventTypeId;
    }

    public void setPassRecordEventTypeId(int passRecordEventTypeId) {
        this.passRecordEventTypeId = passRecordEventTypeId;
    }

    public void clear() {
        passRecordId = 0;
        passRecordCaptureImage = null;
        passRecordStaffImage = null;
        passRecordCardNumber = "";
        passRecordName = "";
        passRecordEquipmentName = "";
        passRecordPassTime = null;
        passRecordSimilarity = 0;
        passRecordPassResult = 0;
        passRecordEquipmentIp = "";
        passRecordNote = "";
        passRecordEventTypeId = 0;
    }
}
