package com.czk.diabetes.medicine;

/**
 * Created by 陈忠凯 on 2017/5/12.
 */

public class MedicineRecord {
    public String name;
    public String doses;
    public String times;
    public String peroidStart;
    public String peroidEnd;
    public String notifition;
    public String description;

    public MedicineRecord(String name, String doses, String times, String peroidStart, String peroidEnd, String notifition, String description) {
        this.name = name;
        this.doses = doses;
        this.times = times;
        this.peroidStart = peroidStart;
        this.peroidEnd = peroidEnd;
        this.notifition = notifition;
        this.description = description;
    }
}