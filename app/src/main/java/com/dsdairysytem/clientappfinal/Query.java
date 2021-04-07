package com.dsdairysytem.clientappfinal;

public class Query {
    String clientName,date,description,orderID,clientMobile;

    public Query(String clientName, String date, String description, String orderID, String clientMobile) {
        this.clientName = clientName;
        this.date = date;
        this.description = description;
        this.orderID = orderID;
        this.clientMobile = clientMobile;
    }


    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getClientMobile() {
        return clientMobile;
    }

    public void setClientMobile(String clientMobile) {
        this.clientMobile = clientMobile;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
