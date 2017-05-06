package com.czk.diabetes.aboutUs;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.czk.diabetes.BaseActivity;
import com.czk.diabetes.R;
import com.czk.diabetes.util.ThemeUtil;
import com.czk.diabetes.util.FileUtil;
import com.czk.diabetes.util.FontIconDrawable;
import com.czk.diabetes.util.ToastUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by 陈忠凯 on 2017/5/3.
 */

public class AboutUsActvity extends BaseActivity {
    private ImageView ivIcon;
    private String versionName = "czk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        initData();
        initView();
        dealEvent();
    }

    private void initData() {
    }

    private void initView() {
        //头部
        ivIcon = (ImageView) findViewById(R.id.icon);
        FontIconDrawable iconArrowLeft = FontIconDrawable.inflate(getApplicationContext(), R.xml.icon_arrow_left);
        iconArrowLeft.setTextColor(getResources().getColor(R.color.white));
        ivIcon.setImageDrawable(iconArrowLeft);
        TextView tvTitle = (TextView) findViewById(R.id.title);
        tvTitle.setText(getResources().getString(R.string.about_us));
        //主体
        TextView tvVersion = (TextView) findViewById(R.id.version);
        try {
            PackageInfo pkg = getPackageManager().getPackageInfo(
                    getApplication().getPackageName(), 0);
            versionName = pkg.versionName;
            tvVersion.setText(String.format(getString(R.string.copyright_version), versionName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        TextView textView = (TextView) findViewById(R.id.phone_type);
        textView.setText(android.os.Build.MODEL);

    }

    private void dealEvent() {
        ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //主体
        findViewById(R.id.share_apk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareApk();
            }
        });

        findViewById(R.id.rate_us).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = "market://details?id=" + getPackageName();
                Intent localIntent = new Intent("android.intent.action.VIEW");
                localIntent.setData(Uri.parse(str));
                startActivity(localIntent);
            }
        });
    }

    private void shareApk() {
        Intent intent = new Intent(Intent.ACTION_SEND);

        String toPath = FileUtil.BACKUP_PATH;
        String fileName = getResources().getString(R.string.app_name) +"_"+versionName+".apk";
        String newFileDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + toPath
                + File.separator;
        String newFileName = newFileDir + fileName;

        backupAPK(getPackageName(),newFileDir, newFileName);

        File file = new File(newFileName);
        if (file.exists()) {
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.setType("*/*");// 此处可发送多种文件
            startActivity(Intent.createChooser(intent, "Share"));
        } else {
            ToastUtil.showShortToast(AboutUsActvity.this, getResources().getString(R.string.failed_share));
        }
    }

    private void backupAPK(String packageName, String path, String outname) {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            int permission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if(permission!=PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
            }
        }
        try {
            String in = getPackageManager().getApplicationInfo(packageName, 0).sourceDir;
                if(null!=in){
                File mBaseFile = new File(path);
                if (!mBaseFile.exists()) {
                    mBaseFile.mkdir();
                }
                File out = new File(outname);
                if (!out.exists()) {
                    out.createNewFile();
                }
                FileInputStream fis = new FileInputStream(in);
                FileOutputStream fos = new FileOutputStream(out);

                int count;
                byte[] buffer = new byte[256 * 1024];
                while ((count = fis.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }

                fis.close();
                fos.flush();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


}
