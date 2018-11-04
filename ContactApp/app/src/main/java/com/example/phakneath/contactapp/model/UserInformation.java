package com.example.phakneath.contactapp.model;

import java.io.Serializable;

public class UserInformation implements Serializable{

    private String username;
    private String phoneNum;
    private String userEmail;
    private String qrCode;
    private String extensionCode;
    private String userID;
    private String title;
    private String id;

    public UserInformation(String username, String phoneNum, String email, String qrCode, String extensionCode, String id) {
        this.username = username;
        this.phoneNum = phoneNum;
        this.userEmail = email;
        this.qrCode = qrCode;
        this.extensionCode = extensionCode;
        this.id = id;
    }

    public UserInformation(){}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAdditional() {
        return phoneNum;
    }

    public void setAdditional(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getExtensionCode() {
        return extensionCode;
    }

    public void setExtensionCode(String extensionCode) {
        this.extensionCode = extensionCode;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
