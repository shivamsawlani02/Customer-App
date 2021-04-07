package com.dsdairysytem.clientappfinal;

import java.util.Map;

public class ClientOrder {
    String ID;
    String date;
    String milkmanMobile;
    Map<String,Object> map;
    String milkmanName,amount;
    String time;

    public ClientOrder(String milkmanMobile, String ID, String date, Map<String,Object> map, String milkmanName, String amount,String time) {
        this.ID=ID;
        this.date = date;
        this.map = map;
        this.milkmanMobile=milkmanMobile;
        this.milkmanName=milkmanName;
        this.amount=amount;
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMilkmanName() {
        return milkmanName;
    }

    public void setMilkmanName(String milkmanName) {
        this.milkmanName = milkmanName;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getMilkmanMobile() {
        return milkmanMobile;
    }

    public void setMilkmanMobile(String milkmanMobile) {
        this.milkmanMobile = milkmanMobile;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }
}
