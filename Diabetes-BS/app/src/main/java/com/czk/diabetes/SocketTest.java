package com.czk.diabetes;

import android.app.Activity;
import android.os.Bundle;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by 陈忠凯 on 2017/5/24.
 */

public class SocketTest extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main);


            new Thread(new Runnable() {
                @Override
                public void run() { try {
                    System.out.println("准备连接");
                    Socket socket = new Socket("120.24.2.161", 3333);
                    System.out.println("连接上了");

//            Intent intent = new Intent();
//            intent.setClass(SocketTest.this, ConnectActivity.class);
//            SocketTest.this.startActivity(intent);
                    InputStream inputStream = socket.getInputStream();
                    byte buffer[] = new byte[1024*4];
                    int temp = 0;
                    String res = null;
                    //从inputstream中读取客户端所发送的数据
                    System.out.println("接收到服务器的信息是：");

                    while ((temp = inputStream.read(buffer)) != -1){
                        System.out.println(new String(buffer, 0, temp));
                        res += new String(buffer, 0, temp);
                    }

                    System.out.println("已经结束接收信息……");

                    socket.close();
                    inputStream.close();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                }
            }).start();





    }
}


