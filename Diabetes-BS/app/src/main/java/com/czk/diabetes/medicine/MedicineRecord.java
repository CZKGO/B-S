package com.czk.diabetes.medicine;

/**
 * Created by 陈忠凯 on 2017/5/12.
 */

public class MedicineRecord {
    public String addTime;
    public String name;
    public String doses;
    public String time;
    public String peroidStart;
    public String peroidEnd;
    public String notifition;
    public String description;

    public MedicineRecord(String addTime, String name, String doses, String time, String peroidStart, String peroidEnd, String notifition, String description) {
        this.addTime = addTime;
        this.name = name;
        this.doses = doses;
        this.time = time;
        this.peroidStart = peroidStart;
        this.peroidEnd = peroidEnd;
        this.notifition = notifition;
        this.description = description;
    }
}