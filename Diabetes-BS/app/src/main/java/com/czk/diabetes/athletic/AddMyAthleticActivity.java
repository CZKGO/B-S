package com.czk.diabetes.athletic;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.czk.diabetes.BaseActivity;
import com.czk.diabetes.MyApplication;
import com.czk.diabetes.R;
import com.czk.diabetes.net.DiabetesClient;
import com.czk.diabetes.net.SearchThread;
import com.czk.diabetes.util.CameraUtil;
import com.czk.diabetes.util.DimensUtil;
import com.czk.diabetes.util.FileUtil;
import com.czk.diabetes.util.FontIconDrawable;
import com.czk.diabetes.util.Imageloader;
import com.czk.diabetes.util.LogUtil;
import com.czk.diabetes.util.SharedPreferencesUtils;
import com.czk.diabetes.util.StringUtil;
import com.czk.diabetes.util.ToastUtil;
import com.czk.diabetes.view.dialog.LoadingDialog;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;

import cz.msebera.android.httpclient.Header;

/**
 * Created by 陈忠凯 on 2017/5/31.
 */

public class AddMyAthleticActivity extends BaseActivity {
    private final static int CODE_CHOSE_IMAGE = 1024;
    private final static int CODE_RESULT_REQUEST = 1025;
    private final static int HANDLER_UPLOAD_ERRO = 0;
    private final static int HANDLER_UPLOAD_SUCCESS = 1;
    private final static int HANDLER_NAME_DUPLICATE = 3;
    private final static int DIALOG_SHOW_TIME = 3000;
    private final static String TEM_IMG_NAMW = "my_athletic.jpg";
    private String name;
    private long time;
    private ImageView ivIcon;
    private ImageView ivCamera;
    private View layoutCamera;
    private float imgWidth;
    private double imgHight;
    private EditText etDescribe;
    private LoadingDialog loadingDialog;
    private long loadingStartTime;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_UPLOAD_ERRO:
                    if (null != loadingDialog) {
                        long loadingTime = System.currentTimeMillis() - loadingStartTime;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                loadingDialog.dismiss();
                                ToastUtil.showShortToast(MyApplication.getInstance(), getResources().getString(R.string.publish_failure));
                            }
                        }, loadingTime > DIALOG_SHOW_TIME ? loadingTime : DIALOG_SHOW_TIME - loadingTime);
                    } else {
                        ToastUtil.showShortToast(MyApplication.getInstance(), getResources().getString(R.string.publish_failure));
                    }
                    break;
                case HANDLER_NAME_DUPLICATE:
                    if (null != loadingDialog) {
                        long loadingTime = System.currentTimeMillis() - loadingStartTime;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                loadingDialog.dismiss();
                                ToastUtil.showShortToast(MyApplication.getInstance(), getResources().getString(R.string.duplicate_user_name));
                            }
                        }, loadingTime > DIALOG_SHOW_TIME ? loadingTime : DIALOG_SHOW_TIME - loadingTime);
                    } else {
                        ToastUtil.showShortToast(MyApplication.getInstance(), getResources().getString(R.string.duplicate_user_name));
                    }
                    break;
                case HANDLER_UPLOAD_SUCCESS:
                    ToastUtil.showShortToast(MyApplication.getInstance(), getResources().getString(R.string.publish_success));
                    if (null != loadingDialog) {
                        long loadingTime = System.currentTimeMillis() - loadingStartTime;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                loadingDialog.dismiss();
                                finish();
                            }
                        }, loadingTime > DIALOG_SHOW_TIME ? loadingTime : DIALOG_SHOW_TIME - loadingTime);
                    } else {
                        finish();
                    }
                    try {
                        DiabetesClient.post(DiabetesClient.getAbsoluteUrl("saveFile")
                                , DiabetesClient.saveFile(AddMyAthleticActivity.this.getFilesDir() + File.separator + FileUtil.CACHEPATH + File.separator + TEM_IMG_NAMW
                                        ,"./img/athletics/"
                                        , name + "_" + time + ".jpg")
                                , new AsyncHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                        LogUtil.d("sdfsafsadf", new String(responseBody));
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                        LogUtil.d("sdfsafsadf", new String(responseBody));
                                    }

                                });

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_add_athletic);
        initData();
        initView();
        dealEvent();
    }

    private void initData() {
        imgWidth = (DimensUtil.getScreenWidthDip(AddMyAthleticActivity.this) - 16);
        imgHight = imgWidth * 0.618;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        switch (requestCode) {
            case CODE_CHOSE_IMAGE:
                CameraUtil.cropRawPhoto(AddMyAthleticActivity.this, intent.getData(),
                        DimensUtil.dpTopx(AddMyAthleticActivity.this, imgWidth),
                        DimensUtil.dpTopx(AddMyAthleticActivity.this, (float) imgHight), CODE_RESULT_REQUEST);
                break;
            case CODE_RESULT_REQUEST:
                if (intent != null) {
                    Bitmap bitmap = CameraUtil.setImageToHeadView(intent);
                    ViewGroup.LayoutParams layoutCameraParams = layoutCamera.getLayoutParams();
                    layoutCameraParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    layoutCameraParams.height = DimensUtil.dpTopx(AddMyAthleticActivity.this, (float) imgHight);
                    layoutCamera.setLayoutParams(layoutCameraParams);
                    ivCamera.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    ivCamera.setImageBitmap(bitmap);
                    Imageloader.getInstance().saveImgToJpg(AddMyAthleticActivity.this, bitmap, TEM_IMG_NAMW);
                }

                break;
        }

        super.onActivityResult(requestCode, resultCode, intent);
    }


    private void initView() {
        /**
         * 头部
         */
        ivIcon = (ImageView) findViewById(R.id.icon);
        FontIconDrawable iconArrowLeft = FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_arrow_left);
        iconArrowLeft.setTextColor(getResources().getColor(R.color.white));
        ivIcon.setImageDrawable(iconArrowLeft);
        TextView tvTitle = (TextView) findViewById(R.id.title);
        tvTitle.setText(getResources().getString(R.string.edit));
        /**
         * 主体
         */
        etDescribe = (EditText) findViewById(R.id.et_describe);
        layoutCamera = findViewById(R.id.layout_camera);
        ivCamera = (ImageView) findViewById(R.id.iv_camera);
        FontIconDrawable iconCamera = FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_camera);
        ivCamera.setImageDrawable(iconCamera);

        loadingDialog = new LoadingDialog(AddMyAthleticActivity.this, getResources().getString(R.string.uploading));
    }


    private void dealEvent() {
        ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        layoutCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraUtil.choseHeadImageFromGallery(AddMyAthleticActivity.this, CODE_CHOSE_IMAGE);
            }
        });

        findViewById(R.id.button_publish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkContent();
            }
        });
    }

    private void checkContent() {
        if (StringUtil.isEmpty(etDescribe.getText().toString())) {
            ToastUtil.showShortToast(AddMyAthleticActivity.this, getResources().getString(R.string.user_name_not_null));
        } else {
            if (null != loadingDialog) {
                loadingDialog.show();
                loadingStartTime = System.currentTimeMillis();
            }
            name = MyApplication
                    .getInstance()
                    .getSharedPreferences(SharedPreferencesUtils.PREFERENCE_FILE, Context.MODE_PRIVATE)
                    .getString(SharedPreferencesUtils.USER_NAME, "null");
            time = System.currentTimeMillis();
            String sql = "INSERT INTO `athletic`(`user`, `text`, `imgUrl`, `likeNum`, `likeUsers`, `time`) " +
                    "VALUES ('" + name + "'," +
                    "'" + etDescribe.getText() + "'," +
                    "'" + DiabetesClient.getAthleticImgUrl(name + "_" + time + ".jpg") + "'," +
                    "'" + 0 + "'," +
                    "'" + "'," +
                    "'" + time + "')";
            DiabetesClient.get(DiabetesClient.getAbsoluteUrl("doSqlTow")
                    , DiabetesClient.doSqlTow(sql)
                    , new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            SearchThread searchThread = new SearchThread(new ByteArrayInputStream(responseBody)
                                    , null
                                    , new SearchThread.OnSearchResult() {
                                @Override
                                public void searchResult(JSONObject jsonObject, Object type) {
                                    try {
                                        analyticJSON(jsonObject);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void error() {
                                    handler.sendEmptyMessage(HANDLER_UPLOAD_ERRO);
                                }
                            });
                            searchThread.start();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            handler.sendEmptyMessage(HANDLER_UPLOAD_ERRO);
                        }
                    });
        }
    }

    private void analyticJSON(JSONObject obj) {
        if (obj != null) {
            try {
                int code = obj.getInt("code");
                if (0 == code) {
                    handler.sendEmptyMessage(HANDLER_UPLOAD_SUCCESS);
                } else if (1 == code) {
                    if (obj.getString("obj").contains("Duplicate entry")) {
                        handler.sendEmptyMessage(HANDLER_NAME_DUPLICATE);
                    } else {
                        handler.sendEmptyMessage(HANDLER_UPLOAD_ERRO);
                    }
                } else {
                    handler.sendEmptyMessage(HANDLER_UPLOAD_ERRO);
                }
            } catch (JSONException e) {
                handler.sendEmptyMessage(HANDLER_UPLOAD_ERRO);
                e.printStackTrace();
            }
        } else {
            handler.sendEmptyMessage(HANDLER_UPLOAD_ERRO);
        }
    }
}
