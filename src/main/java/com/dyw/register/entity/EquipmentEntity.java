package com.dyw.register.entity;

import org.springframework.stereotype.Component;

@Component
public class EquipmentEntity {
    private int equipmentId;
    private String equipmentName;
    private String equipmentIp;
    private int equipmentType;
    private int equipmentPermission;
    private String equipmentSwitchIp;
    private String equipmentHostIp;
    private int equipmentValidity;

    //独有
    private int cardNumber;//卡数量
    private int isLogin;//是否在线
    private int passMode;//通行模式

    public int getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(int equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public String getEquipmentIp() {
        return equipmentIp;
    }

    public void setEquipmentIp(String equipmentIp) {
        this.equipmentIp = equipmentIp;
    }

    public int getEquipmentType() {
        return equipmentType;
    }

    public void setEquipmentType(int equipmentType) {
        this.equipmentType = equipmentType;
    }

    public int getEquipmentPermission() {
        return equipmentPermission;
    }

    public void setEquipmentPermission(int equipmentPermission) {
        this.equipmentPermission = equipmentPermission;
    }

    public String getEquipmentSwitchIp() {
        return equipmentSwitchIp;
    }

    public void setEquipmentSwitchIp(String equipmentSwitchIp) {
        this.equipmentSwitchIp = equipmentSwitchIp;
    }

    public String getEquipmentHostIp() {
        return equipmentHostIp;
    }

    public void setEquipmentHostIp(String equipmentHostIp) {
        this.equipmentHostIp = equipmentHostIp;
    }

    public int getEquipmentValidity() {
        return equipmentValidity;
    }

    public void setEquipmentValidity(int equipmentValidity) {
        this.equipmentValidity = equipmentValidity;
    }

    public int getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(int cardNumber) {
        this.cardNumber = cardNumber;
    }

    public int getIsLogin() {
        return isLogin;
    }

    public void setIsLogin(int isLogin) {
        this.isLogin = isLogin;
    }

    public int getPassMode() {
        return passMode;
    }

    public void setPassMode(int passMode) {
        this.passMode = passMode;
    }
}
