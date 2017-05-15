package com.czk.diabetes.recipe;

/**
 * Created by 陈忠凯 on 2017/5/15.
 */

public class EatRecord {
    public String addTime;
    public String name;
    public String material;
    public String time;
    public String type;
    public String beforeDining;
    public String afterDining;
    public String description;
    public EatRecord(String addTime, String name, String material, String type, String time, String beforeDining, String afterDining, String description) {
        this.addTime = addTime;
        this.name = name;
        this.material = material;
        this.type = type;
        this.time = time;
        this.beforeDining = beforeDining;
        this.afterDining = afterDining;
        this.description = description;
    }
}
