package com.czk.diabetes.contact;

/**
 * Created by 陈忠凯 on 2017/6/4.
 */

public class DoctorData {
    public final String position;
    public int id;
    public String name;
    public String hospital;
    public String years;
    public String imgUrl;
    public String feedback;

    public DoctorData(int id, String name, String hospital, String position, String years, String feedback, String img) {
        this.id = id;
        this.name = name;
        this.hospital = hospital;
        this.years = years;
        this.feedback = feedback;
        this.imgUrl = img;
        this.position = position;
    }
}
