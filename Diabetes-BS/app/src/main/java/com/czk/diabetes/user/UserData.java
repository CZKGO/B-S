package com.czk.diabetes.user;

/**
 * Created by 陈忠凯 on 2017/5/31.
 */

public class UserData {
    public final String name;
    public final String pwd;
    public final int sex;
    public final int year;
    public final String imgUrl;

    public UserData(String name, String pwd, int sex, int year, String imgUrl) {
        this.name = name;
        this.pwd = pwd;
        this.sex = sex;
        this.year = year;
        this.imgUrl = imgUrl;
    }
}
