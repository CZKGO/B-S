package com.czk.diabetes.util;

/**
 * Created by 陈忠凯 on 2017/2/22.
 */
public class StringUtil {
    /**
     * 判断字符串是否为空
     * @param str
     * @return
     */
    public static boolean isEmpty(String str){
        if (str == null || str.length() <= 0) {
            return true;
        }
        return false;
    }

}
