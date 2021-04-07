package com.dsdairysytem.clientappfinal;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

@IgnoreExtraProperties
public class Profile_Deatils {

    String name;
    String email;
    String mobile;
    String device_token;
    String adl1,adl2,City,postalCode;


    public Profile_Deatils(String name, String email, String mobile, String device_token, String adl1, String adl2, String City, String postalCode) {
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.device_token = device_token;
        this.adl1 = adl1;
        this.adl2 = adl2;
        this.City = City;
        this.postalCode = postalCode;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAdl1() {
        return adl1;
    }

    public void setAdl1(String adl1) {
        this.adl1 = adl1;
    }

    public String getAdl2() {
        return adl2;
    }

    public void setAdl2(String adl2) {
        this.adl2 = adl2;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        this.City = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
}

