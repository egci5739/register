package com.dyw.register.entity;

import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class StaffEntity {
    private int staffId;
    private int staffEmpId;//人员empId
    private String staffName;//姓名
    private String staffCardId;//证件号
    private String staffCardNumber;//卡号
    private String staffBirthday;//出生日期
    private int staffGender;//性别
    private String staffCompany;//公司
    private byte[] staffImage;//照片
    private int staffValidity;//状态
    private int Type;//操作类型
    private String staffOldCardNumber;//旧卡号

    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public int getStaffEmpId() {
        return staffEmpId;
    }

    public void setStaffEmpId(int staffEmpId) {
        this.staffEmpId = staffEmpId;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getStaffCardId() {
        return staffCardId;
    }

    public void setStaffCardId(String staffCardId) {
        this.staffCardId = staffCardId;
    }

    public String getStaffCardNumber() {
        return staffCardNumber;
    }

    public void setStaffCardNumber(String staffCardNumber) {
        this.staffCardNumber = staffCardNumber;
    }

    public String getStaffBirthday() {
        return staffBirthday;
    }

    public void setStaffBirthday(String staffBirthday) {
        this.staffBirthday = staffBirthday;
    }

    public int getStaffGender() {
        return staffGender;
    }

    public void setStaffGender(int staffGender) {
        this.staffGender = staffGender;
    }

    public String getStaffCompany() {
        return staffCompany;
    }

    public void setStaffCompany(String staffCompany) {
        this.staffCompany = staffCompany;
    }

    public byte[] getStaffImage() {
        return staffImage;
    }

    public void setStaffImage(byte[] staffImage) {
        this.staffImage = staffImage;
    }

    public int getStaffValidity() {
        return staffValidity;
    }

    public void setStaffValidity(int staffValidity) {
        this.staffValidity = staffValidity;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public String getStaffOldCardNumber() {
        return staffOldCardNumber;
    }

    public void setStaffOldCardNumber(String staffOldCardNumber) {
        this.staffOldCardNumber = staffOldCardNumber;
    }

    @Override
    public String toString() {
        return "StaffEntity{" +
                "staffId=" + staffId +
                ", staffEmpId=" + staffEmpId +
                ", staffName='" + staffName + '\'' +
                ", staffCardId='" + staffCardId + '\'' +
                ", staffCardNumber='" + staffCardNumber + '\'' +
                ", staffBirthday='" + staffBirthday + '\'' +
                ", staffGender=" + staffGender +
                ", staffCompany='" + staffCompany + '\'' +
                ", staffImage=" + Arrays.toString(staffImage) +
                ", staffValidity=" + staffValidity +
                ", Type=" + Type +
                ", staffOldCardNumber='" + staffOldCardNumber + '\'' +
                '}';
    }
}
