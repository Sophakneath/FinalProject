package com.example.phakneath.contactapp.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.design.internal.ParcelableSparseArray;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

public class ContactInPhone implements Parcelable , Serializable{

    private String name;
    private List<String> phone;
    private Bitmap img;

    public ContactInPhone(){}

    public ContactInPhone(String name, List<String> phone, Bitmap img) {
        this.name = name;
        this.phone = phone;
        this.img = img;
    }

    protected ContactInPhone(Parcel in) {
        name = in.readString();
        phone = in.createStringArrayList();
        byte [] encodeByte=Base64.decode(in.readString(),Base64.DEFAULT);
        Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        img = bitmap;
    }

    public static final Creator<ContactInPhone> CREATOR = new Creator<ContactInPhone>() {
        @Override
        public ContactInPhone createFromParcel(Parcel in) {
            return new ContactInPhone(in);
        }

        @Override
        public ContactInPhone[] newArray(int size) {
            return new ContactInPhone[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getPhone() {
        return phone;
    }

    public void setPhone(List<String> phone) {
        this.phone = phone;
    }

    public Bitmap getImg() {
        return img;
    }

    public void setImg(Bitmap img) {
        this.img = img;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp=Base64.encodeToString(b, Base64.DEFAULT);
        dest.writeString(name);
        dest.writeStringList(phone);
        dest.writeString(temp);
    }
}
