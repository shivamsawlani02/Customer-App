package com.dsdairysytem.clientappfinal;

import java.util.ArrayList;
import java.util.Map;

public class AddedOrders {
    public static ArrayList<String> orderMilkmanNames = new ArrayList<>();
    public static ArrayList<Map<String,Long>> orderDetails = new ArrayList<>();
    public static ArrayList<Long> orderAmounts = new ArrayList<>();

    public static ArrayList<String> getOrderMilkmanNames() {
        return orderMilkmanNames;
    }

    public static void setOrderMilkmanNames(ArrayList<String> orderMilkmanNames) {
        AddedOrders.orderMilkmanNames = orderMilkmanNames;
    }

    public static ArrayList<Map<String, Long>> getOrderDetails() {
        return orderDetails;
    }

    public static void setOrderDetails(ArrayList<Map<String, Long>> orderDetails) {
        AddedOrders.orderDetails = orderDetails;
    }

    public static ArrayList<Long> getOrderAmounts() {
        return orderAmounts;
    }

    public static void setOrderAmounts(ArrayList<Long> orderAmounts) {
        AddedOrders.orderAmounts = orderAmounts;
    }
}
