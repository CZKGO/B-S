package com.czk.diabetes.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

/**
 * Created by 陈忠凯 on 2017/5/25.
 */

public class CameraUtil {

    // 从本地相册选取图片作为头像
    public static void choseHeadImageFromGallery(Activity activity, int code) {
        Intent intentFromGallery = new Intent();
        // 设置文件类型
        intentFromGallery.setType("image/*");
        intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(intentFromGallery, code);
    }

    /**
     * 裁剪原始的图片
     */
    public static void cropRawPhoto(Activity activity, Uri uri,
                                    int width, int hight, int code) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");

        // 设置裁剪
        intent.putExtra("crop", "true");

        // aspectX , aspectY :宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX , outputY : 裁剪图片宽高
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", hight);
        intent.putExtra("return-data", true);

        activity.startActivityForResult(intent, code);
    }

    /**
     * 提取保存裁剪之后的图片数据
     */
    public static Bitmap setImageToHeadView(Intent intent) {
        Bundle extras = intent.getExtras();
        Bitmap photo = null;
        if (extras != null) {
            photo = extras.getParcelable("data");
        }
        return photo;
    }

//    // 启动手机相机拍摄照片作为头像
//    private void choseHeadImageFromCameraCapture() {
//        Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//        // 判断存储卡是否可用，存储照片文件
//        if (hasSdcard()) {
//            intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri
//                    .fromFile(new File(Environment
//                            .getExternalStorageDirectory(), IMAGE_FILE_NAME)));
//        }
//
//        startActivityForResult(intentFromCapture, CODE_CAMERA_REQUEST);
//    }
}
