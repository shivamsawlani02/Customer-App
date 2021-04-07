package com.dsdairysytem.clientappfinal;

import java.util.ArrayList;

public class Carddetails {
    private String mob;
    private String name;
    private ArrayList<String > listView;
    ;


    public Carddetails(String mob, String name, ArrayList<String > listView) {
        this.mob = mob;
        this.name = name;
        this.listView = listView;

    }

    public String getTitle() {
        return name;
    }

    public String getSubtitle() {
        return mob;
    }

    public  ArrayList<String > arrayList() {
        return listView;
    }
}



