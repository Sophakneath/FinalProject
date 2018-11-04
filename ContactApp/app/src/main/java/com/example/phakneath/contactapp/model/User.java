package com.example.phakneath.contactapp.model;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {

    private String id;
    private String username;
    private String phone;
    private String image;
    private String gender;
    private List<UserInformation> contact;
    private String cover;
    private String extension;
    private String cvExtension;

    public User(String id, String username, String phone, String image, String gender, List<UserInformation> contact, String cover, String extension, String cvExtension) {
        this.id = id;
        this.username = username;
        this.phone = phone;
        this.image = image;
        this.gender = gender;
        this.contact = contact;
        this.cover = cover;
        this.extension = extension;
        this.cvExtension = cvExtension;
    }

    public User(String id, String username, String phone, String image, String gender, String cover, String extension, String cvExtension) {
        this.id = id;
        this.username = username;
        this.phone = phone;
        this.image = image;
        this.gender = gender;
        this.cover = cover;
        this.extension = extension;
        this.cvExtension = cvExtension;
    }

    public User(String username, String phone, String image, String extension)
    {
        this.username = username;
        this.phone = phone;
        this.image = image;
        this.extension = extension;
    }

    public User(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public List<UserInformation> getContact() {
        return contact;
    }

    public void setContact(List<UserInformation> contact) {
        this.contact = contact;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getCvExtension() {
        return cvExtension;
    }

    public void setCvExtension(String cvExtension) {
        this.cvExtension = cvExtension;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", phone='" + phone + '\'' +
                ", image='" + image + '\'' +
                ", gender='" + gender + '\'' +
                ", contact='" + contact + '\'' +
                '}';
    }
}
