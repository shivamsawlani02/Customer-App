package com.dsdairysytem.clientappfinal;

import java.util.ArrayList;
import java.util.Map;

public class SelfLogConstant {
public static ArrayList<String> itemList;
public static String state;
public static ArrayList<String> selectedMilkType;
public static Map<String,Long> orderDetail;
public static Long totalAmount;
public static Map<String,String> allMilkmanMa;
public static boolean editUsualOrder;
public static Map<String,Long> usualOrderAmount;

    public static Map<String, Long> getUsualOrderAmount() {
        return usualOrderAmount;
    }

    public static void setUsualOrderAmount(Map<String, Long> usualOrderAmount) {
        SelfLogConstant.usualOrderAmount = usualOrderAmount;
    }

    public static boolean isEditUsualOrder() {
        return editUsualOrder;
    }

    public static void setEditUsualOrder(boolean editUsualOrder) {
        SelfLogConstant.editUsualOrder = editUsualOrder;
    }

    public static Map<String, String> getAllMilkmanMa() {
        return allMilkmanMa;
    }

    public static void setAllMilkmanMa(Map<String, String> allMilkmanMa) {
        SelfLogConstant.allMilkmanMa = allMilkmanMa;
    }

    public static Long getTotalAmount() {
        return totalAmount;
    }

    public static void setTotalAmount(Long totalAmount) {
        SelfLogConstant.totalAmount = totalAmount;
    }

    public static Map<String, Long> getOrderDetail() {
        return orderDetail;
    }

    public static void setOrderDetail(Map<String, Long> orderDetail) {
        SelfLogConstant.orderDetail = orderDetail;
    }

    public static ArrayList<String> getSelectedMilkType() {
        return selectedMilkType;
    }

    public static void setSelectedMilkType(ArrayList<String> selectedMilkType) {
        SelfLogConstant.selectedMilkType = selectedMilkType;
    }

    public static String getMilkmanName() {
        return milkmanName;
    }

    public static void setMilkmanName(String milkmanName) {
        SelfLogConstant.milkmanName = milkmanName;
    }

    public static String milkmanName;

    public static String getState() {
        return state;
    }

    public static void setState(String state) {
        SelfLogConstant.state = state;
    }

    public static ArrayList<String> getItemList() {
        return itemList;
    }

    public static void setItemList(ArrayList<String> itemList) {
        SelfLogConstant.itemList = itemList;
    }
}
