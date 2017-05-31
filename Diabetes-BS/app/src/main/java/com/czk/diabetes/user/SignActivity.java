package com.czk.diabetes.user;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
 * Created by 陈忠凯 on 2017/5/25.
 */

public class SignActivity extends BaseActivity {
    private final static int CODE_CHOSE_IMAGE = 1024;
    private final static int CODE_RESULT_REQUEST = 1025;
    private final static int HANDLER_SIGN_ERRO = 0;
    private final static int HANDLER_SIGN_SUCCESS = 1;
    private final static int HANDLER_NAME_DUPLICATE = 3;
    private final static String TEM_IMG_NAMW = "username.jpg";
    private ImageView ivIcon;
    private View layoutMan;
    private View layoutWoman;
    private ImageView ivMan;
    private ImageView ivWoman;
    private TextView tvMan;
    private TextView tvWoman;
    private View layoutHead;
    private ImageView ivHead;
    private EditText etName;
    private EditText etPwd;
    private EditText etAge;
    private LoadingDialog loadingDialog;
    private long loadingStartTime;
    private int sex = 0;//0表示男，1表示女

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_SIGN_ERRO:
                    if (null != loadingDialog) {
                        long loadingTime = System.currentTimeMillis() - loadingStartTime;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                loadingDialog.dismiss();
                                ToastUtil.showShortToast(MyApplication.getInstance(), getResources().getString(R.string.sign_error));
                            }
                        }, loadingTime > 3000 ? loadingTime : 3000 - loadingTime);
                    } else {
                        ToastUtil.showShortToast(MyApplication.getInstance(), getResources().getString(R.string.sign_error));
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
                        }, loadingTime > 3000 ? loadingTime : 3000 - loadingTime);
                    } else {
                        ToastUtil.showShortToast(MyApplication.getInstance(), getResources().getString(R.string.duplicate_user_name));
                    }
                    break;
                case HANDLER_SIGN_SUCCESS:
                    ToastUtil.showShortToast(MyApplication.getInstance(), getResources().getString(R.string.sign_success));
                    if (null != loadingDialog) {
                        long loadingTime = System.currentTimeMillis() - loadingStartTime;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(SignActivity.this, LogInActivity.class);
                                intent.putExtra(LogInActivity.INTENT_SHOW_SPLASH, false);
                                startActivity(intent);
                                finish();
                                loadingDialog.dismiss();
                            }
                        }, loadingTime > 3000 ? loadingTime : 3000 - loadingTime);
                    } else {
                        Intent intent = new Intent(SignActivity.this, LogInActivity.class);
                        intent.putExtra(LogInActivity.INTENT_SHOW_SPLASH, false);
                        startActivity(intent);
                        finish();
                    }
                    try {
                        DiabetesClient.post(DiabetesClient.getAbsoluteUrl("saveFile")
                                , DiabetesClient.saveFile(SignActivity.this.getFilesDir() + File.separator + FileUtil.CACHEPATH + File.separator + TEM_IMG_NAMW
                                        ,"./img/users/"
                                        ,etName.getText()+".jpg")
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
        setContentView(R.layout.activity_sign);
        initView();
        dealEvent();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        switch (requestCode) {
            case CODE_CHOSE_IMAGE:
                CameraUtil.cropRawPhoto(SignActivity.this
                        , intent.getData()
                        , DimensUtil.dpTopx(MyApplication.getInstance(), 80)
                        , DimensUtil.dpTopx(MyApplication.getInstance(), 80)
                        , CODE_RESULT_REQUEST);
                break;
            case CODE_RESULT_REQUEST:
                if (intent != null) {
                    Bitmap bitmap = CameraUtil.setImageToHeadView(intent);
                    ivHead.setImageBitmap(bitmap);
                    Imageloader.getInstance().saveImgToJpg(SignActivity.this, bitmap, TEM_IMG_NAMW);
                }

                break;
        }

        super.onActivityResult(requestCode, resultCode, intent);
    }


    private void initView() {
        /**头部**/
        ivIcon = (ImageView) findViewById(R.id.icon);
        FontIconDrawable iconArrowLeft = FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_arrow_left);
        iconArrowLeft.setTextColor(getResources().getColor(R.color.white));
        ivIcon.setImageDrawable(iconArrowLeft);
        TextView tvTitle = (TextView) findViewById(R.id.title);
        tvTitle.setText(getResources().getString(R.string.sidn_in));

        /**主体**/
        layoutHead = findViewById(R.id.head_layout);
        ivHead = (ImageView) findViewById(R.id.iv_head);

        etName = (EditText) findViewById(R.id.et_name);
        etPwd = (EditText) findViewById(R.id.et_pwd);
        etAge = (EditText) findViewById(R.id.et_age);

        layoutMan = findViewById(R.id.man_layout);
        ivMan = (ImageView) findViewById(R.id.man_iv);
        tvMan = (TextView) findViewById(R.id.man_text);

        layoutWoman = findViewById(R.id.woman_layout);
        ivWoman = (ImageView) findViewById(R.id.woman_iv);
        tvWoman = (TextView) findViewById(R.id.woman_text);

        FontIconDrawable iconMan = FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_radio_checked);
        iconMan.setTextColor(getResources().getColor(R.color.safe_color));
        ivMan.setImageDrawable(iconMan);
        tvMan.setTextColor(getResources().getColor(R.color.safe_color));

        FontIconDrawable iconWoman = FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_radio_checked);
        iconWoman.setTextColor(getResources().getColor(R.color.txt_light_color));
        ivWoman.setImageDrawable(iconWoman);

        loadingDialog = new LoadingDialog(SignActivity.this,getResources().getString(R.string.signing));
    }


    private void dealEvent() {
        //头部
        ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        layoutHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraUtil.choseHeadImageFromGallery(SignActivity.this, CODE_CHOSE_IMAGE);
            }
        });

        layoutMan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FontIconDrawable iconMan = FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_radio_checked);
                iconMan.setTextColor(getResources().getColor(R.color.safe_color));
                ivMan.setImageDrawable(iconMan);
                tvMan.setTextColor(getResources().getColor(R.color.safe_color));

                FontIconDrawable iconWoman = FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_radio_checked);
                iconWoman.setTextColor(getResources().getColor(R.color.txt_light_color));
                ivWoman.setImageDrawable(iconWoman);
                tvWoman.setTextColor(getResources().getColor(R.color.txt_light_color));
                sex = 0;
            }
        });

        layoutWoman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FontIconDrawable iconMan = FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_radio_checked);
                iconMan.setTextColor(getResources().getColor(R.color.txt_light_color));
                ivMan.setImageDrawable(iconMan);
                tvMan.setTextColor(getResources().getColor(R.color.txt_light_color));

                FontIconDrawable iconWoman = FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_radio_checked);
                iconWoman.setTextColor(getResources().getColor(R.color.safe_color));
                ivWoman.setImageDrawable(iconWoman);
                tvWoman.setTextColor(getResources().getColor(R.color.safe_color));
                sex = 1;
            }
        });

        findViewById(R.id.bt_singn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInfo();
            }
        });
    }

    private void checkInfo() {
        if (StringUtil.isEmpty(etName.getText().toString())) {
            ToastUtil.showShortToast(SignActivity.this, getResources().getString(R.string.user_name_not_null));
        } else if (StringUtil.isEmpty(etPwd.getText().toString())) {
            ToastUtil.showShortToast(SignActivity.this, getResources().getString(R.string.pwd_not_null));
        } else if (StringUtil.isEmpty(etAge.getText().toString())) {
            ToastUtil.showShortToast(SignActivity.this, getResources().getString(R.string.age_not_null));
        } else {
            if (null != loadingDialog) {
                loadingDialog.show();
                loadingStartTime = System.currentTimeMillis();
            }
            String sql = "INSERT INTO `users` (`name`, `pwd`, `sex`, `year`, `img`) " +
                    "VALUES ('" + etName.getText() + "','" + etPwd.getText() + "'," + sex + "," + etAge.getText() + ",'" + DiabetesClient.getUserImgUrl(etName.getText() + ".jpg") + "')";
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
                                    handler.sendEmptyMessage(HANDLER_SIGN_ERRO);
                                }
                            });
                            searchThread.start();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            handler.sendEmptyMessage(HANDLER_SIGN_ERRO);
                        }
                    });
        }
    }

    private void analyticJSON(JSONObject obj) {
        if (obj != null) {
            try {
                int code = obj.getInt("code");
                if (0 == code) {
                    handler.sendEmptyMessage(HANDLER_SIGN_SUCCESS);
                } else if (1 == code) {
                    if (obj.getString("obj").contains("Duplicate entry")) {
                        handler.sendEmptyMessage(HANDLER_NAME_DUPLICATE);
                    } else {
                        handler.sendEmptyMessage(HANDLER_SIGN_ERRO);
                    }
                } else {
                    handler.sendEmptyMessage(HANDLER_SIGN_ERRO);
                }
            } catch (JSONException e) {
                handler.sendEmptyMessage(HANDLER_SIGN_ERRO);
                e.printStackTrace();
            }
        } else {
            handler.sendEmptyMessage(HANDLER_SIGN_ERRO);
        }
    }
}
