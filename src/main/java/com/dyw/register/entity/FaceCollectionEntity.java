package com.dyw.register.entity;

import org.springframework.stereotype.Component;

@Component
public class FaceCollectionEntity {
    private int faceCollectionId;
    private String faceCollectionName;
    private String faceCollectionCardId;
    private String faceCollectionNation;
    private int faceCollectionGender;
    private String faceCollectionBirthday;
    private String faceCollectionExpirationDate;
    private byte[] faceCollectionStaffImage;
    private byte[] faceCollectionCardImage;
    private String faceCollectionOrganization;
    private int faceCollectionSimilarity;

    public int getFaceCollectionId() {
        return faceCollectionId;
    }

    public void setFaceCollectionId(int faceCollectionId) {
        this.faceCollectionId = faceCollectionId;
    }

    public String getFaceCollectionName() {
        return faceCollectionName;
    }

    public void setFaceCollectionName(String faceCollectionName) {
        this.faceCollectionName = faceCollectionName;
    }

    public String getFaceCollectionCardId() {
        return faceCollectionCardId;
    }

    public void setFaceCollectionCardId(String faceCollectionCardId) {
        this.faceCollectionCardId = faceCollectionCardId;
    }

    public String getFaceCollectionNation() {
        return faceCollectionNation;
    }

    public void setFaceCollectionNation(String faceCollectionNation) {
        this.faceCollectionNation = faceCollectionNation;
    }

    public int getFaceCollectionGender() {
        return faceCollectionGender;
    }

    public void setFaceCollectionGender(int faceCollectionGender) {
        this.faceCollectionGender = faceCollectionGender;
    }

    public String getFaceCollectionBirthday() {
        return faceCollectionBirthday;
    }

    public void setFaceCollectionBirthday(String faceCollectionBirthday) {
        this.faceCollectionBirthday = faceCollectionBirthday;
    }

    public String getFaceCollectionExpirationDate() {
        return faceCollectionExpirationDate;
    }

    public void setFaceCollectionExpirationDate(String faceCollectionExpirationDate) {
        this.faceCollectionExpirationDate = faceCollectionExpirationDate;
    }

    public byte[] getFaceCollectionStaffImage() {
        return faceCollectionStaffImage;
    }

    public void setFaceCollectionStaffImage(byte[] faceCollectionStaffImage) {
        this.faceCollectionStaffImage = faceCollectionStaffImage;
    }

    public byte[] getFaceCollectionCardImage() {
        return faceCollectionCardImage;
    }

    public void setFaceCollectionCardImage(byte[] faceCollectionCardImage) {
        this.faceCollectionCardImage = faceCollectionCardImage;
    }

    public String getFaceCollectionOrganization() {
        return faceCollectionOrganization;
    }

    public void setFaceCollectionOrganization(String faceCollectionOrganization) {
        this.faceCollectionOrganization = faceCollectionOrganization;
    }

    public int getFaceCollectionSimilarity() {
        return faceCollectionSimilarity;
    }

    public void setFaceCollectionSimilarity(int faceCollectionSimilarity) {
        this.faceCollectionSimilarity = faceCollectionSimilarity;
    }
}
