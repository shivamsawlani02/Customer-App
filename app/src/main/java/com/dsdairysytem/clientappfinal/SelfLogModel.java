package com.dsdairysytem.clientappfinal;

public class SelfLogModel {
    String name,mobile,type;

    public SelfLogModel(String name, String mobile, String type) {
        this.name = name;
        this.mobile = mobile;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
