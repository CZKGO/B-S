package com.czk.diabetes.contact;

/**
 * Created by xuezaishao on 2017/6/4.
 */

public class ContactData {
    public final static int TYPE_SEND = 1;
    public int type;
    public String text;
    public String time;
    public String doctor;
    public String userImg;
    public String doctorImg;
    public String usetName;
    public String doctorName;

    public ContactData(int type, String text, String time, String doctor,
                       String userImg, String doctorImg, String usetName, String doctorName) {
        this.type = type;
        this.text = text;
        this.time = time;
        this.doctor = doctor;
        this.userImg = userImg;
        this.doctorImg = doctorImg;
        this.usetName = usetName;
        this.doctorName = doctorName;
    }
}
