package com.czk.diabetes.util;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by xuezaishao on 2017/3/27.
 */

public class ConnectionUtil {
    private static String TAG = "ConnectionUtil";

    public static InputStream getHistoryAndHotDrugs(){
        InputStream inputStream = null;
        try {
            URL url = new URL(URlUtil.HistoryAndHotDrugs);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(5 * 1000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);
            conn.setRequestProperty("Content-Length", "180");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Host", "diabetesintf.izhangkong.com");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Accept-Encoding", "gzip");
            DataOutputStream outputStream = new DataOutputStream(conn.getOutputStream());
            //写参数
            outputStream.writeBytes("dev="+ URLEncoder.encode("867068024369976"));
            outputStream.writeBytes("&dev_type="+ URLEncoder.encode("android"));
            outputStream.writeBytes("&join_id="+ URLEncoder.encode("123"));
            outputStream.writeBytes("&loadFrom="+ URLEncoder.encode("1000115"));
            outputStream.writeBytes("&req_num="+ URLEncoder.encode(""));
            outputStream.writeBytes("&sessionID="+ URLEncoder.encode("eba63ded5fff28a977b89fed9ab34c6c"));
            outputStream.writeBytes("&sessionMemberID="+ URLEncoder.encode("eba63ded5fff28a9f045d0363e4d0f7d"));
            outputStream.writeBytes("&valid="+ URLEncoder.encode(""));
            outputStream.writeBytes("&ver="+ URLEncoder.encode("59"));
            outputStream.flush();
            outputStream.close();

//            if (conn.getContentLength() >0) {
                inputStream = conn.getInputStream();//获取输入流
//            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            LogUtil.e(TAG+ "-getInputStream", e.getMessage());
        }
        return inputStream;
    }
}
