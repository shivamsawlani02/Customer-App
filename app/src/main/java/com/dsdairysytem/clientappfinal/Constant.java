package com.dsdairysytem.clientappfinal;

import java.util.Map;

public class Constant {
    public static Map<String,Long> product_detail_map;
    public static String cMilkman;
    public static Long total_amount;

    public static Map<String, Long> getProduct_detail_map() {
        return product_detail_map;
    }

    public static void setProduct_detail_map(Map<String, Long> product_detail_map) {
        Constant.product_detail_map = product_detail_map;
    }

    public static Long getTotal_amount() {
        return total_amount;
    }

    public static void setTotal_amount(Long total_amount) {
        Constant.total_amount = total_amount;
    }

    public static String getcMilkman() {
        return cMilkman;
    }

    public static void setcMilkman(String cMilkman) {
        Constant.cMilkman = cMilkman;
    }
}
